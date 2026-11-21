# Registro de resultados de los instrumentos de evaluación

**Aplicación:** Agenda Académica v1.0 · **Equipo 1**

> **Regla de honestidad:** en este documento solo se registran resultados que realmente se ejecutaron. Las secciones marcadas como **PENDIENTE** las debe llenar el equipo al aplicar los instrumentos. **No se deben inventar** resultados de usuarios.

Entorno de las pruebas que ya se ejecutaron (2026-11-20, ejecutadas por Claude Code en la computadora del equipo):
- **Dispositivo:** emulador Android "Pixel_6" (AVD): Android 17, API 37, 1080 × 2400 px, 420 dpi, x86_64, 2 GB de RAM, aceleración WHPX y renderizado de GPU por software (SwiftShader, sin ventana).
- **Build:** Gradle 9.6.0 · AGP 9.4.1 · Kotlin 2.4.21 · JDK 21.0.8.

---

## 1. Instrumento 1 — Verificación funcional

### 1.a Parte automatizada — EJECUTADA ✅

| Suite | Tipo | Comando | Pruebas | Aprobadas | Fallidas |
|---|---|---|---|---|---|
| `ValidadorTareaTest` | Unitaria (JVM) | `gradlew testDebugUnitTest` | 17 | 17 | 0 |
| `ValidadorMateriaTest` | Unitaria (JVM) | 〃 | 6 | 6 | 0 |
| `CalculadoraAvanceTest` | Unitaria (JVM) | 〃 | 13 | 13 | 0 |
| `FiltroTareasTest` | Unitaria (JVM) | 〃 | 10 | 10 | 0 |
| `FormatoFechasTest` | Unitaria (JVM) | 〃 | 5 | 5 | 0 |
| `RepositorioAcademicoTest` | Instrumentada (Room en memoria y en disco, emulador) | `gradlew connectedDebugAndroidTest` | 16 | 16 | 0 |
| `FlujoPrincipalTest` | Instrumentada de UI de extremo a extremo (Compose UI Test) | 〃 | 2 | 2 | 0 |
| **Total app** | | | **69** | **69** | **0** |
| Prototipo HTML (`03_Maquetado`) | JavaScript en Edge headless (arnés de prueba) | — | 14 | 14 | 0 |

Las 18 instrumentadas se ejecutaron dos veces: el 2026-11-20 en la primera versión (16 pruebas) y de nuevo sobre el código final, después de agregar las pruebas de inyección SQL y persistencia en disco y el ajuste del buscador.

- **Reportes HTML generados por Gradle:** `app-android/app/build/reports/tests/testDebugUnitTest/index.html` y `app-android/app/build/reports/androidTests/connected/debug/index.html`. Se regeneran al volver a ejecutar los comandos.
- **Defectos que encontraron estas pruebas durante el desarrollo** (ya corregidos; véase `06_Desarrollo_Agentico/Problemas_y_Soluciones.md`):
  - El orden alfabético colocaba "Práctica" después de "Proyecto"; se corrigió con un `Collator` en español.
  - Dos fallos del entorno de pruebas: Espresso incompatible con API 37 y un selector del botón flotante de materias.

### 1.b Aplicación manual — PENDIENTE (equipo)

Copiar aquí la tabla PF-01 a PF-16 de `Instrumentos_Evaluacion.md` con las columnas *Resultado obtenido / C-CP-NC / Evidencia / Probó* llenas.

| ID | Resultado obtenido | C/CP/NC | Evidencia | Probó |
|---|---|---|---|---|
| PF-01 | | | | |
| … | | | | |
| PF-16 | | | | |
| **% aprobados** | | | | |

---

## 2. Instrumento 2 — Usabilidad — PENDIENTE (requiere participantes reales)

### 2.a Datos de los participantes
| Part. | Edad | Carrera / semestre | ¿Usa Android? | Fecha |
|---|---|---|---|---|
| P1 | | | | |
| P2 | | | | |
| P3 | | | | |
| P4 | | | | |
| P5 | | | | |

### 2.b Tareas observadas (Éxito 1 / 0.5 / 0 · Tiempo en s · Errores)
| Part. | T1 | T2 | T3 | T4 | T5 | T6 | Comentarios / problemas observados |
|---|---|---|---|---|---|---|---|
| P1 | / / | / / | / / | / / | / / | / / | |
| P2 | | | | | | | |
| P3 | | | | | | | |
| P4 | | | | | | | |
| P5 | | | | | | | |
| **Tasa de éxito** | | | | | | | |

### 2.c Resultados SUS
Capturar las respuestas en `plantilla_respuestas_sus.csv` y pegar aquí la salida de `python calcular_sus.py plantilla_respuestas_sus.csv`:

```
(pendiente)
```

**Interpretación y mejoras propuestas:** _(pendiente)_

> La fórmula de la calculadora se verificó con valores de referencia de la guía (todas las respuestas en 3 → 50; respuestas óptimas → 100; respuestas pésimas → 0). Esos valores **no** son resultados de usuarios y no se guardaron en la plantilla.

---

## 3. Instrumento 3 — Evaluación de interfaz — PENDIENTE (evaluadores del equipo)

| Criterio | P1 Inicio | P2 Tareas | P3 Materias | P4 Avances | P5 Detalle | P6 Formulario | Observaciones |
|---|---|---|---|---|---|---|---|
| I-1 … I-12 | | | | | | | |

Apoyo: las capturas reales en `06_Desarrollo_Agentico/capturas_app/` incluyen los temas claro (01–16) y oscuro (17).

---

## 4. Instrumento 4 — Rendimiento

### R-1 Arranque en frío — EJECUTADO en el emulador ✅ (pendiente en un dispositivo físico)
Comando: `adb shell am start -W -S -n com.equipo1.agendaacademica/.MainActivity` (valor `TotalTime`, en ms).

| Build | Medición 1 | 2 | 3 | 4 | 5 | 6 | Mediana (2–6) |
|---|---|---|---|---|---|---|---|
| Debug (`app-debug.apk`) | 15012* | 11069 | 11480 | 10435 | 11029 | 11629 | **11 069 ms** |
| Release (`app-release.apk`) | 7612* | 5869 | 5434 | 3022 | 3612 | 3847 | **3 847 ms** |
| Referencia: app del sistema *Ajustes* en el mismo emulador | 3961 | 3679 | 2797 | — | — | — | 3 679 ms |

\* Primer arranque, descartado (incluye la compilación inicial).

**Análisis:** el emulador no tiene GPU (renderizado por software), así que incluso la app *Ajustes* del sistema tarda unos 3.7 s. El build release de Agenda Académica queda en el mismo rango (unos 3.8 s), mientras que el debug es unas 3 veces más lento por no estar optimizado. **Con el emulador no se puede afirmar que se cumpla la meta de < 2 s**; hay que medir en un teléfono físico con el build release.

### R-4 Memoria — EJECUTADO en el emulador ✅
`adb shell dumpsys meminfo com.equipo1.agendaacademica` con los datos de ejemplo cargados, en la pantalla de Inicio (build release): **TOTAL PSS = 49 266 KB (≈ 48 MB)** → Aprobado (meta < 150 MB).

### R-5 Tamaño del APK ✅
- Debug (versión final): 19 549 696 bytes (≈ 18.6 MB).
- Release sin minificar: 13 096 352 bytes (≈ 12.5 MB).

### R-2 y R-3 — PENDIENTE (cronómetro o grabación en un dispositivo físico)
| Medición | 1 | 2 | 3 | 4 | 5 | Mediana | Resultado |
|---|---|---|---|---|---|---|---|
| R-2 Guardar → ver en la lista | | | | | | | |
| R-3 Filtrar con 50+ tareas | | | | | | | |

---

## 5. Instrumento 5 — Compatibilidad

| Criterio | Emulador Pixel 6 (API 37) | Teléfono físico: marca/modelo/Android ____ |
|---|---|---|
| C-1 Instala | ✅ Funciona (`adb install` → Success; debug y release) | |
| C-2 Abre sin cierres | ✅ Funciona (sesión de pruebas y capturas sin cierres de la app). *Nota:* el emulador mostró una vez "System UI isn't responding": es un ANR del propio sistema por la lentitud del renderizado por software, no de la app | |
| C-3 Pantallas completas | ✅ Funciona (capturas 01–18) | |
| C-4 Tema claro / oscuro | ✅ Funciona (captura 17 en tema oscuro) | |
| C-5 Rotación | ✅ Funciona. Con el formulario a medio llenar ("Prueba de rotacion") se giró a horizontal y de vuelta a vertical: el texto se conservó, el proceso no se reinició (mismo PID 5655) y el diseño horizontal es usable con desplazamiento (captura 19) | |
| C-6 Fuente grande | ✅ Funciona tras una corrección. Con `font_scale` = 1.3 (mayor que "Grande") los textos se ajustan en varias líneas y los chips se desplazan horizontalmente (capturas 20–22). **Defecto encontrado:** el texto indicativo del buscador ocupaba 2 líneas → se limitó a 1 línea con "…" y se verificó de nuevo (captura 21) | |
| C-7 Idioma del sistema | ✅ El emulador está en inglés y la app se muestra completamente en español | |
| C-8 Navegación por gestos | ✅ (el emulador usa gestos) | |

---

## 6. Instrumento 6 — Seguridad

| ID | Resultado | Evidencia | Riesgo |
|---|---|---|---|
| S-1 Validación de entradas | ✅ Cumple | 23 pruebas unitarias de validadores + `FlujoPrincipalTest` (formulario vacío → 3 errores; materia duplicada → error) | — |
| S-2 Inyección SQL | ✅ Cumple | Revisión de código: todas las consultas de `Daos.kt` usan parámetros `:param` de Room. Prueba automatizada `textoConSqlMalicioso_seGuardaLiteralYNoAfectaLaBase`: el título `'); DROP TABLE tareas;--` se guarda literal y la tabla sigue intacta | — |
| S-3 Integridad referencial | ✅ Cumple | Prueba `llaveForaneaRestrict_impideBorrarMateriaDesdeLaBaseDeDatos` aprobada | — |
| S-4 Permisos mínimos | ✅ Cumple | `dumpsys package`: el único permiso es `…DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, un permiso interno de firma que agrega AndroidX automáticamente. No hay permisos de red, cámara, ubicación ni almacenamiento | — |
| S-5 Almacenamiento privado | ✅ Cumple | Room guarda `agenda_academica.db` en el directorio privado de la app | — |
| S-6 Respaldo desactivado | ✅ Cumple | `dumpsys`: `pkgFlags=[ HAS_CODE ALLOW_CLEAR_USER_DATA ]` (sin `ALLOW_BACKUP`) | — |
| S-7 Componentes exportados | ✅ Cumple | Manifiesto: solo `MainActivity` con `exported=true` (launcher) | — |
| S-8 Build no depurable | ✅ Cumple (release) | Sin la bandera `DEBUGGABLE`; `run-as com.equipo1.agendaacademica` → "package not debuggable" | — |
| S-9 Control de acceso | ❌ No cumple (limitación aceptada) | La app no tiene PIN ni biometría; depende del bloqueo de pantalla del teléfono | Bajo (los datos no son sensibles) |
