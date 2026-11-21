package com.equipo1.agendaacademica.data

import androidx.room.withTransaction
import com.equipo1.agendaacademica.data.local.AgendaDatabase
import com.equipo1.agendaacademica.data.local.HistorialEstadoEntity
import com.equipo1.agendaacademica.data.local.MateriaConAvance
import com.equipo1.agendaacademica.data.local.MateriaEntity
import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.data.local.TareaEntity
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.PaletaMaterias
import com.equipo1.agendaacademica.domain.Prioridad
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/** Resultado de una operación de escritura que puede ser rechazada por una regla de negocio. */
sealed interface ResultadoOperacion {
    data object Exito : ResultadoOperacion
    data class Error(val mensaje: String) : ResultadoOperacion
}

/**
 * Único punto de acceso a los datos para los ViewModels (patrón Repository).
 * Aplica las reglas de negocio que dependen de la base de datos.
 *
 * @param reloj fuente de la hora actual; se sustituye en las pruebas.
 */
class RepositorioAcademico(
    private val db: AgendaDatabase,
    private val reloj: () -> Long = System::currentTimeMillis,
) {
    private val tareaDao = db.tareaDao()
    private val materiaDao = db.materiaDao()

    // ---------- Consultas (Flow: la interfaz se actualiza sola al cambiar los datos) ----------
    val tareas: Flow<List<TareaConMateria>> = tareaDao.observarTodas()
    val materias: Flow<List<MateriaEntity>> = materiaDao.observarTodas()
    val materiasConAvance: Flow<List<MateriaConAvance>> = materiaDao.observarConAvance()

    fun observarTarea(id: Long): Flow<TareaConMateria?> = tareaDao.observarPorId(id)
    fun observarHistorial(tareaId: Long): Flow<List<HistorialEstadoEntity>> = tareaDao.observarHistorial(tareaId)
    suspend fun obtenerTarea(id: Long): TareaEntity? = tareaDao.obtenerPorId(id)

    // ---------- Tareas ----------

    /** RN-05: toda tarea nueva inicia en estado Pendiente. */
    suspend fun crearTarea(
        titulo: String,
        descripcion: String,
        materiaId: Long,
        prioridad: Prioridad,
        fechaEntrega: LocalDate,
    ): Long = tareaDao.insertarConHistorial(
        TareaEntity(
            titulo = titulo.trim(),
            descripcion = descripcion.trim(),
            materiaId = materiaId,
            prioridad = prioridad,
            fechaEntrega = fechaEntrega,
            estado = EstadoTarea.PENDIENTE,
            fechaCreacion = reloj(),
        )
    )

    suspend fun actualizarTarea(
        id: Long,
        titulo: String,
        descripcion: String,
        materiaId: Long,
        prioridad: Prioridad,
        fechaEntrega: LocalDate,
        estado: EstadoTarea,
    ): ResultadoOperacion {
        val anterior = tareaDao.obtenerPorId(id) ?: return ResultadoOperacion.Error("La tarea ya no existe")
        val editada = anterior.copy(
            titulo = titulo.trim(),
            descripcion = descripcion.trim(),
            materiaId = materiaId,
            prioridad = prioridad,
            fechaEntrega = fechaEntrega,
        )
        tareaDao.actualizarConHistorial(anterior, aplicarEstado(editada, estado), reloj())
        return ResultadoOperacion.Exito
    }

    suspend fun cambiarEstado(id: Long, nuevoEstado: EstadoTarea): ResultadoOperacion {
        val anterior = tareaDao.obtenerPorId(id) ?: return ResultadoOperacion.Error("La tarea ya no existe")
        if (anterior.estado == nuevoEstado) return ResultadoOperacion.Exito
        tareaDao.actualizarConHistorial(anterior, aplicarEstado(anterior, nuevoEstado), reloj())
        return ResultadoOperacion.Exito
    }

    suspend fun eliminarTarea(id: Long): ResultadoOperacion {
        val tarea = tareaDao.obtenerPorId(id) ?: return ResultadoOperacion.Error("La tarea ya no existe")
        tareaDao.eliminar(tarea) // el historial se elimina en cascada
        return ResultadoOperacion.Exito
    }

    /** RN-06: al completar se guarda la fecha; al salir de Completada se limpia. */
    private fun aplicarEstado(tarea: TareaEntity, estado: EstadoTarea): TareaEntity = when {
        estado == EstadoTarea.COMPLETADA && tarea.estado != EstadoTarea.COMPLETADA ->
            tarea.copy(estado = estado, fechaCompletada = reloj())
        estado != EstadoTarea.COMPLETADA -> tarea.copy(estado = estado, fechaCompletada = null)
        else -> tarea.copy(estado = estado)
    }

    // ---------- Materias ----------

    /** Crea ([id] = null) o actualiza una materia. RN-10: el nombre no se repite (sin distinguir mayúsculas). */
    suspend fun guardarMateria(id: Long?, nombre: String, profesor: String, color: Int): ResultadoOperacion {
        val nombreLimpio = nombre.trim()
        if (materiaDao.contarConNombre(nombreLimpio, id ?: 0L) > 0) {
            return ResultadoOperacion.Error("Ya existe una materia llamada \"$nombreLimpio\"")
        }
        val profesorLimpio = profesor.trim().ifBlank { null }
        if (id == null) {
            materiaDao.insertar(
                MateriaEntity(nombre = nombreLimpio, profesor = profesorLimpio, color = color, fechaCreacion = reloj())
            )
        } else {
            val actual = materiaDao.obtenerPorId(id) ?: return ResultadoOperacion.Error("La materia ya no existe")
            materiaDao.actualizar(actual.copy(nombre = nombreLimpio, profesor = profesorLimpio, color = color))
        }
        return ResultadoOperacion.Exito
    }

    /** RN-09: no se puede eliminar una materia con tareas asociadas. */
    suspend fun eliminarMateria(id: Long): ResultadoOperacion {
        val materia = materiaDao.obtenerPorId(id) ?: return ResultadoOperacion.Error("La materia ya no existe")
        val asociadas = tareaDao.contarPorMateria(id)
        if (asociadas > 0) {
            return ResultadoOperacion.Error(
                "No se puede eliminar \"${materia.nombre}\": tiene $asociadas tarea(s) asociada(s)"
            )
        }
        materiaDao.eliminar(materia)
        return ResultadoOperacion.Exito
    }

    // ---------- Datos de demostración ----------

    /**
     * Carga materias y tareas de ejemplo (fechas relativas a [hoy]) para la demostración.
     * Solo se permite con la agenda vacía, para no mezclarlas con datos reales del usuario.
     */
    suspend fun cargarDatosEjemplo(hoy: LocalDate): ResultadoOperacion {
        if (materiaDao.contar() > 0) {
            return ResultadoOperacion.Error("Los datos de ejemplo solo se pueden cargar con la agenda vacía")
        }
        db.withTransaction {
            val colores = PaletaMaterias.colores
            fun nuevaMateria(nombre: String, profesor: String, color: Int) = MateriaEntity(
                nombre = nombre, profesor = profesor, color = color, fechaCreacion = reloj()
            )
            val moviles = materiaDao.insertar(nuevaMateria("Programación de Móviles", "Docente de Móviles", colores[0]))
            val bd = materiaDao.insertar(nuevaMateria("Bases de Datos", "Docente de BD", colores[1]))
            val ingSoft = materiaDao.insertar(nuevaMateria("Ingeniería de Software", "Docente de IS", colores[2]))
            val calculo = materiaDao.insertar(nuevaMateria("Cálculo Integral", "Docente de Cálculo", colores[3]))

            suspend fun tarea(
                titulo: String, descripcion: String, materia: Long, prioridad: Prioridad, dias: Long,
                vararg estados: EstadoTarea,
            ) {
                val id = crearTarea(titulo, descripcion, materia, prioridad, hoy.plusDays(dias))
                estados.forEach { cambiarEstado(id, it) }
            }
            tarea("Entregar maquetas en Figma", "Pantallas principales y prototipo navegable.", moviles, Prioridad.ALTA, 1, EstadoTarea.EN_PROGRESO)
            tarea("Expediente técnico del examen", "Requerimientos, mapa de navegación y modelo de datos.", moviles, Prioridad.ALTA, 4)
            tarea("Práctica de Room", "CRUD con DAO y Flow.", moviles, Prioridad.MEDIA, -3, EstadoTarea.EN_PROGRESO, EstadoTarea.COMPLETADA)
            tarea("Diagrama entidad-relación", "Normalizar hasta 3FN.", bd, Prioridad.MEDIA, 2)
            tarea("Consultas SQL avanzadas", "JOIN, GROUP BY y subconsultas.", bd, Prioridad.BAJA, -2)
            tarea("Historias de usuario", "Redactar criterios de aceptación.", ingSoft, Prioridad.MEDIA, 6, EstadoTarea.COMPLETADA)
            tarea("Plan de pruebas", "Casos de prueba funcionales.", ingSoft, Prioridad.BAJA, 12)
            tarea("Ejercicios de integración por partes", "Lista 5, ejercicios 1 al 20.", calculo, Prioridad.ALTA, 0, EstadoTarea.EN_PROGRESO)
        }
        return ResultadoOperacion.Exito
    }
}
