# Components -- Material 3 Expressive

All components on this page require:
```kotlin
@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
```

## Table of contents

- [#button-group](#button-group) -- ButtonGroup, ToggleButton (4 styles, 5 sizes)
- [#split-button](#split-button) -- SplitButtonLayout (4 styles, 5 sizes)
- [#icon-button](#icon-button) -- IconButton family (4 styles, 5 sizes, 3 widths)
- [#fab](#fab) -- FAB variants, FloatingActionButtonMenu, ToggleFloatingActionButton
- [#floating-toolbars](#floating-toolbars) -- Horizontal, Vertical, FlexibleBottomAppBar
- [#top-bars](#top-bars) -- MediumFlexibleTopAppBar, LargeFlexibleTopAppBar, TwoRowsTopAppBar, AppBarRow, AppBarColumn, AppBarWithSearch
- [#navigation](#navigation) -- WideNavigationRail, ModalWideNavigationRail, ShortNavigationBar, NavigationBar, NavigationDrawer
- [#loading](#loading) -- LoadingIndicator, ContainedLoadingIndicator, wavy progress
- [#lists](#lists) -- SegmentedListItem
- [#text-fields](#text-fields) -- ScrollField, SecureTextField, OutlinedSecureTextField
- [#search](#search) -- SearchBar, TopSearchBar, DockedSearchBar, ExpandedDockedSearchBar, ExpandedDockedSearchBarWithGap, ExpandedFullScreenSearchBar, ExpandedFullScreenContainedSearchBar
- [#menus](#menus) -- DropdownMenuGroup, DropdownMenuPopup, ExposedDropdownMenuBox
- [#sliders](#sliders) -- VerticalSlider, RangeSlider
- [#chips](#chips) -- AssistChip, FilterChip, InputChip, SuggestionChip (+ Elevated variants)
- [#cards](#cards) -- Card, ElevatedCard, OutlinedCard
- [#dialogs](#dialogs) -- AlertDialog, BasicAlertDialog, DatePickerDialog
- [#bottom-sheets](#bottom-sheets) -- ModalBottomSheet, BottomSheetScaffold
- [#tooltips](#tooltips) -- PlainTooltip, RichTooltip, TooltipBox
- [#snackbar](#snackbar) -- Snackbar, SnackbarHost
- [#badges](#badges) -- Badge, BadgedBox
- [#carousel](#carousel) -- HorizontalMultiBrowseCarousel, HorizontalUncontainedCarousel, HorizontalCenteredHeroCarousel
- [#segmented-button](#segmented-button) -- SegmentedButton, SingleChoiceSegmentedButtonRow, MultiChoiceSegmentedButtonRow
- [#pull-to-refresh](#pull-to-refresh) -- PullToRefreshBox
- [#swipe-to-dismiss](#swipe-to-dismiss) -- SwipeToDismissBox
- [#tabs](#tabs) -- PrimaryTabRow, SecondaryTabRow, PrimaryScrollableTabRow, SecondaryScrollableTabRow
- [#date-time-pickers](#date-time-pickers) -- DatePicker, DateRangePicker, TimePicker, TimeInput
- [#buttons](#buttons) -- Button, ElevatedButton, FilledTonalButton, OutlinedButton, TextButton

---

## #button-group

`ButtonGroup` is a layout container for a row of `ToggleButton`s. The active button grows
while its neighbors compress, creating the expressive "breathing" interaction.

### ButtonGroup -- two modes

**Separated mode**: buttons have standard spacing. Use `Row` with
`ButtonGroupDefaults.ConnectedSpaceBetween` for visual grouping without physical connection.

**Connected mode**: buttons share edges with shape morphing per position. Pass
`ButtonGroupDefaults.connectedLeadingButtonShapes()`, `connectedMiddleButtonShapes()`,
or `connectedTrailingButtonShapes()` to each `ToggleButton`.

```kotlin
// Separated / single-select
@Composable
fun SingleSelectButtonGroup() {
    val options = listOf("Work", "Restaurant", "Coffee")
    var selectedIndex by remember { mutableIntStateOf(0) }

    Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked         = selectedIndex == index,
                onCheckedChange = { selectedIndex = index },
            ) { Text(label) }
        }
    }
}

// Connected / multi-select with position-aware shapes
@Composable
fun ConnectedButtonGroup() {
    val checked = remember { mutableStateListOf(false, false, false) }
    val options = listOf("Bold", "Italic", "Underline")

    Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked         = checked[index],
                onCheckedChange = { checked[index] = it },
                shapes = when (index) {
                    0              -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else           -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) { Text(label) }
        }
    }
}
```

**With overflow**: wrap in `ButtonGroup` for automatic overflow handling.

```kotlin
ButtonGroup(
    overflowIndicator = { menuState ->
        FilledIconButton(onClick = {
            if (menuState.isExpanded) menuState.dismiss() else menuState.show()
        }) { Icon(Icons.Filled.MoreVert, contentDescription = "More") }
    }
) {
    repeat(8) { i -> clickableItem(onClick = {}, label = "Item $i") }
}
```

### ToggleButton -- 4 color styles

| Composable | Style | When to use |
|---|---|---|
| `ToggleButton` | Filled | Primary toggle action |
| `ElevatedToggleButton` | Elevated | Toggle that needs to stand above the surface |
| `TonalToggleButton` | Tonal | Secondary toggle in the same region |
| `OutlinedToggleButton` | Outlined | Low-emphasis toggle |

```kotlin
var checked by remember { mutableStateOf(false) }

ToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text("Filled") }
ElevatedToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text("Elevated") }
TonalToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text("Tonal") }
OutlinedToggleButton(checked = checked, onCheckedChange = { checked = it }) { Text("Outlined") }
```

### ToggleButton -- 5 sizes

Pass a `ButtonDefaults` container height constant to scale the button and all its internal
dimensions consistently.

```kotlin
val size = ButtonDefaults.ExtraLargeContainerHeight   // swap for any size constant

ToggleButton(
    checked         = checked,
    onCheckedChange = { checked = it },
    modifier        = Modifier.heightIn(size),
    shapes          = ToggleButtonDefaults.shapesFor(size),
    contentPadding  = ButtonDefaults.contentPaddingFor(size),
) {
    Icon(
        imageVector         = Icons.Filled.Edit,
        contentDescription  = null,
        modifier            = Modifier.size(ButtonDefaults.iconSizeFor(size)),
    )
    Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
    Text("Label", style = ButtonDefaults.textStyleFor(size))
}
```

Size constants: `ExtraSmallContainerHeight`, `MinHeight`, `MediumContainerHeight`,
`LargeContainerHeight`, `ExtraLargeContainerHeight`.

### Shape morphing in ToggleButton

When `shapes` is set via `ToggleButtonDefaults.shapesFor(size)`, the button automatically
morphs between rest, pressed, and checked shapes. For custom morphing:

```kotlin
ToggleButton(
    checked = checked,
    onCheckedChange = { checked = it },
    shapes = ToggleButtonDefaults.shapes(
        shape        = CircleShape,          // rest
        pressedShape = RoundedCornerShape(8.dp),
        checkedShape = RoundedCornerShape(50.dp),
    )
) { Icon(Icons.Filled.Star, null) }
```

---

## #split-button

`SplitButtonLayout` pairs a primary action button (leading) with a secondary action
button (trailing) that typically toggles a dropdown menu. The trailing button icon
rotates to signal the expanded state.

### 4 color styles

Use `SplitButtonDefaults` helpers to get the correct leading and trailing button for each style.

| Style | Leading button | Trailing button |
|---|---|---|
| Filled (default) | `SplitButtonDefaults.LeadingButton` | `SplitButtonDefaults.TrailingButton` |
| Elevated | `SplitButtonDefaults.ElevatedLeadingButton` | `SplitButtonDefaults.ElevatedTrailingButton` |
| Tonal | `SplitButtonDefaults.TonalLeadingButton` | `SplitButtonDefaults.TonalTrailingButton` |
| Outlined | `SplitButtonDefaults.OutlinedLeadingButton` | `SplitButtonDefaults.OutlinedTrailingButton` |

```kotlin
// Filled (default)
@Composable
fun FilledSplitButton() {
    var expanded by remember { mutableStateOf(false) }

    SplitButtonLayout(
        leadingButton = {
            SplitButtonDefaults.LeadingButton(onClick = { /* primary action */ }) {
                Icon(Icons.Filled.Edit, null, Modifier.size(SplitButtonDefaults.LeadingIconSize))
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Edit")
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                checked       = expanded,
                onCheckedChange = { expanded = it },
            ) {
                val rotation by animateFloatAsState(
                    targetValue   = if (expanded) 180f else 0f,
                    label         = "arrow_rotation"
                )
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "More options",
                    modifier           = Modifier.size(SplitButtonDefaults.TrailingIconSize)
                                                 .graphicsLayer { rotationZ = rotation },
                )
            }
        }
    )
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(text = { Text("Save As") }, onClick = { expanded = false })
        DropdownMenuItem(text = { Text("Save to Cloud") }, onClick = { expanded = false })
    }
}

// Elevated style -- same structure, swap the helpers
SplitButtonLayout(
    leadingButton = {
        SplitButtonDefaults.ElevatedLeadingButton(onClick = {}) { Text("Elevated") }
    },
    trailingButton = {
        SplitButtonDefaults.ElevatedTrailingButton(checked = expanded, onCheckedChange = { expanded = it }) {
            Icon(Icons.Filled.KeyboardArrowDown, null)
        }
    }
)
```

### 5 sizes for SplitButtonLayout

Use `SplitButtonDefaults` size helpers so padding, icon size, and shape scale together.

```kotlin
val size = SplitButtonDefaults.ExtraLargeContainerHeight

SplitButtonLayout(
    leadingButton = {
        SplitButtonDefaults.LeadingButton(
            onClick        = {},
            modifier       = Modifier.heightIn(size),
            shapes         = SplitButtonDefaults.leadingButtonShapesFor(size),
            contentPadding = SplitButtonDefaults.leadingButtonContentPaddingFor(size),
        ) {
            Icon(Icons.Filled.Edit, null, Modifier.size(SplitButtonDefaults.leadingButtonIconSizeFor(size)))
            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
            Text("Button", style = ButtonDefaults.textStyleFor(size))
        }
    },
    trailingButton = {
        SplitButtonDefaults.TrailingButton(
            checked         = expanded,
            onCheckedChange = { expanded = it },
            modifier        = Modifier.heightIn(size),
        ) {
            Icon(Icons.Filled.KeyboardArrowDown, null, Modifier.size(SplitButtonDefaults.TrailingIconSize))
        }
    }
)
```

Size constants on `SplitButtonDefaults`: `ExtraSmallContainerHeight`, `SmallContainerHeight`,
`MediumContainerHeight`, `LargeContainerHeight`, `ExtraLargeContainerHeight`.

---

## #icon-button

Material 3 Expressive expands the icon button family with 4 styles, 5 sizes, 3 widths,
and 2 shape variants.

### 4 color styles

| Composable | Style |
|---|---|
| `IconButton` | Standard (no container) |
| `FilledIconButton` | Filled |
| `FilledTonalIconButton` | Filled Tonal |
| `OutlinedIconButton` | Outlined |

```kotlin
IconButton(onClick = {}) { Icon(Icons.Filled.Search, null) }
FilledIconButton(onClick = {}) { Icon(Icons.Filled.Add, null) }
FilledTonalIconButton(onClick = {}) { Icon(Icons.Filled.Share, null) }
OutlinedIconButton(onClick = {}) { Icon(Icons.Filled.Edit, null) }
```

### 5 sizes

Use `IconButtonDefaults` size tokens the same way as buttons:

```kotlin
val size = IconButtonDefaults.LargeContainerSize

FilledIconButton(
    onClick  = {},
    modifier = Modifier.size(size),
) {
    Icon(Icons.Filled.Add, null, Modifier.size(IconButtonDefaults.iconSizeFor(size)))
}
```

Size constants on `IconButtonDefaults`: `XSmallContainerSize`, `SmallContainerSize`,
`MediumContainerSize` (default), `LargeContainerSize`, `XLargeContainerSize`.

### 3 widths and 2 shapes

Width variants (narrow / default / wide) and shape variants (round / square) are controlled
through the `shapes` parameter or by applying a `Modifier.width(...)`:

```kotlin
// Square shape variant
FilledIconButton(
    onClick = {},
    shape   = IconButtonDefaults.squareShape,
) { Icon(Icons.Filled.Edit, null) }

// Round shape (default)
FilledIconButton(
    onClick = {},
    shape   = IconButtonDefaults.roundShape,
) { Icon(Icons.Filled.Edit, null) }
```

---

## #fab

### Standard FAB variants

```kotlin
SmallFloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "Add") }
FloatingActionButton(onClick = {})     { Icon(Icons.Filled.Add, "Add") }
MediumFloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "Add") }  // new in M3E
LargeFloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "Add") }
```

### Extended FAB -- 3 sizes (new in M3E)

```kotlin
SmallExtendedFloatingActionButton(
    onClick = {},
    icon    = { Icon(Icons.Filled.Edit, null) },
    text    = { Text("Edit") },
)
MediumExtendedFloatingActionButton(
    onClick = {},
    icon    = { Icon(Icons.Filled.Share, null) },
    text    = { Text("Share") },
)
LargeExtendedFloatingActionButton(
    onClick = {},
    icon    = { Icon(Icons.Filled.Add, null) },
    text    = { Text("Create") },
)
```

Guidelines: use `SmallExtendedFAB` inside toolbars or constrained layouts; use
`LargeExtendedFAB` as the dominant action on empty-state screens.

### FloatingActionButtonMenu

Replaces the Speed Dial pattern. The FAB morphs shape when the menu opens.

```kotlin
@Composable
fun FABMenuSample() {
    var expanded by rememberSaveable { mutableStateOf(false) }

    FloatingActionButtonMenu(
        expanded = expanded,
        button   = {
            ToggleFloatingActionButton(
                checked         = expanded,
                onCheckedChange = { expanded = it },
            ) {
                Icon(
                    imageVector        = if (expanded) Icons.Filled.Close else Icons.Filled.Add,
                    contentDescription = if (expanded) "Close" else "Open menu",
                )
            }
        }
    ) {
        FloatingActionButtonMenuItem(
            onClick = { expanded = false },
            icon    = { Icon(Icons.Filled.Share, null) },
            text    = { Text("Share") },
        )
        FloatingActionButtonMenuItem(
            onClick = { expanded = false },
            icon    = { Icon(Icons.Filled.Edit, null) },
            text    = { Text("Edit") },
        )
    }
}
```

### ToggleFloatingActionButton

Standalone FAB that morphs between circle (unchecked) and squircle (checked).

```kotlin
var checked by rememberSaveable { mutableStateOf(false) }

ToggleFloatingActionButton(checked = checked, onCheckedChange = { checked = it }) {
    Icon(
        if (checked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
        contentDescription = "Favorite",
    )
}
```

---

## #floating-toolbars

Floating toolbars replace `BottomAppBar` in M3E. They hover above the content and react
to scroll by collapsing or hiding.

### HorizontalFloatingToolbar -- 2 color styles

| Style function | Appearance |
|---|---|
| `FloatingToolbarDefaults.standardFloatingToolbarColors()` | Surface-colored container |
| `FloatingToolbarDefaults.vibrantFloatingToolbarColors()` | Primary-colored container |

```kotlin
@Composable
fun HorizontalFloatingToolbarSample() {
    val scrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        state = rememberFloatingToolbarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(contentPadding = padding) { items(50) { Text("Item $it") } }

            HorizontalFloatingToolbar(
                modifier      = Modifier.align(Alignment.BottomCenter),
                expanded      = true,
                scrollBehavior = scrollBehavior,
                // Vibrant style: primary background
                colors        = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
                floatingActionButton = {
                    FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = {}) {
                        Icon(Icons.Filled.Add, contentDescription = "Add")
                    }
                },
                content = {
                    IconButton(onClick = {}) { Icon(Icons.Filled.Search, null) }
                    IconButton(onClick = {}) { Icon(Icons.Filled.Share, null) }
                    IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null) }
                }
            )
        }
    }
}
```

Scroll behaviors available: `exitAlwaysScrollBehavior`, `exitUntilCollapsedScrollBehavior`.

### VerticalFloatingToolbar

Use on tablets or as a side-rail action bar. Reacts to vertical scroll.

```kotlin
@Composable
fun VerticalFloatingToolbarSample() {
    var expanded by rememberSaveable { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.floatingToolbarVerticalNestedScroll(
                expanded   = expanded,
                onExpand   = { expanded = true },
                onCollapse = { expanded = false },
            )
        ) { items(50) { Text("Item $it") } }

        VerticalFloatingToolbar(
            modifier       = Modifier.align(Alignment.CenterEnd).offset(x = (-16).dp),
            expanded       = expanded,
            leadingContent = {
                IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null) }
            },
            trailingContent = {
                IconButton(onClick = {}) { Icon(Icons.Filled.Settings, null) }
            },
            content = {
                IconButton(onClick = {}) { Icon(Icons.Filled.Edit, null) }
                IconButton(onClick = {}) { Icon(Icons.Filled.Share, null) }
            }
        )
    }
}
```

### FlexibleBottomAppBar

Collapses its lower row when the user scrolls down. Use when migrating from `BottomAppBar`
and you want a traditional docked bar rather than a floating toolbar.

```kotlin
@Composable
fun FlexibleBottomAppBarSample() {
    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    Scaffold(
        modifier  = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        bottomBar = {
            FlexibleBottomAppBar(
                scrollBehavior      = scrollBehavior,
                actions             = {
                    IconButton(onClick = {}) { Icon(Icons.Filled.Check, "Done") }
                    IconButton(onClick = {}) { Icon(Icons.Filled.Edit, "Edit") }
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "Add") }
                }
            )
        }
    ) { /* content */ }
}
```

---

## #top-bars

M3E top bars collapse their lower section on scroll using the standard
`exitUntilCollapsedScrollBehavior`.

```kotlin
val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar   = {
        MediumFlexibleTopAppBar(
            title          = { Text("Title") },
            subtitle       = { Text("Subtitle") },
            navigationIcon = { IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            actions        = { IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null) } },
            scrollBehavior = scrollBehavior,
        )
    }
) { /* content */ }
```

| Composable | Collapsed height | Expanded height | Subtitle |
|---|---|---|---|
| `MediumFlexibleTopAppBar` | 64 dp | 112 dp | Yes |
| `LargeFlexibleTopAppBar` | 64 dp | 152 dp | Yes |
| `TwoRowsTopAppBar` | 64 dp | explicit two rows | Yes |

### AppBarRow

A horizontal layout for app bar actions that automatically handles overflow. When actions
don't fit, they collapse into an overflow menu indicator.

```kotlin
@Composable
fun AppBarRowSample() {
    TopAppBar(
        title = { Text("Title") },
        actions = {
            AppBarRow(
                overflowIndicator = { menuState ->
                    IconButton(onClick = { menuState.show() }) {
                        Icon(Icons.Filled.MoreVert, "More")
                    }
                }
            ) {
                clickableItem(onClick = {}) { Icon(Icons.Filled.Search, "Search") }
                clickableItem(onClick = {}) { Icon(Icons.Filled.Share, "Share") }
                clickableItem(onClick = {}) { Icon(Icons.Filled.Edit, "Edit") }
                clickableItem(onClick = {}) { Icon(Icons.Filled.Delete, "Delete") }
            }
        }
    )
}
```

### AppBarColumn

A vertical layout variant of `AppBarRow` for vertical app bars or side panels. Same
overflow handling behavior.

```kotlin
AppBarColumn(
    overflowIndicator = { menuState ->
        IconButton(onClick = { menuState.show() }) {
            Icon(Icons.Filled.MoreVert, "More")
        }
    }
) {
    clickableItem(onClick = {}) { Icon(Icons.Filled.Edit, "Edit") }
    clickableItem(onClick = {}) { Icon(Icons.Filled.Share, "Share") }
    clickableItem(onClick = {}) { Icon(Icons.Filled.Delete, "Delete") }
}
```

---

## #navigation

### WideNavigationRail

Expandable side navigation rail for tablets and foldables. Collapses to icon-only;
expands to show labels alongside icons.

```kotlin
@Composable
fun WideNavigationRailSample() {
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()
    var selected by remember { mutableIntStateOf(0) }
    val items   = listOf("Home", "Search", "Settings")
    val icons   = listOf(Icons.Outlined.Home, Icons.Outlined.Search, Icons.Outlined.Settings)
    val selIcons = listOf(Icons.Filled.Home, Icons.Filled.Search, Icons.Filled.Settings)

    WideNavigationRail(
        state  = state,
        header = {
            IconButton(onClick = {
                scope.launch {
                    if (state.targetValue == WideNavigationRailValue.Expanded)
                        state.collapse() else state.expand()
                }
            }) {
                Icon(
                    if (state.targetValue == WideNavigationRailValue.Expanded)
                        Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
                    contentDescription = "Toggle rail",
                )
            }
        }
    ) {
        items.forEachIndexed { i, label ->
            WideNavigationRailItem(
                railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
                selected     = selected == i,
                onClick      = { selected = i },
                icon         = { Icon(if (selected == i) selIcons[i] else icons[i], label) },
                label        = { Text(label) },
            )
        }
    }
}
```

### ModalWideNavigationRail

A modal variant of `WideNavigationRail` that overlays content with a scrim when expanded.
Use when the rail should not push content aside but instead float over it.

```kotlin
@Composable
fun ModalWideNavigationRailSample() {
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()
    var selected by remember { mutableIntStateOf(0) }

    ModalWideNavigationRail(
        state  = state,
        header = {
            IconButton(onClick = {
                scope.launch {
                    if (state.targetValue == WideNavigationRailValue.Expanded)
                        state.collapse() else state.expand()
                }
            }) {
                Icon(Icons.Filled.Menu, "Toggle")
            }
        }
    ) {
        WideNavigationRailItem(
            railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
            selected     = selected == 0,
            onClick      = { selected = 0 },
            icon         = { Icon(Icons.Filled.Home, null) },
            label        = { Text("Home") },
        )
        WideNavigationRailItem(
            railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
            selected     = selected == 1,
            onClick      = { selected = 1 },
            icon         = { Icon(Icons.Filled.Settings, null) },
            label        = { Text("Settings") },
        )
    }
}
```

### ShortNavigationBar

A compact navigation bar with smaller items. Use when screen space is limited or when
you want a more subtle bottom navigation.

```kotlin
@Composable
fun ShortNavigationBarSample() {
    var selected by remember { mutableIntStateOf(0) }

    ShortNavigationBar {
        ShortNavigationBarItem(
            selected = selected == 0,
            onClick  = { selected = 0 },
            icon     = { Icon(Icons.Filled.Home, null) },
            label    = { Text("Home") },
        )
        ShortNavigationBarItem(
            selected = selected == 1,
            onClick  = { selected = 1 },
            icon     = { Icon(Icons.Filled.Search, null) },
            label    = { Text("Search") },
        )
        ShortNavigationBarItem(
            selected = selected == 2,
            onClick  = { selected = 2 },
            icon     = { Icon(Icons.Filled.Person, null) },
            label    = { Text("Profile") },
        )
    }
}
```

### NavigationBar

Standard bottom navigation bar for phones. Displays 3–5 destinations with icons and labels.

```kotlin
@Composable
fun NavigationBarSample() {
    var selected by remember { mutableIntStateOf(0) }

    NavigationBar {
        NavigationBarItem(
            selected = selected == 0,
            onClick  = { selected = 0 },
            icon     = { Icon(Icons.Filled.Home, null) },
            label    = { Text("Home") },
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick  = { selected = 1 },
            icon     = { Icon(Icons.Filled.Favorite, null) },
            label    = { Text("Favorites") },
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick  = { selected = 2 },
            icon     = { Icon(Icons.Filled.Person, null) },
            label    = { Text("Profile") },
        )
    }
}
```

### NavigationRail

Standard vertical navigation rail for medium-width screens. Use `WideNavigationRail` for
the M3E expandable variant.

```kotlin
@Composable
fun NavigationRailSample() {
    var selected by remember { mutableIntStateOf(0) }

    NavigationRail {
        NavigationRailItem(
            selected = selected == 0,
            onClick  = { selected = 0 },
            icon     = { Icon(Icons.Filled.Home, null) },
            label    = { Text("Home") },
        )
        NavigationRailItem(
            selected = selected == 1,
            onClick  = { selected = 1 },
            icon     = { Icon(Icons.Filled.Search, null) },
            label    = { Text("Search") },
        )
    }
}
```

### ModalNavigationDrawer

A modal drawer that slides in from the start edge with a scrim overlay.

```kotlin
@Composable
fun ModalNavigationDrawerSample() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()
                NavigationDrawerItem(
                    label    = { Text("Home") },
                    selected = true,
                    onClick  = { scope.launch { drawerState.close() } },
                    icon     = { Icon(Icons.Filled.Home, null) },
                )
                NavigationDrawerItem(
                    label    = { Text("Settings") },
                    selected = false,
                    onClick  = { scope.launch { drawerState.close() } },
                    icon     = { Icon(Icons.Filled.Settings, null) },
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, "Menu")
                        }
                    }
                )
            }
        ) { /* content */ }
    }
}
```

### DismissibleNavigationDrawer & PermanentNavigationDrawer

| Variant | Behavior | Use case |
|---|---|---|
| `ModalNavigationDrawer` | Overlays content with scrim | Phones, compact screens |
| `DismissibleNavigationDrawer` | Pushes content aside, can be dismissed | Medium screens |
| `PermanentNavigationDrawer` | Always visible, never dismisses | Large screens, desktop |

```kotlin
// Permanent drawer (always visible)
PermanentNavigationDrawer(
    drawerContent = {
        PermanentDrawerSheet {
            NavigationDrawerItem(
                label = { Text("Home") }, selected = true, onClick = {},
                icon  = { Icon(Icons.Filled.Home, null) },
            )
        }
    }
) { /* main content */ }
```

### NavigationSuiteScaffold

Automatically switches between `NavigationBar` (phone), `NavigationRail` (medium), and
`NavigationDrawer` (large) based on window size.

```kotlin
NavigationSuiteScaffold(
    navigationSuiteItems = {
        items.forEachIndexed { i, label ->
            item(
                icon     = { Icon(icons[i], null) },
                label    = { Text(label) },
                selected = selected == i,
                onClick  = { selected = i },
            )
        }
    }
) { /* screen content */ }
```

### Navigation variant selection guide

| Screen size | Recommended component |
|---|---|
| Compact (phone) | `NavigationBar` or `ShortNavigationBar` |
| Medium (tablet portrait) | `NavigationRail` or `WideNavigationRail` |
| Expanded (tablet landscape, desktop) | `WideNavigationRail`, `ModalWideNavigationRail`, or `PermanentNavigationDrawer` |
| Adaptive (all sizes) | `NavigationSuiteScaffold` |

---

## #loading

### LoadingIndicator and ContainedLoadingIndicator

Use for short indeterminate waits under ~5 seconds. The indicator morphs through a
sequence of abstract shapes.

```kotlin
// Plain indicator
LoadingIndicator()

// With fewer shapes (faster cycle)
LoadingIndicator(
    polygons = LoadingIndicatorDefaults.IndeterminateIndicatorPolygons.take(2)
)

// Contained (colored background box)
ContainedLoadingIndicator()

// Custom container color
ContainedLoadingIndicator(containerColor = MaterialTheme.colorScheme.primaryContainer)
```

### CircularWavyProgressIndicator

Use for longer indeterminate waits or for determinate progress.

```kotlin
// Indeterminate
CircularWavyProgressIndicator()

// Determinate with custom amplitude
CircularWavyProgressIndicator(
    progress    = { 0.7f },
    amplitude   = { 0.5f },
    waveLength  = 25.dp,
    modifier    = Modifier.size(48.dp),
)
```

### LinearWavyProgressIndicator

```kotlin
LinearWavyProgressIndicator(modifier = Modifier.fillMaxWidth())               // indeterminate
LinearWavyProgressIndicator(progress = { 0.6f }, modifier = Modifier.fillMaxWidth()) // determinate
```

---

## #lists

### SegmentedListItem

Use for grouped list items where the top, middle, and bottom items have distinct corner radii
to form a visual block. Assign shapes manually based on position.

```kotlin
@Composable
fun SegmentedList(items: List<String>) {
    val topShape    = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    val middleShape = RoundedCornerShape(4.dp)
    val bottomShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)

    Column {
        items.forEachIndexed { i, label ->
            SegmentedListItem(
                selected          = false,
                onClick           = {},
                headlineContent   = { Text(label) },
                supportingContent = { Text("Description") },
                leadingContent    = { Icon(Icons.Filled.Star, null) },
                shape = when (i) {
                    0              -> topShape
                    items.lastIndex -> bottomShape
                    else           -> middleShape
                }
            )
            if (i < items.lastIndex) Spacer(Modifier.height(2.dp))
        }
    }
}
```

---

## #text-fields

### ScrollField

Multi-line text field with internal vertical scroll. Use for notes, bios, or any free-form
text input that may exceed a few lines.

```kotlin
var text by remember { mutableStateOf("") }

ScrollField(
    value         = text,
    onValueChange = { text = it },
    modifier      = Modifier.fillMaxWidth().height(120.dp),
    placeholder   = { Text("Write a note...") },
    label         = { Text("Notes") },
)
```

### SecureTextField

A text field that masks input for passwords and sensitive data. Provides a visibility
toggle by default.

```kotlin
var password by remember { mutableStateOf("") }

SecureTextField(
    value         = password,
    onValueChange = { password = it },
    modifier      = Modifier.fillMaxWidth(),
    label         = { Text("Password") },
)
```

### OutlinedSecureTextField

Outlined variant of `SecureTextField` with the same masking behavior.

```kotlin
var password by remember { mutableStateOf("") }

OutlinedSecureTextField(
    value         = password,
    onValueChange = { password = it },
    modifier      = Modifier.fillMaxWidth(),
    label         = { Text("Password") },
)
```

---

## #search

Material 3 provides a family of search bar composables for different use cases and screen
sizes. The collapsed `SearchBar` or `TopSearchBar` is paired with an expanded variant to
show results.

### SearchBar (collapsed)

The base collapsed search bar. Use in conjunction with `ExpandedFullScreenSearchBar` or
`ExpandedDockedSearchBar` to display results when expanded.

```kotlin
@Composable
fun SearchBarSample() {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query            = query,
                onQueryChange    = { query = it },
                onSearch         = { expanded = false },
                expanded         = expanded,
                onExpandedChange = { expanded = it },
                placeholder      = { Text("Search...") },
                leadingIcon      = { Icon(Icons.Default.Search, null) },
                trailingIcon     = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    }
                },
            )
        },
        expanded         = expanded,
        onExpandedChange = { expanded = it },
    ) {
        // Suggestion content when expanded
        LazyColumn {
            items(suggestions) { suggestion ->
                ListItem(
                    headlineContent = { Text(suggestion) },
                    leadingContent  = { Icon(Icons.Default.History, null) },
                    modifier        = Modifier.clickable { query = suggestion; expanded = false }
                )
            }
        }
    }
}
```

### TopSearchBar

A `SearchBar` with additional handling for top app bar behavior (window insets, scrolling).
Use as the `topBar` of a `Scaffold` to keep the search bar pinned at the top.

```kotlin
@Composable
fun TopSearchBarSample() {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopSearchBar(
                scrollBehavior = scrollBehavior,
                inputField = {
                    SearchBarDefaults.InputField(
                        query            = query,
                        onQueryChange    = { query = it },
                        onSearch         = { expanded = false },
                        expanded         = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder      = { Text("Search...") },
                        leadingIcon      = { Icon(Icons.Default.Search, null) },
                    )
                },
                expanded         = expanded,
                onExpandedChange = { expanded = it },
            ) {
                // Results content
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) { /* main content */ }
    }
}
```

Scroll behaviors: `enterAlwaysSearchBarScrollBehavior` (hides on scroll up, shows on scroll
down), `pinnedSearchBarScrollBehavior` (always visible).

### DockedSearchBar

Displays search results in a bounded popup below the input field. Alternative to `SearchBar`
when full-screen expansion is undesirable on large screens (tablets).

```kotlin
@Composable
fun DockedSearchBarSample() {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query            = query,
                onQueryChange    = { query = it },
                onSearch         = { expanded = false },
                expanded         = expanded,
                onExpandedChange = { expanded = it },
                placeholder      = { Text("Search...") },
                leadingIcon      = { Icon(Icons.Default.Search, null) },
            )
        },
        expanded         = expanded,
        onExpandedChange = { expanded = it },
    ) {
        // Results shown in a bounded popup below the bar
        LazyColumn { items(results) { Text(it) } }
    }
}
```

### ExpandedDockedSearchBar

Represents the expanded state of a docked search bar. Shown as a popup over the collapsed
search bar. Use when you need to separate the collapsed and expanded states into different
composables.

```kotlin
ExpandedDockedSearchBar(
    inputField = {
        SearchBarDefaults.InputField(
            query            = query,
            onQueryChange    = { query = it },
            onSearch         = { expanded = false },
            expanded         = expanded,
            onExpandedChange = { expanded = it },
            placeholder      = { Text("Search...") },
            leadingIcon      = { Icon(Icons.Default.Search, null) },
        )
    },
    expanded         = expanded,
    onExpandedChange = { expanded = it },
) {
    // Results content
}
```

### ExpandedDockedSearchBarWithGap

Search bar that expands inline with a visible gap between the input field and the results
list. Preferred for M3E screens where you want visual separation between input and results.

```kotlin
@Composable
fun DockedSearchWithGapSample() {
    var query  by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    ExpandedDockedSearchBarWithGap(
        inputField = {
            SearchBarDefaults.InputField(
                query            = query,
                onQueryChange    = { query = it },
                onSearch         = { active = false },
                expanded         = active,
                onExpandedChange = { active = it },
                placeholder      = { Text("Search...") },
                leadingIcon      = { Icon(Icons.Default.Search, null) },
            )
        },
        expanded         = active,
        onExpandedChange = { active = it },
    ) {
        Text("Results for: $query")
    }
}
```

### ExpandedFullScreenSearchBar

Full-screen dialog search. The search results overlay the entire screen. Use on phones
where full-screen expansion is the expected behavior.

```kotlin
ExpandedFullScreenSearchBar(
    inputField = {
        SearchBarDefaults.InputField(
            query            = query,
            onQueryChange    = { query = it },
            onSearch         = { expanded = false },
            expanded         = expanded,
            onExpandedChange = { expanded = it },
            placeholder      = { Text("Search...") },
            leadingIcon      = { Icon(Icons.Default.Search, null) },
        )
    },
    expanded         = expanded,
    onExpandedChange = { expanded = it },
) { /* full-screen results */ }
```

### ExpandedFullScreenContainedSearchBar

Full-screen overlay search with a contained surface. Use when search is the primary flow
of a screen and you want the results to appear within a contained card-like surface.

```kotlin
ExpandedFullScreenContainedSearchBar(
    inputField = {
        SearchBarDefaults.InputField(
            query            = query,
            onQueryChange    = { query = it },
            onSearch         = { expanded = false },
            expanded         = expanded,
            onExpandedChange = { expanded = it },
            placeholder      = { Text("Search...") },
            leadingIcon      = { Icon(Icons.Default.Search, null) },
        )
    },
    expanded         = expanded,
    onExpandedChange = { expanded = it },
) { /* results */ }
```

### AppBarWithSearch

A `SearchBar` with top app bar behavior (window insets, scrolling). Similar to
`TopSearchBar` but designed to be used as a full app bar replacement with integrated search.

```kotlin
@Composable
fun AppBarWithSearchSample() {
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                inputField = {
                    SearchBarDefaults.InputField(
                        query            = query,
                        onQueryChange    = { query = it },
                        onSearch         = { expanded = false },
                        expanded         = expanded,
                        onExpandedChange = { expanded = it },
                        placeholder      = { Text("Search...") },
                        leadingIcon      = { Icon(Icons.Default.Menu, null) },
                        trailingIcon     = { Icon(Icons.Default.AccountCircle, null) },
                    )
                },
                expanded         = expanded,
                onExpandedChange = { expanded = it },
            ) { /* results */ }
        }
    ) { padding -> /* content */ }
}
```

### Search variant selection guide

| Variant | Screen size | Behavior |
|---|---|---|
| `SearchBar` | Any | Standalone collapsed bar, pairs with expanded variant |
| `TopSearchBar` | Any | Pinned to top of Scaffold, handles insets/scroll |
| `AppBarWithSearch` | Any | Full app bar replacement with integrated search |
| `DockedSearchBar` | Medium/Large | Results in bounded popup below bar |
| `ExpandedDockedSearchBar` | Medium/Large | Expanded state as popup over collapsed bar |
| `ExpandedDockedSearchBarWithGap` | Medium/Large | Expanded with gap between input and results |
| `ExpandedFullScreenSearchBar` | Compact | Full-screen dialog overlay |
| `ExpandedFullScreenContainedSearchBar` | Compact | Full-screen with contained surface |

---

## #menus

### DropdownMenu

Standard dropdown menu anchored to a button or icon.

```kotlin
@Composable
fun DropdownMenuSample() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.MoreVert, "Options")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text         = { Text("Edit") },
                onClick      = { expanded = false },
                leadingIcon  = { Icon(Icons.Filled.Edit, null) },
            )
            DropdownMenuItem(
                text         = { Text("Delete") },
                onClick      = { expanded = false },
                leadingIcon  = { Icon(Icons.Filled.Delete, null) },
            )
        }
    }
}
```

### DropdownMenuGroup

Group related dropdown items with a visual separator between groups.

```kotlin
DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
    DropdownMenuGroup {
        DropdownMenuItem(text = { Text("Edit") }, onClick = {})
        DropdownMenuItem(text = { Text("Copy") }, onClick = {})
    }
    HorizontalDivider()
    DropdownMenuGroup {
        DropdownMenuItem(text = { Text("Delete") }, onClick = {})
    }
}
```

### DropdownMenuPopup

Use `DropdownMenuPopup` when you need anchor-relative positioning, for example to build
cascading submenus:

```kotlin
DropdownMenuPopup(
    popupPositionProvider = rememberDropdownMenuPopupPositionProvider(
        DropdownMenuPopupPositionProviders.StartToEnd   // submenu appears to the right
    ),
    onDismissRequest = { /* dismiss */ }
) {
    // submenu content
}
```

### ExposedDropdownMenuBox

A dropdown menu that is anchored to a text field, commonly used for autocomplete or
selection from a predefined list.

```kotlin
@Composable
fun ExposedDropdownSample() {
    val options = listOf("Option 1", "Option 2", "Option 3")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded         = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value         = selectedText,
            onValueChange = {},
            readOnly      = true,
            label         = { Text("Select") },
            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier      = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(option) },
                    onClick = { selectedText = option; expanded = false },
                )
            }
        }
    }
}
```

---

## #sliders

### Slider

Standard horizontal slider for selecting a single value from a range.

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

Slider(
    value         = value,
    onValueChange = { value = it },
    modifier      = Modifier.fillMaxWidth(),
)

// With steps and value range
Slider(
    value         = value,
    onValueChange = { value = it },
    valueRange    = 0f..100f,
    steps         = 9,  // 10 discrete positions
)
```

### RangeSlider

Allows selecting a range with two thumbs (start and end values).

```kotlin
var range by remember { mutableStateOf(20f..80f) }

RangeSlider(
    value         = range,
    onValueChange = { range = it },
    valueRange    = 0f..100f,
    modifier      = Modifier.fillMaxWidth(),
)
```

### VerticalSlider

Same semantics as `Slider` but rendered vertically.

```kotlin
var value by remember { mutableFloatStateOf(0.5f) }

VerticalSlider(
    value         = value,
    onValueChange = { value = it },
    modifier      = Modifier.height(200.dp),
)
```


---

## #chips

Chips are compact elements that represent an attribute, action, or filter.

### 4 chip types

| Composable | Purpose | Selectable |
|---|---|---|
| `AssistChip` | Represent an action (e.g., "Add to calendar") | No |
| `FilterChip` | Filter content from a set of options | Yes |
| `InputChip` | Represent user input (e.g., contact in a field) | Dismissible |
| `SuggestionChip` | Dynamically generated suggestions | No |

### Elevated variants

Each chip type has an elevated variant for more visual prominence:
`ElevatedAssistChip`, `ElevatedFilterChip`, `ElevatedSuggestionChip`.

```kotlin
// AssistChip
AssistChip(
    onClick      = {},
    label        = { Text("Add to calendar") },
    leadingIcon  = { Icon(Icons.Filled.Add, null, Modifier.size(AssistChipDefaults.IconSize)) },
)

// ElevatedAssistChip
ElevatedAssistChip(
    onClick = {},
    label   = { Text("Elevated assist") },
)

// FilterChip (selectable)
var selected by remember { mutableStateOf(false) }
FilterChip(
    selected      = selected,
    onClick       = { selected = !selected },
    label         = { Text("Filter") },
    leadingIcon   = if (selected) {
        { Icon(Icons.Filled.Done, null, Modifier.size(FilterChipDefaults.IconSize)) }
    } else null,
)

// ElevatedFilterChip
ElevatedFilterChip(
    selected = selected,
    onClick  = { selected = !selected },
    label    = { Text("Elevated filter") },
)

// InputChip (dismissible)
var shown by remember { mutableStateOf(true) }
if (shown) {
    InputChip(
        selected       = true,
        onClick        = {},
        label          = { Text("John Doe") },
        avatar         = { Icon(Icons.Filled.Person, null) },
        trailingIcon   = {
            IconButton(onClick = { shown = false }, Modifier.size(InputChipDefaults.IconSize)) {
                Icon(Icons.Filled.Close, "Remove")
            }
        },
    )
}

// SuggestionChip
SuggestionChip(
    onClick = {},
    label   = { Text("Suggested reply") },
)

// ElevatedSuggestionChip
ElevatedSuggestionChip(
    onClick = {},
    label   = { Text("Elevated suggestion") },
)
```

### Chip group layout

Use `FlowRow` to lay out multiple chips that wrap to the next line:

```kotlin
FlowRow(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement   = Arrangement.spacedBy(8.dp),
) {
    filters.forEach { filter ->
        FilterChip(
            selected = filter.selected,
            onClick  = { filter.toggle() },
            label    = { Text(filter.name) },
        )
    }
}
```

---

## #cards

Cards contain content and actions about a single subject.

### 3 card variants

| Composable | Style | When to use |
|---|---|---|
| `Card` | Filled | Default container for grouped content |
| `ElevatedCard` | Elevated | Content that needs to stand above the surface |
| `OutlinedCard` | Outlined | Low-emphasis container with a border |

```kotlin
// Filled Card
Card(
    modifier = Modifier.fillMaxWidth(),
    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
) {
    Column(Modifier.padding(16.dp)) {
        Text("Title", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("Card content goes here.")
    }
}

// Elevated Card
ElevatedCard(
    modifier  = Modifier.fillMaxWidth(),
    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
) {
    Column(Modifier.padding(16.dp)) {
        Text("Elevated Card", style = MaterialTheme.typography.titleMedium)
        Text("With shadow elevation.")
    }
}

// Outlined Card
OutlinedCard(
    modifier = Modifier.fillMaxWidth(),
    border   = CardDefaults.outlinedCardBorder(),
) {
    Column(Modifier.padding(16.dp)) {
        Text("Outlined Card", style = MaterialTheme.typography.titleMedium)
        Text("With a subtle border.")
    }
}
```

### Clickable cards

```kotlin
ElevatedCard(
    onClick  = { /* navigate */ },
    modifier = Modifier.fillMaxWidth(),
) {
    ListItem(
        headlineContent   = { Text("Clickable card") },
        supportingContent = { Text("Tap to navigate") },
        leadingContent    = { Icon(Icons.Filled.Article, null) },
    )
}
```

---

## #dialogs

### AlertDialog

Standard Material 3 alert dialog with title, text, and action buttons.

```kotlin
@Composable
fun AlertDialogSample() {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon             = { Icon(Icons.Filled.Warning, null) },
            title            = { Text("Confirm deletion") },
            text             = { Text("This action cannot be undone. Are you sure?") },
            confirmButton    = {
                TextButton(onClick = { showDialog = false }) { Text("Delete") }
            },
            dismissButton    = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        )
    }
}
```

### BasicAlertDialog

A lower-level dialog composable that provides only the dialog container without
predefined layout. Use for fully custom dialog content.

```kotlin
@Composable
fun BasicAlertDialogSample() {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        BasicAlertDialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 6.dp,
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Custom Dialog", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(16.dp))
                    Text("Fully custom content here.")
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("OK") }
                    }
                }
            }
        }
    }
}
```

### DatePickerDialog

Container dialog for `DatePicker`. Provides confirm/dismiss buttons and proper dialog
styling.

```kotlin
@Composable
fun DatePickerDialogSample() {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton    = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            },
            dismissButton    = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
```

---

## #bottom-sheets

### ModalBottomSheet

A modal sheet that slides up from the bottom with a scrim overlay. Use for actions,
selections, or supplementary content.

```kotlin
@Composable
fun ModalBottomSheetSample() {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState       = sheetState,
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Bottom Sheet", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text("Sheet content goes here.")
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick  = { showSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Close") }
                Spacer(Modifier.height(32.dp)) // bottom padding for gesture bar
            }
        }
    }

    Button(onClick = { showSheet = true }) { Text("Show Sheet") }
}
```

### ModalBottomSheet with partial expansion

```kotlin
val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

ModalBottomSheet(
    onDismissRequest = { showSheet = false },
    sheetState       = sheetState,
) {
    // Content -- sheet will stop at partial height first
    LazyColumn {
        items(50) { Text("Item $it", Modifier.padding(16.dp)) }
    }
}
```

### BottomSheetScaffold

A scaffold with a persistent (non-modal) bottom sheet integrated into the layout.

```kotlin
@Composable
fun BottomSheetScaffoldSample() {
    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState   = scaffoldState,
        sheetPeekHeight = 64.dp,
        sheetContent    = {
            Column(Modifier.padding(16.dp)) {
                Text("Persistent Sheet", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Drag up to expand.")
                Spacer(Modifier.height(200.dp))
            }
        },
        topBar = {
            TopAppBar(title = { Text("Bottom Sheet Scaffold") })
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Text("Main content")
        }
    }
}
```

---

## #tooltips

### PlainTooltip

Brief descriptive text for icon buttons. Shown on long press (mobile) or hover (desktop).

```kotlin
@Composable
fun PlainTooltipSample() {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip          = { PlainTooltip { Text("Add item") } },
        state            = rememberTooltipState(),
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }
}
```

### RichTooltip

Detailed tooltip with optional title, body text, and action button.

```kotlin
@Composable
fun RichTooltipSample() {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title  = { Text("Feature info") },
                action = {
                    TextButton(onClick = { scope.launch { tooltipState.dismiss() } }) {
                        Text("Got it")
                    }
                },
            ) {
                Text("This button creates a new document in your workspace.")
            }
        },
        state = tooltipState,
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Info, contentDescription = "Info")
        }
    }
}
```

---

## #snackbar

### Snackbar and SnackbarHost

Snackbars provide brief messages at the bottom of the screen with an optional action.

```kotlin
@Composable
fun SnackbarSample() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Button(
            onClick = {
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message     = "Item deleted",
                        actionLabel = "Undo",
                        duration    = SnackbarDuration.Short,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        // Undo action
                    }
                }
            },
            modifier = Modifier.padding(padding),
        ) { Text("Delete item") }
    }
}
```

---

## #badges

### Badge and BadgedBox

Badges convey dynamic information such as notification counts.

```kotlin
// Simple dot badge
BadgedBox(badge = { Badge() }) {
    Icon(Icons.Filled.Notifications, "Notifications")
}

// Badge with count
BadgedBox(
    badge = { Badge { Text("5") } }
) {
    Icon(Icons.Filled.Mail, "Mail")
}

// In a NavigationBarItem
NavigationBarItem(
    selected = true,
    onClick  = {},
    icon     = {
        BadgedBox(badge = { Badge { Text("3") } }) {
            Icon(Icons.Filled.Home, null)
        }
    },
    label = { Text("Home") },
)
```

---

## #carousel

Carousels showcase a collection of related content with different layout strategies.

### HorizontalMultiBrowseCarousel

Displays many items at once for quick browsing. Items have different sizes -- large focal
items and smaller side items.

```kotlin
@Composable
fun MultiBrowseCarouselSample() {
    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5")

    HorizontalMultiBrowseCarousel(
        state         = rememberCarouselState { items.size },
        preferredItemWidth = 240.dp,
        modifier      = Modifier.fillMaxWidth().height(200.dp),
    ) { index ->
        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = MaterialTheme.shapes.large,
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(items[index], style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
```

### HorizontalUncontainedCarousel

Items are a single size and flow past the edge of the screen. The last visible item is
cut off to indicate more content.

```kotlin
@Composable
fun UncontainedCarouselSample() {
    HorizontalUncontainedCarousel(
        state    = rememberCarouselState { 10 },
        itemWidth = 200.dp,
        modifier = Modifier.fillMaxWidth().height(180.dp),
    ) { index ->
        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = MaterialTheme.shapes.large,
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Card $index")
            }
        }
    }
}
```

### HorizontalCenteredHeroCarousel

Centers one large item between two smaller items. Recommended for spotlighting content
like movie thumbnails or featured articles.

```kotlin
@Composable
fun HeroCarouselSample() {
    HorizontalCenteredHeroCarousel(
        state    = rememberCarouselState { 5 },
        modifier = Modifier.fillMaxWidth().height(240.dp),
    ) { index ->
        Card(
            modifier = Modifier.fillMaxSize(),
            shape    = MaterialTheme.shapes.extraLarge,
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Hero $index", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}
```

### Carousel variant selection guide

| Variant | Use case |
|---|---|
| `HorizontalMultiBrowseCarousel` | Browsing many items (photos, albums) |
| `HorizontalUncontainedCarousel` | Single-size items with text below |
| `HorizontalCenteredHeroCarousel` | Spotlighting featured content |

---

## #segmented-button

Segmented buttons let users select options, toggle views, or sort elements.

### SingleChoiceSegmentedButtonRow

Only one option can be selected at a time (radio-like behavior).

```kotlin
@Composable
fun SingleChoiceSegmentedButtonSample() {
    val options = listOf("Day", "Week", "Month")
    var selectedIndex by remember { mutableIntStateOf(0) }

    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                selected = selectedIndex == index,
                onClick  = { selectedIndex = index },
                shape    = SegmentedButtonDefaults.itemShape(index, options.size),
                icon     = { SegmentedButtonDefaults.ActiveIcon() },
            ) {
                Text(label)
            }
        }
    }
}
```

### MultiChoiceSegmentedButtonRow

Multiple options can be selected simultaneously (checkbox-like behavior).

```kotlin
@Composable
fun MultiChoiceSegmentedButtonSample() {
    val options = listOf("Bold", "Italic", "Underline")
    val checked = remember { mutableStateListOf(false, false, false) }

    MultiChoiceSegmentedButtonRow {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                checked  = checked[index],
                onCheckedChange = { checked[index] = it },
                shape    = SegmentedButtonDefaults.itemShape(index, options.size),
                icon     = { SegmentedButtonDefaults.ActiveIcon() },
            ) {
                Text(label)
            }
        }
    }
}
```

---

## #pull-to-refresh

### PullToRefreshBox

Wraps scrollable content and shows a refresh indicator when the user pulls down.

```kotlin
@Composable
fun PullToRefreshSample() {
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh    = {
            isRefreshing = true
            scope.launch {
                // Simulate network call
                delay(2000)
                isRefreshing = false
            }
        },
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            items(50) { Text("Item $it", Modifier.padding(16.dp)) }
        }
    }
}
```

---

## #swipe-to-dismiss

### SwipeToDismissBox

Allows users to dismiss or reveal actions on a list item by swiping horizontally.

```kotlin
@Composable
fun SwipeToDismissSample() {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd) {
                // Handle delete
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state            = dismissState,
        backgroundContent = {
            Box(
                Modifier.fillMaxSize().background(Color.Red).padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Icon(Icons.Filled.Delete, "Delete", tint = Color.White)
            }
        },
        enableDismissFromEndToStart = false,
    ) {
        Card(Modifier.fillMaxWidth()) {
            ListItem(
                headlineContent   = { Text("Swipe me to delete") },
                supportingContent = { Text("Swipe right to dismiss") },
            )
        }
    }
}
```

---

## #tabs

### PrimaryTabRow

Fixed primary tabs for top-level navigation between equally important destinations.

```kotlin
@Composable
fun PrimaryTabRowSample() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Photos", "Videos", "Music")

    PrimaryTabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick  = { selectedTab = index },
                text     = { Text(title) },
            )
        }
    }
}
```

### SecondaryTabRow

Secondary tabs for sub-navigation within a section. Less prominent than primary tabs.

```kotlin
@Composable
fun SecondaryTabRowSample() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Unread", "Starred")

    SecondaryTabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick  = { selectedTab = index },
                text     = { Text(title) },
            )
        }
    }
}
```

### PrimaryScrollableTabRow & SecondaryScrollableTabRow

Scrollable variants for when there are too many tabs to fit on screen.

```kotlin
@Composable
fun ScrollableTabRowSample() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5", "Tab 6", "Tab 7")

    PrimaryScrollableTabRow(selectedTabIndex = selectedTab) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick  = { selectedTab = index },
                text     = { Text(title) },
            )
        }
    }
}
```

### Tab with icon (LeadingIconTab)

```kotlin
LeadingIconTab(
    selected = selectedTab == 0,
    onClick  = { selectedTab = 0 },
    text     = { Text("Photos") },
    icon     = { Icon(Icons.Filled.Photo, null) },
)
```

---

## #date-time-pickers

### DatePicker

Allows users to select a single date. Can be docked (inline) or placed inside a
`DatePickerDialog` for modal use.

```kotlin
@Composable
fun DatePickerSample() {
    val state = rememberDatePickerState()

    DatePicker(
        state    = state,
        modifier = Modifier.fillMaxWidth(),
    )

    // Access selected date
    val selectedMillis = state.selectedDateMillis
}
```

### DateRangePicker

Allows users to select a start and end date.

```kotlin
@Composable
fun DateRangePickerSample() {
    val state = rememberDateRangePickerState()

    DateRangePicker(
        state    = state,
        modifier = Modifier.fillMaxSize(),
    )

    // Access range
    val startMillis = state.selectedStartDateMillis
    val endMillis   = state.selectedEndDateMillis
}
```

### TimePicker

Clock-face time picker for selecting hours and minutes.

```kotlin
@Composable
fun TimePickerSample() {
    val state = rememberTimePickerState(
        initialHour   = 10,
        initialMinute = 30,
        is24Hour      = false,
    )

    TimePicker(state = state)

    // Access selected time
    val hour   = state.hour
    val minute = state.minute
}
```

### TimeInput

Keyboard-based time input as an alternative to the clock-face picker.

```kotlin
@Composable
fun TimeInputSample() {
    val state = rememberTimePickerState(
        initialHour   = 14,
        initialMinute = 0,
        is24Hour      = true,
    )

    TimeInput(state = state)
}
```

### TimePicker in a dialog

Since there's no built-in `TimePickerDialog`, wrap `TimePicker` in an `AlertDialog`:

```kotlin
@Composable
fun TimePickerDialogSample() {
    var showDialog by remember { mutableStateOf(false) }
    val state = rememberTimePickerState()

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title            = { Text("Select time") },
            text             = { TimePicker(state = state) },
            confirmButton    = {
                TextButton(onClick = { showDialog = false }) { Text("OK") }
            },
            dismissButton    = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        )
    }
}
```

---

## #buttons

Standard Material 3 buttons for actions. These are the non-toggle, non-icon button family.

### 5 button styles

| Composable | Style | When to use |
|---|---|---|
| `Button` | Filled | Primary, high-emphasis action |
| `ElevatedButton` | Elevated | Important action that needs to stand above surface |
| `FilledTonalButton` | Tonal | Secondary action in the same region |
| `OutlinedButton` | Outlined | Medium-emphasis action, or cancel/back |
| `TextButton` | Text | Low-emphasis action, or tertiary action |

```kotlin
Button(onClick = {}) { Text("Filled") }
ElevatedButton(onClick = {}) { Text("Elevated") }
FilledTonalButton(onClick = {}) { Text("Tonal") }
OutlinedButton(onClick = {}) { Text("Outlined") }
TextButton(onClick = {}) { Text("Text") }
```

### Buttons with icons

```kotlin
Button(
    onClick        = {},
    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
) {
    Icon(Icons.Filled.Add, null, Modifier.size(ButtonDefaults.IconSize))
    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
    Text("Create")
}
```

### Button sizes (M3E)

M3E buttons support the same 5 size tokens as toggle buttons:

```kotlin
val size = ButtonDefaults.LargeContainerHeight

Button(
    onClick        = {},
    modifier       = Modifier.heightIn(size),
    contentPadding = ButtonDefaults.contentPaddingFor(size),
) {
    Icon(Icons.Filled.Add, null, Modifier.size(ButtonDefaults.iconSizeFor(size)))
    Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
    Text("Create", style = ButtonDefaults.textStyleFor(size))
}
```
