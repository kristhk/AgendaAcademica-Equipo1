package com.equipo1.agendaacademica.ui.formulario

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.data.ResultadoOperacion
import com.equipo1.agendaacademica.data.local.MateriaEntity
import com.equipo1.agendaacademica.domain.EstadoTarea
import com.equipo1.agendaacademica.domain.FormatoFechas
import com.equipo1.agendaacademica.domain.Prioridad
import com.equipo1.agendaacademica.domain.ValidadorTarea
import com.equipo1.agendaacademica.ui.FabricaViewModels
import com.equipo1.agendaacademica.ui.componentes.PuntoColor
import com.equipo1.agendaacademica.ui.navegacion.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

private const val MILIS_POR_DIA = 86_400_000L

// ------------------------------------------------------------------ ViewModel

data class FormularioTareaUiState(
    val esEdicion: Boolean = false,
    val cargando: Boolean = false,
    val titulo: String = "",
    val descripcion: String = "",
    val materiaId: Long? = null,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val fechaEntrega: LocalDate? = null,
    val fechaOriginal: LocalDate? = null,
    val estado: EstadoTarea = EstadoTarea.PENDIENTE,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorMateria: String? = null,
    val errorFecha: String? = null,
    /** Después del primer intento de guardar, los errores se recalculan mientras el usuario escribe. */
    val intentoGuardar: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val mensaje: String? = null,
)

class FormularioTareaViewModel(
    savedStateHandle: SavedStateHandle,
    private val repositorio: RepositorioAcademico,
) : ViewModel() {
    private val tareaId: Long? = savedStateHandle.get<Long>(Rutas.ARG_TAREA_ID)?.takeIf { it > 0 }

    private val _estado = MutableStateFlow(
        FormularioTareaUiState(esEdicion = tareaId != null, cargando = tareaId != null)
    )
    val estado: StateFlow<FormularioTareaUiState> = _estado.asStateFlow()

    val materias: StateFlow<List<MateriaEntity>> =
        repositorio.materias.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        if (tareaId != null) {
            viewModelScope.launch {
                val t = repositorio.obtenerTarea(tareaId)
                _estado.update {
                    if (t == null) {
                        it.copy(cargando = false, mensaje = "La tarea no existe")
                    } else {
                        it.copy(
                            cargando = false,
                            titulo = t.titulo,
                            descripcion = t.descripcion,
                            materiaId = t.materiaId,
                            prioridad = t.prioridad,
                            fechaEntrega = t.fechaEntrega,
                            fechaOriginal = t.fechaEntrega,
                            estado = t.estado,
                        )
                    }
                }
            }
        }
    }

    private val hoy: LocalDate get() = LocalDate.now()

    fun cambiarTitulo(v: String) = _estado.update {
        it.copy(titulo = v, errorTitulo = if (it.intentoGuardar) ValidadorTarea.validarTitulo(v) else null)
    }

    fun cambiarDescripcion(v: String) = _estado.update {
        it.copy(descripcion = v, errorDescripcion = ValidadorTarea.validarDescripcion(v))
    }

    fun cambiarMateria(id: Long) = _estado.update { it.copy(materiaId = id, errorMateria = null) }
    fun cambiarPrioridad(p: Prioridad) = _estado.update { it.copy(prioridad = p) }
    fun cambiarEstado(e: EstadoTarea) = _estado.update { it.copy(estado = e) }

    fun cambiarFecha(f: LocalDate) = _estado.update {
        it.copy(fechaEntrega = f, errorFecha = ValidadorTarea.validarFechaEntrega(f, hoy, it.fechaOriginal))
    }

    fun mensajeMostrado() = _estado.update { it.copy(mensaje = null) }

    fun guardar() {
        val s = _estado.value
        if (s.guardando) return
        val validado = s.copy(
            intentoGuardar = true,
            errorTitulo = ValidadorTarea.validarTitulo(s.titulo),
            errorDescripcion = ValidadorTarea.validarDescripcion(s.descripcion),
            errorMateria = ValidadorTarea.validarMateria(s.materiaId),
            errorFecha = ValidadorTarea.validarFechaEntrega(s.fechaEntrega, hoy, s.fechaOriginal),
        )
        val hayErrores = listOf(validado.errorTitulo, validado.errorDescripcion, validado.errorMateria, validado.errorFecha)
            .any { it != null }
        if (hayErrores) {
            _estado.value = validado.copy(mensaje = "Revisa los campos marcados en rojo")
            return
        }
        _estado.value = validado.copy(guardando = true)

        val materiaId = checkNotNull(s.materiaId)
        val fecha = checkNotNull(s.fechaEntrega)
        viewModelScope.launch {
            val resultado = if (tareaId == null) {
                repositorio.crearTarea(s.titulo, s.descripcion, materiaId, s.prioridad, fecha)
                ResultadoOperacion.Exito
            } else {
                repositorio.actualizarTarea(tareaId, s.titulo, s.descripcion, materiaId, s.prioridad, fecha, s.estado)
            }
            _estado.update {
                when (resultado) {
                    ResultadoOperacion.Exito -> it.copy(guardando = false, guardado = true)
                    is ResultadoOperacion.Error -> it.copy(guardando = false, mensaje = resultado.mensaje)
                }
            }
        }
    }
}

// ------------------------------------------------------------------ Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioTareaScreen(
    onVolver: () -> Unit,
    onIrAMaterias: () -> Unit,
    viewModel: FormularioTareaViewModel = viewModel(factory = FabricaViewModels.Factory),
) {
    val s by viewModel.estado.collectAsStateWithLifecycle()
    val materias by viewModel.materias.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(s.guardado) { if (s.guardado) onVolver() }
    LaunchedEffect(s.mensaje) {
        s.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (s.esEdicion) "Editar tarea" else "Nueva tarea") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        if (s.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            OutlinedTextField(
                value = s.titulo,
                onValueChange = viewModel::cambiarTitulo,
                label = { Text("Título *") },
                isError = s.errorTitulo != null,
                supportingText = { TextoAyuda(s.errorTitulo, s.titulo.trim().length, ValidadorTarea.TITULO_MAX) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth().testTag("campo_titulo"),
            )
            OutlinedTextField(
                value = s.descripcion,
                onValueChange = viewModel::cambiarDescripcion,
                label = { Text("Descripción (opcional)") },
                isError = s.errorDescripcion != null,
                supportingText = { TextoAyuda(s.errorDescripcion, s.descripcion.trim().length, ValidadorTarea.DESCRIPCION_MAX) },
                minLines = 3,
                maxLines = 6,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth().testTag("campo_descripcion"),
            )

            SelectorMateria(materias, s.materiaId, s.errorMateria, viewModel::cambiarMateria, onIrAMaterias)

            Text("Prioridad *", style = MaterialTheme.typography.titleSmall)
            val prioridades = listOf(Prioridad.BAJA, Prioridad.MEDIA, Prioridad.ALTA)
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                prioridades.forEachIndexed { i, p ->
                    SegmentedButton(
                        selected = s.prioridad == p,
                        onClick = { viewModel.cambiarPrioridad(p) },
                        shape = SegmentedButtonDefaults.itemShape(i, prioridades.size),
                    ) { Text(p.etiqueta) }
                }
            }

            CampoFecha(s.fechaEntrega, s.errorFecha, s.fechaOriginal, viewModel::cambiarFecha)

            if (s.esEdicion) {
                Text("Estado", style = MaterialTheme.typography.titleSmall)
                val estados = EstadoTarea.entries
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    estados.forEachIndexed { i, e ->
                        SegmentedButton(
                            selected = s.estado == e,
                            onClick = { viewModel.cambiarEstado(e) },
                            shape = SegmentedButtonDefaults.itemShape(i, estados.size),
                        ) { Text(e.etiqueta, maxLines = 1) }
                    }
                }
            }

            Button(
                onClick = viewModel::guardar,
                enabled = !s.guardando,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (s.esEdicion) "Guardar cambios" else "Registrar tarea")
            }
            Text("* Campos obligatorios", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TextoAyuda(error: String?, longitud: Int, maximo: Int) {
    Row(Modifier.fillMaxWidth()) {
        Text(error ?: "", modifier = Modifier.weight(1f))
        Text("$longitud/$maximo")
    }
}

@Composable
private fun SelectorMateria(
    materias: List<MateriaEntity>,
    materiaId: Long?,
    error: String?,
    onSeleccionar: (Long) -> Unit,
    onIrAMaterias: () -> Unit,
) {
    if (materias.isEmpty()) {
        OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Aún no tienes materias registradas.", style = MaterialTheme.typography.bodyMedium)
                if (error != null) Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = onIrAMaterias) { Text("Registrar una materia") }
            }
        }
        return
    }
    var expandido by remember { mutableStateOf(false) }
    val seleccionada = materias.find { it.id == materiaId }
    Box {
        OutlinedTextField(
            value = seleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Materia *") },
            placeholder = { Text("Selecciona una materia") },
            leadingIcon = seleccionada?.let { m -> { PuntoColor(m.color, 14.dp) } },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        // Capa transparente que abre el menú al tocar el campo
        Box(
            Modifier
                .matchParentSize()
                .clickable { expandido = true }
                .testTag("selector_materia")
                .semantics { contentDescription = "Seleccionar materia" }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            materias.forEach { m ->
                DropdownMenuItem(
                    text = { Text(m.nombre) },
                    leadingIcon = { PuntoColor(m.color) },
                    onClick = {
                        onSeleccionar(m.id)
                        expandido = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampoFecha(
    fecha: LocalDate?,
    error: String?,
    fechaOriginal: LocalDate?,
    onFecha: (LocalDate) -> Unit,
) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = fecha?.let { FormatoFechas.corta(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de entrega *") },
            placeholder = { Text("Selecciona una fecha") },
            leadingIcon = { Icon(Icons.Filled.Event, contentDescription = null) },
            isError = error != null,
            supportingText = error?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { abierto = true }
                .testTag("selector_fecha")
                .semantics { contentDescription = "Seleccionar fecha de entrega" }
        )
    }
    if (abierto) {
        val hoy = LocalDate.now()
        val minimo = hoy.toEpochDay() * MILIS_POR_DIA
        val original = fechaOriginal?.toEpochDay()?.times(MILIS_POR_DIA)
        // El DatePicker trabaja en milisegundos UTC a medianoche.
        val estadoPicker = rememberDatePickerState(
            initialSelectedDateMillis = (fecha ?: hoy).toEpochDay() * MILIS_POR_DIA,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= minimo || utcTimeMillis == original

                override fun isSelectableYear(year: Int): Boolean = year >= hoy.year - 1
            },
        )
        DatePickerDialog(
            onDismissRequest = { abierto = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        estadoPicker.selectedDateMillis?.let { onFecha(LocalDate.ofEpochDay(Math.floorDiv(it, MILIS_POR_DIA))) }
                        abierto = false
                    },
                    enabled = estadoPicker.selectedDateMillis != null,
                ) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { abierto = false }) { Text("Cancelar") } },
        ) {
            DatePicker(state = estadoPicker)
        }
    }
}
