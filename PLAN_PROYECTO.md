# PLAN DEL PROYECTO — Examen: Análisis, documentación y desarrollo agéntico de una aplicación móvil

> Documento de análisis y planificación (Fase 1). **Todavía no se ha implementado nada.**
> Fuentes analizadas (sin modificar):
> - `Actividad_Examen_Desarrollo_Agentico_Aplicaciones_Moviles.docx` (en adelante **ACT**)
> - `5-3 guia-evaluacion-sus.docx` (en adelante **SUS**)
>
> Fecha del análisis: 2026-11-20. El texto se extrajo con la biblioteca estándar de Python (zipfile + XML) leyendo `word/document.xml`, incluidas las tablas; ninguno de los dos documentos tiene imágenes, notas al pie ni comentarios.

---

## 1. Resumen de los documentos

### 1.1 ACT — Enunciado del examen
- Examen **por equipos (6 equipos)**. Cada equipo analiza un problema real y construye una **aplicación móvil funcional** usando **herramientas de desarrollo agéntico** (Agentic Coding).
- Herramientas mencionadas: **Qoder, OpenCode, Antigravity, DeepSeek** "y otras herramientas o agentes autorizados por el profesor". **La elección de la herramienta debe justificarse.**
- El profesor asigna un proyecto a cada equipo; el equipo debe **ampliar los requerimientos y definir el alcance** antes de desarrollar.
- 6 entregables, 100 puntos (50 de documentación + 50 de aplicación), defensa presencial ante el profesor.

### 1.2 SUS — Guía metodológica del cuestionario System Usability Scale
- Instrumento estandarizado (Brooke, 1986) de **10 reactivos Likert de 5 puntos**; los impares son positivos y los pares negativos.
- Protocolo: se aplica **justo después** de que el participante termine las tareas; respuestas espontáneas; **los 10 ítems son obligatorios** (si hay duda, se marca 3 = Neutral).
- Cálculo: impares → `respuesta − 1`; pares → `5 − respuesta`; suma (0–40) × **2.5** = puntaje 0–100.
- Subescalas: **Usabilidad** (ítems 1, 2, 3, 5, 6, 7, 8, 9; suma × 3.125) y **Aprendizaje** (ítems 4 y 10; suma × 12.5).
- Interpretación: el promedio histórico es **68**. **No es un porcentaje**. Tabla de grados A+ … F con percentil, adjetivo, aceptabilidad y categoría NPS.
- Recomienda combinarlo con **pruebas de usabilidad basadas en tareas observadas** y, opcionalmente, con el **modelo TAM**.

---

## 2. Proyectos posibles (ACT §3) — requisitos mínimos de cada uno

| Equipo | Proyecto | Funciones mínimas indicadas |
|---|---|---|
| 1 | Gestor de tareas y actividades académicas | Registrar tareas y descripciones; clasificar por materia y prioridad; fechas de entrega; cambiar estados y consultar avances |
| 2 | Sistema móvil de inventario y ventas | Registrar productos; controlar inventario; registrar ventas; consultar movimientos y existencias |
| 3 | Sistema de citas y reservaciones | Registrar clientes; catálogo de servicios; calendario de citas; consultar, modificar y cancelar reservaciones |
| 4 | Administrador de gastos personales | Registrar ingresos y gastos; clasificar por categorías; presupuestos mensuales; reportes y estadísticas |
| 5 | Sistema de pedidos para restaurante | Consultar catálogo; carrito de compras; registrar pedidos; seguimiento del estado de preparación y entrega |
| 6 | Reportes de incidencias y mantenimiento | Registrar incidencias; describir y **fotografiar** el problema; identificar **ubicación**/sitio; seguimiento del estado de atención |

> ⚠️ **No sé qué proyecto le tocó a tu equipo.** Es la decisión principal que tienes que confirmar (ver §9).

---

## 3. Requisitos identificados

### 3.1 Objetivos de aprendizaje (ACT §2), que también funcionan como requisitos implícitos
1. Identificar y analizar un problema que se pueda resolver con una app móvil.
2. Documento de análisis de requerimientos funcionales y no funcionales.
3. Mapa de navegación y arquitectura de información.
4. Maquetas (mockups) y prototipos de interfaces móviles.
5. Modelo de datos para almacenar y administrar la información.
6. Diseñar **y aplicar** instrumentos de evaluación de la calidad tecnológica.
7. Usar herramientas de desarrollo agéntico para construir una app funcional.
8. Documentar el proceso y justificar las decisiones tecnológicas.

### 3.2 Entregables (ACT §4 y §5)

**E1. Análisis de requerimientos** → *Documento técnico*
- Descripción del problema y justificación de la aplicación.
- Identificación de usuarios y actores.
- Requerimientos funcionales (RF) y no funcionales (RNF).
- Historias de usuario o casos de uso.
- Reglas de negocio, alcance y limitaciones.

**E2. Mapa de navegación** → *Diagrama*
- Representación gráfica de la estructura y la navegación.
- Pantalla de inicio, menú principal y pantallas secundarias.
- Formularios de captura, consultas y reportes.
- Rutas de navegación, acciones y transiciones principales.

**E3. Maquetado de la aplicación** → *Mockups y prototipo*
- Diseño de todas las pantallas principales.
- Distribución de botones, formularios y componentes.
- Colores, tipografías e iconografía.
- Prototipo visual de las operaciones principales.
- Herramientas: Figma, Penpot u otras.

**E4. Modelo de datos** → *Diagrama y diccionario*
- Diagrama entidad-relación.
- Entidades, atributos, llaves primarias (PK) y foráneas (FK).
- Relaciones entre entidades.
- Diccionario de datos.
- Justificación de la tecnología de almacenamiento.
- Explicación de cómo se almacenan, consultan, modifican y eliminan los datos (CRUD).

**E5. Instrumentos de evaluación tecnológica** → *Instrumentos aplicados y resultados*
- Instrumentos a diseñar:
  1. Lista de verificación funcional (contra los RF).
  2. Pruebas de usabilidad (facilidad de uso y navegación).
  3. Evaluación de la interfaz (consistencia y presentación visual).
  4. Pruebas de rendimiento (tiempos de respuesta).
  5. Pruebas de compatibilidad en el dispositivo objetivo.
  6. Evaluación de seguridad (validación de datos, accesos, protección de la información).
- **Cada instrumento debe especificar:** objetivo, criterios o indicadores, escala, procedimiento y formato de registro.
- **Obligatorio aplicar** al menos las pruebas **funcionales** y de **usabilidad**, con **evidencias y resultados**.
- La guía SUS se entregó como apoyo para el instrumento de usabilidad.

**E6. Diseño y desarrollo mediante desarrollo agéntico** → *App funcional, código fuente y evidencias*
- Definición de la arquitectura tecnológica.
- Selección del lenguaje, framework, herramientas y agente de desarrollo.
- Preparación de las instrucciones (prompts) para el agente.
- Generación, revisión e integración del código fuente.
- Integración de las interfaces con el modelo de datos.
- Pruebas, corrección de errores y **generación de la app para Android**.
- Validación de los requerimientos implementados.
- Documentar: herramienta y **modelo de IA** usado (cuando se pueda identificar); prompts relevantes; **capturas del proceso**; **modificaciones hechas por el equipo**; problemas y soluciones; estructura del proyecto y código fuente.

**Formato de entrega:** **un solo expediente digital por equipo**, organizado en carpetas y acompañado de la app **instalable o ejecutable** (en Android, un APK).

### 3.3 Condiciones generales (ACT §8)
- Trabajo en 6 equipos; **todos** participan en el análisis, el diseño y el desarrollo.
- **La documentación debe corresponder a la app realmente construida.**
- La app debe **ejecutarse en un dispositivo Android o en un emulador**.
- Se entrega el **código fuente**.
- **No** se aceptan diseños estáticos ni capturas de pantalla en lugar de la app.
- Se valora la creatividad, la solución del problema y el **uso adecuado de la IA**.
- Cada equipo debe poder **explicar y justificar** sus decisiones tecnológicas.

### 3.4 Presentación y defensa (ACT §7 y §10)
1. Explicar el problema. 2. Presentar los requerimientos y el mapa de navegación. 3. Mostrar las maquetas y cómo se llevaron al desarrollo. 4. Explicar el modelo de datos. 5. Demostrar las operaciones principales. 6. **Ejecutar pruebas funcionales en vivo.** 7. Explicar las herramientas agénticas que se usaron. 8. Responder preguntas técnicas.
- El profesor puede **pedir modificaciones en vivo** u operaciones específicas, y hacer **preguntas individuales** para comprobar que no todo se delegó a la IA. → El código debe ser **simple, legible y explicable**, y el equipo debe estudiarlo.

---

## 4. Criterios de evaluación (ACT §6), con su lista de verificación

### A. Documentación — 50 pts

| Criterio | Pts | Checklist de cumplimiento |
|---|---|---|
| Análisis de requerimientos | 10 | ☐ Problema y justificación ☐ Usuarios/actores ☐ RF numerados ☐ RNF numerados ☐ Historias de usuario o casos de uso ☐ Reglas de negocio ☐ Alcance ☐ Limitaciones ☐ Trazabilidad RF ↔ pantalla ↔ prueba |
| Mapa de navegación | 7 | ☐ Diagrama gráfico ☐ Inicio + menú principal + secundarias ☐ Formularios, consultas y reportes ☐ Rutas, acciones y transiciones ☐ Coincide con la app real |
| Maquetado | 8 | ☐ Todas las pantallas principales ☐ Distribución de componentes ☐ Paleta, tipografía e iconos documentados ☐ Prototipo navegable de las operaciones principales ☐ Comparación maqueta vs. app final |
| Modelo de datos | 8 | ☐ Diagrama ER ☐ Entidades, atributos, PK y FK ☐ Relaciones con cardinalidad ☐ Diccionario de datos (tipo, longitud, nulos, restricciones, descripción) ☐ Justificación del almacenamiento ☐ Explicación del CRUD |
| Instrumentos de evaluación tecnológica | 9 | ☐ 6 instrumentos diseñados ☐ Cada uno con objetivo, indicadores, escala, procedimiento y formato de registro ☐ Funcional **aplicada** con resultados ☐ Usabilidad **aplicada** (SUS + tareas) con resultados y su interpretación ☐ Evidencias (capturas/formularios llenos) |
| Documentación del desarrollo agéntico | 8 | ☐ Arquitectura ☐ Justificación del lenguaje/framework/herramientas/agente ☐ Herramienta y modelo de IA ☐ Prompts relevantes ☐ Capturas del proceso ☐ Modificaciones del equipo ☐ Problemas y soluciones ☐ Estructura del proyecto |

### B. Aplicación móvil — 50 pts

| Criterio | Pts | Checklist de cumplimiento |
|---|---|---|
| Implementación de los RF | 15 | ☐ Todas las funciones mínimas del proyecto asignado ☐ RF ampliados implementados ☐ Cada RF verificado en la checklist funcional |
| Funcionamiento y manejo de datos | 10 | ☐ CRUD completo y persistente (sobrevive al cierre de la app) ☐ Validaciones ☐ Sin cierres inesperados ☐ Datos de ejemplo para la demo |
| Interfaz gráfica y navegación | 8 | ☐ Consistente con las maquetas ☐ Navegación clara y botón "atrás" correcto ☐ Material Design 3 ☐ Estados vacíos y mensajes de error |
| Calidad tecnológica y pruebas | 7 | ☐ Pruebas unitarias (lógica/ViewModel) ☐ Pruebas instrumentadas (Room/UI) ☐ Rendimiento medido ☐ Compatibilidad (emulador + dispositivo físico si lo hay) ☐ Seguridad/validación |
| Presentación y explicación técnica | 10 | ☐ Guion de la defensa ☐ Demo ensayada ☐ Cada integrante domina una parte ☐ Preparados para cambios en vivo |

---

## 5. Herramientas instaladas en la computadora (verificado el 2026-11-20)

| Herramienta | Estado | Detalle |
|---|---|---|
| Android Studio | ✅ Instalado | `C:\Program Files\Android\Android Studio`, build AI-262.9437 (2026) |
| JDK | ⚠️ Instalado pero **no está en el PATH** | JBR 25.0.3 (`...\Android Studio\jbr`) y OpenJDK 21.0.8 (`C:\Program Files\Android\openjdk\jdk-21.0.8`). `java`/`javac` no se encuentran desde la terminal y `JAVA_HOME` está vacío |
| Android SDK | ✅ Instalado, **no está en el PATH** | `%LOCALAPPDATA%\Android\Sdk`. Platforms android-36 y android-37.0; build-tools 35.0.0 y 36.0.0; cmdline-tools latest; platform-tools (adb 1.0.41). `ANDROID_HOME` está vacío |
| Emulador | ✅ | v37.2.12; AVD **Pixel_6** (x86_64, android-37.2, 2 GB de RAM). Aceleración **WHPX disponible** |
| Gradle | ✅ vía wrapper | No hay `gradle` global (no hace falta); en la caché están las distribuciones 8.14.3, 9.5.1 y 9.6.0 |
| Git | ✅ | 2.56.1 (la carpeta **no** es todavía un repositorio git) |
| Node.js | ✅ | v24.22.3 (útil para generar diagramas/mockups si hiciera falta) |
| Python | ✅ | 3.11.9 (sin `python-docx`; no lo instalé y usé la biblioteca estándar) |
| Flutter | ❌ | No instalado (no se necesita con la arquitectura propuesta) |
| Hardware | ✅ | 15.6 GB de RAM, 136 GB libres en C: |

**Conclusión:** no hace falta instalar nada. Para compilar desde la terminal basta con definir `JAVA_HOME` y `ANDROID_HOME` **solo en la sesión** (sin cambiar variables del sistema) y escribir `local.properties` dentro del proyecto con `sdk.dir`. Android Studio funciona sin cambios.

---

## 6. Decisión técnica propuesta (pendiente de tu confirmación)

| Aspecto | Propuesta | Justificación |
|---|---|---|
| Plataforma | Android nativo | El examen exige ejecutarse en Android o en un emulador; el entorno ya está listo |
| Lenguaje | **Kotlin** | Lenguaje oficial de Android y el mejor soportado por Android Studio |
| UI | **Jetpack Compose + Material 3** | Declarativo, menos código, y con `@Preview` se pueden comparar maquetas con la implementación |
| Arquitectura | **MVVM + patrón Repository** (una sola capa `data` / `ui`, sin sobreingeniería) | Separación clara y fácil de explicar en la defensa |
| Navegación | Navigation Compose | Corresponde directamente al mapa de navegación (E2) |
| Persistencia | **Room (SQLite local)** | Modelo relacional ⇒ diagrama ER con PK/FK reales; funciona sin conexión y no necesita backend |
| Inyección de dependencias | Manual (contenedor simple) en lugar de Hilt | Menos "magia" y más fácil de explicar a preguntas individuales |
| Asincronía | Coroutines + Flow | Estándar con Room y Compose |
| Pruebas | JUnit4 (ViewModel/Repository), pruebas instrumentadas de Room y Compose UI Test | Respaldan el criterio "Calidad tecnológica y pruebas" |
| Build | Gradle wrapper (Kotlin DSL) + version catalog; `minSdk 26`, `targetSdk 36` (platform instalada) | Compatibilidad amplia; usa las platforms ya instaladas |
| Entregable instalable | APK debug (y release firmado si se pide) | "Aplicación instalable" |

**Estructura de la carpeta del expediente (propuesta):**
```
ProyectoExamenMoviles/
├── PLAN_PROYECTO.md
├── (documentos originales sin tocar)
├── 01_Analisis_Requerimientos/
├── 02_Mapa_Navegacion/
├── 03_Maquetado/
├── 04_Modelo_Datos/
├── 05_Instrumentos_Evaluacion/      (instrumentos, formatos llenos, resultados SUS)
├── 06_Desarrollo_Agentico/          (prompts, bitácora, capturas, problemas/soluciones)
├── 07_APK/
└── app-android/                     (código fuente del proyecto Gradle)
```

---

## 7. Plan de desarrollo por fases

| Fase | Contenido | Entregable | Puntos asociados |
|---|---|---|---|
| **0. Confirmaciones** | Resolver las decisiones de §9 | — | — |
| **1. Análisis** | Problema, actores, RF/RNF ampliados, historias de usuario/casos de uso, reglas de negocio, alcance y limitaciones; matriz de trazabilidad | E1 | 10 |
| **2. Diseño de navegación** | Diagrama (Mermaid → imagen) con pantallas, rutas, acciones y transiciones | E2 | 7 |
| **3. Maquetado** | Guía visual (paleta, tipografía, iconos), mockups de todas las pantallas y prototipo navegable | E3 | 8 |
| **4. Modelo de datos** | Diagrama ER, diccionario, justificación de Room y explicación del CRUD | E4 | 8 |
| **5. Diseño de instrumentos** | 6 instrumentos con objetivo, indicadores, escala, procedimiento y formato; incluye SUS + guion de tareas observadas | E5 (diseño) | 9 (parte) |
| **6. Implementación agéntica** | Proyecto Gradle; capa de datos (Room); repositorios; ViewModels; pantallas; navegación; datos semilla; **bitácora de prompts y capturas en cada iteración** | E6 + app | 15 + 10 + 8 |
| **7. Pruebas** | Unitarias e instrumentadas; checklist funcional aplicada; rendimiento (tiempos de arranque y operaciones); compatibilidad (emulador Pixel_6 / dispositivo físico); seguridad/validación | E5 (aplicación) | 7 + 9 (parte) |
| **8. Usabilidad con usuarios reales** | El equipo aplica las tareas + SUS a participantes; **yo hago los cálculos** (global, subescalas y grado) con los datos reales | E5 (resultados) | 9 (parte) |
| **9. Cierre** | APK, documentación del desarrollo agéntico, ajuste para que la documentación coincida con la app final, guion de la defensa y preguntas técnicas probables | Expediente + APK | 8 + 10 |

**Control de calidad entre fases:** al final de cada fase reviso que la documentación siga coincidiendo con la app (condición explícita de ACT §8).

---

## 8. Ambigüedades detectadas

1. **Proyecto asignado:** ACT dice que lo asigna el profesor; no sé cuál es el de tu equipo.
2. **Herramienta agéntica autorizada:** ACT nombra Qoder, OpenCode, Antigravity y DeepSeek, "y otras autorizadas por el profesor". **Claude Code no aparece en la lista**: si es la herramienta principal, conviene confirmar que el profesor la autoriza y justificarla en E6.
3. **Número de pruebas aplicadas:** solo es obligatorio *aplicar* las funcionales y de usabilidad; las otras cuatro (interfaz, rendimiento, compatibilidad, seguridad) solo dicen "diseñar". Propuesta: diseñar las 6 y aplicar también las técnicas (que son baratas) para sumar en "Calidad tecnológica y pruebas".
4. **SUS:** ACT no dice que SUS sea obligatorio; la guía se entregó como apoyo. Tampoco se fija un **número mínimo de participantes** (la práctica habitual es ≥ 5). Los resultados deben venir de **personas reales**: **no inventaré respuestas**.
5. **"Historias de usuario o casos de uso":** se puede elegir; propongo historias de usuario con criterios de aceptación y casos de uso para los flujos principales.
6. **Herramienta de maquetado:** Figma/Penpot "u otras". Yo no puedo manejar Figma directamente; puedo generar mockups como imágenes/HTML o con `@Preview` de Compose, o ustedes los hacen en Figma.
7. **Formato del expediente:** no se especifica (DOCX, PDF, Markdown). Probablemente se espera un documento formal (DOCX/PDF).
8. **Tipo de almacenamiento:** no se exige local ni en la nube. Para los equipos 3, 5 y 6 (varios roles: cliente/cocina, reportante/mantenimiento) podría esperarse multiusuario; con Room local se simularía con **selección de rol dentro de la app** y se declararía como limitación.
9. **Autenticación/roles:** no se pide explícitamente; "evaluación de seguridad: accesos" sugiere al menos un control de acceso básico (p. ej., un PIN o login local).
10. **Integrantes:** la defensa es por equipo con preguntas individuales; en la documentación hay que poner integrantes, grupo y asignatura (aparecen campos en blanco en ACT).
11. **Contradicción menor en SUS:** la tabla de interpretación marca 65.0–74.0 como "Marginal / Aceptable" y 51.7–64.9 como "Marginal"; se usará tal cual con la cita a la guía.

---

## 9. Decisiones que necesito que confirmes

1. **¿Qué proyecto (equipo 1–6) les asignaron?**
2. **¿Herramienta agéntica?** ¿Claude Code (con autorización del profesor), alguna de las que se listan, o una combinación?
3. **¿Aceptas el stack** Kotlin + Jetpack Compose + MVVM + Room (local, sin backend)?
4. **¿Maquetas?** (a) las genero yo como imágenes/HTML navegable, (b) ustedes en Figma/Penpot, (c) ambas.
5. **¿Formato del expediente?** DOCX, PDF o Markdown (o DOCX + PDF).
6. **¿Datos del equipo** (asignatura, grupo, nombre del equipo, integrantes) para las portadas?
7. **¿Dispositivo físico Android disponible** para la prueba de compatibilidad, o solo el emulador Pixel_6?
8. **¿Inicializo un repositorio git** en esta carpeta? (Sirve como evidencia del proceso agéntico.)
9. ¿Autorizas que defina `JAVA_HOME`/`ANDROID_HOME` **solo de forma temporal en mis comandos** (sin modificar el sistema) para compilar desde la terminal?
