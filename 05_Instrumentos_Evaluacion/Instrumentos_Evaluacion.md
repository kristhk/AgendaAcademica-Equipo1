# Entregable 5 — Diseño de instrumentos de evaluación tecnológica

**Aplicación:** Agenda Académica v1.0 · **Equipo 1**

Cada instrumento especifica **objetivo, criterios o indicadores, escala, procedimiento y formato de registro**, como pide el examen.
- **Obligatorios de aplicar:** el Instrumento 1 (funcional) y el Instrumento 2 (usabilidad: tareas observadas + SUS).
- **También diseñados:** el 3 (interfaz), el 4 (rendimiento), el 5 (compatibilidad) y el 6 (seguridad). Sus partes técnicas ya se ejecutaron; véase `Registro_Resultados.md`.

| # | Instrumento | Estado de aplicación |
|---|---|---|
| 1 | Lista de verificación funcional | 🟡 Parte automatizada ejecutada (51 unitarias + 18 instrumentadas, todas aprobadas). **Pendiente:** aplicación manual por el equipo con su firma. |
| 2 | Pruebas de usabilidad (tareas + SUS) | 🔴 **Pendiente:** requiere participantes reales. Instrumentos, guion y calculadora listos. |
| 3 | Evaluación de interfaz | 🟡 Pendiente de que evaluadores del equipo llenen la rúbrica. |
| 4 | Pruebas de rendimiento | 🟢 Arranque en frío, memoria y tamaño medidos en el emulador (ver registro). Pendiente: repetir en un dispositivo físico; R-2 y R-3 con cronómetro. |
| 5 | Pruebas de compatibilidad | 🟢 Emulador Pixel 6, API 37: C-1 a C-8 aprobados (C-6 después de corregir un defecto menor). Pendiente: dispositivo físico. |
| 6 | Evaluación de seguridad | 🟢 S-1 a S-8 cumplen (S-2 con prueba automatizada); S-9 es una limitación declarada. |

---

## Instrumento 1 — Lista de verificación funcional

- **Objetivo:** comprobar que cada requerimiento funcional (RF-01 a RF-19) y las reglas de negocio se cumplen en la app instalada.
- **Criterios o indicadores:** resultado observado vs. resultado esperado de cada caso de prueba (PF). **Indicador global:** % de casos aprobados (meta: 100 % de los RF de prioridad alta y ≥ 90 % del total).
- **Escala:** **Cumple (C)** = resultado idéntico al esperado · **Cumple parcialmente (CP)** = funciona con un defecto menor · **No cumple (NC)**.
- **Procedimiento:**
  1. Instalar `07_APK/AgendaAcademica-v1.0-debug.apk` en el emulador o el dispositivo.
  2. Borrar los datos de la app (*Ajustes > Apps > Agenda Académica > Almacenamiento > Borrar datos*).
  3. Ejecutar cada caso en orden, anotar el resultado y, en NC/CP, tomar una captura.
  4. Complemento automatizado: `gradlew testDebugUnitTest connectedDebugAndroidTest` (véase el registro).
- **Formato de registro** (llenar en `Registro_Resultados.md`, sección 1):

| ID | RF | Caso de prueba (pasos) | Resultado esperado | Resultado obtenido | C/CP/NC | Evidencia | Probó (nombre) |
|---|---|---|---|---|---|---|---|
| PF-01 | RF-01 | Materias → Nueva materia → nombre "Física", elegir color → Guardar | Aparece en la lista con "0 de 0 tareas · 0%" y el Snackbar "Materia registrada" | | | | |
| PF-02 | RF-02 | Materias → ✎ en "Física" → cambiar profesor → Guardar | Se ve el profesor actualizado | | | | |
| PF-03 | RF-03, RN-09 | 🗑 en una materia con tareas; después 🗑 en una materia sin tareas → Eliminar | 1) "No se puede eliminar…" 2) Se elimina | | | | |
| PF-04 | RF-04 | Completar 1 de 2 tareas de una materia → Materias | "1 de 2 tareas completadas · 50%" | | | | |
| PF-05 | RF-05, RN-05 | + → título, descripción, materia, prioridad Alta, fecha → Registrar | Regresa a la lista; la tarea aparece como *Pendiente* | | | | |
| PF-06 | RF-06 | Detalle → ✎ → cambiar título, materia y estado → Guardar cambios | El detalle muestra los datos nuevos | | | | |
| PF-07 | RF-07 | Detalle → 🗑 → Cancelar; después 🗑 → Eliminar | 1) No se borra 2) Se borra y regresa | | | | |
| PF-08 | RF-08 | Abrir una tarea completada | Se ven la fecha de entrega, la de registro y la de finalización, y "a tiempo" o "con retraso" | | | | |
| PF-09 | RF-09, RF-10, RF-17 | Tareas → chip "Prioridad alta" + una materia; buscar texto; Ordenar por título | La lista y el contador "X de Y" reflejan los filtros; el orden alfabético es correcto (con acentos) | | | | |
| PF-10 | RF-11, RN-04 | Abrir el calendario e intentar elegir ayer | Los días pasados están deshabilitados | | | | |
| PF-11 | RF-12, RN-08 | Ver una tarea con fecha pasada (datos de ejemplo) | Texto rojo "Venció hace N días" y aviso en el detalle | | | | |
| PF-12 | RF-13, RF-14, RN-06, RN-07 | Detalle → En progreso → Completada; en la lista, desmarcar la casilla | El historial muestra cada cambio; la fecha de finalización aparece y desaparece | | | | |
| PF-13 | RF-15, RF-16, RF-18 | Comparar Inicio y Avances con los datos conocidos | Porcentajes y conteos correctos | | | | |
| PF-14 | RNF-02 | Registrar una tarea, cerrar la app desde Recientes y reiniciar el dispositivo | La tarea sigue ahí | | | | |
| PF-15 | RF-19, RN-12 | Agenda vacía → Cargar datos de ejemplo | 4 materias y 8 tareas; el botón ya no aparece | | | | |
| PF-16 | RNF-07, RN-01/02/10 | Guardar el formulario vacío; título de 2 letras; materia duplicada "física" | Mensajes de error junto a cada campo; no se guarda | | | | |

---

## Instrumento 2 — Pruebas de usabilidad (tareas observadas + cuestionario SUS)

- **Objetivo:** medir la facilidad de uso y de navegación percibida (SUS) y observada (éxito, tiempo y errores por tarea) con usuarios representativos (estudiantes).
- **Participantes:** mínimo **5 estudiantes** que **no** hayan participado en el desarrollo (la guía SUS no fija un mínimo; 5 es la práctica habitual para detectar la mayoría de los problemas de usabilidad). Registrar su edad, carrera y si usan Android.
- **Criterios o indicadores:**

| Indicador | Cómo se mide | Meta |
|---|---|---|
| Tasa de éxito por tarea | Completa sin ayuda = 1, con ayuda = 0.5, no completa = 0 | ≥ 80 % |
| Tiempo por tarea | Cronómetro desde la instrucción hasta completarla | Referencia en el guion |
| Errores | Toques en un lugar equivocado o retrocesos innecesarios | ≤ 1 por tarea |
| **Puntaje SUS global** | Fórmula de la guía (0–100) | **≥ 68** (promedio de la industria) |
| Subescalas SUS | Usabilidad (ítems 1, 2, 3, 5, 6, 7, 8, 9 × 3.125) y Aprendizaje (ítems 4, 10 × 12.5) | ≥ 68 |

- **Escala:** tareas = éxito 0 / 0.5 / 1, segundos y número de errores. **SUS:** Likert de 1 (Totalmente en desacuerdo) a 5 (Totalmente de acuerdo). Interpretación con la tabla de grados de la guía (A+ … F; ≥ 68 = por encima del promedio).
- **Procedimiento** (protocolo de la guía SUS §3):
  1. Preparar el dispositivo: datos borrados y app instalada. Aplicar a un participante a la vez, en un lugar tranquilo.
  2. Explicar: "Evaluamos la app, no a ti. Piensa en voz alta." **No** ayudar salvo que lo pida (anotar la ayuda).
  3. Entregar las tareas una por una (guion de abajo) y anotar el éxito, el tiempo, los errores y los comentarios.
  4. **Inmediatamente después**, entregar `Cuestionario_SUS.md` impreso (o un formulario equivalente). Pedir respuestas espontáneas; si duda, marcar 3. **Los 10 ítems son obligatorios.**
  5. Capturar las respuestas en `plantilla_respuestas_sus.csv` y ejecutar `python calcular_sus.py plantilla_respuestas_sus.csv`.
  6. Reportar el promedio, la desviación, las subescalas, el grado y el adjetivo, y los problemas observados con sus mejoras propuestas.

**Guion de tareas** (con los datos de ejemplo cargados, salvo T1):

| Tarea | Instrucción al participante | Éxito = | Tiempo de referencia |
|---|---|---|---|
| T1 | "Con la app vacía, registra la materia *Química* de color verde." | La materia aparece en la lista | 60 s |
| T2 | "Registra la tarea *Reporte de laboratorio* de Química, prioridad alta, para dentro de 3 días." | La tarea aparece en la lista | 90 s |
| T3 | "Encuentra todas las tareas de prioridad alta de Programación de Móviles." | Aplica los dos filtros | 45 s |
| T4 | "Marca *Diagrama entidad-relación* como en progreso." | Estado actualizado | 30 s |
| T5 | "¿Cuál es tu porcentaje de avance en Ingeniería de Software?" | Responde 50 % | 30 s |
| T6 | "Elimina la tarea *Plan de pruebas*." | Eliminada | 30 s |

**Formato de registro:** sección 2 de `Registro_Resultados.md` (tabla por participante y tarea) + `plantilla_respuestas_sus.csv`.

---

## Instrumento 3 — Evaluación de la interfaz (consistencia y presentación visual)

- **Objetivo:** verificar la consistencia visual, la legibilidad y el apego a Material Design 3 y a las maquetas.
- **Criterios o indicadores** (lista basada en las heurísticas de Nielsen y en las guías de Material):

| # | Criterio |
|---|---|
| I-1 | La paleta de colores es consistente en todas las pantallas (según la guía de estilo de `03_Maquetado`) |
| I-2 | La tipografía es legible (≥ 12 sp en textos de contenido) y la jerarquía es clara |
| I-3 | Los iconos son reconocibles y consistentes (Material Icons) |
| I-4 | La barra superior y la barra inferior son coherentes; el usuario siempre sabe en qué sección está |
| I-5 | Los mensajes de error son claros, en español y junto al campo |
| I-6 | Retroalimentación tras cada acción (Snackbar, cambio visual) |
| I-7 | Hay estados vacíos con explicación y acción |
| I-8 | Las acciones destructivas piden confirmación |
| I-9 | El tema oscuro es legible (contraste suficiente) |
| I-10 | La pantalla implementada corresponde a su maqueta |
| I-11 | Accesibilidad: descripción en los botones de icono; objetivos táctiles ≥ 48 dp |
| I-12 | No hay textos cortados ni encimados en un teléfono de 6" |

- **Escala:** 1 = No cumple · 2 = Cumple con problemas importantes · 3 = Cumple con detalles menores · 4 = Cumple totalmente. **Indicador:** promedio ≥ 3.5.
- **Procedimiento:** dos evaluadores del equipo recorren las 6 pantallas y los diálogos en tema claro y oscuro (*Ajustes > Pantalla > Tema oscuro*) y califican cada criterio por pantalla. Pueden usar el *Accessibility Scanner* de Google (opcional) para I-11.
- **Formato de registro:** matriz de criterio × pantalla (P1–P6) con su calificación y observaciones; sección 3 de `Registro_Resultados.md`.

---

## Instrumento 4 — Pruebas de rendimiento (tiempos de respuesta)

- **Objetivo:** verificar que la app responde dentro de los tiempos del RNF-05.
- **Criterios o indicadores:**

| ID | Medición | Herramienta | Meta |
|---|---|---|---|
| R-1 | Arranque en frío (TotalTime) | `adb shell am start -W -S -n com.equipo1.agendaacademica/.MainActivity` | < 2 s en un dispositivo físico (build release) |
| R-2 | Tiempo de guardar una tarea hasta verla en la lista | Cronómetro o grabación de pantalla | < 1 s |
| R-3 | Filtrar o buscar con 50+ tareas | Cronómetro | < 0.5 s (percibido instantáneo) |
| R-4 | Uso de memoria (PSS) | `adb shell dumpsys meminfo com.equipo1.agendaacademica` | < 150 MB |
| R-5 | Tamaño del APK | Explorador de archivos | Informativo |

- **Escala:** valor numérico (ms, MB) comparado con la meta → **Aprobado / No aprobado**.
- **Procedimiento:** medir con el build release (el debug no está optimizado). Repetir cada medición **5 veces** después de un arranque de descarte; reportar la mediana. Anotar el dispositivo, la versión de Android y si hay aceleración gráfica.
- **Formato de registro:** sección 4 de `Registro_Resultados.md` (dispositivo, build, las 5 mediciones, la mediana y el resultado).

---

## Instrumento 5 — Pruebas de compatibilidad en el dispositivo objetivo

- **Objetivo:** confirmar que la app se instala y funciona en el dispositivo objetivo y en distintas configuraciones.
- **Criterios o indicadores:**

| ID | Criterio |
|---|---|
| C-1 | Instala sin errores |
| C-2 | Abre sin cierres inesperados |
| C-3 | Todas las pantallas se ven completas |
| C-4 | Tema claro y oscuro |
| C-5 | Rotación horizontal: la app no se cierra y no pierde los datos capturados (se acepta un diseño no optimizado, que es una limitación declarada) |
| C-6 | Tamaño de fuente del sistema "Grande" sin textos cortados críticos |
| C-7 | Idioma del sistema en español y en inglés: la app permanece en español |
| C-8 | Navegación por gestos y con 3 botones |

- **Escala:** Funciona / Funciona con defectos / No funciona, por configuración.
- **Procedimiento:** aplicar en (a) el emulador Pixel 6, API 37, y (b) al menos un teléfono físico de un integrante. Requisito: Android 8.0 o superior.
- **Formato de registro:** matriz de dispositivo × criterio; sección 5 de `Registro_Resultados.md`.

---

## Instrumento 6 — Evaluación de seguridad (validación de datos, accesos y protección de la información)

- **Objetivo:** verificar que la app valida las entradas, controla el acceso a su información y protege los datos del usuario.
- **Criterios o indicadores:**

| ID | Criterio | Cómo se verifica | Esperado |
|---|---|---|---|
| S-1 | Validación de entradas | Pruebas unitarias de `ValidadorTarea`/`ValidadorMateria` + PF-16 | Se rechazan los datos inválidos |
| S-2 | Inyección SQL | Revisión de código: todas las consultas usan parámetros enlazados de Room (`:param`); registrar el texto `'); DROP TABLE tareas;--` como título | El texto se guarda literal y no hay error |
| S-3 | Integridad referencial | Prueba instrumentada `llaveForaneaRestrict…` | La BD rechaza el borrado |
| S-4 | Permisos mínimos | `adb shell dumpsys package … \| grep permission` | Ningún permiso peligroso (red, cámara, ubicación, almacenamiento) |
| S-5 | Almacenamiento privado | La BD está en `/data/data/<paquete>/databases` (sandbox de Android) | No es accesible para otras apps |
| S-6 | Respaldo en la nube desactivado | `android:allowBackup="false"` y bandera `ALLOW_BACKUP` ausente | Los datos no salen del dispositivo |
| S-7 | Componentes exportados | Manifiesto: solo `MainActivity` (launcher) exportada | Sin superficies de ataque adicionales |
| S-8 | Build de entrega no depurable | La bandera `DEBUGGABLE` está ausente en release | `run-as` debe fallar |
| S-9 | Control de acceso | Revisar si existe un bloqueo de la app | **No existe** (limitación declarada: depende del bloqueo del teléfono) |

- **Escala:** Cumple / No cumple / No aplica, con su nivel de riesgo (Bajo / Medio / Alto) para cada incumplimiento.
- **Procedimiento:** revisión de código + comandos `adb` sobre el build release + prueba manual de S-2.
- **Formato de registro:** sección 6 de `Registro_Resultados.md`.
