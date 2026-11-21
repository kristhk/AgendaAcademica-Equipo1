package com.equipo1.agendaacademica.domain

import java.time.LocalDate

/**
 * Reglas de validación del formulario de tareas (RN-01 a RN-04).
 * Cada función devuelve el mensaje de error en español o `null` si el valor es válido.
 */
object ValidadorTarea {
    const val TITULO_MIN = 3
    const val TITULO_MAX = 80
    const val DESCRIPCION_MAX = 500

    fun validarTitulo(titulo: String): String? {
        val t = titulo.trim()
        return when {
            t.isEmpty() -> "El título es obligatorio"
            t.length < TITULO_MIN -> "El título debe tener al menos $TITULO_MIN caracteres"
            t.length > TITULO_MAX -> "El título no puede superar $TITULO_MAX caracteres"
            else -> null
        }
    }

    fun validarDescripcion(descripcion: String): String? =
        if (descripcion.trim().length > DESCRIPCION_MAX) {
            "La descripción no puede superar $DESCRIPCION_MAX caracteres"
        } else {
            null
        }

    fun validarMateria(materiaId: Long?): String? =
        if (materiaId == null) "Selecciona una materia" else null

    /**
     * La fecha es obligatoria y no puede ser anterior a hoy. Al editar se permite conservar
     * la fecha original aunque ya haya pasado (para no obligar a cambiarla).
     */
    fun validarFechaEntrega(fecha: LocalDate?, hoy: LocalDate, fechaOriginal: LocalDate? = null): String? =
        when {
            fecha == null -> "Selecciona la fecha de entrega"
            fecha.isBefore(hoy) && fecha != fechaOriginal -> "La fecha de entrega no puede ser anterior a hoy"
            else -> null
        }
}

/** Reglas de validación de materias (RN-10). */
object ValidadorMateria {
    const val NOMBRE_MIN = 2
    const val NOMBRE_MAX = 50
    const val PROFESOR_MAX = 60

    fun validarNombre(nombre: String): String? {
        val n = nombre.trim()
        return when {
            n.isEmpty() -> "El nombre de la materia es obligatorio"
            n.length < NOMBRE_MIN -> "El nombre debe tener al menos $NOMBRE_MIN caracteres"
            n.length > NOMBRE_MAX -> "El nombre no puede superar $NOMBRE_MAX caracteres"
            else -> null
        }
    }

    fun validarProfesor(profesor: String): String? =
        if (profesor.trim().length > PROFESOR_MAX) "El nombre del profesor no puede superar $PROFESOR_MAX caracteres" else null
}
