package com.equipo1.agendaacademica.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Formatos de fecha en español usados en toda la interfaz. */
object FormatoFechas {
    private val locale: Locale = Locale.forLanguageTag("es-MX")
    private val formatoCorto = DateTimeFormatter.ofPattern("d MMM yyyy", locale)
    private val formatoLargo = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale)
    private val formatoFechaHora = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", locale)

    fun corta(fecha: LocalDate): String = formatoCorto.format(fecha)

    fun larga(fecha: LocalDate): String =
        formatoLargo.format(fecha).replaceFirstChar { it.titlecase(locale) }

    fun fechaHora(milis: Long, zona: ZoneId = ZoneId.systemDefault()): String =
        formatoFechaHora.format(Instant.ofEpochMilli(milis).atZone(zona))

    /** Texto amigable con los días que faltan (o que han pasado) para la entrega. */
    fun textoRelativo(fecha: LocalDate, hoy: LocalDate): String {
        val dias = ChronoUnit.DAYS.between(hoy, fecha)
        return when {
            dias == 0L -> "Vence hoy"
            dias == 1L -> "Vence mañana"
            dias > 1L -> "Faltan $dias días"
            dias == -1L -> "Venció ayer"
            else -> "Venció hace ${-dias} días"
        }
    }
}
