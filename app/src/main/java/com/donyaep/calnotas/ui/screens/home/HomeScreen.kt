package com.donyaep.calnotas.ui.screens.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.components.LayeredBackgroundShape
import com.donyaep.calnotas.ui.components.MorphShape
import com.donyaep.calnotas.ui.components.WavyHeadline
import com.donyaep.calnotas.ui.components.depthColor
import com.donyaep.calnotas.ui.components.lerpDp
import com.donyaep.calnotas.ui.screens.defaultcalculator.StandardPlanWeights
import com.donyaep.calnotas.ui.theme.isAppInDarkTheme
import java.time.LocalTime
import kotlin.math.roundToInt

// Medidas del diseño, tomadas sobre un ancho de referencia de 390 dp y escaladas al ancho real.
private const val DesignWidth = 390f

private enum class DayPart { Morning, Afternoon, Evening }

private fun dayPartAt(hour: Int): DayPart = when (hour) {
    in 5..11 -> DayPart.Morning
    in 12..18 -> DayPart.Afternoon
    else -> DayPart.Evening
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    onNavigateToDefault: () -> Unit,
    onNavigateToCustom: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToUpdate: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val savedFields by viewModel.savedFields.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    // La hora se toma al abrir el inicio: el saludo y las formas de fondo cambian con el día.
    val dayPart = remember { dayPartAt(LocalTime.now().hour) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        val screenWidth = maxWidth
        val k = screenWidth.value / DesignWidth
        fun u(value: Int): Dp = (value * k).dp

        val ambient = when (dayPart) {
            DayPart.Morning -> MaterialShapes.VerySunny
            DayPart.Afternoon -> MaterialShapes.Sunny
            DayPart.Evening -> MaterialShapes.Puffy
        }
        val accent = when (dayPart) {
            DayPart.Morning -> MaterialShapes.Circle
            DayPart.Afternoon -> MaterialShapes.Diamond
            DayPart.Evening -> NightStar
        }

        // Fondo: dos capas por forma en tonos vecinos, para que se lea profundidad sin ruido.
        LayeredBackgroundShape(ambient.toShape(), u(380), u(340), u(26), u(8), Modifier.align(Alignment.TopEnd).offset(u(132), u(-96)))
        LayeredBackgroundShape(
            MaterialShapes.Clover4Leaf.toShape(), u(250), u(222), u(8), u(8),
            Modifier.align(Alignment.BottomStart).offset(u(-92), u(-6)).rotate(18f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 112.dp)
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, letterSpacing = (-0.2).sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
                    .padding(top = 16.dp)
            )

            Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 22.dp)) {
                Text(
                    text = stringResource(
                        when (dayPart) {
                            DayPart.Morning -> R.string.home_greeting_morning
                            DayPart.Afternoon -> R.string.home_greeting_afternoon
                            DayPart.Evening -> R.string.home_greeting_evening
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = colors.onSurfaceVariant
                )
                WavyHeadline(
                    text = stringResource(R.string.home_question_format, stringResource(R.string.home_question_highlight)),
                    highlight = stringResource(R.string.home_question_highlight),
                    waveColor = colors.primary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // Escenario de los dos botones: posiciones del diseño escaladas al ancho real.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .height(u(460))
            ) {
                Box(
                    modifier = Modifier
                        .offset(u(296), u(96))
                        .size(u(54))
                        .background(colors.primaryContainer.copy(alpha = 0.55f), accent.toShape())
                )

                CookieButton(
                    title = stringResource(R.string.home_default_action),
                    detail = standardPlanSummary(),
                    size = u(236),
                    onClick = onNavigateToDefault,
                    modifier = Modifier.offset(u(16), u(0))
                )

                SlantedButton(
                    title = stringResource(R.string.home_custom_action),
                    detail = savedFieldsSummary(savedFields),
                    size = u(182),
                    onClick = onNavigateToCustom,
                    modifier = Modifier.offset(screenWidth - u(24) - u(182), u(262))
                )
            }
        }

        HorizontalFloatingToolbar(
            expanded = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
        ) {
            ToolbarAction(ImageVector.vectorResource(R.drawable.ic_help_outline), stringResource(R.string.help), onNavigateToHelp)
            ToolbarAction(Icons.Filled.Settings, stringResource(R.string.settings), onNavigateToSettings)
            ToolbarAction(Icons.Filled.Info, stringResource(R.string.about), onNavigateToAbout)
            ToolbarAction(ImageVector.vectorResource(R.drawable.ic_update), stringResource(R.string.check_updates), onNavigateToUpdate)
        }
    }
}

// Estrella de cuatro puntas para la noche: no está en MaterialShapes.
private val NightStar: RoundedPolygon =
    RoundedPolygon.star(numVerticesPerRadius = 4, innerRadius = 0.42f, rounding = CornerRounding(0.12f)).normalized()

/**
 * La calculadora estándar: una galleta con canto, como una pegatina. Al presionarla se hunde
 * sobre su canto y se transforma hacia un cuadrado; al soltarla vuelve con el rebote del resorte.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CookieButton(title: String, detail: String, size: Dp, onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "cookiePress"
    )
    val morph = remember { Morph(MaterialShapes.Cookie9Sided, MaterialShapes.Square) }
    val shape = MorphShape(morph, (press * 0.55f).coerceIn(0f, 1f))
    val depth = depthColor(colors.primary)

    Box(modifier = modifier.size(size).scale(1f - 0.04f * press)) {
        Box(
            Modifier
                .offset(lerpDp(6.dp, 2.dp, press), lerpDp(8.dp, 2.dp, press))
                .fillMaxSize()
                .background(depth, shape)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
                .background(lerp(colors.primary, Color.Black, 0.1f * press))
                .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
                .padding(size * 0.15f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 28.sp, lineHeight = 30.sp, letterSpacing = (-0.3).sp),
                    fontWeight = FontWeight.Bold,
                    color = colors.onPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = detail,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, fontFeatureSettings = "tnum"),
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onPrimary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * La calculadora personalizada: un cuadrado de esquinas cerradas e inclinado, en tensión con la
 * galleta redonda. El texto queda derecho para que se lea sin esfuerzo.
 */
@Composable
private fun SlantedButton(title: String, detail: String, size: Dp, onClick: () -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        label = "slantedPress"
    )
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .size(size)
            .scale(1f - 0.04f * press)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
    ) {
        Box(
            Modifier
                .offset(lerpDp(6.dp, 2.dp, press), lerpDp(8.dp, 2.dp, press))
                .fillMaxSize()
                .rotate(-8f)
                .background(depthColor(colors.tertiaryContainer), shape)
        )
        Box(
            Modifier
                .fillMaxSize()
                .rotate(-8f)
                .background(lerp(colors.tertiaryContainer, Color.Black, 0.08f * press), shape)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 22.sp, lineHeight = 25.sp, letterSpacing = (-0.2).sp),
                fontWeight = FontWeight.Bold,
                color = colors.onTertiaryContainer
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp, lineHeight = 16.sp),
                color = colors.onTertiaryContainer.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolbarAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { PlainTooltip { Text(label) } },
        state = rememberTooltipState()
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = label)
        }
    }
}

/** «3 cortes · 30 · 30 · 40», calculado con los pesos reales del plan estándar. */
@Composable
private fun standardPlanSummary(): String {
    val cuts = StandardPlanWeights.toList().chunked(2).map { (formative, cognitive) ->
        ((formative + cognitive) * 100).roundToInt()
    }
    return stringResource(R.string.home_default_plan_format, cuts.size, cuts.joinToString(" · "))
}

@Composable
private fun savedFieldsSummary(fields: List<SavedField>): String {
    if (fields.isEmpty()) return stringResource(R.string.home_custom_hint)
    val resources = LocalResources.current
    val names = fields.joinToString(", ") { it.name.ifBlank { resources.getString(R.string.field_prefix, it.position) } }
    return stringResource(R.string.home_custom_saved_format, names)
}
