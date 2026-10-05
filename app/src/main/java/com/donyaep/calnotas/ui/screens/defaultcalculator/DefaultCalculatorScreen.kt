package com.donyaep.calnotas.ui.screens.defaultcalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.toShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.components.HintKind
import com.donyaep.calnotas.ui.components.LayeredBackgroundShape
import com.donyaep.calnotas.ui.components.NumberTile
import com.donyaep.calnotas.ui.components.connectedShape
import com.donyaep.calnotas.ui.components.depthColor
import com.donyaep.calnotas.ui.components.rememberMorphingShape
import kotlin.math.roundToInt

private const val PassingGrade = 3.0
private const val GradeCount = 6

/** Cómo va la nota: decide la forma y el color de la galleta del resultado. */
private enum class ResultMood { Empty, Progress, Passed, Failed }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultCalculatorScreen(
    onBack: () -> Unit,
    viewModel: DefaultCalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme

    uiState.error?.let { error ->
        val message = when (error) {
            DefaultCalculatorError.INVALID_NUMBER -> stringResource(R.string.invalid_number_message)
            DefaultCalculatorError.INVALID_RANGE -> stringResource(R.string.invalid_range_message)
        }
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text(stringResource(R.string.invalid_grade_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) { Text(stringResource(R.string.ok)) }
            }
        )
    }

    val grades = listOf(uiState.grade1, uiState.grade2, uiState.grade3, uiState.grade4, uiState.grade5, uiState.grade6)
    val finals = listOf(uiState.final1, uiState.final2, uiState.final3)
    val filled = grades.count { it.isNotBlank() }
    val complete = uiState.predictionState == PredictionState.COMPLETE

    // Mientras faltan notas el resultado no se pinta de rojo: solo cuando el 3,0 ya no es posible.
    val mood = when {
        !uiState.hasAnyInput -> ResultMood.Empty
        uiState.predictionState == PredictionState.IMPOSSIBLE -> ResultMood.Failed
        complete && uiState.total < PassingGrade -> ResultMood.Failed
        complete || uiState.predictionState == PredictionState.GUARANTEED -> ResultMood.Passed
        else -> ResultMood.Progress
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        LayeredBackgroundShape(
            MaterialShapes.Sunny.toShape(), 300.dp, 260.dp, 16.dp, 10.dp,
            Modifier.align(Alignment.TopEnd).offset(120.dp, (-90).dp)
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
                text = stringResource(R.string.default_calculator_title),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, letterSpacing = (-0.2).sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
                    .padding(top = 16.dp)
            )

            ResultCookie(
                mood = mood,
                total = uiState.total,
                filled = filled,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = statusText(uiState, complete),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 6.dp)
            )

            GradeScale(
                value = uiState.total.takeIf { uiState.hasAnyInput },
                markerColor = when (mood) {
                    ResultMood.Passed -> colors.primary
                    ResultMood.Failed -> colors.error
                    else -> colors.onSurface
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 22.dp)
                    .height(52.dp)
            )

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val required = uiState.requiredMinGrade.formatOneDecimal()
                val safety = uiState.safetyGrade?.formatOneDecimal()
                val suggestion = stringResource(R.string.prediction_field_hint_format, required)
                val secure = safety?.let { stringResource(R.string.prediction_secure_hint_format, it) }

                fun hintFor(index: Int): Pair<HintKind, String>? = when {
                    secure != null && uiState.safetyGradeFieldIndex == index -> HintKind.Secure to secure
                    grades[index - 1].isBlank() && uiState.predictionState == PredictionState.POSSIBLE ->
                        HintKind.Suggestion to suggestion
                    else -> null
                }

                StandardPlanWeights.toList().chunked(2).forEachIndexed { cut, (formativeWeight, cognitiveWeight) ->
                    val formativeIndex = cut * 2 + 1
                    val cognitiveIndex = cut * 2 + 2
                    CutSection(
                        title = stringResource(R.string.cut_title_format, cut + 1),
                        weight = percentLabel(formativeWeight + cognitiveWeight),
                        final = finals[cut].format(),
                        hasGrades = grades[formativeIndex - 1].isNotBlank() || grades[cognitiveIndex - 1].isNotBlank(),
                        index = cut,
                        count = 3
                    ) {
                        NumberTile(
                            label = stringResource(R.string.formative),
                            value = grades[formativeIndex - 1],
                            onValueChange = { viewModel.onGradeChanged(formativeIndex, it) },
                            trailingLabel = percentLabel(formativeWeight),
                            hint = hintFor(formativeIndex),
                            resyncKey = uiState.error,
                            modifier = Modifier.weight(1f)
                        )
                        NumberTile(
                            label = stringResource(R.string.cognitive),
                            value = grades[cognitiveIndex - 1],
                            onValueChange = { viewModel.onGradeChanged(cognitiveIndex, it) },
                            trailingLabel = percentLabel(cognitiveWeight),
                            hint = hintFor(cognitiveIndex),
                            resyncKey = uiState.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        HorizontalFloatingToolbar(
            expanded = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            FilledIconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            FilledIconButton(
                onClick = { viewModel.clearAll() },
                enabled = uiState.hasAnyInput,
                colors = IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                Icon(ImageVector.vectorResource(R.drawable.ic_cleaning_services), contentDescription = stringResource(R.string.clear))
            }
        }
    }
}

/**
 * El resultado: la galleta del inicio con canto, rodeada por un anillo ondulado que se llena con
 * las notas escritas. Al aprobar se transforma en sol; si el 3,0 ya no es posible, en una forma
 * más cerrada y roja.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ResultCookie(mood: ResultMood, total: Double, filled: Int, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val polygon = when (mood) {
        ResultMood.Passed -> MaterialShapes.Sunny
        ResultMood.Failed -> MaterialShapes.Cookie4Sided
        else -> MaterialShapes.Cookie9Sided
    }
    val shape = rememberMorphingShape(polygon)
    val face by animateColorAsState(
        when (mood) {
            ResultMood.Empty -> colors.surfaceContainerHighest
            ResultMood.Progress -> colors.primaryContainer
            ResultMood.Passed -> colors.primary
            ResultMood.Failed -> colors.errorContainer
        },
        label = "resultFace"
    )
    val content by animateColorAsState(
        when (mood) {
            ResultMood.Empty -> colors.onSurfaceVariant
            ResultMood.Progress -> colors.onPrimaryContainer
            ResultMood.Passed -> colors.onPrimary
            ResultMood.Failed -> colors.onErrorContainer
        },
        label = "resultContent"
    )
    val label = when (mood) {
        ResultMood.Empty -> R.string.calc_label_empty
        ResultMood.Progress -> R.string.calc_label_progress
        ResultMood.Passed -> R.string.calc_label_passed
        ResultMood.Failed -> R.string.calc_label_failed
    }

    Box(modifier = modifier.height(304.dp)) {
        CircularWavyProgressIndicator(
            progress = { filled / GradeCount.toFloat() },
            color = if (mood == ResultMood.Failed) colors.error else colors.primary,
            trackColor = colors.surfaceContainerHigh,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
                .size(272.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 34.dp)
                .size(212.dp)
        ) {
            Box(
                Modifier
                    .offset(5.dp, 6.dp)
                    .fillMaxSize()
                    .background(depthColor(face), shape)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(face, shape),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(label), style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp), color = content)
                Text(
                    text = total.format(),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 54.sp,
                        lineHeight = 60.sp,
                        letterSpacing = (-1).sp,
                        fontFeatureSettings = "tnum"
                    ),
                    fontWeight = FontWeight.Bold,
                    color = content
                )
                Text(
                    stringResource(R.string.calc_out_of),
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp),
                    color = content,
                    modifier = Modifier.graphicsLayer { alpha = 0.8f }
                )
            }
        }
        Text(
            text = stringResource(R.string.calc_filled_format, filled, GradeCount),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp, fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurfaceVariant,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/** La frase bajo el resultado: qué falta, o cómo terminó. La cifra clave va resaltada. */
@Composable
private fun statusText(state: DefaultCalculatorUiState, complete: Boolean): AnnotatedString {
    val highlight = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    return when {
        !state.hasAnyInput -> AnnotatedString(stringResource(R.string.default_empty_status))
        state.predictionState == PredictionState.POSSIBLE -> {
            val grade = state.requiredMinGrade.formatOneDecimal()
            val text = stringResource(R.string.prediction_needed_format, grade, state.emptyFieldsCount)
            buildAnnotatedString {
                append(text)
                val start = text.indexOf(grade)
                if (start >= 0) addStyle(highlight, start, start + grade.length)
            }
        }
        state.predictionState == PredictionState.GUARANTEED -> AnnotatedString(stringResource(R.string.prediction_guaranteed))
        state.predictionState == PredictionState.IMPOSSIBLE -> AnnotatedString(stringResource(R.string.prediction_impossible))
        complete && state.total >= PassingGrade -> AnnotatedString(stringResource(R.string.passing_message))
        complete -> AnnotatedString(stringResource(R.string.failing_message))
        else -> AnnotatedString("")
    }
}

/** La escala de 0 a 5 con la franja desde el 3,0 y, si hay notas, dónde está la nota. */
@Composable
private fun GradeScale(value: Double?, markerColor: Color, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val measurer = rememberTextMeasurer()
    val small = TextStyle(fontSize = 12.sp, color = colors.outline)
    val passStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.primary)
    val markerStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = markerColor, fontFeatureSettings = "tnum")
    val zone = lerp(colors.background, colors.primaryContainer, 0.6f)
    val passLabel = PassingGrade.formatOneDecimal()
    val markerLabel = value?.format()

    Canvas(modifier = modifier) {
        val w = size.width
        fun x(v: Double) = (v / 5.0 * w).toFloat()
        val y = 22.dp.toPx()

        drawRoundRect(zone, Offset(x(PassingGrade), y - 6.dp.toPx()), Size(w - x(PassingGrade), 12.dp.toPx()), CornerRadius(6.dp.toPx()))
        drawLine(colors.outlineVariant, Offset(0f, y), Offset(w, y), 2.dp.toPx())
        for (v in 0..5) {
            if (v == 3) continue
            drawLine(colors.outlineVariant, Offset(x(v.toDouble()), y - 5.dp.toPx()), Offset(x(v.toDouble()), y + 5.dp.toPx()), 2.dp.toPx())
            val label = measurer.measure(v.toString(), small)
            drawText(label, topLeft = Offset((x(v.toDouble()) - label.size.width / 2f).coerceIn(0f, w - label.size.width), y + 10.dp.toPx()))
        }
        drawLine(colors.primary, Offset(x(PassingGrade), y - 14.dp.toPx()), Offset(x(PassingGrade), y + 14.dp.toPx()), 3.dp.toPx())
        val pass = measurer.measure(passLabel, passStyle)
        drawText(pass, topLeft = Offset(x(PassingGrade) - pass.size.width / 2f, y + 14.dp.toPx()))

        if (value != null && markerLabel != null) {
            val mx = x(value.coerceIn(0.0, 5.0))
            drawCircle(colors.background, 11.dp.toPx(), Offset(mx, y))
            drawCircle(markerColor, 8.dp.toPx(), Offset(mx, y))
            val marker = measurer.measure(markerLabel, markerStyle)
            drawText(marker, topLeft = Offset((mx - marker.size.width / 2f).coerceIn(0f, w - marker.size.width), y - 30.dp.toPx()))
        }
    }
}

@Composable
private fun CutSection(
    title: String,
    weight: String,
    final: String,
    hasGrades: Boolean,
    index: Int,
    count: Int,
    tiles: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(shape = connectedShape(index, count), color = colors.surfaceContainerLow) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp), fontWeight = FontWeight.SemiBold)
                Text(
                    " · $weight",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = colors.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                Text(
                    final,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontFeatureSettings = "tnum"),
                    fontWeight = FontWeight.Bold,
                    color = if (hasGrades) colors.onSurface else colors.outline
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), content = tiles)
        }
    }
}

@Composable
private fun percentLabel(weight: Double): String =
    stringResource(R.string.percent_format, (weight * 100).roundToInt().toString())

private fun Double.format(): String = String.format("%.2f", this)
private fun Double.formatOneDecimal(): String = String.format("%.1f", this)
