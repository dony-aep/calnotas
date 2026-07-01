# Flutter to Compose -- Migration Reference

Conceptual mapping for developers moving from Flutter/Dart to Kotlin/Jetpack Compose.


## Mental model differences

| Flutter | Compose |
|---|---|
| `Widget` (immutable, rebuilds on setState) | `@Composable fun` (recomposes when state changes) |
| `StatelessWidget` | `@Composable fun` with no `remember` |
| `StatefulWidget` + `State<T>` | `remember { mutableStateOf(...) }` |
| `setState(() {})` | Update a `MutableState` -- recomposition is automatic |
| `BuildContext` | Implicit in the composition tree; no explicit passing |
| Hot reload | Live Edit / `@Preview` in Android Studio |
| `pubspec.yaml` | `gradle/libs.versions.toml` + `build.gradle.kts` |
| `MaterialApp` | `MaterialExpressiveTheme { }` |
| `Scaffold` | `Scaffold` -- nearly identical structure |
| `main()` | `MainActivity.onCreate()` -> `setContent { }` |


## Layout composables

| Flutter | Compose | Notes |
|---|---|---|
| `Column` | `Column` | vertical |
| `Row` | `Row` | horizontal |
| `Stack` | `Box` | overlapping children |
| `Expanded` | `Modifier.weight(1f)` | fills remaining space |
| `Flexible` | `Modifier.weight(x, fill = false)` | proportional, not filling |
| `SizedBox(width, height)` | `Spacer(Modifier.size(...))` | fixed spacing |
| `Padding(EdgeInsets)` | `Modifier.padding(...)` | on the modifier |
| `Container` | `Box` with modifiers | generic container |
| `Center` | `Box(contentAlignment = Alignment.Center)` | center single child |
| `Wrap` | `FlowRow` / `FlowColumn` | wrapping layout |
| `ListView` | `LazyColumn` | lazy vertical list |
| `ListView.builder` | `LazyColumn { items(list) { } }` | indexed lazy list |
| `GridView.builder` | `LazyVerticalGrid` | lazy grid |
| `SingleChildScrollView` | `Column(Modifier.verticalScroll(rememberScrollState()))` | scrollable column |
| `PageView` | `HorizontalPager` | horizontal paging |


## Text and images

| Flutter | Compose | Notes |
|---|---|---|
| `Text(data, style: TextStyle(...))` | `Text(text, style = TextStyle(...))` | similar API |
| `RichText` | `AnnotatedString` + `Text` | inline style spans |
| `Image.asset(path)` | `Image(painterResource(R.drawable.x), null)` | drawable resources |
| `Image.network(url)` | `AsyncImage(model = url, ...)` via Coil | network images |
| `Icon(Icons.X)` | `Icon(Icons.Filled.X, contentDescription = null)` | always provide contentDescription |
| `CircleAvatar` | `AsyncImage` with `Modifier.clip(CircleShape)` | circular image |

Add Coil for network images:
```kotlin
// build.gradle.kts
implementation("io.coil-kt:coil-compose:2.7.0")

// usage
AsyncImage(
    model          = "https://example.com/image.jpg",
    contentDescription = "Description",
    contentScale   = ContentScale.Crop,
    modifier       = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)),
)
```


## Material components

| Flutter / Material | Jetpack Compose / M3E | M3E note |
|---|---|---|
| `AppBar` | `TopAppBar` / `MediumFlexibleTopAppBar` | Flexible = M3E |
| `BottomNavigationBar` | `NavigationBar` | |
| `NavigationRail` | `NavigationRail` / `WideNavigationRail` | Wide = M3E tablet |
| `NavigationDrawer` | `ModalNavigationDrawer` | |
| `FloatingActionButton` | `FloatingActionButton` / `MediumFloatingActionButton` | Medium = M3E |
| Speed Dial (custom) | `FloatingActionButtonMenu` | native M3E |
| `ElevatedButton` | `ElevatedButton` | |
| `TextButton` | `TextButton` | |
| `OutlinedButton` | `OutlinedButton` | |
| `IconButton` | `IconButton` / `FilledIconButton` | |
| `ToggleButtons` | `ButtonGroup` + `ToggleButton` | more expressive in M3E |
| `Card` | `Card` / `ElevatedCard` / `OutlinedCard` | |
| `ListTile` | `ListItem` / `SegmentedListItem` | Segmented = M3E |
| `TextField` | `OutlinedTextField` / `TextField` / `ScrollField` | ScrollField = M3E |
| `DropdownButton` | `ExposedDropdownMenuBox` | |
| `Chip` | `FilterChip` / `AssistChip` / `InputChip` / `SuggestionChip` | |
| `SnackBar` | `Snackbar` + `SnackbarHost` | |
| `AlertDialog` | `AlertDialog` | |
| `BottomSheet` | `ModalBottomSheet` / `BottomSheetScaffold` | |
| `Slider` | `Slider` / `RangeSlider` / `VerticalSlider` | Vertical = M3E |
| `Checkbox` | `Checkbox` | |
| `Switch` | `Switch` | |
| `Radio` | `RadioButton` | |
| `LinearProgressIndicator` | `LinearWavyProgressIndicator` | Wavy = M3E |
| `CircularProgressIndicator` | `CircularWavyProgressIndicator` / `LoadingIndicator` | M3E variants |
| `Divider` | `HorizontalDivider` / `VerticalDivider` | |
| `Tooltip` | `TooltipBox` | |
| `BottomAppBar` | `FlexibleBottomAppBar` / `HorizontalFloatingToolbar` | M3E replaces BottomAppBar |
| `TabBar` + `TabBarView` | `TabRow` + `HorizontalPager` | |
| `SearchBar` | `SearchBar` / `ExpandedDockedSearchBarWithGap` | Gap version = M3E |


## State management

| Flutter | Compose | Notes |
|---|---|---|
| `setState(() {})` | assign to `MutableState` | recomposition is automatic |
| `ValueNotifier` | `mutableStateOf` or `StateFlow` | |
| `ChangeNotifier` | `ViewModel` + `StateFlow` | |
| `Provider` | `Hilt` + `ViewModel` | |
| `Riverpod` | `Hilt` + `ViewModel` + `StateFlow` | |
| `BLoC` | `ViewModel` + sealed `UiState` class | |
| `initState()` | `LaunchedEffect(Unit)` | runs on first composition |
| `didUpdateWidget(old)` | `LaunchedEffect(key)` | runs when key changes |
| `dispose()` | `DisposableEffect { onDispose { } }` | cleanup on leaving composition |

```kotlin
// Standard ViewModel pattern (equivalent to ChangeNotifier / Riverpod)
data class HomeUiState(
    val items:     List<Item> = emptyList(),
    val isLoading: Boolean    = false,
    val error:     String?    = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ItemRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadItems() }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repo.getItems()
                .onSuccess { items -> _uiState.update { it.copy(items = items, isLoading = false) } }
                .onFailure { e     -> _uiState.update { it.copy(error = e.message,  isLoading = false) } }
        }
    }
}

// In the composable:
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}
```


## Navigation

| Flutter | Compose | Notes |
|---|---|---|
| `Navigator.push(context, route)` | `navController.navigate("route")` | |
| `Navigator.pop(context)` | `navController.popBackStack()` | |
| `MaterialPageRoute` | `composable("route") { }` | |
| Named routes | `NavHost` with string routes or type-safe routes | |
| `GoRouter` | Navigation Compose / Navigation3 | |

```kotlin
NavHost(navController, startDestination = "home") {
    composable("home") {
        HomeScreen(onItemClick = { id -> navController.navigate("detail/$id") })
    }
    composable(
        route     = "detail/{itemId}",
        arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
    ) { backStack ->
        val id = backStack.arguments?.getString("itemId") ?: return@composable
        DetailScreen(itemId = id, onBack = { navController.popBackStack() })
    }
}
```


## Side effects

| Flutter | Compose | When to use |
|---|---|---|
| `initState()` | `LaunchedEffect(Unit)` | on first composition |
| `didUpdateWidget()` | `LaunchedEffect(key)` | when dependency changes |
| `dispose()` | `DisposableEffect { onDispose { } }` | cleanup on removal |
| `WidgetsBinding.addPostFrameCallback` | `SideEffect` | after every recomposition |
| `FutureBuilder` | `produceState` | async value to State |
| `StreamBuilder` | `collectAsStateWithLifecycle()` | Flow to State |


## Async primitives

| Dart / Flutter | Kotlin / Compose |
|---|---|
| `async` / `await` | `suspend` + coroutines |
| `Future<T>` | `suspend fun` / `Deferred<T>` |
| `Stream<T>` | `Flow<T>` |
| `StreamBuilder` | `collectAsStateWithLifecycle()` |
| Extension methods | Extension functions (same concept in Kotlin) |
| Null safety | Kotlin null safety -- same intent, `?` and `!!` operators |
