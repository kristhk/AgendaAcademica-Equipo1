package com.equipo1.agendaacademica.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.ResultadoOperacion
import com.equipo1.agendaacademica.data.local.HistorialEstadoEntity
import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.domain.CalculadoraAvance
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.FormatoFechas
import com.equipo1.agendaacademica.ui.FabricaViewModels
import com.equipo1.agendaacademica.ui.componentes.Etiqueta
import com.equipo1.agendaacademica.ui.componentes.EtiquetaEstado
import com.equipo1.agendaacademica.ui.componentes.EtiquetaPrioridad
import com.equipo1.agendaacademica.ui.componentes.EstadoVacio
import com.equipo1.agendaacademica.ui.navegacion.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.ui.graphics.Color as ColorCompose

// ------------------------------------------------------------------ ViewModel

data class DetalleUiState(
    val cargando: Boolean = true,
    val tarea: TareaConMateria? = null,
    val historial: List<HistorialEstadoEntity> = emptyList(),
    val eliminada: Boolean = false,
    val mensaje: String? = null,
    val hoy: LocalDate = LocalDate.now(),
)

private data class EstadoLocal(val eliminada: Boolean = false, val mensaje: String? = null)

class DetalleTareaViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: RepositorioAcademico,
) : ViewModel() {
    private val tareaId: Long = checkNotNull(savedStateHandle[Rutas.ARG_TAREA_ID])
    private val local = MutableStateFlow(EstadoLocal())

    val uiState: StateFlow<DetalleUiState> = combine(
        repositorio.observarTarea(tareaId),
        repositorio.observarHistorial(tareaId),
        local,
    ) { tarea, historial, l ->
        DetalleUiState(
            cargando = false,
            tarea = tarea,
            historial = historial,
            eliminada = l.eliminada,
            mensaje = l.mensaje,
            hoy = LocalDate.now(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())

    fun cambiarEstado(estado: EstadoTarea) {
        viewModelScope.launch {
            val r = repositorio.cambiarEstado(tareaId, estado)
            if (r is ResultadoOperacion.Error) local.update { it.copy(mensaje = r.mensaje) }
        }
    }

    fun eliminar() {
        viewModelScope.launch {
            when (val r = repositorio.eliminarTarea(tareaId)) {
                ResultadoOperacion.Exito -> local.update { it.copy(eliminada = true) }
                is ResultadoOperacion.Error -> local.update { it.copy(mensaje = r.mensaje) }
            }
        }
    }
}

// ------------------------------------------------------------------ Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: DetalleTareaViewModel = viewModel(factory = FabricaViewModels.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(estado.eliminada) { if (estado.eliminada) onVolver() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la tarea") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    estado.tarea?.let { t ->
                        IconButton(onClick = { onEditar(t.tarea.id) }) { Icon(Icons.Filled.Edit, contentDescription = "Editar tarea") }
                        IconButton(onClick = { confirmarEliminar = true }) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar tarea") }
                    }
                },
            )
        },
    ) { padding ->
        val item = estado.tarea
        when {
            estado.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            item == null -> EstadoVacio(
                icono = Icons.Filled.Warning,
                titulo = "Tarea no encontrada",
                mensaje = "La tarea ya no existe o fue eliminada.",
                modifier = Modifier.padding(padding),
            )
            else -> ContenidoDetalle(
                item = item,
                historial = estado.historial,
                hoy = estado.hoy,
                onCambiarEstado = viewModel::cambiarEstado,
                modifier = Modifier.padding(padding),
            )
        }
    }

    if (confirmarEliminar && estado.tarea != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("¿Eliminar tarea?") },
            text = { Text("Se eliminará \"${estado.tarea?.tarea?.titulo}\" y su historial. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    viewModel.eliminar()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun ContenidoDetalle(
    item: TareaConMateria,
    historial: List<HistorialEstadoEntity>,
    hoy: LocalDate,
    onCambiarEstado: (EstadoTarea) -> Unit,
    modifier: Modifier = Modifier,
) {
    val t = item.tarea
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(t.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Etiqueta(item.materia.nombre, ColorCompose(item.materia.color))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            EtiquetaPrioridad(t.prioridad)
            EtiquetaEstado(t.estado)
        }

        if (CalculadoraAvance.esVencida(t, hoy)) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Esta tarea está vencida: ${FormatoFechas.textoRelativo(t.fechaEntrega, hoy).lowercase()}.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }

        OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FilaDato(
                    Icons.Filled.Event,
                    "Fecha de entrega",
                    FormatoFechas.corta(t.fechaEntrega) +
                        if (t.estado != EstadoTarea.COMPLETADA) " (${FormatoFechas.textoRelativo(t.fechaEntrega, hoy)})" else "",
                )
                FilaDato(Icons.Filled.Schedule, "Registrada", FormatoFechas.fechaHora(t.fechaCreacion))
                t.fechaCompletada?.let { ms ->
                    val aTiempo = CalculadoraAvance.fueCompletadaATiempo(t) == true
                    FilaDato(
                        Icons.Filled.CheckCircle,
                        "Completada",
                        FormatoFechas.fechaHora(ms) + if (aTiempo) " · a tiempo" else " · con retraso",
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Descripción", style = MaterialTheme.typography.titleSmall)
            Text(
                t.descripcion.ifBlank { "Sin descripción" },
                style = MaterialTheme.typography.bodyMedium,
                color = if (t.descripcion.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Cambiar estado", style = MaterialTheme.typography.titleSmall)
            val estados = EstadoTarea.entries
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                estados.forEachIndexed { i, e ->
                    SegmentedButton(
                        selected = t.estado == e,
                        onClick = { onCambiarEstado(e) },
                        shape = SegmentedButtonDefaults.itemShape(index = i, count = estados.size),
                    ) { Text(e.etiqueta, maxLines = 1) }
                }
            }
        }

        HorizontalDivider()
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Historial de estados", style = MaterialTheme.typography.titleSmall)
            historial.forEach { h ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            if (h.estadoAnterior == null) "Registrada como ${h.estadoNuevo.etiqueta}"
                            else "${h.estadoAnterior.etiqueta} → ${h.estadoNuevo.etiqueta}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(FormatoFechas.fechaHora(h.fecha), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
