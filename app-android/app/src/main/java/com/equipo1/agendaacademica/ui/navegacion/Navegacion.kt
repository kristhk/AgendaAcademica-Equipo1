package com.equipo1.agendaacademica.ui.navegacion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.equipo1.agendaacademica.ui.avances.AvancesScreen
import com.equipo1.agendaacademica.ui.detalle.DetalleTareaScreen
import com.equipo1.agendaacademica.ui.formulario.FormularioTareaScreen
import com.equipo1.agendaacademica.ui.inicio.InicioScreen
import com.equipo1.agendaacademica.ui.materias.MateriasScreen
import com.equipo1.agendaacademica.ui.tareas.TareasScreen

/** Rutas de navegación (corresponden al mapa de navegación del expediente). */
object Rutas {
    const val INICIO = "inicio"
    const val TAREAS = "tareas"
    const val MATERIAS = "materias"
    const val AVANCES = "avances"
    const val ARG_TAREA_ID = "tareaId"
    const val DETALLE = "tarea/{$ARG_TAREA_ID}"
    const val FORMULARIO = "formulario_tarea?$ARG_TAREA_ID={$ARG_TAREA_ID}"

    fun detalle(id: Long) = "tarea/$id"
    fun formulario(id: Long? = null) = if (id == null) "formulario_tarea" else "formulario_tarea?$ARG_TAREA_ID=$id"
}

/** Destinos de la barra de navegación inferior (menú principal). */
enum class DestinoPrincipal(val ruta: String, val etiqueta: String, val icono: ImageVector) {
    INICIO(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    TAREAS(Rutas.TAREAS, "Tareas", Icons.AutoMirrored.Filled.Assignment),
    MATERIAS(Rutas.MATERIAS, "Materias", Icons.AutoMirrored.Filled.MenuBook),
    AVANCES(Rutas.AVANCES, "Avances", Icons.Filled.Insights),
}

@Composable
fun BarraNavegacion(rutaActual: String, onNavegar: (String) -> Unit) {
    NavigationBar {
        DestinoPrincipal.entries.forEach { destino ->
            NavigationBarItem(
                selected = rutaActual == destino.ruta,
                onClick = { onNavegar(destino.ruta) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = { Text(destino.etiqueta) },
            )
        }
    }
}

@Composable
fun AgendaNavHost(navController: NavHostController = rememberNavController()) {
    // Navegación entre pestañas: conserva el estado de cada una y evita duplicar pantallas.
    val navegarPrincipal: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    val abrirTarea: (Long) -> Unit = { navController.navigate(Rutas.detalle(it)) }
    val nuevaTarea: () -> Unit = { navController.navigate(Rutas.formulario()) }

    NavHost(navController = navController, startDestination = Rutas.INICIO) {
        composable(Rutas.INICIO) {
            InicioScreen(
                barraInferior = { BarraNavegacion(Rutas.INICIO, navegarPrincipal) },
                onAbrirTarea = abrirTarea,
                onNuevaTarea = nuevaTarea,
                onIrAMaterias = { navegarPrincipal(Rutas.MATERIAS) },
                onVerTareas = { navegarPrincipal(Rutas.TAREAS) },
            )
        }
        composable(Rutas.TAREAS) {
            TareasScreen(
                barraInferior = { BarraNavegacion(Rutas.TAREAS, navegarPrincipal) },
                onAbrirTarea = abrirTarea,
                onNuevaTarea = nuevaTarea,
            )
        }
        composable(Rutas.MATERIAS) {
            MateriasScreen(barraInferior = { BarraNavegacion(Rutas.MATERIAS, navegarPrincipal) })
        }
        composable(Rutas.AVANCES) {
            AvancesScreen(barraInferior = { BarraNavegacion(Rutas.AVANCES, navegarPrincipal) })
        }
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument(Rutas.ARG_TAREA_ID) { type = NavType.LongType }),
        ) {
            DetalleTareaScreen(
                onVolver = { navController.navigateUp() },
                onEditar = { id -> navController.navigate(Rutas.formulario(id)) },
            )
        }
        composable(
            route = Rutas.FORMULARIO,
            arguments = listOf(
                navArgument(Rutas.ARG_TAREA_ID) {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            ),
        ) {
            FormularioTareaScreen(
                onVolver = { navController.navigateUp() },
                onIrAMaterias = { navegarPrincipal(Rutas.MATERIAS) },
            )
        }
    }
}
