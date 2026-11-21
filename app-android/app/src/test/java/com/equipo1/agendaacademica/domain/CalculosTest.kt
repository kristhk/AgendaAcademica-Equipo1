package com.equipo1.agendaacademica.domain

import com.equipo1.agendaacademica.data.local.MateriaEntity
import com.equipo1.agendaacademica.data.local.TareaConMateria
import com.equipo1.agendaacademica.data.local.TareaEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

private val HOY: LocalDate = LocalDate.of(2026, 11, 20)
private val UTC = ZoneOffset.UTC
private val MATERIA_A = MateriaEntity(id = 1, nombre = "Móviles", color = 0)
private val MATERIA_B = MateriaEntity(id = 2, nombre = "Bases de Datos", color = 0)

private fun tarea(
    id: Long,
    entrega: LocalDate = HOY,
    estado: EstadoTarea = EstadoTarea.PENDIENTE,
    prioridad: Prioridad = Prioridad.MEDIA,
    materia: Long = 1,
    titulo: String = "Tarea $id",
    descripcion: String = "",
    completadaEl: LocalDate? = null,
) = TareaEntity(
    id = id, titulo = titulo, descripcion = descripcion, materiaId = materia, prioridad = prioridad,
    fechaEntrega = entrega, estado = estado, fechaCreacion = 0,
    fechaCompletada = completadaEl?.atStartOfDay(UTC)?.plusHours(12)?.toInstant()?.toEpochMilli(),
)

private fun conMateria(t: TareaEntity) = TareaConMateria(t, if (t.materiaId == 1L) MATERIA_A else MATERIA_B)

class CalculadoraAvanceTest {

    @Test fun porcentajeSinTareas_esCero() = assertEquals(0, CalculadoraAvance.porcentaje(0, 0))
    @Test fun porcentajeRedondeaHaciaAbajo() = assertEquals(33, CalculadoraAvance.porcentaje(1, 3))
    @Test fun porcentajeRedondeaHaciaArriba() = assertEquals(67, CalculadoraAvance.porcentaje(2, 3))
    @Test fun porcentajeCompleto() = assertEquals(100, CalculadoraAvance.porcentaje(4, 4))

    @Test fun tareaPendienteConFechaPasada_estaVencida() =
        assertTrue(CalculadoraAvance.esVencida(tarea(1, entrega = HOY.minusDays(1)), HOY))

    @Test fun tareaQueVenceHoy_noEstaVencida() =
        assertFalse(CalculadoraAvance.esVencida(tarea(1, entrega = HOY), HOY))

    @Test fun tareaCompletadaConFechaPasada_noEstaVencida() =
        assertFalse(CalculadoraAvance.esVencida(tarea(1, entrega = HOY.minusDays(5), estado = EstadoTarea.COMPLETADA), HOY))

    @Test fun completadaElDiaDeEntrega_esATiempo() = assertEquals(
        true,
        CalculadoraAvance.fueCompletadaATiempo(tarea(1, entrega = HOY, estado = EstadoTarea.COMPLETADA, completadaEl = HOY), UTC),
    )

    @Test fun completadaDespuesDeLaEntrega_esConRetraso() = assertEquals(
        false,
        CalculadoraAvance.fueCompletadaATiempo(
            tarea(1, entrega = HOY.minusDays(2), estado = EstadoTarea.COMPLETADA, completadaEl = HOY), UTC
        ),
    )

    @Test fun noCompletada_noTienePuntualidad() = assertNull(CalculadoraAvance.fueCompletadaATiempo(tarea(1), UTC))

    @Test
    fun resumen_cuentaCadaCategoria() {
        val tareas = listOf(
            tarea(1, estado = EstadoTarea.PENDIENTE),
            tarea(2, estado = EstadoTarea.PENDIENTE, entrega = HOY.minusDays(1)), // vencida
            tarea(3, estado = EstadoTarea.EN_PROGRESO),
            tarea(4, estado = EstadoTarea.COMPLETADA, entrega = HOY, completadaEl = HOY), // a tiempo
            tarea(5, estado = EstadoTarea.COMPLETADA, entrega = HOY.minusDays(3), completadaEl = HOY), // con retraso
        )
        val r = CalculadoraAvance.resumen(tareas, HOY, UTC)
        assertEquals(5, r.total)
        assertEquals(2, r.pendientes)
        assertEquals(1, r.enProgreso)
        assertEquals(2, r.completadas)
        assertEquals(1, r.vencidas)
        assertEquals(1, r.completadasATiempo)
        assertEquals(1, r.completadasConRetraso)
        assertEquals(40, r.porcentaje)
    }

    @Test
    fun proximasEntregas_filtraRangoYOrdenaPorFechaYPrioridad() {
        val tareas = listOf(
            tarea(1, entrega = HOY.plusDays(3), prioridad = Prioridad.BAJA),
            tarea(2, entrega = HOY.plusDays(3), prioridad = Prioridad.ALTA),
            tarea(3, entrega = HOY),
            tarea(4, entrega = HOY.plusDays(8)), // fuera de rango
            tarea(5, entrega = HOY.minusDays(1)), // vencida: no es "próxima"
            tarea(6, entrega = HOY.plusDays(1), estado = EstadoTarea.COMPLETADA), // completada
        ).map(::conMateria)
        val ids = CalculadoraAvance.proximasEntregas(tareas, HOY).map { it.tarea.id }
        assertEquals(listOf(3L, 2L, 1L), ids)
    }

    @Test
    fun pendientesPorPrioridad_incluyeCerosYExcluyeCompletadas() {
        val tareas = listOf(
            tarea(1, prioridad = Prioridad.ALTA),
            tarea(2, prioridad = Prioridad.ALTA, estado = EstadoTarea.EN_PROGRESO),
            tarea(3, prioridad = Prioridad.ALTA, estado = EstadoTarea.COMPLETADA),
            tarea(4, prioridad = Prioridad.BAJA),
        )
        val mapa = CalculadoraAvance.pendientesPorPrioridad(tareas)
        assertEquals(2, mapa[Prioridad.ALTA])
        assertEquals(0, mapa[Prioridad.MEDIA])
        assertEquals(1, mapa[Prioridad.BAJA])
    }
}

class FiltroTareasTest {
    private val tareas = listOf(
        tarea(1, titulo = "Ensayo de historia", entrega = HOY.plusDays(5), prioridad = Prioridad.BAJA, materia = 1),
        tarea(2, titulo = "Práctica SQL", descripcion = "consultas JOIN", entrega = HOY.plusDays(1),
            prioridad = Prioridad.ALTA, materia = 2, estado = EstadoTarea.EN_PROGRESO),
        tarea(3, titulo = "Proyecto final", entrega = HOY.plusDays(10), prioridad = Prioridad.ALTA, materia = 1,
            estado = EstadoTarea.COMPLETADA),
    ).map(::conMateria)

    private fun ids(f: FiltrosTareas) = FiltroTareas.aplicar(tareas, f).map { it.tarea.id }

    @Test fun sinFiltros_ordenaPorFecha() = assertEquals(listOf(2L, 1L, 3L), ids(FiltrosTareas()))
    @Test fun textoBuscaEnTituloSinDistinguirMayusculas() = assertEquals(listOf(1L), ids(FiltrosTareas(texto = "ENSAYO")))
    @Test fun textoBuscaEnDescripcion() = assertEquals(listOf(2L), ids(FiltrosTareas(texto = "join")))
    @Test fun filtroPorEstado() = assertEquals(listOf(3L), ids(FiltrosTareas(estado = EstadoTarea.COMPLETADA)))
    @Test fun filtroPorPrioridad() = assertEquals(listOf(2L, 3L), ids(FiltrosTareas(prioridad = Prioridad.ALTA)))
    @Test fun filtroPorMateria() = assertEquals(listOf(1L, 3L), ids(FiltrosTareas(materiaId = 1)))

    @Test fun filtrosCombinados() =
        assertEquals(listOf(3L), ids(FiltrosTareas(materiaId = 1, prioridad = Prioridad.ALTA)))

    @Test fun ordenPorPrioridad() = assertEquals(listOf(2L, 3L, 1L), ids(FiltrosTareas(orden = OrdenTareas.PRIORIDAD)))
    @Test fun ordenPorTitulo() = assertEquals(listOf(1L, 2L, 3L), ids(FiltrosTareas(orden = OrdenTareas.TITULO)))

    @Test fun hayFiltrosActivos() {
        assertFalse(FiltrosTareas(orden = OrdenTareas.TITULO).hayFiltrosActivos)
        assertTrue(FiltrosTareas(texto = "x").hayFiltrosActivos)
    }
}

class FormatoFechasTest {
    @Test fun venceHoy() = assertEquals("Vence hoy", FormatoFechas.textoRelativo(HOY, HOY))
    @Test fun venceManana() = assertEquals("Vence mañana", FormatoFechas.textoRelativo(HOY.plusDays(1), HOY))
    @Test fun faltanDias() = assertEquals("Faltan 5 días", FormatoFechas.textoRelativo(HOY.plusDays(5), HOY))
    @Test fun vencioAyer() = assertEquals("Venció ayer", FormatoFechas.textoRelativo(HOY.minusDays(1), HOY))
    @Test fun vencioHaceDias() = assertEquals("Venció hace 4 días", FormatoFechas.textoRelativo(HOY.minusDays(4), HOY))
}
