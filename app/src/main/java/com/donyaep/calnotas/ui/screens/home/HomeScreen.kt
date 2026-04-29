package com.donyaep.calnotas.ui.screens.home


import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.theme.isAppInDarkTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    onNavigateToDefault: () -> Unit,
    onNavigateToCustom: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToUpdate: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val logoRes = if (isAppInDarkTheme()) {
        R.drawable.logo_calnotas_app_light
    } else {
        R.drawable.logo_calnotas_app_black
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.home_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background gradient
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorScheme.surface,
                                colorScheme.surfaceContainerLow,
                                colorScheme.surface
                            )
                        )
                    )
            )

            // Decorative blobs
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .align(Alignment.TopEnd)
                    .graphicsLayer {
                        translationX = 64f
                        translationY = -32f
                    }
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.10f))
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopStart)
                    .graphicsLayer {
                        translationX = -42f
                        translationY = 164f
                    }
                    .clip(CircleShape)
                    .background(colorScheme.secondary.copy(alpha = 0.12f))
            )

            // Scrollable content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .padding(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Hero icon (static)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(128.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = logoRes),
                            contentDescription = null,
                            modifier = Modifier.size(104.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.home_welcome_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.home_welcome_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(4.dp))

                HomeActionCard(
                    icon = Icons.AutoMirrored.Outlined.TrendingUp,
                    title = stringResource(R.string.grade_calculator),
                    subtitle = stringResource(R.string.home_default_description),
                    containerColor = colorScheme.primaryContainer,
                    onContainerColor = colorScheme.onPrimaryContainer,
                    accentColor = colorScheme.primary,
                    onClick = onNavigateToDefault
                )

                HomeActionCard(
                    icon = Icons.Outlined.Bolt,
                    title = stringResource(R.string.custom_grade_calculator),
                    subtitle = stringResource(R.string.home_custom_description),
                    containerColor = colorScheme.secondaryContainer,
                    onContainerColor = colorScheme.onSecondaryContainer,
                    accentColor = colorScheme.secondary,
                    onClick = onNavigateToCustom
                )
            }

            // M3 Expressive: Floating toolbar for secondary navigation
            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
            ) {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(stringResource(R.string.help)) } },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = onNavigateToHelp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                            contentDescription = stringResource(R.string.help)
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(stringResource(R.string.settings)) } },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(stringResource(R.string.about)) } },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = onNavigateToAbout) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.about)
                        )
                    }
                }
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(stringResource(R.string.check_updates)) } },
                    state = rememberTooltipState()
                ) {
                    IconButton(onClick = onNavigateToUpdate) {
                        Icon(
                            imageVector = Icons.Outlined.Update,
                            contentDescription = stringResource(R.string.check_updates)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    containerColor: Color,
    onContainerColor: Color,
    accentColor: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon badge on the left
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(accentColor.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Text block in the middle
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onContainerColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = onContainerColor.copy(alpha = 0.80f)
                )
            }

            // Arrow on the right
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
