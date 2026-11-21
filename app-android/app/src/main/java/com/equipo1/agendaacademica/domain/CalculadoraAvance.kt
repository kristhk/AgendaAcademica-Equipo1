package com.equipo1.agendaacademica.domain

import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.data.local.TareaEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Resumen numérico del avance de un conjunto de tareas. */
data class ResumenAvance(
    val total: Int = 0,
    val pendientes: Int = 0,
    val enProgreso: Int = 0,
    val completadas: Int = 0,
    val vencidas: Int = 0,
    val completadasATiempo: Int = 0,
    val completadasConRetraso: Int = 0,
) {
    /** Porcentaje de avance (RN-11): completadas / total × 100, redondeado. */
    val porcentaje: Int get() = CalculadoraAvance.porcentaje(completadas, total)
}

/** Funciones puras para calcular avances y estadísticas (sin dependencias de Android). */
object CalculadoraAvance {

    fun porcentaje(parte: Int, total: Int): Int =
        if (total <= 0) 0 else Math.round(parte * 100f / total)

    /** RN-08: una tarea está vencida si no está completada y su fecha de entrega ya pasó. */
    fun esVencida(tarea: TareaEntity, hoy: LocalDate): Boolean =
        tarea.estado != EstadoTarea.COMPLETADA && tarea.fechaEntrega.isBefore(hoy)

    /** `true` si se completó el día de entrega o antes; `null` si no está completada. */
    fun fueCompletadaATiempo(tarea: TareaEntity, zona: ZoneId = ZoneId.systemDefault()): Boolean? {
        val marca = tarea.fechaCompletada ?: return null
        val dia = Instant.ofEpochMilli(marca).atZone(zona).toLocalDate()
        return !dia.isAfter(tarea.fechaEntrega)
    }

    fun resumen(tareas: List<TareaEntity>, hoy: LocalDate, zona: ZoneId = ZoneId.systemDefault()): ResumenAvance {
        val completadas = tareas.filter { it.estado == EstadoTarea.COMPLETADA }
        val aTiempo = completadas.count { fueCompletadaATiempo(it, zona) == true }
        return ResumenAvance(
            total = tareas.size,
            pendientes = tareas.count { it.estado == EstadoTarea.PENDIENTE },
            enProgreso = tareas.count { it.estado == EstadoTarea.EN_PROGRESO },
            completadas = completadas.size,
            vencidas = tareas.count { esVencida(it, hoy) },
            completadasATiempo = aTiempo,
            completadasConRetraso = completadas.size - aTiempo,
        )
    }

    /** Tareas no completadas que vencen entre hoy y hoy + [dias], ordenadas por fecha y prioridad. */
    fun proximasEntregas(tareas: List<TareaConMateria>, hoy: LocalDate, dias: Long = 7): List<TareaConMateria> {
        val limite = hoy.plusDays(dias)
        return tareas
            .filter {
                it.tarea.estado != EstadoTarea.COMPLETADA &&
                    !it.tarea.fechaEntrega.isBefore(hoy) &&
                    !it.tarea.fechaEntrega.isAfter(limite)
            }
            .sortedWith(compareBy<TareaConMateria>({ it.tarea.fechaEntrega }, { -it.tarea.prioridad.peso }))
    }

    /** Número de tareas sin completar por prioridad (incluye todas las prioridades, aunque sean 0). */
    fun pendientesPorPrioridad(tareas: List<TareaEntity>): Map<Prioridad, Int> =
        Prioridad.entries.associateWith { p ->
            tareas.count { it.prioridad == p && it.estado != EstadoTarea.COMPLETADA }
        }
}
