# Changelog

All notable changes to CalNotas are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-04-29

### Added
- Native Android release signing flow with keystore-based configuration.
- Release-ready APK generation for GitHub Releases distribution.
- New app logo pack integrated into Android resources.
- Dedicated light/dark logo variants for Home and About screens.
- Updated project documentation and screenshot gallery.

### Changed
- Full migration from Flutter/Dart implementation to Kotlin + Jetpack Compose.
- UI modernized with Material 3 Expressive components and theming.
- App architecture updated to Android native patterns (Compose, ViewModel, DataStore, Retrofit).
- Home and About branding updated to use the new CalNotas logo.

### Security
- Signing credentials externalized through `keystore.properties` (excluded from version control).

---

## [1.1.0] - 2026-01-20

### Added
- Update checker via GitHub Releases API.
- GitHub service integration for release metadata.
- Persistent preferences service.

### Changed
- Material 3 Expressive style update in the Flutter codebase.
- Typography and toolbar improvements.

### Fixed
- Color consistency and deprecated API usage in Flutter implementation.

---

## [1.0.0] - 2025-04-30

### Added
- Initial public release.
- Default and custom grade calculators.
- Theme support (light, dark, system).
- Bilingual interface (Spanish and English).
- Help and About sections.
