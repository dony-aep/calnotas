package com.donyaep.calnotas.ui.screens.update

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.R
import com.donyaep.calnotas.data.remote.dto.GitHubReleaseDto
import com.donyaep.calnotas.ui.components.SecondaryScreenScaffold
import com.donyaep.calnotas.ui.components.connectedRowShape
import com.donyaep.calnotas.ui.components.depthColor

private enum class UpdateStatus { Checking, UpToDate, Available, Failed }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UpdateScreen(
    onBack: () -> Unit,
    viewModel: UpdateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val latestRelease = uiState.latestRelease
    val status = when {
        uiState.isLoading -> UpdateStatus.Checking
        uiState.errorKey != null || latestRelease == null -> UpdateStatus.Failed
        viewModel.isUpdateAvailable() -> UpdateStatus.Available
        else -> UpdateStatus.UpToDate
    }
    val motion = MaterialTheme.motionScheme

    SecondaryScreenScaffold(
        title = stringResource(R.string.update_title),
        backgroundShape = MaterialShapes.SoftBurst.toShape(),
        toolbar = {
            // Volver solo se destaca cuando la pantalla no tiene su propia acción principal.
            val backIsMain = status == UpdateStatus.Checking || status == UpdateStatus.UpToDate
            FilledIconButton(
                onClick = onBack,
                colors = if (backIsMain) IconButtonDefaults.filledIconButtonColors() else IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            if (status == UpdateStatus.UpToDate || status == UpdateStatus.Available) {
                FilledIconButton(onClick = viewModel::retry, colors = IconButtonDefaults.filledTonalIconButtonColors()) {
                    Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.check_again))
                }
            }
        }
    ) {
        AnimatedContent(
            targetState = status,
            transitionSpec = {
                (fadeIn(motion.defaultEffectsSpec()) + scaleIn(motion.defaultSpatialSpec(), initialScale = 0.92f)) togetherWith
                    fadeOut(motion.fastEffectsSpec())
            },
            label = "updateStatus"
        ) { current ->
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                when (current) {
                    UpdateStatus.Checking -> CheckingContent(uiState.currentVersion)
                    UpdateStatus.UpToDate -> UpToDateContent(uiState.currentVersion, latestRelease?.version().orEmpty())
                    UpdateStatus.Available -> latestRelease?.let { AvailableContent(uiState.currentVersion, it) }
                    UpdateStatus.Failed -> FailedContent(uiState.errorKey, onRetry = viewModel::retry)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CheckingContent(currentVersion: String) {
    val colors = MaterialTheme.colorScheme
    ContainedLoadingIndicator(
        modifier = Modifier
            .padding(top = 150.dp)
            .size(136.dp),
        containerColor = colors.primaryContainer,
        indicatorColor = colors.primary
    )
    Text(
        stringResource(R.string.checking_for_updates),
        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 32.dp)
    )
    Text(
        stringResource(R.string.update_current_format, currentVersion),
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, fontFeatureSettings = "tnum"),
        color = colors.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpToDateContent(currentVersion: String, latestVersion: String) {
    val colors = MaterialTheme.colorScheme
    // El sol de cuando se aprueba en la calculadora: todo está en orden.
    StatusHero(
        shape = MaterialShapes.VerySunny.toShape(),
        face = colors.primary,
        icon = { Icon(Icons.Filled.Check, contentDescription = null, tint = colors.onPrimary, modifier = Modifier.size(76.dp)) },
        size = 220.dp,
        modifier = Modifier.padding(top = 60.dp)
    )
    StatusText(stringResource(R.string.up_to_date), stringResource(R.string.you_have_latest_version), Modifier.padding(top = 32.dp))
    VersionRow(
        leftLabel = stringResource(R.string.current_version),
        left = currentVersion,
        rightLabel = stringResource(R.string.latest_version),
        right = latestVersion,
        highlightRight = false,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AvailableContent(currentVersion: String, release: GitHubReleaseDto) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current
    val notes = releaseNoteLines(release.body)

    StatusHero(
        shape = MaterialShapes.Cookie4Sided.toShape(),
        face = colors.tertiaryContainer,
        icon = {
            Icon(ImageVector.vectorResource(R.drawable.ic_download), contentDescription = null, tint = colors.onTertiaryContainer, modifier = Modifier.size(64.dp))
        },
        size = 184.dp,
        modifier = Modifier.padding(top = 20.dp)
    )
    StatusText(stringResource(R.string.update_available), stringResource(R.string.new_version_available), Modifier.padding(top = 28.dp))
    VersionRow(
        leftLabel = stringResource(R.string.update_you_have),
        left = currentVersion,
        rightLabel = stringResource(R.string.update_new),
        right = release.version(),
        highlightRight = true,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 28.dp)
    )

    if (notes.isNotEmpty()) {
        Surface(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = colors.surfaceContainerLow
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_auto_awesome), contentDescription = null, tint = colors.tertiary, modifier = Modifier.size(20.dp))
                    Text(stringResource(R.string.release_notes), style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    notes.forEach { note ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier
                                    .padding(top = 7.dp)
                                    .size(8.dp)
                                    .rotate(45f)
                                    .background(colors.tertiary, RoundedCornerShape(3.dp))
                            )
                            Text(note, style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.5.sp, lineHeight = 21.sp, letterSpacing = 0.sp))
                        }
                    }
                }
            }
        }
    }

    Button(
        onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(release.htmlUrl))) },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 20.dp)
            .fillMaxWidth()
            .heightIn(min = ButtonDefaults.MediumContainerHeight),
        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight)
    ) {
        Icon(
            ImageVector.vectorResource(R.drawable.ic_download),
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MediumContainerHeight))
        )
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MediumContainerHeight)))
        Text(stringResource(R.string.download_update), style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun FailedContent(errorKey: String?, onRetry: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val icon = if (errorKey == "connection_error") R.drawable.ic_wifi_off else R.drawable.ic_error_outline

    StatusHero(
        shape = MaterialShapes.Puffy.toShape(),
        face = colors.errorContainer,
        icon = {
            Icon(ImageVector.vectorResource(icon), contentDescription = null, tint = colors.onErrorContainer, modifier = Modifier.size(68.dp))
        },
        size = 200.dp,
        modifier = Modifier.padding(top = 60.dp)
    )
    StatusText(stringResource(updateErrorMessageRes(errorKey)), stringResource(R.string.try_again_later), Modifier.padding(top = 32.dp))
    Button(
        onClick = onRetry,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 32.dp)
            .fillMaxWidth()
            .heightIn(min = ButtonDefaults.MediumContainerHeight),
        contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight)
    ) {
        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.MediumContainerHeight)))
        Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MediumContainerHeight)))
        Text(stringResource(R.string.retry), style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight))
    }
}

/** La forma grande del estado, con su canto, igual que los botones del inicio. */
@Composable
private fun StatusHero(shape: Shape, face: Color, icon: @Composable () -> Unit, size: Dp, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(size)) {
        Box(
            Modifier
                .offset(7.dp, 9.dp)
                .fillMaxSize()
                .background(depthColor(face), shape)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(face, shape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
    }
}

@Composable
private fun StatusText(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp, lineHeight = 22.sp, letterSpacing = 0.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VersionRow(
    leftLabel: String,
    left: String,
    rightLabel: String,
    right: String,
    highlightRight: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        VersionBlock(leftLabel, left, colors.surfaceContainerLow, colors.onSurface, connectedRowShape(0, 2, outer = 24.dp, inner = 6.dp), Modifier.weight(1f))
        VersionBlock(
            rightLabel,
            right,
            if (highlightRight) colors.tertiaryContainer else colors.surfaceContainerLow,
            if (highlightRight) colors.onTertiaryContainer else colors.onSurface,
            connectedRowShape(1, 2, outer = 24.dp, inner = 6.dp),
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun VersionBlock(label: String, version: String, container: Color, content: Color, shape: Shape, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(container, shape)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp), color = content.copy(alpha = 0.8f))
        Text(version, style = MaterialTheme.typography.headlineSmall.copy(fontSize = 26.sp, fontFeatureSettings = "tnum"), fontWeight = FontWeight.Bold, color = content)
    }
}

private fun GitHubReleaseDto.version(): String = tagName.removePrefix("v")

/**
 * Las notas de la versión vienen en Markdown («## What's new» y viñetas). Se muestran como lista:
 * sin encabezados, sin marcas de viñeta y sin negritas ni código.
 */
private fun releaseNoteLines(body: String): List<String> =
    body.lines()
        .map { it.trim() }
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .map { it.removePrefix("- ").removePrefix("* ").replace("**", "").replace("`", "") }

private fun updateErrorMessageRes(errorKey: String?): Int {
    return when (errorKey) {
        "no_releases_found" -> R.string.no_releases_found
        "error_fetching_updates" -> R.string.error_fetching_updates
        "connection_error" -> R.string.connection_error
        else -> R.string.update_screen_error_generic
    }
}
