package com.donyaep.calnotas.ui.screens.help

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.components.DashedBlock
import com.donyaep.calnotas.ui.components.SecondaryScreenScaffold
import com.donyaep.calnotas.ui.components.SectionLabel
import com.donyaep.calnotas.ui.components.WavyHeadline
import com.donyaep.calnotas.ui.components.connectedRowShape
import com.donyaep.calnotas.ui.components.connectedShape
import com.donyaep.calnotas.ui.components.depthColor
import com.donyaep.calnotas.ui.screens.defaultcalculator.StandardPlanWeights
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

private enum class HelpTab { Standard, Custom }

// El ejemplo resuelto: una formativa y una cognitiva iguales en los tres cortes.
private const val ExampleFormative = 4.5
private const val ExampleCognitive = 3.0
private const val PassingGrade = 3.0

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    onOpenDefault: () -> Unit,
    onOpenCustom: () -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(HelpTab.Standard) }
    val motion = MaterialTheme.motionScheme

    SecondaryScreenScaffold(
        title = stringResource(R.string.help_title),
        backgroundShape = MaterialShapes.Clover4Leaf.toShape(),
        toolbar = {
            FilledIconButton(onClick = onBack, colors = IconButtonDefaults.filledTonalIconButtonColors()) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            // Después de leer cómo funciona, la calculadora de la pestaña abierta queda a un toque.
            Button(onClick = if (tab == HelpTab.Standard) onOpenDefault else onOpenCustom) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_calculate),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(if (tab == HelpTab.Standard) R.string.help_open_default else R.string.help_open_custom))
            }
        }
    ) {
        HelpTabs(
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp)
        )

        AnimatedContent(
            targetState = tab,
            transitionSpec = { fadeIn(motion.defaultEffectsSpec()) togetherWith fadeOut(motion.fastEffectsSpec()) },
            label = "helpTab"
        ) { current ->
            Column {
                when (current) {
                    HelpTab.Standard -> StandardHelp()
                    HelpTab.Custom -> CustomHelp()
                }
            }
        }
    }
}

/** Selector de pestaña: cada una toma el color de su calculadora, azul la estándar y lila la personalizada. */
@Composable
private fun HelpTabs(selected: HelpTab, onSelect: (HelpTab) -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val tabs = listOf(
        Triple(HelpTab.Standard, R.string.help_tab_standard, colors.primary to colors.onPrimary),
        Triple(HelpTab.Custom, R.string.help_tab_custom, colors.tertiary to colors.onTertiary)
    )
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        tabs.forEachIndexed { index, (tab, label, checkedColors) ->
            val checked = tab == selected
            ToggleButton(
                checked = checked,
                onCheckedChange = { onSelect(tab) },
                shapes = if (index == 0) ButtonGroupDefaults.connectedLeadingButtonShapes()
                else ButtonGroupDefaults.connectedTrailingButtonShapes(),
                colors = ToggleButtonDefaults.colors(
                    containerColor = colors.surfaceContainerHigh,
                    contentColor = colors.onSurfaceVariant,
                    checkedContainerColor = checkedColors.first,
                    checkedContentColor = checkedColors.second
                ),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
            ) {
                if (checked) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(6.dp))
                }
                Text(stringResource(label), maxLines = 1)
            }
        }
    }
}

@Composable
private fun StandardHelp() {
    val colors = MaterialTheme.colorScheme
    val highlight = stringResource(R.string.help_standard_headline_highlight)

    HelpIntro(
        overline = stringResource(R.string.default_calculator_title),
        headline = stringResource(R.string.help_standard_headline_format, highlight),
        highlight = highlight,
        waveColor = colors.primary,
        body = stringResource(R.string.help_standard_intro, ExampleFormative.oneDecimal(), ExampleCognitive.oneDecimal())
    )
    WeightsSection(Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp))
    GradeKindBlocks(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp))
    ExampleCard(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp))
    PredictionCard(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp))
    PassingCard(Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp))

    Row(
        modifier = Modifier.padding(start = 28.dp, end = 28.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(Icons.Outlined.Info, contentDescription = null, tint = colors.outline, modifier = Modifier.size(18.dp))
        Text(
            stringResource(R.string.help_predefined_percentages),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp, letterSpacing = 0.sp),
            color = colors.outline
        )
    }
}

@Composable
private fun HelpIntro(overline: String, headline: String, highlight: String, waveColor: Color, body: String) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp)) {
        Text(overline, style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), color = colors.onSurfaceVariant)
        WavyHeadline(
            text = headline,
            highlight = highlight,
            waveColor = waveColor,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 34.sp, lineHeight = 40.sp, letterSpacing = (-0.6).sp),
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 23.sp, letterSpacing = 0.sp),
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/** Los seis pesos del plan estándar a escala, agrupados por corte. Salen de los mismos datos que usa la calculadora. */
@Composable
private fun WeightsSection(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val cuts = StandardPlanWeights.toList().chunked(2)
    Column(modifier = modifier) {
        SectionLabel(stringResource(R.string.help_weights_title))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            cuts.forEachIndexed { cut, weights ->
                Column(modifier = Modifier.weight(weights.sum().toFloat()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row {
                        Text(
                            stringResource(R.string.cut_title_format, cut + 1),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.weight(1f))
                        Text(percentLabel(weights.sum()), style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = colors.outline)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        weights.forEachIndexed { index, weight ->
                            val (face, content) = if (index == 0) colors.primary to colors.onPrimary
                            else colors.primaryContainer to colors.onPrimaryContainer
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .background(face, connectedRowShape(index, 2))
                                    .padding(8.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(
                                    (weight * 100).roundToInt().toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontFeatureSettings = "tnum"),
                                    fontWeight = FontWeight.Bold,
                                    color = content
                                )
                            }
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            LegendItem(colors.primary, stringResource(R.string.formative))
            LegendItem(colors.primaryContainer, stringResource(R.string.cognitive))
        }
        Text(
            stringResource(R.string.help_weights_note),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 19.sp, letterSpacing = 0.sp),
            color = colors.outline,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            Modifier
                .size(12.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Text(label, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun GradeKindBlocks(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val kinds = listOf(
        Triple(stringResource(R.string.formative), R.string.help_formative_title to R.string.help_formative_body, colors.primary to colors.onPrimary),
        Triple(stringResource(R.string.cognitive), R.string.help_cognitive_title to R.string.help_cognitive_body, colors.primaryContainer to colors.onPrimaryContainer)
    )
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        kinds.forEachIndexed { index, (name, texts, badge) ->
            Surface(shape = connectedShape(index, kinds.size), color = colors.surfaceContainerLow) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(badge.first, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(name.take(1), style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp), fontWeight = FontWeight.Bold, color = badge.second)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(stringResource(texts.first), style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp), fontWeight = FontWeight.SemiBold)
                        Text(
                            stringResource(texts.second),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp),
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/** El ejemplo resuelto, con el mismo redondeo por corte que la calculadora (mitad hacia arriba). */
@Composable
private fun ExampleCard(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val formative = ExampleFormative.oneDecimal()
    val cognitive = ExampleCognitive.oneDecimal()
    val cuts = StandardPlanWeights.toList().chunked(2).map { (formativeWeight, cognitiveWeight) ->
        val value = BigDecimal.valueOf(ExampleFormative) * BigDecimal.valueOf(formativeWeight) +
            BigDecimal.valueOf(ExampleCognitive) * BigDecimal.valueOf(cognitiveWeight)
        Triple(formativeWeight, cognitiveWeight, value.setScale(2, RoundingMode.HALF_UP))
    }
    val total = cuts.fold(BigDecimal.ZERO) { sum, cut -> sum + cut.third }

    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = colors.surfaceContainerLow) {
        Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 22.dp)) {
            Text(stringResource(R.string.help_example_title), style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.help_example_description, formative, cognitive),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp),
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
            Column(modifier = Modifier.padding(top = 14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                cuts.forEachIndexed { index, (formativeWeight, cognitiveWeight, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colors.surfaceContainerHigh, connectedShape(index, cuts.size, outer = 16.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(stringResource(R.string.cut_title_format, index + 1), style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), fontWeight = FontWeight.SemiBold)
                            Text(
                                "$formative × ${percentLabel(formativeWeight)} + $cognitive × ${percentLabel(cognitiveWeight)}",
                                style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp, fontFeatureSettings = "tnum"),
                                color = colors.outline
                            )
                        }
                        Text(value.twoDecimals(), style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontFeatureSettings = "tnum"), fontWeight = FontWeight.Bold)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(stringResource(R.string.help_example_total), style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), fontWeight = FontWeight.SemiBold)
                    Text(
                        cuts.joinToString(" + ") { it.third.twoDecimals() },
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp, fontFeatureSettings = "tnum"),
                        color = colors.outline
                    )
                    Row(
                        modifier = Modifier
                            .background(colors.primaryContainer, CircleShape)
                            .padding(start = 8.dp, end = 10.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = colors.onPrimaryContainer, modifier = Modifier.size(14.dp))
                        Text(
                            stringResource(R.string.help_example_passes),
                            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onPrimaryContainer
                        )
                    }
                }
                Text(
                    total.twoDecimals(),
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 52.sp, lineHeight = 56.sp, letterSpacing = (-1).sp, fontFeatureSettings = "tnum"),
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
            }
        }
    }
}

/** Las dos pistas de la calculadora, dibujadas como se ven bajo un campo vacío. */
@Composable
private fun PredictionCard(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val lastWeight = StandardPlanWeights.last()
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = colors.surfaceContainerLow) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(stringResource(R.string.help_prediction_title), style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.SemiBold)
            Text(
                stringResource(R.string.help_prediction_summary),
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.sp),
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp)
            )
            Row(modifier = Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SampleTile(
                    label = stringResource(R.string.formative),
                    weight = percentLabel(lastWeight),
                    hint = stringResource(R.string.prediction_secure_hint_format, 4.1.oneDecimal()),
                    hintColor = colors.tertiary,
                    hintWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                SampleTile(
                    label = stringResource(R.string.cognitive),
                    weight = percentLabel(lastWeight),
                    hint = stringResource(R.string.prediction_field_hint_format, 2.1.oneDecimal()),
                    hintColor = colors.primary,
                    hintWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SampleTile(label: String, weight: String, hint: String, hintColor: Color, hintWeight: FontWeight, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceContainerHigh, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Text(label, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp), color = colors.onSurfaceVariant)
                Spacer(Modifier.weight(1f))
                Text(weight, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp), color = colors.outline)
            }
            Text("—", style = MaterialTheme.typography.headlineMedium.copy(fontSize = 26.sp, lineHeight = 32.sp), color = colors.outline)
        }
        Text(
            hint,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp, lineHeight = 16.sp),
            fontWeight = hintWeight,
            color = hintColor,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PassingCard(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val passing = PassingGrade.oneDecimal()
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = colors.primaryContainer) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // El mismo sol que toma el resultado de la calculadora al aprobar.
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colors.primary, MaterialShapes.VerySunny.toShape()),
                contentAlignment = Alignment.Center
            ) {
                Text(passing, style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontFeatureSettings = "tnum"), fontWeight = FontWeight.Bold, color = colors.onPrimary)
            }
            Text(
                stringResource(R.string.help_remember_passing, passing),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.sp),
                fontWeight = FontWeight.SemiBold,
                color = colors.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun CustomHelp() {
    val colors = MaterialTheme.colorScheme
    val highlight = stringResource(R.string.help_custom_headline_highlight)

    HelpIntro(
        overline = stringResource(R.string.custom_screen_title),
        headline = stringResource(R.string.help_custom_headline_format, highlight),
        highlight = highlight,
        waveColor = colors.tertiary,
        body = stringResource(R.string.help_custom_description)
    )
    SampleAllocation(Modifier.padding(start = 24.dp, end = 24.dp, top = 32.dp))

    SectionLabel(
        stringResource(R.string.help_custom_steps_title),
        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 12.dp)
    )
    val steps = listOf(
        stringResource(R.string.help_custom_point_1),
        stringResource(R.string.help_custom_point_2),
        stringResource(R.string.help_custom_point_3),
        stringResource(R.string.help_custom_point_4, PassingGrade.oneDecimal()),
        stringResource(R.string.help_custom_point_5)
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        steps.forEachIndexed { index, text -> StepBlock(index, steps.size, text) }
    }

    Surface(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = colors.tertiaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // El cuadrado inclinado de la calculadora personalizada, en pequeño.
            Box(Modifier.size(60.dp)) {
                Box(
                    Modifier
                        .offset(4.dp, 5.dp)
                        .size(52.dp)
                        .rotate(-8f)
                        .background(depthColor(colors.tertiaryContainer), RoundedCornerShape(10.dp))
                )
                Box(
                    Modifier
                        .size(52.dp)
                        .rotate(-8f)
                        .background(colors.tertiary, RoundedCornerShape(10.dp))
                )
            }
            Text(
                stringResource(R.string.help_customize_note),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 21.sp, letterSpacing = 0.sp),
                fontWeight = FontWeight.Medium,
                color = colors.onTertiaryContainer
            )
        }
    }
}

/** Una muestra de la barra de reparto de la calculadora personalizada, con un hueco por asignar. */
@Composable
private fun SampleAllocation(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    // Tramos anchos a propósito: «Falta 30 %» y cada porcentaje caben enteros en un teléfono de 360 dp.
    val parts = listOf(
        stringResource(R.string.help_custom_sample_1) to 40,
        stringResource(R.string.help_custom_sample_2) to 30
    )
    val missing = 100 - parts.sumOf { it.second }
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel(stringResource(R.string.help_custom_bar_title))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            parts.forEachIndexed { index, (name, percent) ->
                val (face, content) = if (index == 0) colors.tertiary to colors.onTertiary
                else colors.tertiaryContainer to colors.onTertiaryContainer
                Column(
                    modifier = Modifier
                        .weight(percent.toFloat())
                        .height(60.dp)
                        .background(face, connectedRowShape(index, parts.size + 1))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(name, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), color = content, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(
                        stringResource(R.string.percent_format, percent.toString()),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontFeatureSettings = "tnum"),
                        fontWeight = FontWeight.Bold,
                        color = content,
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Clip
                    )
                }
            }
            DashedBlock(
                text = stringResource(R.string.custom_allocation_missing_format, stringResource(R.string.percent_format, missing.toString())),
                modifier = Modifier
                    .weight(missing.toFloat())
                    .height(60.dp),
                shape = connectedRowShape(parts.size, parts.size + 1)
            )
        }
        Text(
            stringResource(R.string.help_custom_bar_caption),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 19.sp, letterSpacing = 0.sp),
            color = colors.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun StepBlock(index: Int, count: Int, text: String) {
    val colors = MaterialTheme.colorScheme
    Surface(shape = connectedShape(index, count), color = colors.surfaceContainerLow) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(colors.tertiaryContainer, MaterialShapes.Cookie6Sided.toShape()),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp), fontWeight = FontWeight.Bold, color = colors.onTertiaryContainer)
            }
            Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 21.sp, letterSpacing = 0.sp))
        }
    }
}

@Composable
private fun percentLabel(weight: Double): String =
    stringResource(R.string.percent_format, (weight * 100).roundToInt().toString())

private fun Double.oneDecimal(): String = String.format("%.1f", this)
private fun BigDecimal.twoDecimals(): String = String.format("%.2f", this)
