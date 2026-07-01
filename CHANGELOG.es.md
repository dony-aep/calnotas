# Registro de cambios

[English](CHANGELOG.md) | **Español**

Todos los cambios relevantes de CalNotas se documentan en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/),
y este proyecto sigue [Versionado Semántico](https://semver.org/lang/es/spec/v2.0.0.html).

## [Sin publicar]

### Añadido
- Panel de predicción de nota para la calculadora por defecto: aparece automáticamente a medida que se ingresan notas, mostrando la nota uniforme mínima necesaria en todos los campos vacíos restantes para alcanzar la nota de aprobación (3.0).
- Sugerencia de predicción por campo (`Sugerido: ≥ X.X`) mostrada debajo de cada campo vacío como referencia rápida.
- Sugerencia de nota de seguridad mostrada debajo del siguiente campo vacío relevante cuando esa única nota por sí sola puede garantizar el aprobado (asumiendo 0.0 en el resto de campos).
- Documentación de la predicción de nota añadida a la pantalla de Ayuda, en la sección de la calculadora por defecto.
- Se porta la funcionalidad de predicción de nota desde la aplicación web (CalNotas Web v4.6.0) para mantener ambas aplicaciones a la par.

### Cambiado
- El diálogo de selección de tema ahora escalona la selección en lugar de aplicarla de inmediato: elegir una opción solo previsualiza la selección, y esta se aplica únicamente al confirmar con los nuevos botones "Cancelar"/"Aceptar".

### Corregido
- Se corrigió que el contenido se desplazara brevemente hacia arriba y volviera a su posición al cambiar de tema (claro/oscuro/sistema) desde Ajustes. El fix de `locale` para el parpadeo de idioma no cubría `uiMode`, por lo que un cambio de tema real seguía generando una recreación completa de la Activity (invisible, pero el estado de scroll/altura de la barra superior colapsable se reseteaba brevemente durante la reconstrucción, causando el salto). Se añadió `uiMode` a `android:configChanges` de `MainActivity` junto a `locale`, de modo que los cambios de tema se manejan igual: sin recreación, solo recomposición.
- La calculadora por defecto ahora redondea cada subtotal de "Corte" a 2 decimales antes de sumar, coincidiendo con el ejemplo resuelto ya documentado en la pantalla de Ayuda y con el cálculo de la aplicación web (antes se sumaban los productos sin redondear).
- Se corrigieron colores de bajo contraste/desvaídos en filas de listas (Ajustes, Acerca de, Ayuda) y en tarjetas de información/resultado con color (destacados de Ayuda, tarjetas de resultado y predicción de ambas calculadoras, pantalla de Actualización). Las filas de listas ahora usan `surfaceContainerHigh` en lugar de mezclarse con el tono de su tarjeta contenedora, y las tarjetas con color usan colores de contenedor sólidos con colores de texto `on*Container` explícitos en lugar de contenedores con transparencia combinados con texto calibrado a opacidad completa.
- Se corrigieron dobles signos de porcentaje literales (`%%`) que aparecían en varios textos de la pantalla de Ayuda que no tenían argumentos de formato para activar el colapso de escape de Android.
- Se corrigió un destello/parpadeo al iniciar en el que la aplicación se renderizaba brevemente en el idioma y tema del sistema antes de cambiar a la preferencia guardada. Los cambios de idioma ya no generan una recreación completa de la Activity en un inicio en frío (se adopta `autoStoreLocales` de AppCompat, que restaura el idioma guardado antes de que la Activity siquiera se cree); el primer frame de Compose para el tema ahora se inicializa desde una caché síncrona en lugar de un valor por defecto fijo.
- Se corrigió un destello de pantalla en negro que aparecía brevemente al cambiar el idioma de la aplicación desde Ajustes mientras la aplicación estaba en ejecución (confirmado mediante análisis de grabación de pantalla cuadro por cuadro). La Activity ya no se recrea ante un cambio de idioma en caliente (`configChanges="locale"`); en su lugar, Compose recompone la interfaz con el nuevo idioma. También se añadieron colores explícitos de `windowBackground` claro/oscuro y se sincronizó la elección de tema de la aplicación con el modo día/noche nativo de `AppCompatDelegate`, de modo que cualquier transición de ventana nativa (por ejemplo, al iniciar la app) use un fondo acorde al tema en lugar de negro por defecto.
- Se corrigió un glitch visual poco frecuente (confirmado mediante una captura de pantalla en tiempo real) en el que el diálogo de selección de tema podía renderizarse brevemente con los colores del tema anterior mientras la pantalla detrás ya había cambiado, justo cuando el diálogo se estaba cerrando. `AlertDialog` congela su contenido mientras reproduce su animación de salida, por lo que llamar a `AppCompatDelegate.setDefaultNightMode` en el mismo instante en que el diálogo comienza a cerrarse podía competir con ese frame congelado. El cambio de tema nativo ahora se difiere un frame para que la animación de salida del diálogo siempre capture un frame estable y ya correcto.

## [2.0.0] - 2026-04-29

### Añadido
- Flujo de firma nativo para releases de Android con configuración basada en keystore.
- Generación de APK lista para distribución en GitHub Releases.
- Nuevo paquete de logos de la aplicación integrado en los recursos de Android.
- Variantes de logo claro/oscuro dedicadas para las pantallas de Inicio y Acerca de.
- Documentación del proyecto y galería de capturas de pantalla actualizadas.

### Cambiado
- Migración completa de la implementación en Flutter/Dart a Kotlin + Jetpack Compose.
- Interfaz modernizada con componentes y theming de Material 3 Expressive.
- Arquitectura de la app actualizada a patrones nativos de Android (Compose, ViewModel, DataStore, Retrofit).
- Branding de Inicio y Acerca de actualizado para usar el nuevo logo de CalNotas.

### Seguridad
- Credenciales de firma externalizadas mediante `keystore.properties` (excluido del control de versiones).

---

## [1.1.0] - 2026-01-20

### Añadido
- Verificador de actualizaciones vía la API de GitHub Releases.
- Integración con el servicio de GitHub para metadatos de releases.
- Servicio de preferencias persistentes.

### Cambiado
- Actualización de estilo a Material 3 Expressive en el código base de Flutter.
- Mejoras de tipografía y barra de herramientas.

### Corregido
- Consistencia de colores y uso de APIs obsoletas en la implementación de Flutter.

---

## [1.0.0] - 2025-04-30

### Añadido
- Lanzamiento público inicial.
- Calculadoras de notas por defecto y personalizada.
- Soporte de tema (claro, oscuro, sistema).
- Interfaz bilingüe (español e inglés).
- Secciones de Ayuda y Acerca de.
