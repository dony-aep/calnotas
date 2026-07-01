# CLAUDE.md

Project instructions for Claude Code when working on **CalNotas**.

## Project overview

- CalNotas is a native Android grade calculator app, migrated from Flutter to Kotlin + Jetpack Compose (see `CHANGELOG.md` v2.0.0).
- Package: `com.donyaep.calnotas`. Min SDK 26, target/compile SDK 36.
- Versioning lives in `app/build.gradle.kts` (`versionCode`, `versionName`).
- Full feature list and tech stack: `README.md`.

## Build commands

- Debug build: `./gradlew assembleDebug`
- Release build (requires `keystore.properties`, never commit it): `./gradlew assembleRelease`
- Output APK: `app/build/outputs/apk/release/app-release.apk`
- Unit tests: `./gradlew test` — Instrumented tests: `./gradlew connectedAndroidTest`

## UI work — Material 3 Expressive (mandatory)

- **IMPORTANT:** for any UI work — new screens, new components, redesigns, or visual tweaks — you MUST follow the `android-m3-expressive` skill (`.claude/skills/android-m3-expressive/`) before writing Compose code.
- Use `MaterialExpressiveTheme` + `expressiveLightColorScheme()`/`expressiveDarkColorScheme()`, never plain `MaterialTheme`, to stay consistent with the rest of the app.
- Prefer existing M3 Expressive component families (`FloatingToolbar`, `ButtonGroup`, `LoadingIndicator`, etc.) over custom-built equivalents.
- Apply the required `@OptIn(ExperimentalMaterial3ExpressiveApi::class)` opt-ins as documented by the skill.
- Do not introduce a second design language (e.g. plain Material 2/3 non-expressive widgets) without an explicit reason from the user.

## CHANGELOG.md workflow (mandatory)

`CHANGELOG.md` follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Semantic Versioning](https://semver.org/).

- **Every change you make to the app** (feature, fix, refactor with user-visible impact, security change) must be recorded under a `## [Unreleased]` section at the top of `CHANGELOG.md`, immediately after the file header. Create that section if it doesn't exist yet.
- Group entries under the standard Keep a Changelog headings, only adding the ones you need: `### Added`, `### Changed`, `### Fixed`, `### Removed`, `### Security`, `### Deprecated`.
- Write entries as short, user-facing bullet points (what changed, not how), matching the style of existing released sections.
- Do not narrate the debugging/implementation process (approaches tried and reverted, intermediate attempts) — only the change that actually shipped.
- Do not invent a version number while work is unreleased — leave it under `[Unreleased]`.
- When the user asks to cut a release, decide the next SemVer version from the accumulated `[Unreleased]` content and tell the user which one you picked and why:
  - **MAJOR** — breaking changes (e.g. incompatible data format, removed features).
  - **MINOR** — new backward-compatible functionality (`### Added` entries present).
  - **PATCH** — only `### Fixed` / `### Security` entries, no new features.
- On release, rename `## [Unreleased]` to `## [X.Y.Z] - YYYY-MM-DD` (today's date) and start a fresh empty `## [Unreleased]` section above it for future work.

## README.md maintenance

- Whenever a change adds, removes, or materially modifies a user-facing feature, update the relevant section of `README.md` in the same change (`Features`, `Tech Stack`, `Screenshots`, etc.) — don't let it drift from what's in `CHANGELOG.md`.
- Keep the badges (Kotlin/Compose/release versions) accurate if dependency or version numbers change.
- Don't duplicate changelog detail in the README; the README describes current capabilities, the changelog describes history.

## Bilingual documentation (mandatory)

- `README.md` and `CHANGELOG.md` (English) are the canonical, primary-language versions. `README.es.md` and `CHANGELOG.es.md` are their Spanish counterparts.
- **Whenever you edit `README.md` or `CHANGELOG.md`, apply the equivalent edit to `README.es.md` / `CHANGELOG.es.md` in the same change** — never let the Spanish versions drift out of sync.
- Both language variants of each file start with a language-switcher line (`**English** | [Español](README.es.md)` / `[English](README.md) | **Español**`, same pattern in the changelogs) — keep that line when editing.
- If a release is cut, translate the new `## [X.Y.Z] - YYYY-MM-DD` section into `CHANGELOG.es.md` too.

## GitHub Releases workflow

When asked to cut/publish a release, follow the pattern established by existing releases (`v1.1.0`, `v2.0.0`):

1. Bump `versionCode` (+1) and `versionName` (the new SemVer, e.g. `2.1.0`) in `app/build.gradle.kts`.
2. Finalize the `CHANGELOG.md` `[Unreleased]` → `[X.Y.Z] - YYYY-MM-DD` section as described above, and update `README.md` if features changed.
3. Build the signed APK: `./gradlew assembleRelease`.
4. Rename the generated APK to `calnotas_v<version>_release.apk` (e.g. `calnotas_v2.1.0_release.apk`) — this exact naming pattern is what `README.md`'s install instructions and the update checker expect.
5. Create a git tag `v<version>` (e.g. `v2.1.0`) on the release commit.
6. Create the GitHub Release with:
   - **Title:** `CalNotas v<version>`
   - **Body:** a `## What's new` section with concise bullets summarizing the changelog entries for this version (mirror the tone of past releases, not a raw changelog dump).
   - **Asset:** the renamed `calnotas_v<version>_release.apk`.
- Always confirm with the user before tagging, pushing, or actually publishing the release (`gh release create`) — these are visible, hard-to-reverse actions.

## Security

- Never commit `keystore.properties`, `keys/`, or any `*.jks`/`*.keystore`/`*.pem` file — they are gitignored on purpose.
