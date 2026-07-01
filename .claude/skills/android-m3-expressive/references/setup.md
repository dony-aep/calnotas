# Setup -- Gradle, BOM, and Project Structure

## Context: Android Studio Panda 4 | 2025.3.4 (mayo 2026)

Tu entorno por defecto genera estas versiones al crear un proyecto nuevo:

```
agp          = "9.2.1"
kotlin       = "2.2.10"
composeBom   = "2026.02.01"
```

El BOM `2026.02.01` incluye `material3:1.4.0` (estable). Esa version contiene
componentes M3E con un subconjunto de APIs ya graduadas a stable, pero la mayoria
de las APIs nuevas como `FloatingActionButtonMenu`, `WideNavigationRail`,
`SplitButtonLayout`, `LoadingIndicator`, `CircularWavyProgressIndicator`,
`MaterialExpressiveTheme`, y `ButtonGroup` siguen bajo
`@ExperimentalMaterial3ExpressiveApi` y requieren sobreescribir la version al alpha.


## Mapa de versiones M3E (mayo 2026)

| Canal | Artefacto | Version | M3E |
|---|---|---|---|
| Stable | `material3` via BOM `2026.04.01` | `1.4.0` | parcial |
| Alpha activa | `material3` override manual | `1.5.0-alpha19` | completo |
| BOM por defecto AS Panda | BOM `2026.02.01` | `1.4.0` | parcial |
| BOM mas reciente (stable) | `compose-bom:2026.04.01` | `1.4.0` | parcial |
| BOM alpha | `compose-bom-alpha:2026.04.01` | `1.5.0-alpha19` | completo |

Para acceder a TODOS los componentes M3E documentados en esta SKILL, necesitas
`material3:1.5.0-alpha19` o superior.

### Que se graduo a stable en cada alpha relevante

| Version | APIs graduadas a stable |
|---|---|
| `1.4.0` (stable) | `MotionScheme`, `SearchBar`, carousels, `SecureTextField` |
| `1.5.0-alpha17` | `TopAppBarScrollBehavior` y metodos asociados |
| `1.5.0-alpha18` | `MaterialExpressiveTheme`, `expressiveLightColorScheme`, `expressiveDarkColorScheme`, `WavyProgressIndicator` APIs |
| `1.5.0-alpha19` | `ToggleButton` (y variantes), menus expressivos graduados; `WavyProgressIndicator` revertido a experimental |

> Nota sobre `1.5.0-alpha19` (6 mayo 2026): `ToggleButton` fue promovido a stable
> pero `LoadingIndicator` y `CircularWavyProgressIndicator` siguen siendo experimentales
> en esta version. Siempre manten el `@OptIn(ExperimentalMaterial3ExpressiveApi::class)`.


## libs.versions.toml recomendado (partiendo de tu config por defecto)

Conserva lo que genera Android Studio y agrega / ajusta solo lo marcado con `<-- agregar`:

```toml
[versions]
agp                   = "9.2.1"           # version por defecto de AS Panda 4
kotlin                = "2.2.10"          # version por defecto de AS Panda 4
composeBom            = "2026.04.01"      # <-- actualizar: BOM estable mas reciente
material3Expressive   = "1.5.0-alpha19"   # <-- agregar: override para M3E completo
coreKtx               = "1.18.0"
junit                 = "4.13.2"
junitVersion          = "1.3.0"
espressoCore          = "3.7.0"
lifecycleRuntimeKtx   = "2.10.0"
activityCompose       = "1.13.0"
hilt                  = "2.56"            # <-- agregar si usas Hilt
navigation            = "2.9.0"           # <-- agregar si usas Navigation Compose
ksp                   = "2.2.10-1.0.31"   # <-- agregar si usas KSP/Hilt

[libraries]
# -- conservar lo que genera Android Studio --
androidx-core-ktx                 = { group = "androidx.core",          name = "core-ktx",               version.ref = "coreKtx" }
junit                             = { group = "junit",                   name = "junit",                  version.ref = "junit" }
androidx-junit                    = { group = "androidx.test.ext",       name = "junit",                  version.ref = "junitVersion" }
androidx-espresso-core            = { group = "androidx.test.espresso",  name = "espresso-core",          version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx    = { group = "androidx.lifecycle",      name = "lifecycle-runtime-ktx",  version.ref = "lifecycleRuntimeKtx" }
androidx-activity-compose         = { group = "androidx.activity",       name = "activity-compose",       version.ref = "activityCompose" }
androidx-compose-bom              = { group = "androidx.compose",        name = "compose-bom",            version.ref = "composeBom" }
androidx-compose-ui               = { group = "androidx.compose.ui",     name = "ui" }
androidx-compose-ui-graphics      = { group = "androidx.compose.ui",     name = "ui-graphics" }
androidx-compose-ui-tooling       = { group = "androidx.compose.ui",     name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui",   name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui",     name = "ui-test-manifest" }
androidx-compose-ui-test-junit4   = { group = "androidx.compose.ui",     name = "ui-test-junit4" }
androidx-compose-material3        = { group = "androidx.compose.material3", name = "material3" }

# -- agregar para M3E completo --
androidx-compose-material3-expressive = { group = "androidx.compose.material3", name = "material3", version.ref = "material3Expressive" }
androidx-compose-foundation       = { group = "androidx.compose.foundation", name = "foundation" }
androidx-compose-animation        = { group = "androidx.compose.animation",  name = "animation" }
# Iconos (ya no se incluyen automaticamente desde material3 1.4.0)
androidx-compose-material-icons-core     = { group = "androidx.compose.material", name = "material-icons-core" }
androidx-compose-material-icons-extended = { group = "androidx.compose.material", name = "material-icons-extended" }
# Adaptive navigation (WideNavigationRail + NavigationSuiteScaffold)
androidx-material3-adaptive-nav   = { group = "androidx.compose.material3", name = "material3-adaptive-navigation-suite" }
# ViewModel en Compose
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle",   name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
# Navigation
androidx-navigation-compose       = { group = "androidx.navigation",     name = "navigation-compose", version.ref = "navigation" }
# Hilt (opcional)
hilt-android                      = { group = "com.google.dagger",      name = "hilt-android",         version.ref = "hilt" }
hilt-compiler                     = { group = "com.google.dagger",      name = "hilt-android-compiler", version.ref = "hilt" }
hilt-navigation-compose           = { group = "androidx.hilt",          name = "hilt-navigation-compose", version = "1.2.0" }

[plugins]
android-application = { id = "com.android.application",             version.ref = "agp" }
kotlin-compose      = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
# <-- agregar:
kotlin-android      = { id = "org.jetbrains.kotlin.android",        version.ref = "kotlin" }
hilt                = { id = "com.google.dagger.hilt.android",      version.ref = "hilt" }
ksp                 = { id = "com.google.devtools.ksp",             version.ref = "ksp" }
```


## build.gradle.kts (app module)

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)   // Kotlin 2.0+: gestiona la version del compilador de Compose
    // alias(libs.plugins.hilt)          // descomentar si usas Hilt
    // alias(libs.plugins.ksp)           // descomentar si usas Hilt
}

android {
    compileSdk = 36
    defaultConfig {
        minSdk    = 26    // minimo para M3E completo (Dynamic Color requiere API 31+)
        targetSdk = 36
    }
    buildFeatures { compose = true }
    // No escribir kotlinCompilerExtensionVersion; lo maneja kotlin.compose plugin
}

dependencies {
    // BOM: controla versiones de todo el ecosistema Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Material 3 Expressive -- override del BOM para acceder a APIs experimentales
    // Reemplaza androidx-compose-material3 cuando necesitas M3E experimental completo:
    implementation(libs.androidx.compose.material3.expressive)
    // Si solo usas APIs ya graduadas a stable (ToggleButton, MaterialExpressiveTheme,
    // expressiveLightColorScheme), puedes usar la linea del BOM sin override:
    // implementation(libs.androidx.compose.material3)

    // Adaptive navigation (WideNavigationRail, NavigationSuiteScaffold)
    implementation(libs.androidx.material3.adaptive.nav)

    // Core Compose (versiones controladas por BOM)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.activity.compose)

    // Iconos -- agregar explicitamente desde material3 1.4.0
    implementation(libs.androidx.compose.material.icons.core)
    // implementation(libs.androidx.compose.material.icons.extended) // solo si necesitas el catalogo completo

    // Preview y tooling
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt (opcional)
    // implementation(libs.hilt.android)
    // ksp(libs.hilt.compiler)
    // implementation(libs.hilt.navigation.compose)
}
```

### Por que usar el override en lugar del BOM para material3

El BOM `2026.04.01` resuelve `material3` a `1.4.0` (stable). Ese canal no incluye
APIs marcadas como `@ExperimentalMaterial3ExpressiveApi`. Al declarar explicitamente
`material3:1.5.0-alpha19`, Gradle usa esa version y eleva sus dependencias transitivas
a las versiones alpha correspondientes. El resto de librerias del BOM (ui, foundation,
animation) no se ven afectadas.

> Cuando `1.5.0` llegue a stable, elimina el override y deja que el BOM lo resuelva.


## compileSdk y Dynamic Color

| Feature | API minima |
|---|---|
| M3E components base | API 21 |
| `dynamicLightColorScheme` / `dynamicDarkColorScheme` | API 31 (Android 12) |
| `enableEdgeToEdge()` requerido | API 35+ (Android 15) |
| `expressiveLightColorScheme()` sin dynamic | cualquier API |

Configura `minSdk = 26` como base segura. Para Dynamic Color, verifica en runtime:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    dynamicLightColorScheme(context)
} else {
    expressiveLightColorScheme()
}
```


## Estructura de carpetas recomendada

```
app/src/main/kotlin/com/example/app/
├── MainActivity.kt
├── di/
│   └── AppModule.kt
├── ui/
│   ├── theme/
│   │   ├── Theme.kt             <- MaterialExpressiveTheme wrapper
│   │   ├── Color.kt             <- color scheme definitions
│   │   ├── Type.kt              <- typography scale
│   │   └── Shape.kt             <- shape scale
│   ├── navigation/
│   │   └── AppNavGraph.kt
│   └── screens/
│       └── home/
│           ├── HomeScreen.kt
│           └── HomeViewModel.kt
└── data/
    └── repository/
```


## MainActivity.kt

```kotlin
@AndroidEntryPoint   // quitar si no usas Hilt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()     // obligatorio en Android 15+ (targetSdk 35+)
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                AppNavGraph()
            }
        }
    }
}
```


## res/values/themes.xml

```xml
<resources>
    <style name="Theme.MyApp" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```


## AndroidManifest.xml

```xml
<application
    android:theme="@style/Theme.MyApp"
    android:enableOnBackInvokedCallback="true">
    <!-- enableOnBackInvokedCallback = true activa Predictive Back, requerido por M3 -->
```
