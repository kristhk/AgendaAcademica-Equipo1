package com.equipo1.agendaacademica.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.domain.CalculadoraAvance
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.FormatoFechas
import com.equipo1.agendaacademica.domain.Prioridad
import com.equipo1.agendaacademica.domain.ResumenAvance
import com.equipo1.agendaacademica.ui.theme.colorEstado
import com.equipo1.agendaacademica.ui.theme.colorPrioridad
import java.time.LocalDate

/** Mensaje centrado para listas vacías, con acción opcional. */
@Composable
fun EstadoVacio(
    icono: ImageVector,
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (accion != null) {
            Spacer(Modifier.height(24.dp))
            accion()
        }
    }
}

/** Etiqueta en forma de píldora con color semántico. */
@Composable
fun Etiqueta(texto: String, color: Color, modifier: Modifier = Modifier) {
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            texto,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            maxLines = 1,
        )
    }
}

@Composable
fun EtiquetaPrioridad(prioridad: Prioridad) =
    Etiqueta("Prioridad ${prioridad.etiqueta.lowercase()}", colorPrioridad(prioridad))

@Composable
fun EtiquetaEstado(estado: EstadoTarea) = Etiqueta(estado.etiqueta, colorEstado(estado))

@Composable
fun PuntoColor(color: Int, tamano: Dp = 12.dp) {
    Box(Modifier.size(tamano).clip(CircleShape).background(Color(color)))
}

@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier, accion: (@Composable () -> Unit)? = null) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(texto, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        accion?.invoke()
    }
}

/**
 * Tarjeta de una tarea en las listas. La franja izquierda usa el color de la materia.
 * Si [onAlternarCompletada] no es nulo, muestra una casilla para completar rápidamente.
 */
@Composable
fun TarjetaTarea(
    item: TareaConMateria,
    hoy: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAlternarCompletada: (() -> Unit)? = null,
) {
    val tarea = item.tarea
    val completada = tarea.estado == EstadoTarea.COMPLETADA
    val vencida = CalculadoraAvance.esVencida(tarea, hoy)
    ElevatedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(6.dp).fillMaxHeight().background(Color(item.materia.color)))
            Row(
                Modifier.padding(start = if (onAlternarCompletada != null) 4.dp else 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (onAlternarCompletada != null) {
                    Checkbox(
                        checked = completada,
                        onCheckedChange = { onAlternarCompletada() },
                        modifier = Modifier.semantics {
                            contentDescription =
                                if (completada) "Marcar como pendiente: ${tarea.titulo}" else "Marcar como completada: ${tarea.titulo}"
                        },
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        tarea.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (completada) TextDecoration.LineThrough else null,
                        color = if (completada) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        item.materia.nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EtiquetaPrioridad(tarea.prioridad)
                        EtiquetaEstado(tarea.estado)
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val colorFecha = if (vencida) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(16.dp), tint = colorFecha)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = FormatoFechas.corta(tarea.fechaEntrega) +
                                if (completada) "" else " · " + FormatoFechas.textoRelativo(tarea.fechaEntrega, hoy),
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFecha,
                            fontWeight = if (vencida) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}

/** Tarjeta con el porcentaje de avance general (gráfico circular). */
@Composable
fun TarjetaAvanceGeneral(resumen: ResumenAvance, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { resumen.porcentaje / 100f },
                    modifier = Modifier.size(88.dp),
                    strokeWidth = 9.dp,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                )
                Text(
                    "${resumen.porcentaje}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            Spacer(Modifier.width(20.dp))
            Column {
                Text(
                    "Avance general",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${resumen.completadas} de ${resumen.total} tareas completadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

/** Barra horizontal etiquetada: "Etiqueta ...... valor (pct %)". */
@Composable
fun BarraProgreso(etiqueta: String, valor: Int, total: Int, color: Color, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Text(etiqueta, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "$valor de $total (${CalculadoraAvance.porcentaje(valor, total)}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else valor.toFloat() / total },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.18f),
            drawStopIndicator = {},
            gapSize = 0.dp,
        )
    }
}
