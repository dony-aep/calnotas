# Theming -- MaterialExpressiveTheme, Colors, Typography, Shapes

## MaterialExpressiveTheme

Wrap the entire app in `MaterialExpressiveTheme`. This composable extends `MaterialTheme`
with `MotionScheme` and the expressive color system.

```kotlin
// ui/theme/Theme.kt
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyAppTheme(
    darkTheme: Boolean    = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> expressiveDarkColorScheme()
        else      -> expressiveLightColorScheme()
    }

    MaterialExpressiveTheme(
        colorScheme  = colorScheme,
        motionScheme = MotionScheme.expressive(),
        typography   = AppTypography,
        shapes       = AppShapes,
        content      = content
    )
}
```

Use `dynamicLightColorScheme` / `dynamicDarkColorScheme` (Android 12+) for personalized
palettes tied to the user's wallpaper. Fall back to `expressiveLightColorScheme()` and
`expressiveDarkColorScheme()` for fixed brand colors.


## Color roles

Access color roles from any composable inside the theme:

```kotlin
MaterialTheme.colorScheme.primary            // dominant brand color
MaterialTheme.colorScheme.primaryContainer   // container for primary elements
MaterialTheme.colorScheme.onPrimary          // content on primary
MaterialTheme.colorScheme.secondary          // secondary actions
MaterialTheme.colorScheme.tertiary           // contrasting accents
MaterialTheme.colorScheme.surface            // card and sheet backgrounds
MaterialTheme.colorScheme.surfaceVariant     // alternate surface
MaterialTheme.colorScheme.error              // error states
MaterialTheme.colorScheme.surfaceContainerHigh  // elevated card color
```

`expressiveLightColorScheme()` generates a more saturated and vibrant palette than
the standard `lightColorScheme()`. Use it as the base for brand-colored apps.


## Custom color seed (fixed brand)

```kotlin
// ui/theme/Color.kt
val BrandLight = lightColorScheme(
    primary          = Color(0xFF6750A4),
    onPrimary        = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEADDFF),
    secondary        = Color(0xFF625B71),
    tertiary         = Color(0xFF7D5260),
)
```

Generate the full token set from a seed using the Material Theme Builder:
https://material-foundation.github.io/material-theme-builder/
Export as "Jetpack Compose" and drop the generated files into `ui/theme/`.


## Typography

```kotlin
// ui/theme/Type.kt
val AppTypography = Typography(
    displayLarge   = TextStyle(fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp),
    displayMedium  = TextStyle(fontSize = 45.sp, lineHeight = 52.sp),
    displaySmall   = TextStyle(fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge  = TextStyle(fontSize = 32.sp, lineHeight = 40.sp),
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp),
    headlineSmall  = TextStyle(fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge     = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Normal),
    titleMedium    = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    titleSmall     = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    bodyLarge      = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontSize = 14.sp, lineHeight = 20.sp),
    bodySmall      = TextStyle(fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge     = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelMedium    = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall     = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
)

// Usage:
Text("Headline", style = MaterialTheme.typography.headlineMedium)
```

M3E uses the same type scale tokens as M3 but encourages bolder display usage and stronger
visual hierarchy. Prefer `displaySmall` or `headlineLarge` for hero content in M3E screens.


## Shape scale

```kotlin
// ui/theme/Shape.kt
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(12.dp),
    large      = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)
```

M3E promotes rounder shapes and uses `CircleShape` for FABs and pill buttons. Component
shape morphing transitions are handled automatically by the component when you pass
`shapesFor(size)` — the component interpolates between rest, pressed, and checked shapes.


## MotionScheme

```kotlin
// Two options:
MotionScheme.expressive()   // spring-based, natural rebound (recommended for M3E)
MotionScheme.standard()     // bezier/easing, predictable (Material You original)

// Read in composables:
val motionScheme = MaterialTheme.motionScheme
```

See `references/motion.md` for animation tokens and usage patterns.
