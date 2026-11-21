# Entregable 1 — Análisis de requerimientos

**Proyecto:** Equipo 1 — Gestor de tareas y actividades académicas
**Aplicación:** *Agenda Académica* (Android nativo)
**Asignatura:** Programación de Móviles · **Grupo:** _(completar)_ · **Integrantes:** _(completar)_

---

## 1. Descripción del problema

Los estudiantes de nivel superior cursan entre 5 y 8 materias en paralelo y cada una genera tareas, prácticas, proyectos y entregas con fechas distintas. Normalmente esa información queda dispersa entre plataformas escolares, mensajes de grupo, libretas y la memoria del propio estudiante. Esto provoca:

- **Olvidos de entregas** u otras que se descubren a última hora.
- **Mala priorización:** se trabaja en lo más cercano y no en lo más importante.
- **Poca visibilidad del avance:** el estudiante no sabe cuánto lleva completado de cada materia ni cuántas tareas tiene atrasadas.

## 2. Justificación de la aplicación

El teléfono es el dispositivo que el estudiante siempre lleva consigo, así que una aplicación móvil permite registrar una tarea en el momento en que se asigna (en el aula) y consultarla en cualquier lugar. *Agenda Académica* concentra en un solo lugar las tareas clasificadas por materia y prioridad, con su fecha de entrega y su estado, y calcula automáticamente el avance. Funciona **sin conexión a Internet** y **sin crear cuentas**, por lo que no tiene costo de infraestructura y protege la privacidad del usuario.

## 3. Usuarios y actores

| Actor | Tipo | Descripción |
|---|---|---|
| **Estudiante** | Primario (único usuario de la app) | Registra sus materias y tareas, actualiza su estado y consulta su avance. |
| **Sistema (Agenda Académica)** | Secundario / interno | Valida datos, persiste la información en la base local (Room/SQLite), calcula vencimientos y estadísticas y registra el historial de estados. |
| **Profesor** | Externo (no interactúa con la app) | Origina las tareas; el estudiante las captura. No tiene acceso a la app. |

> La app es de uso **personal y monousuario**: los datos viven solo en el dispositivo del estudiante.

## 4. Funcionalidades mínimas del Equipo 1 (enunciado del examen) y su cobertura

| # | Funcionalidad mínima (ACT §3, Equipo 1) | Requerimientos que la cubren | Estado |
|---|---|---|---|
| 1 | Registrar tareas y descripciones | RF-05, RF-06, RF-07, RF-08 | ✅ Implementado |
| 2 | Clasificar por materia y prioridad | RF-01 a RF-04, RF-09, RF-10 | ✅ Implementado |
| 3 | Establecer fechas de entrega | RF-11, RF-12, RF-18 | ✅ Implementado |
| 4 | Cambiar estados y consultar avances | RF-13 a RF-16 | ✅ Implementado |

Como el examen pide **ampliar los requerimientos**, se agregaron la búsqueda y el ordenamiento (RF-17), el historial de estados (RF-14), las estadísticas de puntualidad (RF-16) y los datos de demostración (RF-19).

## 5. Requerimientos funcionales (RF)

| ID | Requerimiento | Prioridad | Pantalla |
|---|---|---|---|
| RF-01 | El sistema debe permitir **registrar una materia** con nombre (obligatorio), profesor (opcional) y color identificador. | Alta | Materias (diálogo) |
| RF-02 | El sistema debe permitir **editar** los datos de una materia. | Media | Materias (diálogo) |
| RF-03 | El sistema debe permitir **eliminar** una materia solo si no tiene tareas asociadas, previa confirmación. | Media | Materias |
| RF-04 | El sistema debe **listar las materias** con el número de tareas totales, las completadas y su porcentaje de avance. | Alta | Materias |
| RF-05 | El sistema debe permitir **registrar una tarea** con título, descripción, materia, prioridad y fecha de entrega. | Alta | Formulario de tarea |
| RF-06 | El sistema debe permitir **editar** todos los datos de una tarea, incluido su estado. | Alta | Formulario de tarea |
| RF-07 | El sistema debe permitir **eliminar** una tarea previa confirmación; también se elimina su historial. | Media | Detalle |
| RF-08 | El sistema debe mostrar el **detalle completo** de una tarea (datos, fechas de registro y de finalización, puntualidad). | Alta | Detalle |
| RF-09 | El sistema debe **clasificar las tareas por materia**: asignar una materia a cada tarea y filtrar la lista por materia. | Alta | Formulario / Tareas |
| RF-10 | El sistema debe **clasificar las tareas por prioridad** (Alta, Media, Baja): asignarla y filtrar por ella. | Alta | Formulario / Tareas |
| RF-11 | El sistema debe permitir **establecer la fecha de entrega** mediante un calendario que no permita elegir fechas pasadas. | Alta | Formulario |
| RF-12 | El sistema debe indicar los **días restantes** o el retraso de cada tarea y resaltar las **vencidas**. | Alta | Tareas / Detalle / Inicio |
| RF-13 | El sistema debe permitir **cambiar el estado** de una tarea (Pendiente, En progreso, Completada) desde el detalle, la edición o una casilla rápida en la lista. | Alta | Detalle / Tareas |
| RF-14 | El sistema debe registrar un **historial de cambios de estado** con fecha y hora, y mostrarlo en el detalle. | Media | Detalle |
| RF-15 | El sistema debe mostrar un **panel de inicio** con el porcentaje de avance general, los contadores por estado y las vencidas. | Alta | Inicio |
| RF-16 | El sistema debe mostrar **reportes de avance**: por estado, por materia, sin completar por prioridad y puntualidad (a tiempo / con retraso). | Alta | Avances |
| RF-17 | El sistema debe permitir **buscar** tareas por texto (título o descripción) y **ordenarlas** por fecha, prioridad o título. | Media | Tareas |
| RF-18 | El sistema debe mostrar las **próximas entregas** (los siguientes 7 días) en el inicio. | Media | Inicio |
| RF-19 | El sistema debe permitir **cargar datos de ejemplo** cuando la agenda está vacía, para conocer la app. | Baja | Inicio |

## 6. Requerimientos no funcionales (RNF)

| ID | Categoría | Requerimiento | Cómo se verifica |
|---|---|---|---|
| RNF-01 | Compatibilidad | Funciona en Android 8.0 (API 26) o superior; probada en un emulador con Android de API 37. | Instrumento de compatibilidad |
| RNF-02 | Disponibilidad | Funciona **sin conexión**; los datos persisten al cerrar la app o reiniciar el dispositivo. | Prueba funcional PF-14 |
| RNF-03 | Idioma | Toda la interfaz y los mensajes están **en español**. | Evaluación de interfaz |
| RNF-04 | Usabilidad | Interfaz Material Design 3, con navegación inferior de 4 secciones; cualquier función se alcanza en ≤ 3 toques. Meta SUS ≥ 68. | SUS + tareas observadas |
| RNF-05 | Rendimiento | Arranque en frío < 2 s y operaciones CRUD reflejadas en pantalla en < 1 s. | Instrumento de rendimiento |
| RNF-06 | Integridad | La base de datos impone llaves foráneas (RESTRICT y CASCADE) y la unicidad del nombre de materia. | Prueba instrumentada |
| RNF-07 | Validación | Todos los formularios validan los datos y muestran el error junto al campo. | Pruebas unitarias + funcionales |
| RNF-08 | Seguridad y privacidad | No solicita permisos, no usa red y desactiva el respaldo en la nube (`allowBackup=false`); los datos quedan en el almacenamiento privado de la app. | Instrumento de seguridad |
| RNF-09 | Mantenibilidad | Arquitectura MVVM + Repository; lógica de negocio en funciones puras con pruebas unitarias. | Revisión de código |
| RNF-10 | Accesibilidad | Iconos con descripción para lectores de pantalla, objetivos táctiles ≥ 48 dp y soporte de tema oscuro. | Evaluación de interfaz |

## 7. Historias de usuario

Formato: *Como [actor], quiero [acción] para [beneficio].* Criterios de aceptación en formato Dado / Cuando / Entonces.

**HU-01 Registrar materia** (RF-01) — *Como estudiante, quiero registrar las materias que curso para clasificar mis tareas.*
- Dado que estoy en Materias, cuando capturo un nombre válido y guardo, entonces la materia aparece en la lista con 0 % de avance.
- Si el nombre está vacío o ya existe (sin importar mayúsculas), entonces se muestra el error en el campo y no se guarda.

**HU-02 Registrar tarea** (RF-05, RF-09 a RF-11) — *Como estudiante, quiero registrar una tarea con su descripción, materia, prioridad y fecha para no olvidarla.*
- Cuando guardo con todos los campos obligatorios válidos, entonces la tarea aparece en la lista en estado *Pendiente*.
- Si falta el título, la materia o la fecha, entonces se muestra el mensaje correspondiente en cada campo.
- El calendario no permite seleccionar fechas anteriores a hoy.

**HU-03 Clasificar y filtrar** (RF-09, RF-10, RF-17) — *Como estudiante, quiero filtrar mis tareas por estado, prioridad y materia, y buscarlas por texto, para encontrar rápido lo que debo hacer.*
- Al activar uno o varios filtros, la lista muestra solo las tareas que cumplen todos y el contador "X de Y tareas" se actualiza.
- Si no hay resultados, aparece "Sin resultados" con el botón *Limpiar filtros*.

**HU-04 Cambiar estado** (RF-13, RF-14) — *Como estudiante, quiero marcar el estado de una tarea para saber en qué voy.*
- Al seleccionar otro estado en el detalle, este se guarda de inmediato y se agrega un renglón "Anterior → Nuevo" al historial.
- Al marcar *Completada* se registra la fecha de finalización; al reabrirla, la fecha se borra.

**HU-05 Consultar avance** (RF-15, RF-16, RF-18) — *Como estudiante, quiero ver mi porcentaje de avance y mis próximas entregas para organizar mi semana.*
- El inicio muestra el porcentaje de avance general, los contadores (pendientes, en progreso, vencidas) y las entregas de los próximos 7 días.
- La pantalla Avances muestra las barras por estado, por materia y por prioridad, y la puntualidad.

**HU-06 Editar y eliminar** (RF-02, RF-03, RF-06, RF-07) — *Como estudiante, quiero corregir o eliminar tareas y materias que capturé mal.*
- Eliminar siempre pide confirmación.
- No se puede eliminar una materia con tareas: el sistema explica por qué.

## 8. Casos de uso principales

**CU-01 Registrar tarea**
- **Actor:** Estudiante. **Precondición:** existe al menos una materia.
- **Flujo principal:**
  1. El estudiante presiona **+ / Nueva tarea**.
  2. El sistema muestra el formulario con prioridad *Media* por omisión.
  3. El estudiante captura título y descripción, elige la materia, la prioridad y la fecha (calendario).
  4. El estudiante presiona *Registrar tarea*.
  5. El sistema valida los campos (RN-01 a RN-04), guarda la tarea en estado *Pendiente* (RN-05), registra el historial inicial (RN-07) y regresa a la pantalla anterior.
- **Flujos alternos:**
  - 4a. Hay datos inválidos: el sistema marca los campos en rojo con su mensaje y muestra "Revisa los campos marcados en rojo"; se regresa al paso 3.
  - 3a. No hay materias: el formulario muestra "Aún no tienes materias registradas" y el enlace *Registrar una materia*.

**CU-02 Cambiar estado de una tarea**
- **Actor:** Estudiante. **Precondición:** la tarea existe.
- **Flujo principal:**
  1. El estudiante abre el detalle de la tarea.
  2. El estudiante elige un estado en *Cambiar estado*.
  3. El sistema actualiza el estado y la fecha de finalización (RN-06), inserta el registro en el historial (RN-07) y refresca la pantalla.
- **Flujo alterno:** 1a. Desde la lista, la casilla alterna entre *Completada* y *Pendiente*.

**CU-03 Eliminar materia**
- **Actor:** Estudiante.
- **Flujo principal:**
  1. El estudiante presiona el icono de eliminar en la materia.
  2. El sistema pide confirmación.
  3. El estudiante confirma y el sistema elimina la materia.
- **Flujo alterno:** 2a. La materia tiene tareas: el sistema muestra "No se puede eliminar…" con el número de tareas (RN-09). La base de datos también lo impide (llave foránea RESTRICT).

**CU-04 Consultar avances** — El estudiante abre *Avances* y el sistema calcula y muestra las estadísticas a partir de los datos actuales (RN-08, RN-11). Si no hay tareas, muestra "Aún no hay datos".

## 9. Reglas de negocio

| ID | Regla | Dónde se aplica (código) |
|---|---|---|
| RN-01 | El título es obligatorio, de 3 a 80 caracteres (sin contar los espacios de los extremos). | `ValidadorTarea.validarTitulo` |
| RN-02 | La descripción es opcional, con un máximo de 500 caracteres. | `ValidadorTarea.validarDescripcion` |
| RN-03 | Toda tarea pertenece a exactamente una materia. | `ValidadorTarea.validarMateria` + FK `materia_id NOT NULL` |
| RN-04 | La fecha de entrega es obligatoria y no puede ser anterior a hoy; al editar se permite conservar la fecha original aunque ya haya pasado. | `ValidadorTarea.validarFechaEntrega` + `SelectableDates` del calendario |
| RN-05 | Toda tarea nueva inicia en estado *Pendiente*. | `RepositorioAcademico.crearTarea` |
| RN-06 | Al pasar a *Completada* se guarda `fecha_completada`; al salir de *Completada* se borra. | `RepositorioAcademico.aplicarEstado` |
| RN-07 | Cada cambio de estado (y la creación) se registra en el historial con fecha y hora. | `TareaDao.insertarConHistorial / actualizarConHistorial` |
| RN-08 | Una tarea está **vencida** si no está completada y su fecha de entrega es anterior a hoy (es un dato calculado, no almacenado). | `CalculadoraAvance.esVencida` |
| RN-09 | No se puede eliminar una materia con tareas asociadas. | `RepositorioAcademico.eliminarMateria` + FK RESTRICT |
| RN-10 | El nombre de materia es obligatorio (2 a 50 caracteres) y único, sin distinguir mayúsculas. | `ValidadorMateria` + `contarConNombre … COLLATE NOCASE` + índice único |
| RN-11 | Avance = completadas ÷ total × 100, redondeado; 0 % si no hay tareas. Una tarea es "a tiempo" si se completó el día de entrega o antes. | `CalculadoraAvance` |
| RN-12 | Los datos de ejemplo solo se cargan con la agenda vacía. | `RepositorioAcademico.cargarDatosEjemplo` |

## 10. Alcance

**Incluye:** CRUD de materias y tareas; clasificación por materia y prioridad; fecha de entrega con calendario; tres estados con historial; búsqueda, filtros y orden; panel de inicio y reportes de avance; persistencia local con Room; tema claro y oscuro; datos de demostración.

## 11. Limitaciones (fuera de alcance)

- **Sin sincronización en la nube ni cuentas de usuario:** los datos están en un solo dispositivo; si se desinstala la app, se pierden (no hay respaldo, `allowBackup=false`).
- **Sin notificaciones o recordatorios push** (se considera mejora futura con WorkManager).
- **Sin adjuntos** (fotos o archivos) en las tareas.
- **Sin integración** con plataformas escolares (Classroom, Moodle).
- **Sin control de acceso** (PIN o biometría): se asume un dispositivo personal; véase el instrumento de seguridad.
- Solo Android; no hay versión iOS ni web.
- Interfaz pensada para teléfono en vertical (no se optimizó para tabletas).

## 12. Matriz de trazabilidad (RF → pantalla → prueba)

| RF | Pantalla | Prueba funcional (Instrumento 1) | Prueba automatizada |
|---|---|---|---|
| RF-01 | Materias | PF-01 | `FlujoPrincipalTest.flujoCompleto…` |
| RF-02 | Materias | PF-02 | `RepositorioAcademicoTest.editarMateria…` |
| RF-03 | Materias | PF-03 | `eliminarMateriaConTareas_esRechazado`, `eliminarMateriaSinTareas…`, `llaveForaneaRestrict…` |
| RF-04 | Materias | PF-04 | `materiasConAvance_cuentaTotalesYCompletadas` |
| RF-05 | Formulario | PF-05 | `crearTarea_iniciaPendienteYRegistraHistorial`, UI flujo |
| RF-06 | Formulario | PF-06 | `actualizarTarea_modificaCampos` |
| RF-07 | Detalle | PF-07 | `eliminarTarea_eliminaSuHistorialEnCascada` |
| RF-08 | Detalle | PF-08 | UI flujo |
| RF-09 / RF-10 | Formulario / Tareas | PF-09 | `FiltroTareasTest.*` |
| RF-11 | Formulario | PF-10 | `ValidadorTareaTest.fecha*` |
| RF-12 | Tareas / Detalle | PF-11 | `CalculadoraAvanceTest.*Vencida*`, `FormatoFechasTest.*` |
| RF-13 / RF-14 | Detalle / Tareas | PF-12 | `completarTarea…`, `reabrirTarea…`, `cambiarAlMismoEstado…` |
| RF-15 / RF-16 / RF-18 | Inicio / Avances | PF-13 | `CalculadoraAvanceTest.resumen…`, `proximasEntregas…` |
| RF-17 | Tareas | PF-09 | `FiltroTareasTest.orden*`, `texto*` |
| RF-19 | Inicio | PF-15 | `datosDeEjemplo_soloConAgendaVacia` |
| RNF-02 | — | PF-14 | (manual: cerrar app / reiniciar) |
| RNF-07 | Formularios | PF-16 | `ValidadorTareaTest`, `ValidadorMateriaTest`, UI flujo (validación) |
