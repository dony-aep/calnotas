package com.donyaep.calnotas.ui.screens.customcalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.R
import com.donyaep.calnotas.ui.components.DashedBlock
import com.donyaep.calnotas.ui.components.LayeredBackgroundShape
import com.donyaep.calnotas.ui.components.NumberTile
import com.donyaep.calnotas.ui.components.connectedRowShape
import com.donyaep.calnotas.ui.components.connectedShape
import com.donyaep.calnotas.ui.components.depthColor
import com.donyaep.calnotas.ui.components.rememberSyncedTextFieldState
import kotlin.math.abs

/** Cómo va la calculadora: decide el color del cuadrado del resultado. */
private enum class SchemeMood { Empty, Progress, Passed, Failed, Check }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomCalculatorScreen(
    onBack: () -> Unit,
    viewModel: CustomCalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = MaterialTheme.colorScheme
    val snackbarHostState = remember { SnackbarHostState() }
    val flashMessageText = when (uiState.flashMessage) {
        CustomFlashMessage.CALCULATOR_SAVED -> stringResource(R.string.calculator_saved)
        CustomFlashMessage.INVALID_GRADE -> stringResource(R.string.invalid_grade_short)
        null -> null
    }

    LaunchedEffect(flashMessageText) {
        if (!flashMessageText.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message = flashMessageText)
            viewModel.dismissFlashMessage()
        }
    }

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_calculator_title)) },
            text = { Text(stringResource(R.string.reset_calculator_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    viewModel.resetAll()
                }) {
                    Text(stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    val minPassing = uiState.minPassingGrade.toDoubleOrNull() ?: 3.0
    // Lo asignado sale de los porcentajes escritos, tengan nota o no. El total del ViewModel solo
    // suma los campos con nota, que es lo que cuenta para la nota pero no para el reparto.
    val assigned = uiState.fields.sumOf { field -> field.percentage.toDoubleOrNull()?.takeIf { it > 0 } ?: 0.0 }
    val allGraded = uiState.fields.isNotEmpty() && uiState.fields.all { it.grade.isNotBlank() && it.percentage.isNotBlank() }
    val mood = when {
        uiState.fields.isEmpty() -> SchemeMood.Empty
        assigned > 100.0001 -> SchemeMood.Check
        abs(assigned - 100.0) < 0.0001 && allGraded ->
            if (uiState.totalFinalGrade >= minPassing) SchemeMood.Passed else SchemeMood.Failed
        else -> SchemeMood.Progress
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
                text = stringResource(R.string.custom_screen_title),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp, letterSpacing = (-0.2).sp),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .height(56.dp)
                    .padding(top = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 24.dp, top = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(26.dp),
                verticalAlignment = Alignment.Top
            ) {
                ResultSquare(
                    mood = mood,
                    total = uiState.totalFinalGrade,
                    onAddField = viewModel::addField
                )
                PassingGradeEditor(
                    value = uiState.minPassingGrade,
                    onValueChange = viewModel::onMinPassingGradeChanged,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 18.dp)
                )
            }

            if (uiState.fields.isEmpty()) {
                Text(
                    text = stringResource(R.string.custom_empty_intro),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp, lineHeight = 22.sp, letterSpacing = 0.sp),
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 22.dp)
                )
            }

            Allocation(
                fields = uiState.fields,
                assigned = assigned,
                passed = mood == SchemeMood.Passed,
                failed = mood == SchemeMood.Failed,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 22.dp)
            )

            if (uiState.fields.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.fields.forEachIndexed { index, field ->
                        FieldSection(
                            field = field,
                            index = index,
                            count = uiState.fields.size,
                            resyncKey = uiState.flashMessage,
                            onNameChange = { viewModel.onNameChanged(field.id, it) },
                            onPercentageChange = { viewModel.onPercentageChanged(field.id, it) },
                            onGradeChange = { viewModel.onGradeChanged(field.id, it) },
                            onRemove = { viewModel.removeField(field.id) }
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 104.dp)
        )

        HorizontalFloatingToolbar(
            expanded = true,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            FilledIconButton(onClick = onBack, colors = IconButtonDefaults.filledTonalIconButtonColors()) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
            }
            FilledIconButton(onClick = { viewModel.addField() }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_field))
            }
            FilledIconButton(
                onClick = { viewModel.saveCalculator() },
                enabled = uiState.fields.isNotEmpty(),
                colors = IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                Icon(ImageVector.vectorResource(R.drawable.ic_save), contentDescription = stringResource(R.string.save_calculator))
            }
            FilledIconButton(
                onClick = { showResetDialog = true },
                enabled = uiState.fields.isNotEmpty() || uiState.hasSavedData,
                colors = IconButtonDefaults.filledTonalIconButtonColors()
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.reset))
            }
        }
    }
}

/**
 * El resultado: el cuadrado inclinado del inicio, con canto. Sin campos es un hueco punteado que
 * invita a agregar el primero; con porcentajes de más pide revisarlos.
 */
@Composable
private fun ResultSquare(mood: SchemeMood, total: Double, onAddField: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val size = 176.dp
    val shape = RoundedCornerShape(16.dp)

    if (mood == SchemeMood.Empty) {
        val outline = colors.outlineVariant
        Box(
            modifier = Modifier
                .size(size)
                .clickable(role = Role.Button, onClick = onAddField),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .rotate(-8f)
                    .drawBehind {
                        drawRoundRect(
                            color = outline,
                            cornerRadius = CornerRadius(16.dp.toPx()),
                            style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 6.dp.toPx())))
                        )
                    }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = colors.tertiary, modifier = Modifier.size(32.dp))
                Text(
                    stringResource(R.string.custom_add_first),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    val face by animateColorAsState(
        when (mood) {
            SchemeMood.Passed -> colors.tertiary
            SchemeMood.Failed, SchemeMood.Check -> colors.errorContainer
            else -> colors.tertiaryContainer
        },
        label = "squareFace"
    )
    val content by animateColorAsState(
        when (mood) {
            SchemeMood.Passed -> colors.onTertiary
            SchemeMood.Failed, SchemeMood.Check -> colors.onErrorContainer
            else -> colors.onTertiaryContainer
        },
        label = "squareContent"
    )
    val (label, number, sub) = when (mood) {
        SchemeMood.Check -> Triple(stringResource(R.string.calc_label_check), "—", stringResource(R.string.calc_check_sub))
        SchemeMood.Passed -> Triple(stringResource(R.string.calc_label_passed), total.format(), stringResource(R.string.calc_out_of))
        SchemeMood.Failed -> Triple(stringResource(R.string.calc_label_failed), total.format(), stringResource(R.string.calc_out_of))
        else -> Triple(stringResource(R.string.calc_label_progress), total.format(), stringResource(R.string.calc_out_of))
    }

    Box(modifier = Modifier.size(size)) {
        Box(
            Modifier
                .offset(6.dp, 8.dp)
                .fillMaxSize()
                .rotate(-8f)
                .background(depthColor(face), shape)
        )
        Box(
            Modifier
                .fillMaxSize()
                .rotate(-8f)
                .background(face, shape)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp), color = content)
            Text(
                number,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 48.sp, lineHeight = 54.sp, letterSpacing = (-1).sp, fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.Bold,
                color = content
            )
            Text(sub, style = MaterialTheme.typography.labelLarge.copy(fontSize = 13.sp), color = content.copy(alpha = 0.8f))
        }
    }
}

/** «Apruebas con 3,0»: la nota mínima, editable ahí mismo. */
@Composable
private fun PassingGradeEditor(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val label = stringResource(R.string.min_passing_grade)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.custom_pass_with),
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
            color = colors.onSurfaceVariant
        )
        BasicTextField(
            state = rememberSyncedTextFieldState(value, onValueChange),
            lineLimits = TextFieldLineLimits.SingleLine,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFeatureSettings = "tnum",
                color = colors.onSurface
            ),
            cursorBrush = SolidColor(colors.primary),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = label },
            decorator = { field ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceContainerHigh, RoundedCornerShape(16.dp))
                        .padding(start = 14.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(1f)) { field() }
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(18.dp))
                }
            }
        )
        Text(
            stringResource(R.string.custom_pass_hint),
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.5.sp, lineHeight = 16.sp),
            color = colors.outline
        )
    }
}

/**
 * «Cómo se reparte tu nota»: cada campo ocupa su porcentaje. Lo que falta se marca punteado y,
 * si la suma pasa de 100, la escala se estira y la línea del 100 % deja ver el exceso.
 */
@Composable
private fun Allocation(
    fields: List<CustomFieldUi>,
    assigned: Double,
    passed: Boolean,
    failed: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val resources = LocalResources.current
    // Cada tramo con su nombre; un campo sin nombre usa el mismo «Campo n» que muestra su bloque.
    val parts = fields.mapIndexedNotNull { index, field ->
        field.percentage.toDoubleOrNull()?.takeIf { it > 0 }?.let { percent ->
            field.name.ifBlank { resources.getString(R.string.field_prefix, index + 1) } to percent
        }
    }
    val missing = 100.0 - assigned
    val exceeded = missing < -0.0001
    val caption = when {
        fields.isEmpty() -> stringResource(R.string.custom_empty_caption)
        exceeded ->
            stringResource(R.string.custom_exceeded_format, stringResource(R.string.percent_format, (-missing).formatPercent()))
        missing > 0.0001 ->
            stringResource(R.string.custom_unassigned_format, stringResource(R.string.percent_format, missing.formatPercent()))
        passed -> stringResource(R.string.passing_message)
        failed -> stringResource(R.string.failing_message)
        else -> stringResource(R.string.custom_complete)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.custom_allocation_title), style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp), fontWeight = FontWeight.SemiBold)

        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val gap = 3.dp
            val scale = maxOf(100.0, assigned)
            val segmentCount = parts.size + if (missing > 0.05 && parts.isNotEmpty()) 1 else 0
            fun widthOf(percent: Double): Dp = (maxWidth * (percent / scale).toFloat() - gap).coerceAtLeast(0.dp)
            val heaviest = parts.indices.maxByOrNull { parts[it].second }

            if (parts.isEmpty()) {
                DashedBlock(
                    text = stringResource(R.string.custom_allocation_none),
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                    parts.forEachIndexed { i, (name, percent) ->
                        val over = exceeded && i == parts.lastIndex
                        val (bg, fg) = when {
                            over -> colors.errorContainer to colors.onErrorContainer
                            i == heaviest -> colors.tertiary to colors.onTertiary
                            else -> colors.tertiaryContainer to colors.onTertiaryContainer
                        }
                        val width = widthOf(percent)
                        Column(
                            modifier = Modifier
                                .width(width)
                                .height(60.dp)
                                .background(bg, connectedRowShape(i, segmentCount))
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (width > 60.dp) {
                                Text(name, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), color = fg, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (width > 34.dp) {
                                Text(
                                    stringResource(R.string.percent_format, percent.formatPercent()),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp, fontFeatureSettings = "tnum"),
                                    fontWeight = FontWeight.Bold,
                                    color = fg,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Clip
                                )
                            }
                        }
                    }
                    if (missing > 0.05) {
                        DashedBlock(
                            text = stringResource(R.string.custom_allocation_missing_format, stringResource(R.string.percent_format, missing.formatPercent())),
                            modifier = Modifier.width(widthOf(missing)).height(60.dp),
                            shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp)
                        )
                    }
                }
                if (exceeded) {
                    // La línea del 100 %: lo que queda a su derecha es lo que sobra.
                    Box(
                        Modifier
                            .offset(x = maxWidth * (100.0 / scale).toFloat() - 1.dp, y = (-6).dp)
                            .width(2.dp)
                            .height(72.dp)
                            .background(colors.error)
                    )
                }
            }
        }

        Text(
            caption,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 18.sp, letterSpacing = 0.sp),
            fontWeight = FontWeight.Medium,
            color = if (exceeded) colors.error else colors.onSurfaceVariant
        )
    }
}

/** Un campo del esquema: el nombre en grande, y su porcentaje y nota lado a lado. */
@Composable
private fun FieldSection(
    field: CustomFieldUi,
    index: Int,
    count: Int,
    resyncKey: Any?,
    onNameChange: (String) -> Unit,
    onPercentageChange: (String) -> Unit,
    onGradeChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val placeholder = stringResource(R.string.field_prefix, index + 1)
    Surface(shape = connectedShape(index, count), color = colors.surfaceContainerLow) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val nameLabel = stringResource(R.string.field_name)
                BasicTextField(
                    state = rememberSyncedTextFieldState(field.name, onNameChange),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = colors.onSurface),
                    cursorBrush = SolidColor(colors.primary),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = nameLabel },
                    decorator = { inner ->
                        Box(Modifier.padding(4.dp)) {
                            if (field.name.isEmpty()) {
                                Text(placeholder, style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp), fontWeight = FontWeight.SemiBold, color = colors.outline)
                            }
                            inner()
                        }
                    }
                )
                FilledTonalIconButton(onClick = onRemove) {
                    Icon(ImageVector.vectorResource(R.drawable.ic_delete_outline), contentDescription = stringResource(R.string.remove_field))
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                NumberTile(
                    label = stringResource(R.string.percentage),
                    value = field.percentage,
                    onValueChange = onPercentageChange,
                    unit = "%",
                    modifier = Modifier.weight(1f)
                )
                NumberTile(
                    label = stringResource(R.string.grade),
                    value = field.grade,
                    onValueChange = onGradeChange,
                    resyncKey = resyncKey,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

private fun Double.format(): String = String.format("%.2f", this)

private fun Double.formatPercent(): String {
    val rounded = Math.round(this * 10) / 10.0
    return if (abs(rounded % 1.0) < 1e-9) rounded.toLong().toString() else String.format("%.1f", rounded)
}
