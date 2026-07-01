# Motion -- Spring Animations and MotionScheme

M3E uses spring physics instead of bezier curves. Configure it through `MotionScheme.expressive()`
inside `MaterialExpressiveTheme` and consume the tokens via `MaterialTheme.motionScheme`.


## MotionScheme tokens

```kotlin
val motionScheme = MaterialTheme.motionScheme

motionScheme.enterSpec<T>()         // entering elements: slide-in, fade-in
motionScheme.exitSpec<T>()          // exiting elements: slide-out, fade-out
motionScheme.fastEffectsSpec<T>()   // micro-interactions: tap feedback, hover, tooltips
motionScheme.slowEffectsSpec<T>()   // heavy content: image transitions, large containers
```

All four return `FiniteAnimationSpec<T>`. Pass them directly to `animateFloatAsState`,
`animateDpAsState`, `AnimatedContent`, and transition APIs.


## Basic usage

```kotlin
// Animate alpha on enter/exit
val alpha by animateFloatAsState(
    targetValue   = if (visible) 1f else 0f,
    animationSpec = if (visible)
        MaterialTheme.motionScheme.enterSpec()
    else
        MaterialTheme.motionScheme.exitSpec(),
    label = "alpha",
)

// Animate size with fast feedback spec
val size by animateDpAsState(
    targetValue   = if (expanded) 200.dp else 100.dp,
    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
    label         = "size",
)
```


## AnimatedContent with motion tokens

```kotlin
AnimatedContent(
    targetState  = count,
    transitionSpec = {
        (slideInVertically(MaterialTheme.motionScheme.enterSpec()) { it } +
         fadeIn(MaterialTheme.motionScheme.enterSpec()))
        .togetherWith(
         slideOutVertically(MaterialTheme.motionScheme.exitSpec()) { -it } +
         fadeOut(MaterialTheme.motionScheme.exitSpec()))
    },
    label = "counter",
) { target ->
    Text("$target", style = MaterialTheme.typography.displaySmall)
}
```


## Scroll-aware behaviors

### FloatingToolbar scroll behavior

```kotlin
val scrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
    state = rememberFloatingToolbarState()
)

Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
) { padding ->
    LazyColumn(contentPadding = padding) { items(50) { Text("Item $it") } }

    HorizontalFloatingToolbar(
        scrollBehavior = scrollBehavior,
        expanded       = true,
        content        = { /* buttons */ }
    )
}
```

### TopAppBar collapse on scroll

```kotlin
val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar   = {
        LargeFlexibleTopAppBar(
            title          = { Text("Title") },
            scrollBehavior = scrollBehavior,
        )
    }
) { /* content */ }
```


## Navigation transitions

```kotlin
NavHost(
    navController      = navController,
    startDestination   = "home",
    enterTransition    = { fadeIn(tween(300)) + slideInHorizontally { it / 4 } },
    exitTransition     = { fadeOut(tween(300)) + slideOutHorizontally { -it / 4 } },
    popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally { -it / 4 } },
    popExitTransition  = { fadeOut(tween(300)) + slideOutHorizontally { it / 4 } },
) {
    composable("home")   { HomeScreen() }
    composable("detail") { DetailScreen() }
}
```

For M3E-native feel, prefer `MaterialTheme.motionScheme.enterSpec()` over fixed `tween(300)`.


## Haptic feedback

```kotlin
val haptic = LocalHapticFeedback.current

Button(onClick = {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}) { Text("Action") }
```

Use `LongPress` for destructive actions or swipe-to-dismiss confirmation.
Use default click haptics (automatic in M3E buttons) for standard interactions.


## Token quick reference

| Token | Typical use | Feel |
|---|---|---|
| `enterSpec()` | Screen transitions, element appearance | Medium, slight spring |
| `exitSpec()` | Screen transitions, element disappearance | Medium, no overshoot |
| `fastEffectsSpec()` | Tap feedback, hover states, tooltip show | Fast, tight |
| `slowEffectsSpec()` | Large image crossfades, heavy container resize | Slow, gradual |
