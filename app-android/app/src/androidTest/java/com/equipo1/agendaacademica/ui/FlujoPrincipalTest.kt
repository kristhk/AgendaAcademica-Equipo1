package com.equipo1.agendaacademica.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.equipo1.agendaacademica.AgendaApp
import com.equipo1.agendaacademica.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba funcional automatizada de extremo a extremo (UI + Room):
 * registrar materia → validar formulario → registrar tarea → cambiar estado → consultar avance.
 * ATENCIÓN: borra los datos de la app en el dispositivo donde se ejecuta.
 */
@RunWith(AndroidJUnit4::class)
class FlujoPrincipalTest {

    init {
        // Se limpia la base antes de que se lance la Activity
        ApplicationProvider.getApplicationContext<AgendaApp>().contenedor.baseDatos.clearAllTables()
    }

    @get:Rule
    val regla = createAndroidComposeRule<MainActivity>()

    private fun esperarTexto(texto: String) =
        regla.waitUntil(5_000) { regla.onAllNodes(hasText(texto)).fetchSemanticsNodes().isNotEmpty() }

    @Before
    fun esperarPantallaInicial() = esperarTexto("Registrar mis materias")

    @Test
    fun flujoCompleto_registrarMateriaTareaYCompletarla() {
        // 1. Registrar una materia
        regla.onNodeWithText("Registrar mis materias").performClick()
        regla.onNodeWithTag("fab_nueva_materia").performClick()
        regla.onNodeWithTag("campo_nombre_materia").performTextInput("Programación Móvil")
        regla.onNodeWithText("Guardar").performClick()
        esperarTexto("0 de 0 tareas completadas · 0%")
        regla.onNodeWithText("Programación Móvil").assertIsDisplayed()

        // 2. Ir a Tareas y abrir el formulario
        regla.onNodeWithText("Tareas").performClick()
        regla.onNodeWithContentDescription("Nueva tarea").performClick()

        // 3. Validación: guardar vacío muestra errores
        regla.onNodeWithText("Registrar tarea").performClick()
        regla.onNodeWithText("El título es obligatorio").assertIsDisplayed()
        regla.onNodeWithText("Selecciona una materia").assertExists()
        regla.onNodeWithText("Selecciona la fecha de entrega").assertExists()

        // 4. Capturar datos válidos
        regla.onNodeWithTag("campo_titulo").performTextInput("Entregar prototipo")
        regla.onNodeWithTag("campo_descripcion").performTextInput("Pantallas en Figma")
        regla.onNodeWithTag("selector_materia").performClick()
        regla.onNodeWithText("Programación Móvil").performClick()
        regla.onNodeWithText("Alta").performClick()
        regla.onNodeWithTag("selector_fecha").performClick()
        regla.onNodeWithText("Aceptar").performClick() // fecha propuesta: hoy
        regla.onNodeWithText("Registrar tarea").performClick()

        // 5. La tarea aparece en la lista (persistida en Room)
        esperarTexto("Entregar prototipo")
        regla.onNodeWithText("1 de 1 tareas").assertIsDisplayed()

        // 6. Abrir detalle y cambiar estado a Completada
        regla.onNodeWithText("Entregar prototipo").performClick()
        esperarTexto("Cambiar estado")
        regla.onNodeWithText("Completada").performClick()
        esperarTexto("Pendiente → Completada")

        // 7. Consultar avance
        regla.onNodeWithContentDescription("Volver").performClick()
        regla.onNodeWithText("Avances").performClick()
        esperarTexto("1 de 1 tareas completadas")
        regla.onNodeWithText("Avance por materia").assertExists()
    }

    @Test
    fun materiaDuplicada_muestraError() {
        regla.onNodeWithText("Registrar mis materias").performClick()
        repeat(2) {
            regla.onNodeWithTag("fab_nueva_materia").performClick()
            regla.onNodeWithTag("campo_nombre_materia").performTextInput("Física")
            regla.onNodeWithText("Guardar").performClick()
        }
        esperarTexto("Ya existe una materia llamada \"Física\"")
    }
}
