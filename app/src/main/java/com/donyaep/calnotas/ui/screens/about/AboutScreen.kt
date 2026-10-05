package com.donyaep.calnotas.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donyaep.calnotas.BuildConfig
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.components.SecondaryScreenScaffold
import com.donyaep.calnotas.ui.components.depthColor
import java.time.Year

private const val WebsiteUrl = "https://donyaep.vercel.app/"

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    SecondaryScreenScaffold(
        title = stringResource(R.string.about_title),
        backgroundShape = MaterialShapes.Sunny.toShape(),
        toolbar = {
            FilledIconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
        }
    ) {
        AppBadge(Modifier.align(Alignment.CenterHorizontally).padding(top = 20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 40.sp, lineHeight = 44.sp, letterSpacing = (-0.8).sp),
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.version_format, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.5.sp, fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurfaceVariant,
                modifier = Modifier
                    .background(colors.surfaceContainerHigh, CircleShape)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Text(
            stringResource(R.string.app_description),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.5.sp, lineHeight = 23.sp, letterSpacing = 0.sp),
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp, top = 20.dp)
        )

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val itemColors = ListItemDefaults.segmentedColors(containerColor = colors.surfaceContainerLow)
            SegmentedListItem(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(WebsiteUrl))) },
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
                modifier = Modifier.fillMaxWidth(),
                colors = itemColors,
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(colors.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(ImageVector.vectorResource(R.drawable.ic_public), contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(22.dp))
                    }
                },
                supportingContent = {
                    Text(WebsiteUrl.removePrefix("https://").trimEnd('/'), color = colors.onSurfaceVariant)
                },
                trailingContent = {
                    Icon(ImageVector.vectorResource(R.drawable.ic_open_in_new), contentDescription = null, tint = colors.outline, modifier = Modifier.size(20.dp))
                }
            ) {
                Text(stringResource(R.string.visit_website))
            }
            SegmentedListItem(
                shapes = ListItemDefaults.segmentedShapes(index = 1, count = 2),
                modifier = Modifier.fillMaxWidth(),
                colors = itemColors,
                leadingContent = {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(colors.tertiaryContainer, MaterialShapes.Cookie4Sided.toShape()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("d", style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp), fontWeight = FontWeight.Bold, color = colors.onTertiaryContainer)
                    }
                },
                supportingContent = {
                    Text(stringResource(R.string.copyright_year_format, Year.now().value), color = colors.onSurfaceVariant)
                }
            ) {
                Text(stringResource(R.string.copyright_developer))
            }
        }
    }
}

/** El logo dentro de la galleta del inicio, con su canto, teñido con el color sobre primario. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppBadge(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val shape = MaterialShapes.Cookie9Sided.toShape()
    Box(modifier = modifier.size(220.dp)) {
        Box(
            Modifier
                .offset(7.dp, 9.dp)
                .fillMaxSize()
                .background(depthColor(colors.primary), shape)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.primary, shape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.logo_calnotas_app_light),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.onPrimary),
                modifier = Modifier.size(200.dp)
            )
        }
    }
}
