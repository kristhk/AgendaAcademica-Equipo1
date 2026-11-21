package com.equipo1.agendaacademica.ui.avances

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.local.MateriaConAvance
import com.equipo1.agendaacademica.domain.CalculadoraAvance
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.Prioridad
import com.equipo1.agendaacademica.domain.ResumenAvance
import com.equipo1.agendaacademica.ui.FabricaViewModels
import com.equipo1.agendaacademica.ui.componentes.BarraProgreso
import com.equipo1.agendaacademica.ui.componentes.EstadoVacio
import com.equipo1.agendaacademica.ui.componentes.TarjetaAvanceGeneral
import com.equipo1.agendaacademica.ui.theme.colorEstado
import com.equipo1.agendaacademica.ui.theme.colorPrioridad
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

// ------------------------------------------------------------------ ViewModel

data class AvancesUiState(
    val cargando: Boolean = true,
    val resumen: ResumenAvance = ResumenAvance(),
    val porMateria: List<MateriaConAvance> = emptyList(),
    val sinCompletarPorPrioridad: Map<Prioridad, Int> = emptyMap(),
)

class AvancesViewModel(repositorio: RepositorioAcademico) : ViewModel() {
    val uiState: StateFlow<AvancesUiState> =
        combine(repositorio.tareas, repositorio.materiasConAvance) { tareas, materias ->
            val entidades = tareas.map { it.tarea }
            AvancesUiState(
                cargando = false,
                resumen = CalculadoraAvance.resumen(entidades, LocalDate.now()),
                porMateria = materias,
                sinCompletarPorPrioridad = CalculadoraAvance.pendientesPorPrioridad(entidades),
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AvancesUiState())
}

// ------------------------------------------------------------------ Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvancesScreen(
    barraInferior: @Composable () -> Unit,
    viewModel: AvancesViewModel = viewModel(factory = FabricaViewModels.Factory),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val r = estado.resumen

    Scaffold(
        topBar = { TopAppBar(title = { Text("Avances y estadísticas") }) },
        bottomBar = barraInferior,
    ) { padding ->
        when {
            estado.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            r.total == 0 -> EstadoVacio(
                icono = Icons.Filled.Insights,
                titulo = "Aún no hay datos",
                mensaje = "Registra tareas para consultar tu avance por estado, materia y prioridad.",
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item { TarjetaAvanceGeneral(r) }
                item {
                    Seccion("Tareas por estado") {
                        BarraProgreso("Pendientes", r.pendientes, r.total, colorEstado(EstadoTarea.PENDIENTE))
                        BarraProgreso("En progreso", r.enProgreso, r.total, colorEstado(EstadoTarea.EN_PROGRESO))
                        BarraProgreso("Completadas", r.completadas, r.total, colorEstado(EstadoTarea.COMPLETADA))
                        BarraProgreso("Vencidas (sin completar)", r.vencidas, r.total, MaterialTheme.colorScheme.error)
                    }
                }
                item {
                    Seccion("Puntualidad de entregas") {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Dato("${r.completadasATiempo}", "A tiempo", colorEstado(EstadoTarea.COMPLETADA))
                            Dato("${r.completadasConRetraso}", "Con retraso", colorPrioridad(Prioridad.MEDIA))
                            Dato("${CalculadoraAvance.porcentaje(r.completadasATiempo, r.completadas)}%", "Puntualidad",
                                MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                item {
                    Seccion("Tareas sin completar por prioridad") {
                        val totalSinCompletar = r.pendientes + r.enProgreso
                        listOf(Prioridad.ALTA, Prioridad.MEDIA, Prioridad.BAJA).forEach { p ->
                            BarraProgreso(p.etiqueta, estado.sinCompletarPorPrioridad[p] ?: 0, totalSinCompletar, colorPrioridad(p))
                        }
                    }
                }
                item {
                    Seccion("Avance por materia") {
                        estado.porMateria.forEach { m ->
                            BarraProgreso(m.materia.nombre, m.completadas, m.total, Color(m.materia.color))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            contenido()
        }
    }
}

@Composable
private fun Dato(valor: String, etiqueta: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
