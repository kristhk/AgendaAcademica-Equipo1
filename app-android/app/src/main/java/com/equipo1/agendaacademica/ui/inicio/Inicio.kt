package com.equipo1.agendaacademica.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.ResultadoOperacion
import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.domain.CalculadoraAvance
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.FormatoFechas
import com.equipo1.agendaacademica.domain.ResumenAvance
import com.equipo1.agendaacademica.ui.FabricaViewModels
import com.equipo1.agendaacademica.ui.componentes.EstadoVacio
import com.equipo1.agendaacademica.ui.componentes.TarjetaAvanceGeneral
import com.equipo1.agendaacademica.ui.componentes.TarjetaTarea
import com.equipo1.agendaacademica.ui.componentes.TituloSeccion
import com.equipo1.agendaacademica.ui.theme.colorEstado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

// ------------------------------------------------------------------ ViewModel

data class InicioUiState(
    val cargando: Boolean = true,
    val hayMaterias: Boolean = false,
    val resumen: ResumenAvance = ResumenAvance(),
    val proximas: List<TareaConMateria> = emptyList(),
    val vencidas: List<TareaConMateria> = emptyList(),
    val hoy: LocalDate = LocalDate.now(),
    val mensaje: String? = null,
)

class InicioViewModel(private val repositorio: RepositorioAcademico) : ViewModel() {
    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<InicioUiState> =
        combine(repositorio.tareas, repositorio.materias, mensaje) { tareas, materias, msg ->
            val hoy = LocalDate.now()
            InicioUiState(
                cargando = false,
                hayMaterias = materias.isNotEmpty(),
                resumen = CalculadoraAvance.resumen(tareas.map { it.tarea }, hoy),
                proximas = CalculadoraAvance.proximasEntregas(tareas, hoy).take(5),
                vencidas = tareas.filter { CalculadoraAvance.esVencida(it.tarea, hoy) },
                hoy = hoy,
                mensaje = msg,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState())

    fun cargarDatosEjemplo() {
        viewModelScope.launch {
            mensaje.value = when (val r = repositorio.cargarDatosEjemplo(LocalDate.now())) {
                ResultadoOperacion.Exito -> "Datos de ejemplo cargados"
                is ResultadoOperacion.Error -> r.mensaje
            }
        }
    }

    fun mensajeMostrado() {
        mensaje.value = null
    }
}

// ------------------------------------------------------------------ Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    barraInferior: @Composable () -> Unit,
    onAbrirTarea: (Long) -> Unit,
    onNuevaTarea: () -> Unit,
    onIrAMaterias: () -> Unit,
    onVerTareas: () -> Unit,
    viewModel: InicioViewModel = viewModel(factory = FabricaViewModels.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Agenda Académica", fontWeight = FontWeight.SemiBold)
                    Text(
                        FormatoFechas.larga(estado.hoy),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            })
        },
        bottomBar = barraInferior,
        floatingActionButton = {
            if (estado.hayMaterias) {
                ExtendedFloatingActionButton(
                    onClick = onNuevaTarea,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Nueva tarea") },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            estado.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            !estado.hayMaterias -> EstadoVacio(
                icono = Icons.Filled.School,
                titulo = "¡Bienvenido a tu Agenda Académica!",
                mensaje = "Comienza registrando las materias que cursas; después podrás agregar tus tareas, " +
                    "clasificarlas por prioridad y dar seguimiento a tu avance.",
                modifier = Modifier.padding(padding),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onIrAMaterias) { Text("Registrar mis materias") }
                    OutlinedButton(onClick = viewModel::cargarDatosEjemplo) { Text("Cargar datos de ejemplo") }
                }
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { TarjetaAvanceGeneral(estado.resumen) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Indicador("Pendientes", estado.resumen.pendientes, Icons.Filled.HourglassEmpty,
                            colorEstado(EstadoTarea.PENDIENTE), Modifier.weight(1f))
                        Indicador("En progreso", estado.resumen.enProgreso, Icons.Filled.Autorenew,
                            colorEstado(EstadoTarea.EN_PROGRESO), Modifier.weight(1f))
                        Indicador("Vencidas", estado.resumen.vencidas, Icons.Filled.Warning,
                            MaterialTheme.colorScheme.error, Modifier.weight(1f))
                    }
                }
                item {
                    TituloSeccion("Próximas entregas (7 días)") {
                        TextButton(onClick = onVerTareas) { Text("Ver todas") }
                    }
                }
                if (estado.proximas.isEmpty()) {
                    item {
                        Text(
                            "No tienes entregas pendientes en los próximos 7 días.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(estado.proximas, key = { "p${it.tarea.id}" }) { item ->
                    TarjetaTarea(item, estado.hoy, onClick = { onAbrirTarea(item.tarea.id) })
                }
                if (estado.vencidas.isNotEmpty()) {
                    item { TituloSeccion("Vencidas") }
                    items(estado.vencidas, key = { "v${it.tarea.id}" }) { item ->
                        TarjetaTarea(item, estado.hoy, onClick = { onAbrirTarea(item.tarea.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Indicador(titulo: String, valor: Int, icono: ImageVector, color: Color, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.fillMaxWidth().padding(12.dp)) {
            Icon(icono, contentDescription = null, tint = color)
            Text("$valor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
