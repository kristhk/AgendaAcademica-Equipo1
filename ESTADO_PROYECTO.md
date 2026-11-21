# ESTADO DEL PROYECTO — Agenda Académica (Equipo 1)

**Última actualización:** 2026-11-20 (2.ª iteración) · **Herramienta:** Claude Code (Claude Opus 5.5), **autorizada por el profesor**
**Proyecto asignado:** Equipo 1 — Gestor de tareas y actividades académicas

---

## 1. Resumen

| Aspecto | Estado |
|---|---|
| App Android (Kotlin + Compose + Material 3 + MVVM + Room) | ✅ Completa: las 4 funciones obligatorias y extras, verificadas otra vez en esta iteración |
| Compilación con Gradle | ✅ `clean assembleDebug` y `assembleRelease`: BUILD SUCCESSFUL, sin errores ni advertencias de Kotlin |
| APK de depuración | ✅ `07_APK\AgendaAcademica-v1.0-debug.apk` (versión final) |
| Pruebas automatizadas de la app | ✅ 69/69 (51 unitarias + 18 instrumentadas en el emulador) |
| Prototipo HTML navegable y funcional | ✅ 10 pantallas + guía de estilo; 14/14 pruebas JavaScript |
| Expediente en Markdown + DOCX + PDF | ✅ Un DOCX y un PDF por documento + expediente consolidado con portada e índice |
| Repositorio Git local | ✅ Commits descriptivos por etapas (`git log`); no publicado |
| Pruebas con usuarios (SUS + tareas observadas) | 🔴 **Pendiente**: instrumentos listos, faltan participantes reales |

## 2. Las cuatro funcionalidades mínimas del Equipo 1 (verificadas)

| # | Funcionalidad (enunciado) | Estado | Verificación |
|---|---|---|---|
| 1 | Registrar tareas y descripciones | ✅ | Prueba de UI de extremo a extremo + `crearTarea…`, `actualizarTarea…`, `eliminarTarea…` |
| 2 | Clasificar por materia y prioridad | ✅ | CRUD de materias (unicidad, RESTRICT), filtros en `FiltroTareasTest` (10 pruebas) |
| 3 | Establecer fechas de entrega | ✅ | Calendario con los días pasados bloqueados; `ValidadorTareaTest` (fechas), `CalculadoraAvanceTest` (vencidas) |
| 4 | Cambiar estados y consultar avances | ✅ | Historial (`completarTarea…`, `reabrirTarea…`), reportes (`resumen…`, `materiasConAvance…`), prueba de UI hasta "Avances" |

**Extras:** búsqueda y orden; historial de estados; tema oscuro; datos de ejemplo; persistencia sin conexión; soporte de rotación y fuente grande.

## 3. Avances de esta iteración

1. **Revisión del estado real:** se volvió a compilar desde cero (`gradlew clean assembleDebug`) y se ejecutaron todas las pruebas. No se dio nada por terminado sin evidencia.
2. **Figma/Penpot:** se verificó en el enunciado ("*Se pueden utilizar Figma, Penpot u otras herramientas de diseño*") que son opcionales.
3. **Git local:**
   - Se configuraron `.gitignore` (Android, Gradle, Office, Python, APK) y `.gitattributes`.
   - El historial tiene commits por etapas con el trailer `Co-Authored-By: Claude`.
   - La identidad Git es solo local al repositorio.
4. **Pruebas nuevas:**
   - Inyección SQL (S-2) y persistencia en disco (RNF-02).
   - Compatibilidad en el emulador: rotación (C-5) y fuente al 130 % (C-6). C-6 reveló un defecto menor (buscador en 2 líneas) que se **corrigió y verificó**.
5. **Prototipo funcional:**
   - Filtros, búsqueda, casillas y formulario con las mismas validaciones que la app.
   - Guía de estilo con colores, tipografía, componentes y flujo de navegación.
6. **Expediente DOCX/PDF:** se generó con `herramientas/generar_expediente.py` usando Word y Edge (ya instalados); **no se instaló software**.
7. Se agregaron `LEEME.md` (índice del expediente) y los anexos *Capturas de la app* e *Historial Git*.

## 4. Resultado de la compilación (evidencia)

Entorno: JDK 21.0.8 (`C:\Program Files\Android\openjdk\jdk-21.0.8`), Gradle 9.6.0 (wrapper), AGP 9.4.1, Kotlin 2.4.21, compileSdk 37, minSdk 26, targetSdk 36.

| Tarea Gradle | Resultado |
|---|---|
| `clean assembleDebug testDebugUnitTest` | `BUILD SUCCESSFUL`: 51 pruebas, 0 fallidas |
| `connectedDebugAndroidTest` (código final) | `BUILD SUCCESSFUL`: 18 pruebas, 0 fallidas (emulador Pixel_6, API 37) |
| `assembleRelease` | `BUILD SUCCESSFUL` (firmado con la llave debug **solo para pruebas**) |

## 5. APK

| Archivo | Ruta exacta | Tamaño | SHA-256 |
|---|---|---|---|
| **APK de depuración (entrega)** | `C:\Users\thomm\Documents\ProyectoExamenMoviles\07_APK\AgendaAcademica-v1.0-debug.apk` | 19 549 696 B | `72e6375dbde0231aed5b574dd3f333846f54ba6faa7f8d46e48baba7ac796df1` |
| Original generado por Gradle | `C:\Users\thomm\Documents\ProyectoExamenMoviles\app-android\app\build\outputs\apk\debug\app-debug.apk` | ídem | ídem |
| APK release (para medir rendimiento) | `C:\Users\thomm\Documents\ProyectoExamenMoviles\07_APK\AgendaAcademica-v1.0-release-firmaDebug.apk` | 13 096 352 B | `cb3cbfa5cb3d907304d4fbfd181e9797c300518cd5b1a3c96982a73c06a54b9b` |

**Instalar en un teléfono:**
- Opción A: copiar el APK al teléfono → abrirlo → permitir "instalar apps de origen desconocido".
- Opción B: con depuración USB activada: `adb install -r AgendaAcademica-v1.0-debug.apk`.

> Los APK no se guardan en Git (son binarios regenerables); se entregan en `07_APK/`.

## 6. Pruebas ejecutadas

| Prueba | Resultado | Evidencia |
|---|---|---|
| 51 pruebas unitarias (validadores, avance, filtros, fechas) | ✅ 51/51 | `05_Instrumentos_Evaluacion/Registro_Resultados.md` §1 |
| 16 pruebas instrumentadas de Room/repositorio (CRUD, historial, FK, unicidad, agregados, **inyección SQL**, **persistencia en disco**) | ✅ 16/16 | ídem |
| 2 pruebas de UI de extremo a extremo | ✅ 2/2 | ídem |
| 14 pruebas JavaScript del prototipo HTML (Edge headless) | ✅ 14/14 | `03_Maquetado/Maquetado.md` §1 |
| Rendimiento en el emulador: arranque en frío, memoria y tamaño | ✅ Medido: release ≈ 3.8 s (*Ajustes* del sistema ≈ 3.7 s); PSS ≈ 48 MB | `Registro_Resultados.md` §4 |
| Compatibilidad en el emulador (C-1 a C-8) | ✅ Todos funcionan (C-6 tras corregir un defecto) | §5 y capturas 19–22 |
| Seguridad (S-1 a S-9) | ✅ 8 cumplen; S-9 (sin PIN) es una limitación declarada | §6 |
| 22 capturas reales de la app | ✅ | `06_Desarrollo_Agentico/capturas_app/` y el anexo *Capturas de la app* |

## 7. Cómo abrir y ejecutar la app en Android Studio

1. Abrir **Android Studio** → *File > Open…* → seleccionar la carpeta **`C:\Users\thomm\Documents\ProyectoExamenMoviles\app-android`** (no la raíz).
2. Esperar el *Gradle Sync* (usa el wrapper, Gradle 9.6.0). Si se pide un JDK: *Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK* → el JDK embebido (`jbr`) o `C:\Program Files\Android\openjdk\jdk-21.0.8`.
3. `local.properties` apunta al SDK (`C:/Users/thomm/AppData/Local/Android/Sdk`). No está en Git; Android Studio lo regenera en otra PC.
4. Elegir el dispositivo **Pixel_6** (o un teléfono con depuración USB) y pulsar ▶ **Run 'app'**.
5. **Primer uso:** en el Inicio, pulsar *Cargar datos de ejemplo* (demo) o *Registrar mis materias* (desde cero).
6. **Pruebas:** clic derecho en `app/src/test/java` → *Run 'Tests in…'*; o en `app/src/androidTest/java` con un emulador encendido. **Aviso:** `FlujoPrincipalTest` borra los datos de la app, y el Android Gradle Plugin desinstala la app al terminar las pruebas instrumentadas.
7. **Terminal** (PowerShell, sin cambiar variables del sistema):
   ```powershell
   cd C:\Users\thomm\Documents\ProyectoExamenMoviles\app-android
   $env:JAVA_HOME = "C:\Program Files\Android\openjdk\jdk-21.0.8"
   .\gradlew.bat assembleDebug              # genera el APK
   .\gradlew.bat testDebugUnitTest          # pruebas unitarias
   .\gradlew.bat connectedDebugAndroidTest  # pruebas instrumentadas (emulador encendido)
   ```
8. **Base de datos:** *View > Tool Windows > App Inspection > Database Inspector* con la app debug en ejecución.
9. **Regenerar los DOCX/PDF después de editar un `.md`:** desde la raíz, `python herramientas/generar_expediente.py`. Requiere Word, Edge y conexión a Internet para los diagramas Mermaid; los ya generados quedan en caché.

## 8. Pendientes para la entrega del lunes (responsable: equipo)

**Imprescindibles (rúbrica):**
1. 🔴 **Pruebas de usabilidad** con ≥ 5 estudiantes ajenos al equipo:
   1. Aplicar el guion T1–T6 y `05_Instrumentos_Evaluacion/Cuestionario_SUS.pdf`.
   2. Capturar las respuestas en `plantilla_respuestas_sus.csv`.
   3. Ejecutar `python calcular_sus.py plantilla_respuestas_sus.csv`.
   4. Pegar los resultados en `Registro_Resultados.md` §2 y regenerar los PDF.
2. 🔴 **Lista de verificación funcional manual** PF-01 a PF-16, con el nombre de quien probó y la evidencia (§1.b).
3. 🟡 **Capturas del proceso agéntico** (la terminal de Claude Code): ver `06_Desarrollo_Agentico/Prompts.md` §5. El historial Git ya sirve como evidencia complementaria.
4. 🟡 **Modificaciones propias del equipo:** cada integrante hace y registra al menos un cambio (`Bitacora_Desarrollo_Agentico.md` §6) y lo guarda con `git commit`.
5. 🟡 **Datos del equipo** (grupo, integrantes): portada del expediente (`herramientas/generar_expediente.py`, variable `portada`) y encabezado de E1. Después, regenerar los documentos.
6. 🟡 **Teléfono físico:** rendimiento (R-1 a R-3) y compatibilidad (§4 y §5 del registro).
7. 🟡 **Evaluación de interfaz** (I-1 a I-12) por 2 evaluadores (§3).

**Preparación de la defensa:**
8. Repartir los temas entre los integrantes y ensayar la demo:
   1. Borrar los datos.
   2. Registrar una materia.
   3. Registrar una tarea mostrando la validación.
   4. Filtrar.
   5. Cambiar el estado y ver el historial.
   6. Revisar Avances.
   7. Intentar borrar una materia con tareas.
9. Prepararse para un cambio en vivo: por ejemplo, agregar un color a `PaletaMaterias` o cambiar `TITULO_MAX`.

## 9. Documentos generados

| Carpeta | Archivos (cada `.md` también en `.docx` y `.pdf`) |
|---|---|
| raíz | `LEEME.md`, `PLAN_PROYECTO.md`, `ESTADO_PROYECTO.md` (+ docx/pdf) |
| `00_Expediente_Final/` | `Expediente_Tecnico_Equipo1.docx` y `.pdf` (portada, índice y entregables 1–6 con anexos) |
| `01_Analisis_Requerimientos/` | `Analisis_Requerimientos` |
| `02_Mapa_Navegacion/` | `Mapa_Navegacion` (diagrama Mermaid renderizado en DOCX/PDF) |
| `03_Maquetado/` | `Maquetado`, `Prototipo_Navegable.html`, `capturas_prototipo/` (11 PNG) |
| `04_Modelo_Datos/` | `Modelo_Datos` (diagrama ER renderizado) |
| `05_Instrumentos_Evaluacion/` | `Instrumentos_Evaluacion`, `Registro_Resultados`, `Cuestionario_SUS`, `calcular_sus.py`, `plantilla_respuestas_sus.csv` |
| `06_Desarrollo_Agentico/` | `Bitacora_Desarrollo_Agentico`, `Prompts`, `Problemas_y_Soluciones`, `Capturas_App`, `Historial_Git`, `capturas_app/` (22 PNG) |
| `07_APK/` | APK debug y release |
| `herramientas/` | `generar_expediente.py`, `exportar_word.ps1` |

> Los documentos originales del examen (`*.docx` de la raíz) **no se modificaron** (fecha original 2026-11-20 07:51).

## 10. Notas técnicas

- Para las pruebas se usó el emulador **Pixel_6** existente, arrancado con `-no-snapshot-save`. Se reinició una vez porque su interfaz del sistema dejó de responder. La rotación y el tamaño de fuente se **restauraron** a sus valores originales (rotación automática activada, `font_scale = 1.0`).
- No se instaló software ni se cambiaron variables de entorno del sistema; `JAVA_HOME` se definió solo dentro de cada comando. Edge se ejecutó con un perfil aislado (`herramientas/_build/edge-perfil`, excluido de Git).
- Los diagramas Mermaid se renderizaron cargando `mermaid.js` desde jsDelivr (solo se descargó la biblioteca; no se envió ningún dato del proyecto).
