package com.equipo1.agendaacademica.domain

import com.equipo1.agendaacademica.data.local.TareaConMateria
import java.text.Collator
import java.util.Locale

enum class OrdenTareas(val etiqueta: String) {
    FECHA("Fecha de entrega"),
    PRIORIDAD("Prioridad"),
    TITULO("Título (A-Z)"),
}

/** Criterios de búsqueda y clasificación de la lista de tareas. `null` = sin filtro. */
data class FiltrosTareas(
    val texto: String = "",
    val estado: EstadoTarea? = null,
    val prioridad: Prioridad? = null,
    val materiaId: Long? = null,
    val orden: OrdenTareas = OrdenTareas.FECHA,
) {
    val hayFiltrosActivos: Boolean
        get() = texto.isNotBlank() || estado != null || prioridad != null || materiaId != null
}

object FiltroTareas {
    fun aplicar(tareas: List<TareaConMateria>, filtros: FiltrosTareas): List<TareaConMateria> {
        val texto = filtros.texto.trim()
        val filtradas = tareas.filter { item ->
            val t = item.tarea
            (texto.isEmpty() ||
                t.titulo.contains(texto, ignoreCase = true) ||
                t.descripcion.contains(texto, ignoreCase = true)) &&
                (filtros.estado == null || t.estado == filtros.estado) &&
                (filtros.prioridad == null || t.prioridad == filtros.prioridad) &&
                (filtros.materiaId == null || t.materiaId == filtros.materiaId)
        }
        return when (filtros.orden) {
            OrdenTareas.FECHA -> filtradas.sortedWith(
                compareBy<TareaConMateria>({ it.tarea.fechaEntrega }, { -it.tarea.prioridad.peso })
            )
            OrdenTareas.PRIORIDAD -> filtradas.sortedWith(
                compareBy<TareaConMateria>({ -it.tarea.prioridad.peso }, { it.tarea.fechaEntrega })
            )
            // Collator en español: ordena "Práctica" antes que "Proyecto" (ignora acentos y mayúsculas)
            OrdenTareas.TITULO -> filtradas.sortedWith(compareBy(colador) { it.tarea.titulo })
        }
    }

    private val colador: Collator = Collator.getInstance(Locale.forLanguageTag("es")).apply {
        strength = Collator.PRIMARY
    }
}
