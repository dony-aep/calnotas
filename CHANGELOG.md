# Changelog

All notable changes to CalNotas will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2026-01-20

### Added
- **Update Checker** - Check for new versions directly from GitHub Releases
- **GitHubService** - Centralized service for GitHub API interactions
- **PreferencesService** - Singleton service for SharedPreferences management

### Changed
- **Material Design 3 Expressive** - Complete UI overhaul with M3 design language
- **Google Sans Typography** - Replaced Poppins font with Google Sans for cleaner look
- **Floating Toolbar** - New floating action toolbar in calculator screens
- **Improved Theme System** - Enhanced theme switching with `DynamicSchemeVariant.tonalSpot`
- **Code Architecture** - Refactored to use services pattern for better maintainability
- **Removed WidgetsBindingObserver** - Simplified widget lifecycle management
- **Updated Dependencies**:
  - Android Gradle Plugin: 8.7.0 → 8.9.1
  - Kotlin: 1.8.22 → 2.1.0
  - Gradle: 8.10.2 → 8.11.1
  - compileSdk: 35 → 36
  - targetSdk: 35 → 36
  - http: ^1.6.0
  - package_info_plus: ^9.0.0

### Fixed
- Deprecated `withOpacity()` replaced with `withValues(alpha:)`
- Removed unnecessary FontManager class
- Improved color consistency across all screens

### Technical
- Minimum SDK: Android 8.0 (API 26)
- Target SDK: Android 13 (API 33)
- Flutter SDK: ^3.7.0

---

## [1.0.0] - 2025-04-30

### Added
- **Initial Release**
- **Default Grade Calculator** - Pre-configured calculator for standard 3-period grading
  - Formative grades (15% each period)
  - Cognitive grades (15% for periods 1-2, 20% for period 3)
- **Custom Grade Calculator** - Flexible calculator with customizable fields
  - Add/remove evaluation fields
  - Custom names, percentages, and grades
  - Percentage validation (must total 100%)
  - Save/load calculator configurations
  - Minimum passing grade setting
- **Theme Support** - Light, Dark, and System default modes
- **Multi-language** - English and Spanish support
- **Help Section** - Usage instructions and tips
- **About Screen** - App information and credits

### Technical
- Compatible with Android 8.0 (Oreo) and newer versions (API 26+)
- Built with Flutter and Material Design
- Provider for state management
- SharedPreferences for local data persistence
