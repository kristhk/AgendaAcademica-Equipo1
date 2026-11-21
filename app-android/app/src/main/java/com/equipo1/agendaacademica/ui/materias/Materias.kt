package com.equipo1.agendaacademica.ui.materias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.ResultadoOperacion
import com.equipo1.agendaacademica.data.local.MateriaConAvance
import com.equipo1.agendaacademica.domain.CalculadoraAvance
import com.equipo1.agendaacademica.domain.PaletaMaterias
import com.equipo1.agendaacademica.domain.ValidadorMateria
import com.equipo1.agendaacademica.ui.FabricaViewModels
import com.equipo1.agendaacademica.ui.componentes.EstadoVacio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ------------------------------------------------------------------ ViewModel

/** Estado del diálogo de alta/edición de materia. [id] = null significa materia nueva. */
data class DialogoMateria(
    val id: Long? = null,
    val nombre: String = "",
    val profesor: String = "",
    val color: Int = PaletaMaterias.colores.first(),
    val errorNombre: String? = null,
    val errorProfesor: String? = null,
    val guardando: Boolean = false,
)

data class MateriasUiState(
    val cargando: Boolean = true,
    val materias: List<MateriaConAvance> = emptyList(),
    val dialogo: DialogoMateria? = null,
    val materiaAEliminar: MateriaConAvance? = null,
    val mensaje: String? = null,
)

private data class EstadoInteraccion(
    val dialogo: DialogoMateria? = null,
    val materiaAEliminar: MateriaConAvance? = null,
    val mensaje: String? = null,
)

class MateriasViewModel(private val repositorio: RepositorioAcademico) : ViewModel() {
    private val interaccion = MutableStateFlow(EstadoInteraccion())

    val uiState: StateFlow<MateriasUiState> =
        combine(repositorio.materiasConAvance, interaccion) { materias, i ->
            MateriasUiState(false, materias, i.dialogo, i.materiaAEliminar, i.mensaje)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MateriasUiState())

    fun abrirNueva() {
        // Propone un color que todavía no se use, si lo hay
        val usados = uiState.value.materias.map { it.materia.color }.toSet()
        val color = PaletaMaterias.colores.firstOrNull { it !in usados } ?: PaletaMaterias.colores.first()
        interaccion.update { it.copy(dialogo = DialogoMateria(color = color)) }
    }

    fun abrirEdicion(m: MateriaConAvance) = interaccion.update {
        it.copy(dialogo = DialogoMateria(m.materia.id, m.materia.nombre, m.materia.profesor.orEmpty(), m.materia.color))
    }

    fun cerrarDialogo() = interaccion.update { it.copy(dialogo = null) }

    fun cambiarNombre(v: String) = interaccion.update {
        it.copy(dialogo = it.dialogo?.copy(nombre = v, errorNombre = null))
    }

    fun cambiarProfesor(v: String) = interaccion.update {
        it.copy(dialogo = it.dialogo?.copy(profesor = v, errorProfesor = ValidadorMateria.validarProfesor(v)))
    }

    fun cambiarColor(c: Int) = interaccion.update { it.copy(dialogo = it.dialogo?.copy(color = c)) }

    fun guardar() {
        val d = interaccion.value.dialogo ?: return
        if (d.guardando) return
        val errorNombre = ValidadorMateria.validarNombre(d.nombre)
        val errorProfesor = ValidadorMateria.validarProfesor(d.profesor)
        if (errorNombre != null || errorProfesor != null) {
            interaccion.update { it.copy(dialogo = d.copy(errorNombre = errorNombre, errorProfesor = errorProfesor)) }
            return
        }
        interaccion.update { it.copy(dialogo = d.copy(guardando = true)) }
        viewModelScope.launch {
            when (val r = repositorio.guardarMateria(d.id, d.nombre, d.profesor, d.color)) {
                ResultadoOperacion.Exito -> interaccion.update {
                    it.copy(dialogo = null, mensaje = if (d.id == null) "Materia registrada" else "Materia actualizada")
                }
                // Nombre duplicado: el error se muestra en el propio campo
                is ResultadoOperacion.Error -> interaccion.update {
                    it.copy(dialogo = d.copy(guardando = false, errorNombre = r.mensaje))
                }
            }
        }
    }

    fun solicitarEliminar(m: MateriaConAvance) = interaccion.update { it.copy(materiaAEliminar = m) }
    fun cancelarEliminar() = interaccion.update { it.copy(materiaAEliminar = null) }

    fun confirmarEliminar() {
        val m = interaccion.value.materiaAEliminar ?: return
        interaccion.update { it.copy(materiaAEliminar = null) }
        viewModelScope.launch {
            val msg = when (val r = repositorio.eliminarMateria(m.materia.id)) {
                ResultadoOperacion.Exito -> "Materia eliminada"
                is ResultadoOperacion.Error -> r.mensaje
            }
            interaccion.update { it.copy(mensaje = msg) }
        }
    }

    fun mensajeMostrado() = interaccion.update { it.copy(mensaje = null) }
}

// ------------------------------------------------------------------ Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateriasScreen(
    barraInferior: @Composable () -> Unit,
    viewModel: MateriasViewModel = viewModel(factory = FabricaViewModels.Factory),
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
        topBar = { TopAppBar(title = { Text("Mis materias") }) },
        bottomBar = barraInferior,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::abrirNueva,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva materia") },
                modifier = Modifier.testTag("fab_nueva_materia"),
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            estado.cargando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            estado.materias.isEmpty() -> EstadoVacio(
                icono = Icons.Filled.School,
                titulo = "Sin materias",
                mensaje = "Registra las materias que cursas para poder clasificar tus tareas.",
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(
                    start = 16.dp, end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 88.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(estado.materias, key = { it.materia.id }) { m ->
                    TarjetaMateria(m, onEditar = { viewModel.abrirEdicion(m) }, onEliminar = { viewModel.solicitarEliminar(m) })
                }
            }
        }
    }

    estado.dialogo?.let { d ->
        DialogoMateriaForm(
            dialogo = d,
            onNombre = viewModel::cambiarNombre,
            onProfesor = viewModel::cambiarProfesor,
            onColor = viewModel::cambiarColor,
            onGuardar = viewModel::guardar,
            onCancelar = viewModel::cerrarDialogo,
        )
    }

    estado.materiaAEliminar?.let { m ->
        if (m.total > 0) {
            // RN-09: se informa al usuario en lugar de permitir el intento
            AlertDialog(
                onDismissRequest = viewModel::cancelarEliminar,
                title = { Text("No se puede eliminar") },
                text = {
                    Text("\"${m.materia.nombre}\" tiene ${m.total} tarea(s) asociada(s). " +
                        "Elimínalas o cámbialas de materia antes de borrarla.")
                },
                confirmButton = { TextButton(onClick = viewModel::cancelarEliminar) { Text("Entendido") } },
            )
        } else {
            AlertDialog(
                onDismissRequest = viewModel::cancelarEliminar,
                title = { Text("¿Eliminar materia?") },
                text = { Text("Se eliminará \"${m.materia.nombre}\". Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = viewModel::confirmarEliminar) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = { TextButton(onClick = viewModel::cancelarEliminar) { Text("Cancelar") } },
            )
        }
    }
}

@Composable
private fun TarjetaMateria(m: MateriaConAvance, onEditar: () -> Unit, onEliminar: () -> Unit) {
    val color = Color(m.materia.color)
    val pct = CalculadoraAvance.porcentaje(m.completadas, m.total)
    ElevatedCard(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
                Text(
                    m.materia.nombre.take(1).uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(m.materia.nombre, style = MaterialTheme.typography.titleMedium)
                m.materia.profesor?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { pct / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.18f),
                    drawStopIndicator = {},
                    gapSize = 0.dp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "${m.completadas} de ${m.total} tareas completadas · $pct%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onEditar) { Icon(Icons.Filled.Edit, contentDescription = "Editar ${m.materia.nombre}") }
            IconButton(onClick = onEliminar) { Icon(Icons.Filled.Delete, contentDescription = "Eliminar ${m.materia.nombre}") }
        }
    }
}

@Composable
private fun DialogoMateriaForm(
    dialogo: DialogoMateria,
    onNombre: (String) -> Unit,
    onProfesor: (String) -> Unit,
    onColor: (Int) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (dialogo.id == null) "Nueva materia" else "Editar materia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = dialogo.nombre,
                    onValueChange = onNombre,
                    label = { Text("Nombre *") },
                    isError = dialogo.errorNombre != null,
                    supportingText = dialogo.errorNombre?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth().testTag("campo_nombre_materia"),
                )
                OutlinedTextField(
                    value = dialogo.profesor,
                    onValueChange = onProfesor,
                    label = { Text("Profesor(a) (opcional)") },
                    isError = dialogo.errorProfesor != null,
                    supportingText = dialogo.errorProfesor?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    modifier = Modifier.fillMaxWidth().testTag("campo_profesor"),
                )
                Text("Color", style = MaterialTheme.typography.labelLarge)
                PaletaMaterias.colores.chunked(4).forEach { fila ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        fila.forEach { c ->
                            val seleccionado = c == dialogo.color
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(c))
                                    .then(
                                        if (seleccionado) Modifier.border(BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface), CircleShape)
                                        else Modifier
                                    )
                                    .clickable { onColor(c) }
                                    .semantics { contentDescription = if (seleccionado) "Color seleccionado" else "Elegir color" },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (seleccionado) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onGuardar, enabled = !dialogo.guardando) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}
