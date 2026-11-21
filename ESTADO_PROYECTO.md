# ESTADO DEL PROYECTO — Agenda Académica (Equipo 1)

**Última actualización:** 2026-11-20 · **Herramienta:** Claude Code (Claude Opus 5.5), **autorizada por el profesor**
**Proyecto asignado:** Equipo 1 — Gestor de tareas y actividades académicas

---

## 1. Resumen

| Aspecto | Estado |
|---|---|
| App Android funcional (Kotlin + Compose + Material 3 + MVVM + Room) | ✅ Completa |
| Compilación con Gradle | ✅ `BUILD SUCCESSFUL` (assembleDebug y assembleRelease) |
| APK de depuración | ✅ `07_APK\AgendaAcademica-v1.0-debug.apk` |
| Pruebas automatizadas | ✅ 67/67 aprobadas (51 unitarias + 16 instrumentadas en el emulador) |
| Expediente (E1–E6) | ✅ Redactado · 🟡 con secciones que debe llenar el equipo (ver §6) |
| Pruebas con usuarios (SUS + tareas) | 🔴 **Pendiente**: instrumentos listos, faltan participantes reales |

## 2. Las cuatro funcionalidades mínimas del Equipo 1

| # | Funcionalidad (enunciado) | Estado | Dónde se ve en la app |
|---|---|---|---|
| 1 | Registrar tareas y descripciones | ✅ | Botón + / *Nueva tarea* → formulario (título 3–80, descripción ≤ 500); editar y eliminar desde el detalle |
| 2 | Clasificar por materia y prioridad | ✅ | Pestaña *Materias* (CRUD con color); materia y prioridad (Alta/Media/Baja) obligatorias en cada tarea; filtros por chip en *Tareas* |
| 3 | Establecer fechas de entrega | ✅ | Calendario (no permite fechas pasadas); "Faltan N días", "Vence hoy", vencidas en rojo; próximas entregas en el Inicio |
| 4 | Cambiar estados y consultar avances | ✅ | Pendiente → En progreso → Completada (detalle, edición o casilla en la lista), historial con fecha; Inicio (% general) y *Avances* (por estado, materia, prioridad y puntualidad) |

**Extras:** búsqueda y orden; historial de estados; tema oscuro; datos de ejemplo; persistencia sin conexión. La persistencia se verificó cerrando el proceso de la app.

## 3. Resultado de la compilación (evidencia)

Entorno: JDK 21.0.8 (`C:\Program Files\Android\openjdk\jdk-21.0.8`), Gradle 9.6.0 (wrapper), AGP 9.4.1, Kotlin 2.4.21, compileSdk 37, minSdk 26, targetSdk 36.

| Tarea Gradle | Resultado |
|---|---|
| `assembleDebug` | `BUILD SUCCESSFUL` (tras 2 correcciones de configuración; ver `06_Desarrollo_Agentico/Problemas_y_Soluciones.md` #4 y #5) |
| `testDebugUnitTest` | `BUILD SUCCESSFUL`: 51 pruebas, 0 fallidas |
| `connectedDebugAndroidTest` | `BUILD SUCCESSFUL`: 16 pruebas, 0 fallidas (emulador Pixel_6, API 37) |
| `assembleRelease` | `BUILD SUCCESSFUL` (firmado con la llave debug **solo para pruebas**) |

## 4. APK

| Archivo | Ruta exacta | Tamaño | SHA-256 |
|---|---|---|---|
| **APK de depuración (entrega)** | `C:\Users\thomm\Documents\ProyectoExamenMoviles\07_APK\AgendaAcademica-v1.0-debug.apk` | 19 523 814 B | `067bdf803200575a96c07e50098e39c84f1e417224aac78330495b474772c578` |
| Original generado por Gradle | `C:\Users\thomm\Documents\ProyectoExamenMoviles\app-android\app\build\outputs\apk\debug\app-debug.apk` | idem | idem |
| APK release (para medir rendimiento) | `C:\Users\thomm\Documents\ProyectoExamenMoviles\07_APK\AgendaAcademica-v1.0-release-firmaDebug.apk` | 13 096 352 B | `5333f2ef43ee430a885646bc2314c1d8ff7fd26f9f329aba9bccebe003895683` |

**Instalar en un teléfono:**
- Opción A: copiar el APK al teléfono → abrirlo → permitir "instalar apps de origen desconocido".
- Opción B: con depuración USB activada: `adb install -r AgendaAcademica-v1.0-debug.apk`.

> Si se instala la versión release sobre la debug (o al revés), funciona porque ambas usan la misma firma.

## 5. Pruebas ejecutadas

| Prueba | Resultado | Evidencia |
|---|---|---|
| 51 pruebas unitarias (validadores, avance, filtros, fechas) | ✅ 51/51 | `05_Instrumentos_Evaluacion/Registro_Resultados.md` §1 |
| 14 pruebas instrumentadas de Room/repositorio (CRUD, historial, FK RESTRICT/CASCADE, unicidad, agregados) | ✅ 14/14 | ídem |
| 2 pruebas de UI de extremo a extremo (materia → validación → tarea → completar → avance; materia duplicada) | ✅ 2/2 | ídem |
| Rendimiento: arranque en frío y memoria (emulador) | ✅ Medido: release ≈ 3.8 s (*Ajustes* del sistema ≈ 3.7 s en el mismo emulador); PSS ≈ 48 MB | `Registro_Resultados.md` §4 |
| Compatibilidad en el emulador Pixel 6 / API 37 | ✅ Instala, abre, temas claro y oscuro, sistema en inglés → app en español | §5 |
| Seguridad técnica (permisos, backup, exportados, no depurable, SQL parametrizado) | ✅ 8 de 9 criterios cumplen; S-9 (sin PIN) es una limitación declarada | §6 |
| Persistencia tras matar el proceso | ✅ Los datos permanecen | captura `18_persistencia_tras_reinicio_proceso.png` |
| 18 capturas reales de la app | ✅ | `06_Desarrollo_Agentico/capturas_app/` |

## 6. Pendientes para la entrega del lunes (responsable: equipo)

**Imprescindibles (rúbrica):**
1. 🔴 **Aplicar las pruebas de usabilidad** a ≥ 5 estudiantes ajenos al equipo (guion T1–T6 + `Cuestionario_SUS.md`), capturar las respuestas en `plantilla_respuestas_sus.csv`, ejecutar `python calcular_sus.py plantilla_respuestas_sus.csv` y pegar los resultados en `Registro_Resultados.md` §2. **(9 pts del instrumento y 7 de calidad.)**
2. 🔴 **Aplicar la lista de verificación funcional manual** PF-01 a PF-16, con nombre de quien probó y evidencias (§1.b).
3. 🟡 **Capturas del proceso agéntico** (terminal de Claude Code, compilación fallida y corregida, BUILD SUCCESSFUL, pruebas): ver `06_Desarrollo_Agentico/Prompts.md` §5.
4. 🟡 **Modificaciones propias del equipo:** cada integrante hace y registra al menos un cambio (`Bitacora_Desarrollo_Agentico.md` §6). Ayuda en la defensa.
5. 🟡 **Datos del equipo** (asignatura, grupo, integrantes) en las portadas: E1 y la portada del expediente.
6. 🟡 **Probar en un teléfono físico:** rendimiento (R-1 a R-3) y compatibilidad (C-1 a C-8) en `Registro_Resultados.md` §4 y §5.
7. 🟡 **Evaluación de interfaz** (rúbrica I-1 a I-12) por 2 evaluadores (§3).

**Decisiones que el equipo debe confirmar:**
8. **Formato final del expediente:** hoy está en Markdown. Si se pide DOCX/PDF, se puede convertir (por ejemplo, con Pandoc) o pedirlo al agente. Los diagramas Mermaid se exportan a PNG desde https://mermaid.live.
9. **¿Maquetas en Figma o Penpot?** Si el profesor las exige, recrear `03_Maquetado/Prototipo_Navegable.html` allí.
10. **¿Inicializar git?** Recomendable como evidencia del proceso; el agente no lo hizo porque no estaba confirmado.

**Preparación de la defensa** (presentación ante el profesor, 10 pts):
11. Repartir los temas entre los integrantes: problema y requerimientos / navegación y maquetas / modelo de datos / demo y pruebas / herramienta agéntica.
12. Ensayar la demo:
    1. Borrar los datos.
    2. Registrar una materia.
    3. Registrar una tarea mostrando la validación.
    4. Filtrar.
    5. Cambiar el estado y ver el historial.
    6. Revisar Avances.
    7. Intentar borrar una materia con tareas.
13. Prepararse para un cambio en vivo; ejemplos fáciles: añadir un color a `PaletaMaterias`, cambiar `TITULO_MAX`, agregar un texto.

## 7. Cómo abrir y ejecutar la app en Android Studio

1. Abrir **Android Studio** → *File > Open…* → seleccionar la carpeta **`C:\Users\thomm\Documents\ProyectoExamenMoviles\app-android`** (no la carpeta raíz).
2. Esperar el *Gradle Sync*. Usa el wrapper (Gradle 9.6.0, ya descargado) y el JDK embebido de Android Studio. Si se pide un JDK: *Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK* → `jbr-25` o el JDK 21 de `C:\Program Files\Android\openjdk\jdk-21.0.8`.
3. `local.properties` ya apunta al SDK (`C:/Users/thomm/AppData/Local/Android/Sdk`). Android Studio lo regenera si se abre en otra PC.
4. En la barra superior, elegir el dispositivo **Pixel_6** (o un teléfono conectado por USB con depuración activada) y pulsar ▶ **Run 'app'**.
5. **Primer uso:** en el Inicio, pulsar *Cargar datos de ejemplo* para la demostración, o *Registrar mis materias* para empezar desde cero.
6. **Ejecutar pruebas desde Android Studio:** clic derecho en `app/src/test/java` → *Run 'Tests in…'* (unitarias); o en `app/src/androidTest/java` con un emulador encendido (instrumentadas). **Aviso:** `FlujoPrincipalTest` **borra los datos** de la app en el dispositivo.
7. **Desde la terminal** (PowerShell, sin cambiar variables del sistema):
   ```powershell
   cd C:\Users\thomm\Documents\ProyectoExamenMoviles\app-android
   $env:JAVA_HOME = "C:\Program Files\Android\openjdk\jdk-21.0.8"
   .\gradlew.bat assembleDebug          # genera el APK
   .\gradlew.bat testDebugUnitTest      # pruebas unitarias
   .\gradlew.bat connectedDebugAndroidTest   # pruebas instrumentadas (emulador encendido)
   ```
8. **Inspeccionar la base de datos:** con la app debug en ejecución, abrir *View > Tool Windows > App Inspection > Database Inspector* (tablas `materias`, `tareas`, `historial_estados`).

## 8. Documentos generados

| Carpeta | Archivo | Entregable |
|---|---|---|
| raíz | `PLAN_PROYECTO.md` (fase 1), `ESTADO_PROYECTO.md` (este archivo) | Planeación y estado |
| `01_Analisis_Requerimientos/` | `Analisis_Requerimientos.md` | E1 (problema, actores, 19 RF, 10 RNF, 6 HU, 4 CU, 12 RN, alcance, limitaciones, trazabilidad) |
| `02_Mapa_Navegacion/` | `Mapa_Navegacion.md` | E2 (diagrama Mermaid, inventario, rutas y transiciones) |
| `03_Maquetado/` | `Maquetado.md`, `Prototipo_Navegable.html` | E3 (10 maquetas enlazadas, guía de estilo, maqueta → código) |
| `04_Modelo_Datos/` | `Modelo_Datos.md` (+ `app-android/app/schemas/…/1.json`) | E4 (ER, diccionario, justificación, CRUD) |
| `05_Instrumentos_Evaluacion/` | `Instrumentos_Evaluacion.md`, `Registro_Resultados.md`, `Cuestionario_SUS.md`, `calcular_sus.py`, `plantilla_respuestas_sus.csv` | E5 (6 instrumentos + resultados ejecutados) |
| `06_Desarrollo_Agentico/` | `Bitacora_Desarrollo_Agentico.md`, `Prompts.md`, `Problemas_y_Soluciones.md`, `capturas_app/` (18 PNG) | E6 |
| `07_APK/` | APK debug y release | App instalable |
| `app-android/` | Código fuente completo | App + código |

> Los documentos originales del examen (`*.docx`) **no se modificaron**.

## 9. Notas técnicas

- Para las pruebas y capturas se usó el emulador **Pixel_6** existente, arrancado con `-no-snapshot-save` (no se alteró su estado guardado). La app quedó instalada en él con los datos de ejemplo.
- No se instaló software ni se modificaron variables de entorno del sistema; `JAVA_HOME` se definió solo dentro de cada comando.
- Gradle descargó a la caché del usuario (`%USERPROFILE%\.gradle`) las dependencias que faltaban (Room, KSP, Navigation, Espresso 3.7.0, etc.). Esto es el comportamiento normal de cualquier compilación.
