# Entregable 6 — Diseño y desarrollo mediante desarrollo agéntico

**Aplicación:** Agenda Académica v1.0 · **Equipo 1** · **Asignatura:** Programación de Móviles

## 1. Herramienta y modelo de IA utilizados

| Concepto | Detalle |
|---|---|
| Herramienta agéntica | **Claude Code** (CLI de Anthropic) ejecutado en la terminal de Windows 11 del equipo |
| Modelo de IA | **Claude Opus 5.5** (`claude-opus-5-5`), identificado por la propia herramienta durante la sesión |
| **Autorización** | **El profesor autorizó oficialmente el uso de Claude Code para este examen** (lo informó el equipo al agente el 2026-11-20, antes de iniciar el desarrollo). Claude Code no está en la lista de ejemplos del enunciado (Qoder, OpenCode, Antigravity, DeepSeek), pero el enunciado admite "otras herramientas o agentes autorizados por el profesor" |
| Modo de trabajo | El agente lee archivos, ejecuta comandos (Gradle, adb, Python) y escribe código y documentos **dentro de la carpeta del proyecto**, con los permisos que concede el usuario en Claude Code |

### Justificación de la herramienta
1. **Agente con ejecución real:** no solo genera código; compila con Gradle, lee los errores, los corrige y vuelve a compilar, además de ejecutar las pruebas en el emulador. Esto encaja con el ciclo "generación → revisión → integración → pruebas" que pide el Entregable 6.
2. **Trabaja sobre el proyecto local** (sin copiar y pegar), así que la documentación se genera a partir del código real y coincide con la app construida (condición del examen, §8).
3. **Transparencia:** cada comando y su salida quedan visibles en la terminal, lo que permite al equipo revisar y aprender de cada paso.

## 2. Arquitectura tecnológica definida

| Capa | Tecnología | Archivos |
|---|---|---|
| Interfaz (View) | Jetpack Compose + Material 3 (BOM 2026.09.00), Material Icons Extended | `ui/**/*.kt` |
| Navegación | Navigation Compose 2.10.1 (rutas de texto + argumentos `Long`) | `ui/navegacion/Navegacion.kt` |
| Presentación (ViewModel) | AndroidX Lifecycle ViewModel 2.11, `StateFlow` + `combine` + `stateIn` | `ui/*/…ViewModel` (en el mismo archivo que su pantalla) |
| Dominio (reglas puras) | Kotlin puro, sin Android: validadores, cálculos de avance, filtros, formato de fechas | `domain/*.kt` |
| Datos | Patrón Repository + Room 2.8.4 (KSP 2.3.13) sobre SQLite | `data/RepositorioAcademico.kt`, `data/local/*.kt` |
| Inyección de dependencias | Manual: `ContenedorApp` + `viewModelFactory` | `AgendaApp.kt`, `ui/FabricaViewModels.kt` |
| Build | Gradle 9.6.0 (wrapper), AGP 9.4.1, Kotlin 2.4.21 (Kotlin integrado en AGP 9), catálogo de versiones | `gradle/libs.versions.toml` |
| Plataforma | `minSdk 26` (Android 8.0), `targetSdk 36`, `compileSdk 37` | `app/build.gradle.kts` |
| Pruebas | JUnit 4 (JVM), AndroidX Test + Room en memoria, Compose UI Test (v2), Espresso 3.7.0 | `app/src/test`, `app/src/androidTest` |

```
            ┌─────────────────────────── UI (Compose) ───────────────────────────┐
            │ Inicio · Tareas · Materias · Avances · DetalleTarea · Formulario   │
            └───────────────▲───────────────────────────────┬───────────────────┘
               StateFlow<UiState>                       eventos (onClick…)
            ┌───────────────┴──────────── ViewModels ───────▼───────────────────┐
            │ combinan Flows del repositorio + estado de la pantalla             │
            └───────────────▲───────────────────────────────┬───────────────────┘
                            │        domain/ (validadores, CalculadoraAvance,   │
                            │                 FiltroTareas: funciones puras)    │
            ┌───────────────┴────────── RepositorioAcademico ───▼───────────────┐
            │ reglas de negocio con BD (RN-05, 06, 07, 09, 10, 12)               │
            └───────────────▲───────────────────────────────┬───────────────────┘
                     Flow<List<…>>                        suspend
            ┌───────────────┴──────────── Room: DAOs ───────▼───────────────────┐
            │ materias · tareas · historial_estados (SQLite)                     │
            └────────────────────────────────────────────────────────────────────┘
```

**¿Por qué estas decisiones?**
- **Kotlin + Compose:** son el estándar actual de Android y la opción que pidió el equipo. Compose permite traducir las maquetas casi uno a uno.
- **MVVM:** separa la lógica de la interfaz; los ViewModels sobreviven a la rotación.
- **Room:** modelo relacional con llaves reales, consultas verificadas al compilar y actualización reactiva (véase `04_Modelo_Datos`).
- **Inyección manual en lugar de Hilt:** menos "magia" y más fácil de explicar en la defensa.

## 3. Estructura del proyecto

```
ProyectoExamenMoviles/
├── PLAN_PROYECTO.md · ESTADO_PROYECTO.md
├── 01_Analisis_Requerimientos/  02_Mapa_Navegacion/  03_Maquetado/
├── 04_Modelo_Datos/  05_Instrumentos_Evaluacion/  06_Desarrollo_Agentico/  07_APK/
└── app-android/                       ← proyecto Gradle (abrir esta carpeta en Android Studio)
    ├── settings.gradle.kts · build.gradle.kts · gradle.properties · gradle/libs.versions.toml
    ├── gradlew · gradlew.bat · gradle/wrapper/
    └── app/
        ├── build.gradle.kts
        ├── schemas/…/1.json             ← esquema de BD exportado por Room
        └── src/
            ├── main/AndroidManifest.xml, res/ (icono, tema, strings)
            ├── main/java/com/equipo1/agendaacademica/
            │   ├── AgendaApp.kt · MainActivity.kt
            │   ├── data/  RepositorioAcademico.kt · local/ (Entidades, Daos, Convertidores, AgendaDatabase)
            │   ├── domain/ Modelos · Validadores · CalculadoraAvance · FiltroTareas · FormatoFechas
            │   └── ui/    FabricaViewModels · navegacion/ · theme/ · componentes/
            │              inicio/ · tareas/ · materias/ · avances/ · detalle/ · formulario/
            ├── test/…        ValidadoresTest.kt · CalculosTest.kt       (51 pruebas unitarias)
            └── androidTest/… RepositorioAcademicoTest.kt · FlujoPrincipalTest.kt (16 instrumentadas)
```

Código fuente: **26 archivos Kotlin**, 22 de la app (2 994 líneas) y 4 de pruebas (485 líneas).

## 4. Bitácora cronológica de la sesión (2026-11-20)

| # | Fase | Qué hizo el agente | Decisión o intervención humana |
|---|---|---|---|
| 1 | Análisis | Extrajo el texto de los dos DOCX (con la biblioteca estándar de Python, sin instalar `python-docx`), identificó los requisitos y la rúbrica y revisó las herramientas instaladas (Android Studio, SDK 36/37, JDK 21/25, AVD Pixel_6). Generó `PLAN_PROYECTO.md` con 11 ambigüedades y 9 preguntas. | El equipo pidió **no instalar nada ni modificar el sistema sin consultar** y **no implementar todavía**. |
| 2 | Confirmación | — | El equipo informó: **proyecto asignado = Equipo 1**, **Claude Code autorizado por el profesor**, **stack: Kotlin + Compose + Material 3 + MVVM + Room**, prioridad: app funcional → pruebas → documentación. |
| 3 | Configuración | Creó el proyecto Gradle a mano (sin el asistente de Android Studio), usando las versiones que ya estaban en la caché local para evitar descargas innecesarias. Definió `JAVA_HOME` **solo dentro de sus comandos** (sin cambiar variables del sistema). | — |
| 4 | Implementación | Escribió las capas de dominio, datos (3 entidades, 2 DAO, repositorio), 6 pantallas con sus ViewModels, el tema, la navegación y el icono. | — |
| 5 | Compilación | 1.ª compilación: falló porque AGP 9.4.1 exige Gradle 9.6.0 → actualizó el wrapper. 2.ª: falló porque las bibliotecas exigen `compileSdk 37` → lo ajustó. 3.ª: **BUILD SUCCESSFUL** sin errores ni advertencias de Kotlin. | — |
| 6 | Pruebas unitarias | 51 pruebas; **1 falló y detectó un defecto real** (orden alfabético con acentos) → corrigió con `Collator`. Después: 51/51. | — |
| 7 | Pruebas instrumentadas | Arrancó el emulador sin ventana (`-no-snapshot-save`, sin alterar el AVD). Las 14 de repositorio pasaron a la primera. Las de UI fallaron 2 veces por causas del entorno (Espresso con API 37 y la semántica del FAB) → corrigió. Resultado: 16/16. | — |
| 8 | Rendimiento y capturas | Midió el arranque en frío (debug ≈ 11 s; release ≈ 3.8 s; *Ajustes* del sistema ≈ 3.7 s en el mismo emulador). Para medir el release lo firmó con la llave de depuración (solo para pruebas). Tomó 18 capturas reales con `adb screencap`. | — |
| 9 | Documentación | Generó los entregables 1–6, el registro de resultados (solo lo ejecutado), el cuestionario SUS, la calculadora SUS (verificada con valores de referencia) y `ESTADO_PROYECTO.md`. | El equipo exigió **no inventar resultados de usuarios ni capturas**. |

## 5. Generación, revisión e integración del código

- **Generación:** el agente escribió cada archivo completo a partir del análisis de requerimientos y del modelo de datos.
- **Revisión automática:** el compilador de Kotlin y el procesador de Room (KSP) verifican los tipos y el SQL al compilar; además se ejecutaron 67 pruebas automatizadas.
- **Revisión visual:** el agente revisó las capturas reales de cada pantalla (claro y oscuro) antes de documentarlas.
- **Integración de interfaces y modelo de datos:** las pantallas observan los `Flow` de Room a través del repositorio. Cualquier cambio (por ejemplo, completar una tarea) se refleja automáticamente en el Inicio, en Tareas, en Materias y en Avances sin recargar.
- **Validación de requerimientos:** véase la matriz de trazabilidad en `01_Analisis_Requerimientos` (§12) y `05_Instrumentos_Evaluacion/Registro_Resultados.md`.

## 6. Modificaciones realizadas por el equipo

> **PENDIENTE — debe llenarlo el equipo.** El examen pide documentar las modificaciones hechas por los integrantes. Hasta este momento todo el código lo generó el agente con instrucciones del equipo. Antes de la entrega, se recomienda que cada integrante haga **al menos un cambio propio** (por ejemplo, ajustar textos, agregar una materia de ejemplo o cambiar un color) y lo registre aquí. Esto también prepara la defensa, donde el profesor puede pedir cambios en vivo.

| Integrante | Archivo / funcionalidad | Cambio realizado | Motivo |
|---|---|---|---|
| | | | |

## 7. Decisiones tomadas por el agente que el equipo debe validar

| Decisión | Alternativa | Motivo |
|---|---|---|
| Expediente en **Markdown** (en lugar de DOCX/PDF) | Convertir a DOCX/PDF | El formato no estaba confirmado; Markdown se versiona y convierte fácilmente (por ejemplo, con Pandoc o pegándolo en Word) |
| Maquetas como **HTML navegable** | Figma / Penpot | El agente no puede operar Figma; el examen acepta "otras herramientas" |
| **Sin login, PIN ni roles** | PIN local | La app es personal y monousuario; se declara como limitación (S-9) |
| `minSdk 26` (Android 8.0) | 24 | Permite usar `java.time` sin *desugaring*; cubre la gran mayoría de los dispositivos actuales |
| Datos de ejemplo cargables desde la app | Precarga automática | La demostración es rápida sin mezclar datos ficticios con los reales (RN-12) |
| Release firmado con la llave debug | Keystore propio | Solo para medir el rendimiento; **no es apto para publicarse en una tienda** |
| **No se inicializó git** | `git init` + commits | Quedó como pregunta abierta en el plan; es recomendable como evidencia |

## 8. Limitaciones de la herramienta observadas

1. **No puede aplicar las pruebas con usuarios:** el SUS y las tareas observadas requieren personas reales. El agente solo preparó los instrumentos y la calculadora.
2. **No puede tomar capturas de su propia sesión** (la terminal de Claude Code): el equipo debe hacerlas (véase `Prompts.md`).
3. **No maneja Figma o Penpot:** las maquetas se hicieron en HTML.
4. **No es infalible:** en este proyecto el agente se equivocó en la versión de Gradle requerida por AGP, en el `compileSdk`, en el orden alfabético con acentos y en dos detalles de las pruebas de UI. Todos se detectaron porque el agente compiló y probó, **no por revisión humana**. Por eso la verificación con pruebas es indispensable.
5. **Depende del entorno:** el emulador sin GPU distorsiona las mediciones de rendimiento; las cifras finales deben tomarse en un dispositivo físico.
6. **Conocimiento de versiones:** el agente consultó el repositorio de Maven de Google para conocer las versiones vigentes de las bibliotecas, en lugar de suponerlas.
7. **Comprensión humana:** el código generado debe estudiarlo el equipo. El profesor hará preguntas individuales (§10 del examen), así que delegar sin entender es un riesgo para la calificación.
