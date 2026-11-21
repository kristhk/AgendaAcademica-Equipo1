package com.equipo1.agendaacademica.data

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.equipo1.agendaacademica.data.local.AgendaDatabase
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.Prioridad
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/** Pruebas de integración de la capa de datos sobre una base Room en memoria. */
@RunWith(AndroidJUnit4::class)
class RepositorioAcademicoTest {
    private lateinit var db: AgendaDatabase
    private lateinit var repo: RepositorioAcademico
    private var ahora = 1_000L
    private val hoy = LocalDate.of(2026, 11, 20)

    @Before
    fun crearBase() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AgendaDatabase::class.java).build()
        repo = RepositorioAcademico(db) { ahora }
    }

    @After
    fun cerrarBase() = db.close()

    private suspend fun crearMateria(nombre: String = "Móviles"): Long {
        repo.guardarMateria(null, nombre, "", 0)
        return repo.materias.first().first { it.nombre == nombre }.id
    }

    @Test
    fun crearTarea_iniciaPendienteYRegistraHistorial() = runTest {
        val materia = crearMateria()
        val id = repo.crearTarea("  Ensayo  ", " desc ", materia, Prioridad.ALTA, hoy)

        val tarea = repo.obtenerTarea(id)!!
        assertEquals("Ensayo", tarea.titulo) // se recortan espacios
        assertEquals("desc", tarea.descripcion)
        assertEquals(EstadoTarea.PENDIENTE, tarea.estado)
        val historial = repo.observarHistorial(id).first()
        assertEquals(1, historial.size)
        assertNull(historial[0].estadoAnterior)
        assertEquals(EstadoTarea.PENDIENTE, historial[0].estadoNuevo)
    }

    @Test
    fun tareaConMateria_devuelveLaRelacion() = runTest {
        val materia = crearMateria("Bases de Datos")
        repo.crearTarea("Diagrama ER", "", materia, Prioridad.MEDIA, hoy)
        val lista = repo.tareas.first()
        assertEquals(1, lista.size)
        assertEquals("Bases de Datos", lista[0].materia.nombre)
    }

    @Test
    fun completarTarea_guardaFechaYRegistraCambio() = runTest {
        val id = repo.crearTarea("T", "", crearMateria(), Prioridad.BAJA, hoy)
        ahora = 5_000L
        repo.cambiarEstado(id, EstadoTarea.COMPLETADA)

        val tarea = repo.obtenerTarea(id)!!
        assertEquals(EstadoTarea.COMPLETADA, tarea.estado)
        assertEquals(5_000L, tarea.fechaCompletada)
        val ultimo = repo.observarHistorial(id).first().first()
        assertEquals(EstadoTarea.PENDIENTE, ultimo.estadoAnterior)
        assertEquals(EstadoTarea.COMPLETADA, ultimo.estadoNuevo)
    }

    @Test
    fun reabrirTarea_limpiaFechaCompletada() = runTest {
        val id = repo.crearTarea("T", "", crearMateria(), Prioridad.BAJA, hoy)
        repo.cambiarEstado(id, EstadoTarea.COMPLETADA)
        repo.cambiarEstado(id, EstadoTarea.EN_PROGRESO)
        assertNull(repo.obtenerTarea(id)!!.fechaCompletada)
        assertEquals(3, repo.observarHistorial(id).first().size)
    }

    @Test
    fun cambiarAlMismoEstado_noDuplicaHistorial() = runTest {
        val id = repo.crearTarea("T", "", crearMateria(), Prioridad.BAJA, hoy)
        repo.cambiarEstado(id, EstadoTarea.PENDIENTE)
        assertEquals(1, repo.observarHistorial(id).first().size)
    }

    @Test
    fun actualizarTarea_modificaCampos() = runTest {
        val m1 = crearMateria("A1")
        val m2 = crearMateria("B2")
        val id = repo.crearTarea("Original", "", m1, Prioridad.BAJA, hoy)
        val r = repo.actualizarTarea(id, "Editada", "nueva", m2, Prioridad.ALTA, hoy.plusDays(2), EstadoTarea.EN_PROGRESO)
        assertEquals(ResultadoOperacion.Exito, r)
        val t = repo.obtenerTarea(id)!!
        assertEquals("Editada", t.titulo)
        assertEquals(m2, t.materiaId)
        assertEquals(Prioridad.ALTA, t.prioridad)
        assertEquals(hoy.plusDays(2), t.fechaEntrega)
        assertEquals(EstadoTarea.EN_PROGRESO, t.estado)
    }

    @Test
    fun eliminarTarea_eliminaSuHistorialEnCascada() = runTest {
        val id = repo.crearTarea("T", "", crearMateria(), Prioridad.BAJA, hoy)
        repo.cambiarEstado(id, EstadoTarea.COMPLETADA)
        repo.eliminarTarea(id)
        assertNull(repo.obtenerTarea(id))
        assertTrue(repo.observarHistorial(id).first().isEmpty())
    }

    @Test
    fun materiaConNombreRepetido_esRechazada_sinDistinguirMayusculas() = runTest {
        crearMateria("Cálculo")
        val r = repo.guardarMateria(null, "  cálculo ", "", 0)
        assertTrue(r is ResultadoOperacion.Error)
        assertEquals(1, repo.materias.first().size)
    }

    @Test
    fun editarMateria_conservandoSuNombre_esPermitido() = runTest {
        val id = crearMateria("Física")
        val r = repo.guardarMateria(id, "Física", "Docente", 5)
        assertEquals(ResultadoOperacion.Exito, r)
        assertEquals("Docente", repo.materias.first().first().profesor)
    }

    @Test
    fun eliminarMateriaConTareas_esRechazado() = runTest {
        val materia = crearMateria()
        repo.crearTarea("T", "", materia, Prioridad.BAJA, hoy)
        val r = repo.eliminarMateria(materia)
        assertTrue(r is ResultadoOperacion.Error)
        assertEquals(1, repo.materias.first().size)
    }

    @Test
    fun eliminarMateriaSinTareas_esPermitido() = runTest {
        val materia = crearMateria()
        assertEquals(ResultadoOperacion.Exito, repo.eliminarMateria(materia))
        assertTrue(repo.materias.first().isEmpty())
    }

    @Test
    fun llaveForaneaRestrict_impideBorrarMateriaDesdeLaBaseDeDatos() = runTest {
        val materia = crearMateria()
        repo.crearTarea("T", "", materia, Prioridad.BAJA, hoy)
        val entidad = db.materiaDao().obtenerPorId(materia)!!
        try {
            db.materiaDao().eliminar(entidad)
            fail("Se esperaba una violación de la llave foránea")
        } catch (e: SQLiteConstraintException) {
            assertNotNull(e.message)
        }
    }

    @Test
    fun materiasConAvance_cuentaTotalesYCompletadas() = runTest {
        val m1 = crearMateria("Con tareas")
        crearMateria("Sin tareas")
        val a = repo.crearTarea("A", "", m1, Prioridad.BAJA, hoy)
        repo.crearTarea("B", "", m1, Prioridad.BAJA, hoy)
        repo.cambiarEstado(a, EstadoTarea.COMPLETADA)

        val avance = repo.materiasConAvance.first().associateBy { it.materia.nombre }
        assertEquals(2, avance.getValue("Con tareas").total)
        assertEquals(1, avance.getValue("Con tareas").completadas)
        assertEquals(0, avance.getValue("Sin tareas").total)
        assertEquals(0, avance.getValue("Sin tareas").completadas)
    }

    /** Instrumento 6, S-2: los textos se guardan como datos (parámetros enlazados), nunca como SQL. */
    @Test
    fun textoConSqlMalicioso_seGuardaLiteralYNoAfectaLaBase() = runTest {
        val malicioso = "'); DROP TABLE tareas;--"
        val id = repo.crearTarea(malicioso, "x' OR '1'='1", crearMateria(), Prioridad.BAJA, hoy)
        repo.crearTarea("Otra tarea", "", repo.materias.first().first().id, Prioridad.BAJA, hoy)

        assertEquals(malicioso, repo.obtenerTarea(id)!!.titulo)
        assertEquals(2, repo.tareas.first().size) // la tabla sigue existiendo y completa
    }

    /** RNF-02: los datos persisten en el archivo SQLite al cerrar y volver a abrir la base. */
    @Test
    fun datosPersistenEnDisco_alCerrarYReabrirLaBase() = runTest {
        val contexto = ApplicationProvider.getApplicationContext<android.content.Context>()
        val nombre = "prueba_persistencia.db"
        contexto.deleteDatabase(nombre)

        val bd1 = Room.databaseBuilder(contexto, AgendaDatabase::class.java, nombre).build()
        val repo1 = RepositorioAcademico(bd1)
        repo1.guardarMateria(null, "Persistente", "", 0)
        repo1.crearTarea("Tarea guardada", "", repo1.materias.first().first().id, Prioridad.ALTA, hoy)
        bd1.close()

        val bd2 = Room.databaseBuilder(contexto, AgendaDatabase::class.java, nombre).build()
        val lista = RepositorioAcademico(bd2).tareas.first()
        assertEquals(1, lista.size)
        assertEquals("Tarea guardada", lista[0].tarea.titulo)
        assertEquals("Persistente", lista[0].materia.nombre)
        bd2.close()
        contexto.deleteDatabase(nombre)
    }

    @Test
    fun datosDeEjemplo_soloConAgendaVacia() = runTest {
        assertEquals(ResultadoOperacion.Exito, repo.cargarDatosEjemplo(hoy))
        assertEquals(4, repo.materias.first().size)
        assertEquals(8, repo.tareas.first().size)
        assertTrue(repo.cargarDatosEjemplo(hoy) is ResultadoOperacion.Error)
    }
}
