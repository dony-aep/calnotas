# Changelog

All notable changes to CalNotas are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Grade prediction panel for the default calculator: appears automatically as grades are entered, showing the minimum uniform grade needed across all remaining empty fields to reach the passing score (3.0).
- Per-field prediction hint (`Sugerido: ≥ X.X` / `Suggested: ≥ X.X`) displayed under each empty input as a quick inline reference.
- Safety grade hint shown under the next relevant empty field when that single grade alone can guarantee a pass (assuming 0.0 in all subsequent fields).
- Grade prediction documentation added to the Help screen under the default calculator section.
- Ports the grade prediction feature from the web app (CalNotas Web v4.6.0) to keep both apps at feature parity.

### Fixed
- Default calculator now rounds each "Corte" subtotal to 2 decimals before summing, matching the worked example already documented in the Help screen and the web app's calculation (previously summed raw, unrounded products).
- Fixed low-contrast/washed-out colors across list rows (Settings, About, Help) and colored info/result cards (Help callouts, both calculators' result and prediction cards, Update screen). List rows now use `surfaceContainerHigh` instead of blending into their parent card's tone, and colored cards use solid container colors with matching explicit `on*Container` text colors instead of alpha-blended containers paired with full-opacity-calibrated text.
- Fixed literal double percent signs (`%%`) showing up in several Help screen strings that had no format arguments to trigger Android's escape collapsing.
- Fixed a startup flash/flicker where the app briefly rendered in the system language and theme before switching to the saved preference. Language changes no longer trigger a full Activity recreate on cold start (adopts AppCompat's `autoStoreLocales`, which restores the persisted locale before the Activity is even created); the theme preference's first Compose frame is now seeded from a synchronous cache instead of a hardcoded default.
- Fixed a black screen flash that appeared briefly when changing the app language from Settings while the app was running (confirmed via frame-by-frame screen recording analysis). The Activity no longer recreates on a live language change (`configChanges="locale"`); Compose recomposes the UI with the new language in place instead. Also added explicit light/dark `windowBackground` colors and synced the app's theme choice to `AppCompatDelegate`'s native day/night mode so any native window transition (e.g. app launch) uses a themed background instead of defaulting to black.

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
