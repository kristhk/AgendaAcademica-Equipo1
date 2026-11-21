package com.equipo1.agendaacademica.ui.tareas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.local.MateriaEntity
import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.FiltroTareas
import com.equipo1.agendaacademica.domain.FiltrosTareas
import com.equipo1.agendaacademica.domain.OrdenTareas
import com.equipo1.agendaacademica.domain.Prioridad
import com.equipo1.agendaacademica.ui.FabricaViewModels
import com.equipo1.agendaacademica.ui.componentes.EstadoVacio
import com.equipo1.agendaacademica.ui.componentes.PuntoColor
import com.equipo1.agendaacademica.ui.componentes.TarjetaTarea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// ------------------------------------------------------------------ ViewModel

data class TareasUiState(
    val cargando: Boolean = true,
    val tareas: List<TareaConMateria> = emptyList(),
    val totalSinFiltro: Int = 0,
    val materias: List<MateriaEntity> = emptyList(),
    val filtros: FiltrosTareas = FiltrosTareas(),
    val hoy: LocalDate = LocalDate.now(),
)

class TareasViewModel(private val repositorio: RepositorioAcademico) : ViewModel() {
    private val filtros = MutableStateFlow(FiltrosTareas())

    val uiState: StateFlow<TareasUiState> =
        combine(repositorio.tareas, repositorio.materias, filtros) { tareas, materias, f ->
            TareasUiState(
                cargando = false,
                tareas = FiltroTareas.aplicar(tareas, f),
                totalSinFiltro = tareas.size,
                materias = materias,
                filtros = f,
                hoy = LocalDate.now(),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TareasUiState())

    fun buscar(texto: String) = filtros.update { it.copy(texto = texto) }
    fun filtrarEstado(estado: EstadoTarea?) = filtros.update { it.copy(estado = estado) }
    fun filtrarPrioridad(prioridad: Prioridad?) = filtros.update { it.copy(prioridad = prioridad) }
    fun filtrarMateria(materiaId: Long?) = filtros.update { it.copy(materiaId = materiaId) }
    fun ordenar(orden: OrdenTareas) = filtros.update { it.copy(orden = orden) }
    fun limpiarFiltros() = filtros.update { FiltrosTareas(orden = it.orden) }

    /** Casilla de la lista: completa la tarea o la regresa a Pendiente. */
    fun alternarCompletada(tarea: TareaConMateria) {
        viewModelScope.launch {
            val nuevo = if (tarea.tarea.estado == EstadoTarea.COMPLETADA) EstadoTarea.PENDIENTE else EstadoTarea.COMPLETADA
            repositorio.cambiarEstado(tarea.tarea.id, nuevo)
        }
    }
}

// ------------------------------------------------------------------ Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasScreen(
    barraInferior: @Composable () -> Unit,
    onAbrirTarea: (Long) -> Unit,
    onNuevaTarea: () -> Unit,
    viewModel: TareasViewModel = viewModel(factory = FabricaViewModels.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val f = estado.filtros
    var menuOrden by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                actions = {
                    Box {
                        IconButton(onClick = { menuOrden = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = "Ordenar tareas")
                        }
                        DropdownMenu(expanded = menuOrden, onDismissRequest = { menuOrden = false }) {
                            OrdenTareas.entries.forEach { orden ->
                                DropdownMenuItem(
                                    text = { Text("Ordenar por: ${orden.etiqueta}") },
                                    leadingIcon = {
                                        if (orden == f.orden) Icon(Icons.Filled.Check, contentDescription = null)
                                    },
                                    onClick = {
                                        viewModel.ordenar(orden)
                                        menuOrden = false
                                    },
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = barraInferior,
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevaTarea) {
                Icon(Icons.Filled.Add, contentDescription = "Nueva tarea")
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = f.texto,
                onValueChange = viewModel::buscar,
                // Una sola línea: con fuente grande del sistema no debe agrandar el campo (prueba C-6)
                placeholder = { Text("Buscar por título o descripción", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (f.texto.isNotEmpty()) {
                        IconButton(onClick = { viewModel.buscar("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Borrar búsqueda")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            )

            // Filtro por estado
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(selected = f.estado == null, onClick = { viewModel.filtrarEstado(null) }, label = { Text("Todas") })
                }
                items(EstadoTarea.entries) { e ->
                    FilterChip(
                        selected = f.estado == e,
                        onClick = { viewModel.filtrarEstado(if (f.estado == e) null else e) },
                        label = { Text(e.etiqueta) },
                    )
                }
            }
            // Filtro por prioridad y por materia
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Prioridad.entries) { p ->
                    FilterChip(
                        selected = f.prioridad == p,
                        onClick = { viewModel.filtrarPrioridad(if (f.prioridad == p) null else p) },
                        label = { Text("Prioridad ${p.etiqueta.lowercase()}") },
                    )
                }
                items(estado.materias, key = { it.id }) { m ->
                    FilterChip(
                        selected = f.materiaId == m.id,
                        onClick = { viewModel.filtrarMateria(if (f.materiaId == m.id) null else m.id) },
                        label = { Text(m.nombre) },
                        leadingIcon = { PuntoColor(m.color) },
                    )
                }
            }

            Text(
                "${estado.tareas.size} de ${estado.totalSinFiltro} tareas",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            )

            when {
                estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }

                estado.tareas.isEmpty() -> EstadoVacio(
                    icono = Icons.AutoMirrored.Filled.Assignment,
                    titulo = if (f.hayFiltrosActivos) "Sin resultados" else "Aún no tienes tareas",
                    mensaje = if (f.hayFiltrosActivos) {
                        "Ninguna tarea coincide con los filtros seleccionados."
                    } else {
                        "Presiona el botón + para registrar tu primera tarea."
                    },
                    accion = if (f.hayFiltrosActivos) {
                        { OutlinedButton(onClick = viewModel::limpiarFiltros) { Text("Limpiar filtros") } }
                    } else {
                        null
                    },
                )

                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(estado.tareas, key = { it.tarea.id }) { item ->
                        TarjetaTarea(
                            item = item,
                            hoy = estado.hoy,
                            onClick = { onAbrirTarea(item.tarea.id) },
                            onAlternarCompletada = { viewModel.alternarCompletada(item) },
                        )
                    }
                }
            }
        }
    }
}
