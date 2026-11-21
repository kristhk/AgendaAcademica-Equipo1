# Problemas encontrados y soluciones aplicadas

Todos los problemas se detectaron porque el agente **ejecutó** la compilación, las pruebas o la app, y cada solución se verificó volviendo a ejecutar.

| # | Fase | Problema (evidencia real) | Causa | Solución | Verificación |
|---|---|---|---|---|---|
| 1 | Análisis | `ModuleNotFoundError: No module named 'docx'` | `python-docx` no está instalado y la regla era no instalar software sin consultar | Se leyó el DOCX como ZIP + XML con la biblioteca estándar (`zipfile`, `xml.etree`), incluidas las tablas | Texto completo extraído de ambos documentos |
| 2 | Análisis | `UnicodeEncodeError: 'charmap' codec can't encode character '\u2211'` | La consola de Windows usa cp1252; la guía SUS contiene el símbolo ∑ | Se ejecutó Python en modo UTF-8 (`-X utf8`) | Salida correcta |
| 3 | Configuración | `local.properties` con `sdk.dir=C\:\Users\…` (barras simples) | El formato `.properties` interpreta `\U` como un escape | Se usaron barras normales: `sdk.dir=C:/Users/…/Android/Sdk` | Gradle encuentra el SDK |
| 4 | Compilación | `Minimum supported Gradle version is 9.6.0. Current version is 9.5.1.` | AGP 9.4.1 requiere Gradle 9.6.0 | Se cambió `distributionUrl` del wrapper a `gradle-9.6.0-bin` (ya estaba en la caché local) | Pasa la configuración |
| 5 | Compilación | `checkDebugAarMetadata FAILED — 14 issues: Dependency 'androidx.navigation:navigation-compose-android:2.10.1' requires … compile against version 37 or later` | Las bibliotecas AndroidX recientes exigen compilar con API 37 | `compileSdk = 37` (la plataforma `android-37.0` ya estaba instalada); `targetSdk` se mantiene en 36 | **BUILD SUCCESSFUL**, sin errores ni advertencias de Kotlin |
| 6 | Pruebas unitarias | `FiltroTareasTest > ordenPorTitulo FAILED` | **Defecto real de la app:** `sortedBy { titulo.lowercase() }` compara por código Unicode, así que "Práctica" (á = U+00E1) quedaba después de "Proyecto" | Se ordena con `Collator.getInstance(es)` con fuerza `PRIMARY` (ignora acentos y mayúsculas) | 51/51 pruebas aprobadas |
| 7 | Pruebas de UI | `NoSuchMethodException: android.hardware.input.InputManager.getInstance` | Compose UI Test trae Espresso 3.5.0 por transitividad, que usa una API eliminada en Android 17 (API 37) | Se declaró `androidx.test.espresso:espresso-core:3.7.0` | El error desaparece |
| 8 | Pruebas de UI | `Expected exactly '1' node but could not find any node that satisfies: Text … 'Nueva materia'` | El texto del `ExtendedFloatingActionButton` no aparece en el árbol semántico fusionado | Se agregó `Modifier.testTag("fab_nueva_materia")` y la prueba lo busca por etiqueta | 16/16 pruebas instrumentadas aprobadas |
| 9 | Pruebas de UI | Advertencia de *deprecated* en `createAndroidComposeRule` | Compose recomienda la API v2 (`StandardTestDispatcher`) | Se importó `androidx.compose.ui.test.junit4.v2.createAndroidComposeRule` | Compila sin esa advertencia y las pruebas pasan |
| 10 | Diseño de pruebas | Una aserción `onNodeWithText("100%")` habría sido ambigua | En Avances, "100%" aparece en el avance general y en la puntualidad | Se cambió por una aserción de texto único ("1 de 1 tareas completadas") | La prueba pasa de forma determinista |
| 11 | Rendimiento | Arranque en frío del debug ≈ 11 s | Build debug (sin optimizar) en un emulador sin GPU | Se midió también el release y una app del sistema como referencia; se documentó que la meta debe verificarse en un dispositivo físico | Release ≈ 3.8 s ≈ *Ajustes* del sistema (3.7 s) |
| 12 | Capturas | `adb pull /sdcard/ui.xml` falló y algunos toques no encontraban los elementos | Git Bash convierte las rutas `/sdcard/...` a rutas de Windows; el emulador es lento y se leía la pantalla antes de que cambiara | `MSYS_NO_PATHCONV=1`, lectura con `adb exec-out cat`, esperas más largas y verificación del contenido antes de cada captura | 18 capturas correctas (revisadas visualmente) |
| 13 | Emulador | Diálogo "System UI isn't responding" | ANR de la interfaz del sistema por el renderizado por software (no es de la app) | Se cerró con *Wait* y se repitió la captura | Ninguna captura incluye el diálogo |

## Lecciones aprendidas

1. **Compilar y probar siempre:** 5 de los 13 problemas habrían llegado a la entrega si solo se hubiera revisado el código a simple vista.
2. **Las pruebas unitarias encuentran defectos reales** (problema 6), no solo confirman lo que ya funciona.
3. **El entorno importa:** las versiones del SDK, de Espresso y del emulador afectan tanto como el código propio.
4. **Medir con referencia:** sin comparar con una app del sistema, la cifra de 11 s se habría interpretado mal.
