package com.equipo1.agendaacademica.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.equipo1.agendaacademica.AgendaApp
import com.equipo1.agendaacademica.data.RepositorioAcademico
import com.equipo1.agendaacademica.ui.avances.AvancesViewModel
import com.equipo1.agendaacademica.ui.detalle.DetalleTareaViewModel
import com.equipo1.agendaacademica.ui.formulario.FormularioTareaViewModel
import com.equipo1.agendaacademica.ui.inicio.InicioViewModel
import com.equipo1.agendaacademica.ui.materias.MateriasViewModel
import com.equipo1.agendaacademica.ui.tareas.TareasViewModel

/** Fábrica única que construye todos los ViewModels con sus dependencias. */
object FabricaViewModels {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { InicioViewModel(repositorio()) }
        initializer { TareasViewModel(repositorio()) }
        initializer { DetalleTareaViewModel(createSavedStateHandle(), repositorio()) }
        initializer { FormularioTareaViewModel(createSavedStateHandle(), repositorio()) }
        initializer { MateriasViewModel(repositorio()) }
        initializer { AvancesViewModel(repositorio()) }
    }
}

private fun CreationExtras.repositorio(): RepositorioAcademico =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AgendaApp).contenedor.repositorio
