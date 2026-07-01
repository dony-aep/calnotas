# CalNotas - Android Nativo (Kotlin)

[English](README.md) | **Español**

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208.0%2B-green?style=flat-square&logo=android" alt="Platform Android 8.0+"/>
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-purple?style=flat-square&logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203%20Expressive-blue?style=flat-square" alt="Jetpack Compose M3 Expressive"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License"/>
  <img src="https://img.shields.io/github/v/release/dony-aep/calnotas?style=flat-square" alt="Release"/>
  <img src="https://img.shields.io/github/downloads/dony-aep/calnotas/total?style=flat-square&color=brightgreen" alt="Downloads"/>
</p>

CalNotas es una aplicación moderna de calculadora de notas para Android, migrada de Flutter a Kotlin nativo usando Jetpack Compose y Material 3 Expressive.

## Funcionalidades

### Calculadora de Notas por Defecto
- Calculadora preconfigurada para el sistema de evaluación estándar
- Cálculo distribuido en 3 cortes
- Componentes formativo y cognitivo
- Actualización en tiempo real mientras se escribe
- Panel de predicción de nota: nota mínima necesaria para aprobar, además de sugerencias por campo y de nota de seguridad

### Calculadora de Notas Personalizada
- Agregar y eliminar campos de evaluación personalizados
- Definir nombres, porcentajes y notas
- Validación de que el total de porcentajes sea 100%
- Nota mínima de aprobación configurable

### UI y Experiencia
- Sistema de diseño Material 3 Expressive
- Modos de apariencia Claro, Oscuro y Predeterminado del sistema
- Idioma de la aplicación dinámico (Español / Inglés)
- Barra de herramientas flotante y estilo visual actualizado

### Extra
- Verificador de actualizaciones integrado vía la API de GitHub Releases
- Secciones de Ayuda y Acerca de
- Configuración y datos de calculadoras persistentes

## Capturas de pantalla

<p align="center">
  <img src="docs/screenshots/home-screen.png" width="250" alt="Pantalla de inicio"/>
  <img src="docs/screenshots/default-calculator-screen.png" width="250" alt="Pantalla de calculadora por defecto"/>
  <img src="docs/screenshots/custom-calculator-screen.png" width="250" alt="Pantalla de calculadora personalizada"/>
</p>

| Inicio | Calculadora por Defecto | Calculadora Personalizada |
|:------:|:------------------------:|:--------------------------:|
| Menú principal y acceso rápido | Calculadora con estructura predefinida | Calculadora flexible con campos personalizados |

## Requisitos

- Android 8.0+ (API 26+)
- JDK 11+
- Android Studio (se recomienda la última versión estable)

## Instalación

### Desde GitHub Releases
1. Abre [Releases](https://github.com/dony-aep/calnotas/releases)
2. Descarga la última versión `calnotas_v<version>_release.apk`
3. Instálala en tu dispositivo Android

### Compilar desde el código fuente
```bash
git clone https://github.com/dony-aep/calnotas.git
cd calnotas
./gradlew assembleRelease
```

APK generado:
`app/build/outputs/apk/release/app-release.apk`

## Stack tecnológico

- Kotlin 2.2.10
- Jetpack Compose + Material 3 Expressive
- Navigation Compose
- DataStore Preferences
- Retrofit + OkHttp
- Gradle (KTS) + Android Gradle Plugin

## Licencia

Este proyecto está licenciado bajo la Licencia MIT. Consulta [LICENSE](LICENSE).

## Autor

**dony.**
- GitHub: [@dony-aep](https://github.com/dony-aep)
