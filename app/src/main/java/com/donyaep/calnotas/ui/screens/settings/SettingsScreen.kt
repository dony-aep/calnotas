package com.donyaep.calnotas.ui.screens.settings

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.BuildConfig
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.components.SecondaryScreenScaffold
import com.donyaep.calnotas.ui.components.SectionLabel
import com.donyaep.calnotas.ui.settings.AppSettingsViewModel
import com.donyaep.calnotas.ui.settings.ThemeModePreference
import com.donyaep.calnotas.ui.theme.isAppInDarkTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToUpdate: () -> Unit,
    viewModel: AppSettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    SecondaryScreenScaffold(
        title = stringResource(R.string.settings_title),
        backgroundShape = MaterialShapes.Cookie12Sided.toShape(),
        toolbar = {
            FilledIconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }
    ) {
        Text(
            stringResource(R.string.settings_screen_subtitle),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 23.sp, letterSpacing = 0.sp),
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
        )

        SectionLabel(stringResource(R.string.theme), Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 12.dp))
        ThemePicker(
            selected = uiState.themeMode,
            onSelect = viewModel::setThemeMode,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        SectionLabel(stringResource(R.string.app_language), Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 12.dp))
        LanguagePicker(
            selected = uiState.languageCode,
            onSelect = viewModel::setLanguageCode,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            stringResource(R.string.settings_language_hint, stringResource(R.string.system_short)),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.sp),
            color = colors.outline,
            modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 10.dp)
        )

        SectionLabel(stringResource(R.string.support), Modifier.padding(start = 24.dp, end = 24.dp, top = 30.dp, bottom = 12.dp))
        // SegmentedListItem anima su color de fondo: al cambiar de tema hacía un fundido desde el
        // color anterior. Con la clave, las filas se crean de nuevo y entran ya con el color nuevo.
        key(isAppInDarkTheme()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SupportItem(0, ImageVector.vectorResource(R.drawable.ic_help_outline), stringResource(R.string.help), stringResource(R.string.view_help_info), onNavigateToHelp)
                SupportItem(1, Icons.Outlined.Info, stringResource(R.string.about), stringResource(R.string.about_summary_format, BuildConfig.VERSION_NAME), onNavigateToAbout)
                SupportItem(2, ImageVector.vectorResource(R.drawable.ic_update), stringResource(R.string.check_updates), stringResource(R.string.check_updates_desc), onNavigateToUpdate)
            }
        }
    }
}

/**
 * Los tres temas con una vista previa en miniatura del inicio. El elegido se aplica al tocarlo y su
 * tarjeta se redondea, como un botón de alternar de M3 Expressive.
 */
@Composable
private fun ThemePicker(selected: ThemeModePreference, onSelect: (ThemeModePreference) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    // Las vistas previas usan los mismos esquemas que aplica CalNotasTheme.
    val dynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val light = if (dynamic) dynamicLightColorScheme(context) else expressiveLightColorScheme()
    val dark = if (dynamic) dynamicDarkColorScheme(context) else darkColorScheme()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ThemeModePreference.entries.forEach { mode ->
            ThemeTile(
                mode = mode,
                selected = mode == selected,
                onClick = { onSelect(mode) },
                light = light,
                dark = dark,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThemeTile(
    mode: ThemeModePreference,
    selected: Boolean,
    onClick: () -> Unit,
    light: ColorScheme,
    dark: ColorScheme,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val corner by animateDpAsState(
        if (selected) 32.dp else 16.dp,
        MaterialTheme.motionScheme.defaultSpatialSpec(),
        label = "themeTileCorner"
    )
    // Sin animar el color: al cambiar de tema, la tarjeta pasa al color nuevo en el mismo cuadro
    // que el resto de la pantalla, en vez de arrastrar el del tema anterior.
    val container = if (selected) colors.primaryContainer else colors.surfaceContainerLow
    Surface(
        selected = selected,
        onClick = onClick,
        shape = RoundedCornerShape(corner),
        color = container,
        contentColor = if (selected) colors.onPrimaryContainer else colors.onSurface,
        modifier = modifier
            .height(172.dp)
            .semantics { role = Role.RadioButton }
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp, top = 14.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ThemePreview(mode, light, dark)
                Text(stringResource(themeLabel(mode)), style = MaterialTheme.typography.labelLarge.copy(fontSize = 14.sp), fontWeight = FontWeight.SemiBold)
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(24.dp)
                        .background(colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

/** Una pantalla en miniatura con la galleta del inicio. «Sistema» muestra mitad clara y mitad oscura. */
@Composable
private fun ThemePreview(mode: ThemeModePreference, light: ColorScheme, dark: ColorScheme) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = Modifier
            .size(width = 70.dp, height = 104.dp)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    ) {
        when (mode) {
            ThemeModePreference.LIGHT -> MiniScreen(light)
            ThemeModePreference.DARK -> MiniScreen(dark)
            ThemeModePreference.SYSTEM -> {
                MiniScreen(light)
                Box(Modifier.drawWithContent { clipRect(left = size.width / 2) { this@drawWithContent.drawContent() } }) {
                    MiniScreen(dark)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MiniScreen(scheme: ColorScheme) {
    Box(
        modifier = Modifier
            .size(width = 70.dp, height = 104.dp)
            .background(scheme.background)
    ) {
        Box(
            Modifier
                .offset(13.dp, 18.dp)
                .size(44.dp)
                .background(scheme.primary, MaterialShapes.Cookie9Sided.toShape())
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 10.dp, end = 10.dp, bottom = 12.dp)
                .fillMaxWidth()
                .height(12.dp)
                .background(scheme.surfaceContainerHigh, CircleShape)
        )
    }
}

/** Idioma en un grupo de botones conectados; el elegido se aplica al tocarlo. */
@Composable
private fun LanguagePicker(selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val options = listOf(
        "system" to stringResource(R.string.system_short),
        "es" to stringResource(R.string.spanish),
        "en" to stringResource(R.string.english)
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        options.forEachIndexed { index, (code, label) ->
            val checked = code == selected
            ToggleButton(
                checked = checked,
                onCheckedChange = { onSelect(code) },
                buttonSize = ToggleButtonSize.Medium,
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                colors = ToggleButtonDefaults.colors(containerColor = colors.surfaceContainerHigh, contentColor = colors.onSurface),
                contentPadding = PaddingValues(horizontal = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton }
            ) {
                if (checked) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                }
                Text(label, maxLines = 1)
            }
        }
    }
}

@Composable
private fun SupportItem(index: Int, icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.segmentedShapes(index = index, count = 3),
        modifier = Modifier.fillMaxWidth(),
        colors = ListItemDefaults.segmentedColors(containerColor = colors.surfaceContainerLow),
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colors.surfaceContainerHigh, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = colors.primary, modifier = Modifier.size(22.dp))
            }
        },
        supportingContent = { Text(subtitle, color = colors.onSurfaceVariant) },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = colors.outline)
        }
    ) {
        Text(title)
    }
}

private fun themeLabel(mode: ThemeModePreference): Int = when (mode) {
    ThemeModePreference.SYSTEM -> R.string.system_short
    ThemeModePreference.LIGHT -> R.string.light
    ThemeModePreference.DARK -> R.string.dark
}
