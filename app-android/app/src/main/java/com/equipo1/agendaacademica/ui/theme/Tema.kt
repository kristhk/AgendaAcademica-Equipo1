package com.equipo1.agendaacademica.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.Prioridad

// Paleta principal: índigo (primario) y verde azulado (secundario). Documentada en 03_Maquetado.
private val EsquemaClaro = lightColorScheme(
    primary = Color(0xFF3949AB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF00105C),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBE3),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFFF4511E),
    onTertiary = Color.White,
    background = Color(0xFFF8F9FF),
    onBackground = Color(0xFF1A1B21),
    surface = Color(0xFFF8F9FF),
    onSurface = Color(0xFF1A1B21),
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFFB9C3FF),
    onPrimary = Color(0xFF08218A),
    primaryContainer = Color(0xFF2B3EA0),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFF80D5C9),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFFB2EBE3),
    tertiary = Color(0xFFFFB59F),
    background = Color(0xFF121318),
    onBackground = Color(0xFFE3E1E9),
    surface = Color(0xFF121318),
    onSurface = Color(0xFFE3E1E9),
    surfaceVariant = Color(0xFF45464F),
    onSurfaceVariant = Color(0xFFC6C5D0),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
fun AgendaAcademicaTheme(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) EsquemaOscuro else EsquemaClaro,
        typography = Typography(),
        content = content,
    )
}

/** Color semántico de cada prioridad (rojo = alta, naranja = media, verde = baja). */
fun colorPrioridad(prioridad: Prioridad): Color = when (prioridad) {
    Prioridad.ALTA -> Color(0xFFD32F2F)
    Prioridad.MEDIA -> Color(0xFFEF6C00)
    Prioridad.BAJA -> Color(0xFF2E7D32)
}

/** Color semántico de cada estado. */
fun colorEstado(estado: EstadoTarea): Color = when (estado) {
    EstadoTarea.PENDIENTE -> Color(0xFF607D8B)
    EstadoTarea.EN_PROGRESO -> Color(0xFF1E88E5)
    EstadoTarea.COMPLETADA -> Color(0xFF2E7D32)
}
