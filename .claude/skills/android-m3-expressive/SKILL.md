---
name: android-m3-expressive
description: >
  Skill for building Android apps in Kotlin with Jetpack Compose using Material 3 Expressive
  (M3E). Use this skill whenever the user mentions Kotlin Android, Jetpack Compose, Material 3
  Expressive, M3E components, MaterialExpressiveTheme, MotionScheme, FloatingToolbar,
  ButtonGroup, SplitButtonLayout, WideNavigationRail, LoadingIndicator, ToggleButton,
  CircularWavyProgressIndicator, expressiveLightColorScheme, or any of the new M3 Expressive
  component families. Also use this skill when the user wants to migrate a Flutter app to
  native Android Kotlin, map Flutter widgets to Compose equivalents, or set up a new Android
  project with the M3 Expressive design system. Apply proactively whenever the user asks about
  component variants, sizes, color styles, shape morphing, spring animations, or design
  guidelines for Android UI built with Compose.
---

# Android -- Material 3 Expressive with Kotlin & Jetpack Compose

Reference skill for building Android apps with the Material 3 Expressive (M3E) design system
using Jetpack Compose. Covers setup, theming, all component families with their sizes and
color styles, motion, and Flutter-to-Compose migration patterns.

All M3E components require opt-in. Apply this at the file or function level:

```kotlin
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
// or per function:
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
```


## Reference files

Load the relevant reference file based on the task. Do not load all files at once.

| Task | File |
|---|---|
| Project setup, Gradle, BOM, versions | `references/setup.md` |
| Theme, colors, typography, shapes | `references/theming.md` |
| Component API, variants, sizes, styles | `references/components-expressive.md` |
| Spring animations, MotionScheme, scroll behaviors | `references/motion.md` |
| Flutter widget to Compose equivalent mapping | `references/flutter-to-compose.md` |


## Core rules

### Use MaterialExpressiveTheme, not MaterialTheme

```kotlin
// Incorrect for M3E apps
MaterialTheme { ... }

// Correct
MaterialExpressiveTheme(
    colorScheme  = expressiveLightColorScheme(),
    motionScheme = MotionScheme.expressive(),
) { ... }
```

### Scale every component with size tokens

M3E buttons, toggle buttons, icon buttons, and split buttons share a unified size token
system. Always pass size through `ButtonDefaults` constants and the corresponding helpers
rather than hardcoding `Modifier.size(...)` or raw font sizes:

```kotlin
val size = ButtonDefaults.LargeContainerHeight   // pick the size token
modifier          = Modifier.heightIn(size)
contentPadding    = ButtonDefaults.contentPaddingFor(size)
// inside the button content:
Modifier.size(ButtonDefaults.iconSizeFor(size))
ButtonDefaults.iconSpacingFor(size)
ButtonDefaults.textStyleFor(size)                // correct TextStyle per size
```

Size tokens available across the button family:

| Token | Approx. height |
|---|---|
| `ButtonDefaults.ExtraSmallContainerHeight` | 24 dp |
| `ButtonDefaults.MinHeight` (default) | 40 dp |
| `ButtonDefaults.MediumContainerHeight` | 48 dp |
| `ButtonDefaults.LargeContainerHeight` | 56 dp |
| `ButtonDefaults.ExtraLargeContainerHeight` | 96 dp |

Pass the same token to shape helpers so shape morphing scales correctly with the size:

```kotlin
shapes = ToggleButtonDefaults.shapesFor(size)
// for split button leading button:
shapes = SplitButtonDefaults.leadingButtonShapesFor(size)
```

### Choose the right color style for each action

Many M3E components expose multiple color styles through `SplitButtonDefaults`,
`ToggleButtonDefaults`, `FloatingToolbarDefaults`, and similar companion objects.
Select the variant based on the visual hierarchy of the action:

| Style | When to use |
|---|---|
| Filled (default) | Primary or high-emphasis action |
| Tonal | Secondary action in the same region |
| Elevated | Action that must visually stand above the surface |
| Outlined | Low-emphasis or destructive action |

### Replace deprecated components in M3E screens

| Deprecated | M3E replacement |
|---|---|
| `BottomAppBar` | `HorizontalFloatingToolbar` or `FlexibleBottomAppBar` |
| `CircularProgressIndicator` (indeterminate, short wait) | `LoadingIndicator` or `ContainedLoadingIndicator` |
| `CircularProgressIndicator` (indeterminate, longer) | `CircularWavyProgressIndicator` |
| `NavigationRail` on large screens | `WideNavigationRail` |
| `ExtendedFloatingActionButton` | `SmallExtendedFAB`, `MediumExtendedFAB`, or `LargeExtendedFAB` |


## Decision guide

```
Need a group of related toggleable options?
  -> ButtonGroup + ToggleButton
  -> see references/components-expressive.md #button-group

Need a button with a secondary overflow action?
  -> SplitButtonLayout (4 style variants, 5 size variants)
  -> see references/components-expressive.md #split-button

Need bottom navigation: phone vs. tablet?
  -> NavigationBar or ShortNavigationBar (phone)
  -> NavigationRail or WideNavigationRail (tablet / foldable)
  -> NavigationSuiteScaffold (adaptive, auto-switches)

Need a side drawer?
  -> ModalNavigationDrawer (phone, overlays content)
  -> DismissibleNavigationDrawer (medium, pushes content)
  -> PermanentNavigationDrawer (large, always visible)

Need a floating dock at the bottom?
  -> HorizontalFloatingToolbar
  -> see references/components-expressive.md #floating-toolbars

Need to show loading for short waits (<5 s)?
  -> LoadingIndicator or ContainedLoadingIndicator

Need to show loading for longer operations?
  -> CircularWavyProgressIndicator (determinate or indeterminate)

Need search functionality?
  -> SearchBar + ExpandedFullScreenSearchBar (phone)
  -> SearchBar + ExpandedDockedSearchBar (tablet)
  -> TopSearchBar or AppBarWithSearch (pinned to top)
  -> see references/components-expressive.md #search

Need a carousel / image gallery?
  -> HorizontalMultiBrowseCarousel (many items, quick browse)
  -> HorizontalUncontainedCarousel (single-size items)
  -> HorizontalCenteredHeroCarousel (spotlight one item)
  -> see references/components-expressive.md #carousel

Need chips for filtering or input?
  -> FilterChip (selectable filter tags)
  -> InputChip (user-entered data, dismissible)
  -> AssistChip (action shortcut)
  -> SuggestionChip (dynamic suggestions)
  -> see references/components-expressive.md #chips

Need a bottom sheet?
  -> ModalBottomSheet (temporary, overlays content)
  -> BottomSheetScaffold (persistent, integrated)
  -> see references/components-expressive.md #bottom-sheets

Need segmented toggle (like iOS segmented control)?
  -> SingleChoiceSegmentedButtonRow (radio-like)
  -> MultiChoiceSegmentedButtonRow (checkbox-like)
  -> see references/components-expressive.md #segmented-button

Need pull-to-refresh?
  -> PullToRefreshBox
  -> see references/components-expressive.md #pull-to-refresh

Need swipe-to-dismiss on list items?
  -> SwipeToDismissBox
  -> see references/components-expressive.md #swipe-to-dismiss

Need date or time selection?
  -> DatePicker / DatePickerDialog (single date)
  -> DateRangePicker (date range)
  -> TimePicker (clock face) / TimeInput (keyboard)
  -> see references/components-expressive.md #date-time-pickers

Need spring-based animation?
  -> MotionScheme.expressive() + motionScheme.enterSpec()
  -> see references/motion.md

Migrating a Flutter widget to Compose?
  -> references/flutter-to-compose.md
```


## Recommended architecture

- UI: Jetpack Compose with M3E components
- State: ViewModel + StateFlow<UiState> (sealed class per screen)
- DI: Hilt
- Navigation: Navigation Compose (or Navigation3 for new projects)
- Data: Repository pattern -- Room for local, Retrofit for remote


## Component families at a glance

Full API, all sizes, all color styles, and shape morphing details are in
`references/components-expressive.md`. The table below maps each family to its section.

| Family | Key components | Section |
|---|---|---|
| Button groups | `ButtonGroup`, `ToggleButton` 4 styles x 5 sizes | #button-group |
| Split button | `SplitButtonLayout` 4 styles x 5 sizes | #split-button |
| Icon button | `IconButton` family, 4 styles x 5 sizes x 3 widths | #icon-button |
| Buttons | `Button`, `ElevatedButton`, `FilledTonalButton`, `OutlinedButton`, `TextButton` | #buttons |
| FAB | Standard, Medium, Large, Extended variants, FAB menu, Toggle FAB | #fab |
| Toolbars | `HorizontalFloatingToolbar`, `VerticalFloatingToolbar`, `FlexibleBottomAppBar` | #floating-toolbars |
| Top bars | `MediumFlexibleTopAppBar`, `LargeFlexibleTopAppBar`, `TwoRowsTopAppBar`, `AppBarRow`, `AppBarColumn` | #top-bars |
| Navigation | `NavigationBar`, `ShortNavigationBar`, `NavigationRail`, `WideNavigationRail`, `ModalWideNavigationRail`, `NavigationDrawer`, `NavigationSuiteScaffold` | #navigation |
| Loading | `LoadingIndicator`, `ContainedLoadingIndicator`, wavy progress indicators | #loading |
| Lists | `SegmentedListItem` | #lists |
| Text fields | `ScrollField`, `SecureTextField`, `OutlinedSecureTextField` | #text-fields |
| Search | `SearchBar`, `TopSearchBar`, `DockedSearchBar`, `ExpandedDockedSearchBar`, `ExpandedDockedSearchBarWithGap`, `ExpandedFullScreenSearchBar`, `ExpandedFullScreenContainedSearchBar`, `AppBarWithSearch` | #search |
| Menus | `DropdownMenu`, `DropdownMenuGroup`, `DropdownMenuPopup`, `ExposedDropdownMenuBox` | #menus |
| Sliders | `Slider`, `RangeSlider`, `VerticalSlider` | #sliders |
| Chips | `AssistChip`, `FilterChip`, `InputChip`, `SuggestionChip` + Elevated variants | #chips |
| Cards | `Card`, `ElevatedCard`, `OutlinedCard` | #cards |
| Dialogs | `AlertDialog`, `BasicAlertDialog`, `DatePickerDialog` | #dialogs |
| Bottom sheets | `ModalBottomSheet`, `BottomSheetScaffold` | #bottom-sheets |
| Tooltips | `PlainTooltip`, `RichTooltip`, `TooltipBox` | #tooltips |
| Snackbar | `Snackbar`, `SnackbarHost` | #snackbar |
| Badges | `Badge`, `BadgedBox` | #badges |
| Carousel | `HorizontalMultiBrowseCarousel`, `HorizontalUncontainedCarousel`, `HorizontalCenteredHeroCarousel` | #carousel |
| Segmented button | `SegmentedButton`, `SingleChoiceSegmentedButtonRow`, `MultiChoiceSegmentedButtonRow` | #segmented-button |
| Pull to refresh | `PullToRefreshBox` | #pull-to-refresh |
| Swipe to dismiss | `SwipeToDismissBox` | #swipe-to-dismiss |
| Tabs | `PrimaryTabRow`, `SecondaryTabRow`, `PrimaryScrollableTabRow`, `SecondaryScrollableTabRow` | #tabs |
| Date/Time pickers | `DatePicker`, `DateRangePicker`, `TimePicker`, `TimeInput` | #date-time-pickers |
| Theme | `MaterialExpressiveTheme`, color schemes | `references/theming.md` |


## Official sources

- https://developer.android.com/develop/ui/compose/designsystems/material3
- https://developer.android.com/jetpack/androidx/releases/compose-material3
- https://m3.material.io/components
