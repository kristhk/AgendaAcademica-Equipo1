package com.equipo1.agendaacademica.domain

/** Prioridad de una tarea académica. [peso] permite ordenar de mayor a menor urgencia. */
enum class Prioridad(val etiqueta: String, val peso: Int) {
    ALTA("Alta", 3),
    MEDIA("Media", 2),
    BAJA("Baja", 1),
}

/** Estados posibles del ciclo de vida de una tarea. */
enum class EstadoTarea(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_PROGRESO("En progreso"),
    COMPLETADA("Completada"),
}

/** Paleta de colores (ARGB) disponible para identificar cada materia. */
object PaletaMaterias {
    val colores: List<Int> = listOf(
        0xFF3949AB, // índigo
        0xFF00897B, // verde azulado
        0xFFF4511E, // naranja
        0xFFD81B60, // rosa
        0xFF8E24AA, // morado
        0xFF43A047, // verde
        0xFFFFB300, // ámbar
        0xFF546E7A, // gris azulado
    ).map { it.toInt() }
}
