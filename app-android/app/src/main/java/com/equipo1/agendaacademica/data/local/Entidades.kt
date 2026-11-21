package com.equipo1.agendaacademica.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.Prioridad
import java.time.LocalDate

/** Tabla `materias`: asignaturas con las que se clasifican las tareas. */
@Entity(
    tableName = "materias",
    indices = [Index(value = ["nombre"], unique = true)],
)
data class MateriaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val profesor: String? = null,
    /** Color ARGB con el que se identifica la materia en la interfaz. */
    val color: Int,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis(),
)

/**
 * Tabla `tareas`. La llave foránea a `materias` usa RESTRICT: la base de datos impide borrar
 * una materia que todavía tiene tareas (RN-09).
 */
@Entity(
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = MateriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["materia_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("materia_id"), Index("fecha_entrega"), Index("estado")],
)
data class TareaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descripcion: String = "",
    @ColumnInfo(name = "materia_id") val materiaId: Long,
    val prioridad: Prioridad,
    @ColumnInfo(name = "fecha_entrega") val fechaEntrega: LocalDate,
    val estado: EstadoTarea = EstadoTarea.PENDIENTE,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long = System.currentTimeMillis(),
    /** Momento en que se marcó como completada; `null` si no está completada (RN-06). */
    @ColumnInfo(name = "fecha_completada") val fechaCompletada: Long? = null,
)

/**
 * Tabla `historial_estados`: bitácora de cada cambio de estado de una tarea (RN-07).
 * Si se elimina la tarea, su historial se elimina en cascada.
 */
@Entity(
    tableName = "historial_estados",
    foreignKeys = [
        ForeignKey(
            entity = TareaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tarea_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("tarea_id")],
)
data class HistorialEstadoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "tarea_id") val tareaId: Long,
    /** `null` en el registro inicial (creación de la tarea). */
    @ColumnInfo(name = "estado_anterior") val estadoAnterior: EstadoTarea?,
    @ColumnInfo(name = "estado_nuevo") val estadoNuevo: EstadoTarea,
    val fecha: Long = System.currentTimeMillis(),
)

/** Resultado de consulta: tarea junto con su materia (relación N:1). */
data class TareaConMateria(
    @Embedded val tarea: TareaEntity,
    @Relation(parentColumn = "materia_id", entityColumn = "id")
    val materia: MateriaEntity,
)

/** Resultado de consulta: materia con el conteo de sus tareas totales y completadas. */
data class MateriaConAvance(
    @Embedded val materia: MateriaEntity,
    val total: Int,
    val completadas: Int,
)
