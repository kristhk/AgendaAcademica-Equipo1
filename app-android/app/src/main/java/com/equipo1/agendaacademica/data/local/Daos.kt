package com.equipo1.agendaacademica.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Operaciones CRUD sobre `tareas` e `historial_estados`. */
@Dao
abstract class TareaDao {

    @Transaction
    @Query("SELECT * FROM tareas ORDER BY fecha_entrega ASC, id ASC")
    abstract fun observarTodas(): Flow<List<TareaConMateria>>

    @Transaction
    @Query("SELECT * FROM tareas WHERE id = :id")
    abstract fun observarPorId(id: Long): Flow<TareaConMateria?>

    @Query("SELECT * FROM tareas WHERE id = :id")
    abstract suspend fun obtenerPorId(id: Long): TareaEntity?

    @Query("SELECT COUNT(*) FROM tareas WHERE materia_id = :materiaId")
    abstract suspend fun contarPorMateria(materiaId: Long): Int

    @Insert
    abstract suspend fun insertar(tarea: TareaEntity): Long

    @Update
    abstract suspend fun actualizar(tarea: TareaEntity)

    @Delete
    abstract suspend fun eliminar(tarea: TareaEntity)

    @Insert
    abstract suspend fun insertarHistorial(registro: HistorialEstadoEntity): Long

    @Query("SELECT * FROM historial_estados WHERE tarea_id = :tareaId ORDER BY fecha DESC, id DESC")
    abstract fun observarHistorial(tareaId: Long): Flow<List<HistorialEstadoEntity>>

    /** Inserta la tarea y su registro inicial de historial en una sola transacción. */
    @Transaction
    open suspend fun insertarConHistorial(tarea: TareaEntity): Long {
        val id = insertar(tarea)
        insertarHistorial(
            HistorialEstadoEntity(
                tareaId = id,
                estadoAnterior = null,
                estadoNuevo = tarea.estado,
                fecha = tarea.fechaCreacion,
            )
        )
        return id
    }

    /** Actualiza la tarea y, si cambió de estado, registra el cambio en el historial. */
    @Transaction
    open suspend fun actualizarConHistorial(anterior: TareaEntity, nueva: TareaEntity, ahora: Long) {
        actualizar(nueva)
        if (anterior.estado != nueva.estado) {
            insertarHistorial(
                HistorialEstadoEntity(
                    tareaId = nueva.id,
                    estadoAnterior = anterior.estado,
                    estadoNuevo = nueva.estado,
                    fecha = ahora,
                )
            )
        }
    }
}

/** Operaciones CRUD sobre `materias`. */
@Dao
interface MateriaDao {

    @Query("SELECT * FROM materias ORDER BY nombre COLLATE NOCASE")
    fun observarTodas(): Flow<List<MateriaEntity>>

    @Query(
        """
        SELECT m.*,
               COUNT(t.id) AS total,
               COALESCE(SUM(CASE WHEN t.estado = 'COMPLETADA' THEN 1 ELSE 0 END), 0) AS completadas
        FROM materias m
        LEFT JOIN tareas t ON t.materia_id = m.id
        GROUP BY m.id
        ORDER BY m.nombre COLLATE NOCASE
        """
    )
    fun observarConAvance(): Flow<List<MateriaConAvance>>

    @Query("SELECT * FROM materias WHERE id = :id")
    suspend fun obtenerPorId(id: Long): MateriaEntity?

    /** Cuenta materias con el mismo nombre sin distinguir mayúsculas, excluyendo [excluirId]. */
    @Query("SELECT COUNT(*) FROM materias WHERE nombre = :nombre COLLATE NOCASE AND id != :excluirId")
    suspend fun contarConNombre(nombre: String, excluirId: Long): Int

    @Query("SELECT COUNT(*) FROM materias")
    suspend fun contar(): Int

    @Insert
    suspend fun insertar(materia: MateriaEntity): Long

    @Update
    suspend fun actualizar(materia: MateriaEntity)

    @Delete
    suspend fun eliminar(materia: MateriaEntity)
}
