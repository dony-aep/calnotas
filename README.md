# CalNotas - Android Native (Kotlin)

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%208.0%2B-green?style=flat-square&logo=android" alt="Platform Android 8.0+"/>
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-purple?style=flat-square&logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203%20Expressive-blue?style=flat-square" alt="Jetpack Compose M3 Expressive"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License"/>
  <img src="https://img.shields.io/github/v/release/dony-aep/calnotas?style=flat-square" alt="Release"/>
  <img src="https://img.shields.io/github/downloads/dony-aep/calnotas/total?style=flat-square&color=brightgreen" alt="Downloads"/>
</p>

CalNotas is a modern grade calculator app for Android, migrated from Flutter to native Kotlin using Jetpack Compose and Material 3 Expressive.

## Features

### Default Grade Calculator
- Preconfigured calculator for standard grading systems
- Calculation across 3 periods
- Formative and cognitive components
- Real-time updates while typing

### Custom Grade Calculator
- Add and remove custom evaluation fields
- Define names, percentages, and grades
- Validation for 100% total percentage
- Configurable minimum passing grade

### UI and Experience
- Material 3 Expressive design system
- Light, Dark, and System appearance modes
- Dynamic app language (Spanish / English)
- Floating toolbar and updated visual style

### Extra
- Built-in update checker via GitHub Releases API
- Help and About sections
- Persistent settings and calculator data

## Screenshots

<p align="center">
  <img src="docs/screenshots/home-screen.png" width="250" alt="Home Screen"/>
  <img src="docs/screenshots/default-calculator-screen.png" width="250" alt="Default Calculator Screen"/>
  <img src="docs/screenshots/custom-calculator-screen.png" width="250" alt="Custom Calculator Screen"/>
</p>

| Home | Default Calculator | Custom Calculator |
|:----:|:------------------:|:-----------------:|
| Main menu and quick access | Calculator with predefined structure | Flexible calculator with custom fields |

## Requirements

- Android 8.0+ (API 26+)
- JDK 11+
- Android Studio (latest stable recommended)

## Installation

### From GitHub Releases
1. Open [Releases](https://github.com/dony-aep/calnotas/releases)
2. Download the latest `calnotas_v<version>_release.apk`
3. Install on your Android device

### Build from Source
```bash
git clone https://github.com/dony-aep/calnotas.git
cd calnotas
./gradlew assembleRelease
```

Generated APK:
`app/build/outputs/apk/release/app-release.apk`

## Tech Stack

- Kotlin 2.2.10
- Jetpack Compose + Material 3 Expressive
- Navigation Compose
- DataStore Preferences
- Retrofit + OkHttp
- Gradle (KTS) + Android Gradle Plugin

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).

## Author

**dony.**
- GitHub: [@dony-aep](https://github.com/dony-aep)
