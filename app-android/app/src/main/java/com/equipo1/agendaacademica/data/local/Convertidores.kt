package com.equipo1.agendaacademica.data.local

import androidx.room.TypeConverter
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.Prioridad
import java.time.LocalDate

/** Convierte tipos de Kotlin a tipos que SQLite puede almacenar. */
class Convertidores {
    /** LocalDate → número de días desde 1970-01-01 (INTEGER). */
    @TypeConverter
    fun fechaANumero(fecha: LocalDate?): Long? = fecha?.toEpochDay()

    @TypeConverter
    fun numeroAFecha(valor: Long?): LocalDate? = valor?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun prioridadATexto(prioridad: Prioridad?): String? = prioridad?.name

    @TypeConverter
    fun textoAPrioridad(texto: String?): Prioridad? = texto?.let(Prioridad::valueOf)

    @TypeConverter
    fun estadoATexto(estado: EstadoTarea?): String? = estado?.name

    @TypeConverter
    fun textoAEstado(texto: String?): EstadoTarea? = texto?.let(EstadoTarea::valueOf)
}
