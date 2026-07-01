package com.donyaep.calnotas.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.settings.AppSettingsViewModel
import com.donyaep.calnotas.ui.settings.ThemeModePreference

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToUpdate: () -> Unit,
    viewModel: AppSettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showThemeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageSheet by rememberSaveable { mutableStateOf(false) }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = uiState.themeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { selectedTheme ->
                viewModel.setThemeMode(selectedTheme)
                showThemeDialog = false
            }
        )
    }

    if (showLanguageSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLanguageSheet = false }
        ) {
            LanguageSheetContent(
                currentLanguage = uiState.languageCode,
                onLanguageSelected = { selectedLanguage ->
                    viewModel.setLanguageCode(selectedLanguage)
                    showLanguageSheet = false
                }
            )
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 14.dp)
                    .fillMaxWidth(0.44f)
                    .height(118.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings_screen_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(R.string.display),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    SettingsNavigationItem(
                        index = 0,
                        lastIndex = 1,
                        icon = Icons.Filled.Palette,
                        title = stringResource(R.string.theme),
                        subtitle = stringResource(themeModeLabelRes(uiState.themeMode)),
                        onClick = { showThemeDialog = true }
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    SettingsNavigationItem(
                        index = 1,
                        lastIndex = 1,
                        icon = Icons.Filled.Language,
                        title = stringResource(R.string.app_language),
                        subtitle = languageAppliedText(uiState.languageCode),
                        onClick = { showLanguageSheet = true }
                    )
                }

                Text(
                    text = stringResource(R.string.support),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    SettingsNavigationItem(
                        index = 0,
                        lastIndex = 2,
                        icon = Icons.AutoMirrored.Outlined.HelpOutline,
                        title = stringResource(R.string.help),
                        subtitle = stringResource(R.string.view_help_info),
                        onClick = onNavigateToHelp
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    SettingsNavigationItem(
                        index = 1,
                        lastIndex = 2,
                        icon = Icons.Outlined.Info,
                        title = stringResource(R.string.about),
                        subtitle = stringResource(R.string.app_name),
                        onClick = onNavigateToAbout
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    SettingsNavigationItem(
                        index = 2,
                        lastIndex = 2,
                        icon = Icons.Outlined.SystemUpdate,
                        title = stringResource(R.string.check_updates),
                        subtitle = stringResource(R.string.check_updates_desc),
                        onClick = onNavigateToUpdate
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsNavigationItem(
    index: Int,
    lastIndex: Int,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = segmentedItemShape(index = index, lastIndex = lastIndex),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
            },
            headlineContent = { Text(title) },
            supportingContent = {
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}

private fun segmentedItemShape(index: Int, lastIndex: Int): RoundedCornerShape {
    return when (index) {
        0 -> RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 4.dp,
            bottomEnd = 4.dp
        )

        lastIndex -> RoundedCornerShape(
            topStart = 4.dp,
            topEnd = 4.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        )

        else -> RoundedCornerShape(4.dp)
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeModePreference,
    onDismiss: () -> Unit,
    onThemeSelected: (ThemeModePreference) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.choose_theme_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ThemeModePreference.entries.forEach { option ->
                    SelectionRow(
                        selected = option == currentTheme,
                        text = stringResource(themeModeLabelRes(option)),
                        onClick = { onThemeSelected(option) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Composable
private fun LanguageSheetContent(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.choose_language_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.settings_choose_language_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SelectionRow(
            selected = currentLanguage == "system",
            text = stringResource(R.string.system_default),
            onClick = { onLanguageSelected("system") }
        )
        SelectionRow(
            selected = currentLanguage == "es",
            text = stringResource(R.string.spanish),
            onClick = { onLanguageSelected("es") }
        )
        SelectionRow(
            selected = currentLanguage == "en",
            text = stringResource(R.string.english),
            onClick = { onLanguageSelected("en") }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SelectionRow(
    selected: Boolean,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun themeModeLabelRes(themeMode: ThemeModePreference): Int {
    return when (themeMode) {
        ThemeModePreference.SYSTEM -> R.string.system_default
        ThemeModePreference.LIGHT -> R.string.light
        ThemeModePreference.DARK -> R.string.dark
    }
}

@Composable
private fun languageAppliedText(languageCode: String): String {
    val configuration = LocalConfiguration.current
    val systemLanguageCode = configuration.locales[0]?.language ?: "es"

    return when (languageCode) {
        "en" -> stringResource(R.string.english)
        "es" -> stringResource(R.string.spanish)
        else -> {
            val systemLabel = when (systemLanguageCode) {
                "en" -> stringResource(R.string.english)
                "es" -> stringResource(R.string.spanish)
                else -> systemLanguageCode.uppercase()
            }
            "${stringResource(R.string.system_default)} ($systemLabel)"
        }
    }
}
