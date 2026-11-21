# Prompts relevantes utilizados con Claude Code

Herramienta: Claude Code (CLI) · Modelo: Claude Opus 5.5 · Fecha: 2026-11-20.
Los prompts 1 y 2 son los que el equipo escribió al agente (se transcriben completos). La sección 3 resume las instrucciones de trabajo que el agente siguió dentro de la sesión.

---

## Prompt 1 — Análisis y planificación

```text
Actúa como un ingeniero senior especializado en desarrollo de aplicaciones móviles Android y desarrollo agéntico.

Estás trabajando en la carpeta ProyectoExamenMoviles. Encontrarás dos documentos DOCX: Actividad_Examen_Desarrollo_Agentico_Aplicaciones_Moviles.docx y 5-3 guia-evaluacion-sus.docx.

PRIMERA FASE: ANÁLISIS Y PLANIFICACIÓN.
1. Lee completamente ambos documentos. Puedes utilizar Python y python-docx para extraer el texto, instalando la dependencia si hace falta.
2. Identifica todos los requisitos funcionales, técnicos, herramientas obligatorias, entregables y criterios de evaluación.
3. Determina la arquitectura, lenguaje, framework y estructura del proyecto adecuados para cumplir las instrucciones.
4. Revisa qué herramientas de desarrollo están instaladas en mi computadora: Java, Android SDK, Gradle, Android Studio y otras necesarias.
5. Elabora un plan de desarrollo por fases, con una lista de verificación de cada criterio de evaluación.
6. Guarda el análisis en PLAN_PROYECTO.md.

REGLAS IMPORTANTES:
- No inventes requisitos ni omitas criterios de evaluación.
- No modifiques ni elimines los documentos originales.
- No instales software ni realices cambios fuera de esta carpeta sin consultarme.
- No empieces todavía a implementar la aplicación.
- Si encuentras ambigüedades, indícalas claramente.

Al finalizar, muéstrame el plan, los requisitos identificados y las decisiones que necesitas que confirme.
```

**Resultado:** `PLAN_PROYECTO.md` (requisitos, rúbrica con checklist, inventario de herramientas, plan por fases, 11 ambigüedades y 9 preguntas).

---

## Prompt 2 — Desarrollo, pruebas y documentación

```text
ACTUALIZACIÓN IMPORTANTE: El profesor ya autorizó oficialmente el uso de Claude Code para nuestro examen de Programación de Móviles.

PROYECTO ASIGNADO: Equipo 1 — Gestión de Tareas Académicas.

Quiero que continúes el desarrollo del proyecto a partir del estado actual, sin comenzar desde cero.

1. Revisa PLAN_PROYECTO.md, ESTADO_PROYECTO.md si existe, los dos documentos originales del examen y todos los archivos que hayas creado.
2. Identifica las cuatro funcionalidades mínimas exactas del Equipo 1 y comprueba cuáles están implementadas.
3. Continúa construyendo la aplicación Android nativa con Kotlin, Jetpack Compose, Material 3, MVVM y Room, respetando los requisitos del profesor.
4. Implementa las funcionalidades obligatorias con persistencia real, navegación funcional, formularios validados y una interfaz moderna, profesional, intuitiva y completamente en español.
5. Compila el proyecto utilizando Gradle. Si aparecen errores, investígalos, corrígelos y vuelve a compilar. No afirmes que compiló si no tienes evidencia del resultado.
6. Genera el APK de depuración y registra su ubicación exacta.
7. Completa el expediente digital: análisis de requerimientos, mapa de navegación, maquetado, modelo de datos, seis instrumentos de evaluación, bitácora de desarrollo agéntico, prompts y problemas solucionados.
8. Prepara las pruebas funcionales y de usabilidad, incluyendo el cuestionario SUS. No inventes resultados de usuarios ni capturas.
9. Crea o actualiza ESTADO_PROYECTO.md, indicando avances, pendientes, pruebas ejecutadas y pasos necesarios para abrir la aplicación en Android Studio.
10. Registra que Claude Code fue autorizado por el profesor. Documenta el uso real de la herramienta, incluyendo sus limitaciones y las decisiones humanas.

Trabaja de manera continua, priorizando primero una aplicación funcional y compilable, después las pruebas y la documentación.

Puedes crear y modificar archivos dentro del proyecto y ejecutar compilaciones y pruebas, sujeto a los permisos de Claude Code.

No modifiques los documentos originales, no elimines archivos ajenos al proyecto, no instales herramientas globales ni realices cambios permanentes del sistema sin consultarme.

No omitas requisitos de la rúbrica y no inventes evidencias. Si una decisión importante requiere mi intervención, detente y pregúntame.

Al terminar, entrégame:
- Funcionalidades implementadas.
- Resultado de compilación.
- Ruta exacta del APK.
- Documentos generados.
- Pruebas ejecutadas.
- Pendientes para la entrega del lunes.
```

---

## 3. Buenas prácticas de prompting que funcionaron

| Práctica | Ejemplo en los prompts | Efecto observado |
|---|---|---|
| Asignar un rol | "Actúa como un ingeniero senior…" | Respuestas con criterio técnico y justificaciones |
| Dividir en fases | Fase 1: solo análisis; fase 2: desarrollo | Se pudieron revisar el plan y las ambigüedades antes de escribir código |
| Restricciones explícitas | "No instales software…", "No inventes resultados" | El agente evitó instalar `python-docx` de forma global y separó lo ejecutado de lo pendiente |
| Exigir evidencia | "No afirmes que compiló si no tienes evidencia" | Cada afirmación del estado se respalda con la salida de Gradle o de adb |
| Fijar prioridades | "Primero app funcional, después pruebas y documentación" | Orden de trabajo claro |
| Definir el entregable final | Lista "Al terminar, entrégame…" | Resumen final verificable |

## 4. Prompts sugeridos para las siguientes iteraciones (opcional)

- "Explícame paso a paso cómo fluye un cambio de estado desde que toco el botón hasta que se guarda en Room." (preparación de la defensa)
- "Con estos resultados del SUS: [pegar la salida de calcular_sus.py], redacta la interpretación y propón 3 mejoras." (usar **solo** los datos reales)
- "Agrega una notificación un día antes de la fecha de entrega usando WorkManager." (mejora futura)

## 5. Capturas del proceso — PENDIENTE (equipo)

El examen pide "capturas del proceso". El agente **no puede capturar su propia terminal**. El equipo debe guardar en `06_Desarrollo_Agentico/capturas_proceso/` capturas de pantalla de:
1. La terminal de Claude Code con el prompt 1 y el resumen del plan.
2. La terminal con el prompt 2.
3. Un momento de compilación fallida y su corrección (por ejemplo, "Minimum supported Gradle version is 9.6.0").
4. El resultado `BUILD SUCCESSFUL`.
5. La salida de las pruebas (o los reportes HTML de Gradle abiertos en el navegador).
6. La app ejecutándose en Android Studio o en el teléfono.

> Las 18 capturas de `capturas_app/` son **de la app**, tomadas por el agente con `adb exec-out screencap` en el emulador; no son capturas del proceso de desarrollo.
