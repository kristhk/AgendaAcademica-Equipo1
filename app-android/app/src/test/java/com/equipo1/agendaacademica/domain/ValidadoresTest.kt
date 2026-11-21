package com.equipo1.agendaacademica.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class ValidadorTareaTest {
    private val hoy = LocalDate.of(2026, 11, 20)

    @Test fun tituloVacio_esError() = assertEquals("El título es obligatorio", ValidadorTarea.validarTitulo("   "))
    @Test fun tituloCorto_esError() = assertNotNull(ValidadorTarea.validarTitulo("ab"))
    @Test fun tituloConEspaciosAlrededor_seRecorta() = assertNotNull(ValidadorTarea.validarTitulo("  ab  "))
    @Test fun tituloMinimo_esValido() = assertNull(ValidadorTarea.validarTitulo("abc"))
    @Test fun tituloMaximo_esValido() = assertNull(ValidadorTarea.validarTitulo("a".repeat(80)))
    @Test fun tituloExcedido_esError() = assertNotNull(ValidadorTarea.validarTitulo("a".repeat(81)))

    @Test fun descripcionVacia_esValida() = assertNull(ValidadorTarea.validarDescripcion(""))
    @Test fun descripcionMaxima_esValida() = assertNull(ValidadorTarea.validarDescripcion("x".repeat(500)))
    @Test fun descripcionExcedida_esError() = assertNotNull(ValidadorTarea.validarDescripcion("x".repeat(501)))

    @Test fun materiaNula_esError() = assertNotNull(ValidadorTarea.validarMateria(null))
    @Test fun materiaSeleccionada_esValida() = assertNull(ValidadorTarea.validarMateria(1L))

    @Test fun fechaNula_esError() = assertNotNull(ValidadorTarea.validarFechaEntrega(null, hoy))
    @Test fun fechaHoy_esValida() = assertNull(ValidadorTarea.validarFechaEntrega(hoy, hoy))
    @Test fun fechaFutura_esValida() = assertNull(ValidadorTarea.validarFechaEntrega(hoy.plusDays(10), hoy))
    @Test fun fechaPasadaAlCrear_esError() = assertNotNull(ValidadorTarea.validarFechaEntrega(hoy.minusDays(1), hoy))

    @Test fun fechaPasadaOriginalAlEditar_seConserva() =
        assertNull(ValidadorTarea.validarFechaEntrega(hoy.minusDays(3), hoy, fechaOriginal = hoy.minusDays(3)))

    @Test fun otraFechaPasadaAlEditar_esError() =
        assertNotNull(ValidadorTarea.validarFechaEntrega(hoy.minusDays(1), hoy, fechaOriginal = hoy.minusDays(3)))
}

class ValidadorMateriaTest {
    @Test fun nombreVacio_esError() = assertNotNull(ValidadorMateria.validarNombre(""))
    @Test fun nombreDeUnCaracter_esError() = assertNotNull(ValidadorMateria.validarNombre("A"))
    @Test fun nombreValido() = assertNull(ValidadorMateria.validarNombre("Bases de Datos"))
    @Test fun nombreExcedido_esError() = assertNotNull(ValidadorMateria.validarNombre("a".repeat(51)))
    @Test fun profesorVacio_esValido() = assertNull(ValidadorMateria.validarProfesor(""))
    @Test fun profesorExcedido_esError() = assertNotNull(ValidadorMateria.validarProfesor("a".repeat(61)))
}
