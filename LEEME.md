# Expediente digital — Equipo 1: Agenda Académica

Examen por equipos de **Programación de Móviles**: análisis, documentación y desarrollo agéntico de una aplicación móvil Android (gestor de tareas y actividades académicas). Herramienta agéntica: **Claude Code**, autorizada por el profesor.

## ¿Por dónde empezar?

| Quiero… | Abrir |
|---|---|
| Leer todo el expediente en un solo archivo | `00_Expediente_Final/Expediente_Tecnico_Equipo1.pdf` (o `.docx` para editar) |
| Instalar la app en un teléfono o emulador | `07_APK/AgendaAcademica-v1.0-debug.apk` |
| Ver el prototipo de las pantallas | `03_Maquetado/Prototipo_Navegable.html` (doble clic) |
| Abrir el código en Android Studio | carpeta `app-android/` (pasos en `ESTADO_PROYECTO.md` §7) |
| Saber qué está terminado y qué falta | `ESTADO_PROYECTO.md` |

## Organización de carpetas

| Carpeta | Entregable del examen | Contenido (cada documento en `.md` editable, `.docx` y `.pdf`) |
|---|---|---|
| `00_Expediente_Final/` | Expediente completo | Documento consolidado con portada, índice y todos los entregables |
| `01_Analisis_Requerimientos/` | 1. Análisis de requerimientos | Problema, actores, RF/RNF, historias de usuario, casos de uso, reglas de negocio, alcance y limitaciones |
| `02_Mapa_Navegacion/` | 2. Mapa de navegación | Diagrama, inventario de pantallas, rutas y transiciones |
| `03_Maquetado/` | 3. Maquetado | Prototipo HTML navegable y funcional, capturas de cada pantalla, guía de estilo |
| `04_Modelo_Datos/` | 4. Modelo de datos | Diagrama ER, diccionario de datos, justificación de Room, operaciones CRUD |
| `05_Instrumentos_Evaluacion/` | 5. Instrumentos de evaluación | 6 instrumentos, registro de resultados, cuestionario SUS, calculadora SUS y plantilla de respuestas |
| `06_Desarrollo_Agentico/` | 6. Desarrollo agéntico | Bitácora, prompts, problemas y soluciones, capturas reales de la app, historial Git |
| `07_APK/` | Aplicación instalable | APK debug (entrega) y APK release (solo para pruebas de rendimiento) |
| `app-android/` | Código fuente | Proyecto Gradle (Kotlin, Jetpack Compose, Material 3, MVVM, Room) y pruebas |
| `herramientas/` | — | Scripts que regeneran los DOCX/PDF desde el Markdown (`python herramientas/generar_expediente.py`) |

Documentos originales del examen (no modificados): `Actividad_Examen_Desarrollo_Agentico_Aplicaciones_Moviles.docx` y `5-3 guia-evaluacion-sus.docx`.

## Historial del desarrollo

Esta carpeta es un repositorio **Git local** (no publicado). Para ver la evidencia del proceso: `git log --stat`.
