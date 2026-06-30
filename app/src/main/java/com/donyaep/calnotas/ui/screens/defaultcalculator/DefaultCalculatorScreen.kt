package com.donyaep.calnotas.ui.screens.defaultcalculator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donyaep.calnotas.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DefaultCalculatorScreen(
    onBack: () -> Unit,
    viewModel: DefaultCalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.default_calculator_title)) }
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

            val passed = uiState.total >= 3.0
            val completedInputs = listOf(
                uiState.grade1,
                uiState.grade2,
                uiState.grade3,
                uiState.grade4,
                uiState.grade5,
                uiState.grade6
            ).count { it.isNotBlank() }
            val completionProgress = completedInputs / 6f
            val targetContainerColor = when {
                !uiState.hasAnyInput -> MaterialTheme.colorScheme.surfaceContainerLow
                passed -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
                else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.60f)
            }
            val totalContainerColor by animateColorAsState(
                targetValue = targetContainerColor,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "result_card_color"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoGraph,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = stringResource(R.string.default_calculator_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.home_default_description),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                val isPossible = uiState.predictionState == PredictionState.POSSIBLE
                val requiredGradeText = uiState.requiredMinGrade.formatOneDecimal()
                val safetyGradeText = uiState.safetyGrade?.formatOneDecimal()

                @Composable
                fun suggestedHintFor(grade: String): String? =
                    if (grade.isBlank() && isPossible) {
                        stringResource(R.string.prediction_field_hint_format, requiredGradeText)
                    } else null

                @Composable
                fun secureHintFor(fieldIndex: Int): String? =
                    if (safetyGradeText != null && uiState.safetyGradeFieldIndex == fieldIndex) {
                        stringResource(R.string.prediction_secure_hint_format, safetyGradeText)
                    } else null

                GradeSectionCard(
                    title = stringResource(R.string.final_grade_1_format, uiState.final1.format())
                ) {
                    GradeInput(
                        value = uiState.grade1,
                        label = stringResource(R.string.grade_1_formative),
                        onValueChange = { viewModel.onGradeChanged(1, it) },
                        suggestedHint = suggestedHintFor(uiState.grade1),
                        secureHint = secureHintFor(1)
                    )
                    Spacer(Modifier.height(10.dp))
                    GradeInput(
                        value = uiState.grade2,
                        label = stringResource(R.string.grade_2_cognitive),
                        onValueChange = { viewModel.onGradeChanged(2, it) },
                        suggestedHint = suggestedHintFor(uiState.grade2),
                        secureHint = secureHintFor(2)
                    )
                }

                GradeSectionCard(
                    title = stringResource(R.string.final_grade_2_format, uiState.final2.format())
                ) {
                    GradeInput(
                        value = uiState.grade3,
                        label = stringResource(R.string.grade_3_formative),
                        onValueChange = { viewModel.onGradeChanged(3, it) },
                        suggestedHint = suggestedHintFor(uiState.grade3),
                        secureHint = secureHintFor(3)
                    )
                    Spacer(Modifier.height(10.dp))
                    GradeInput(
                        value = uiState.grade4,
                        label = stringResource(R.string.grade_4_cognitive),
                        onValueChange = { viewModel.onGradeChanged(4, it) },
                        suggestedHint = suggestedHintFor(uiState.grade4),
                        secureHint = secureHintFor(4)
                    )
                }

                GradeSectionCard(
                    title = stringResource(R.string.final_grade_3_format, uiState.final3.format())
                ) {
                    GradeInput(
                        value = uiState.grade5,
                        label = stringResource(R.string.grade_5_formative),
                        onValueChange = { viewModel.onGradeChanged(5, it) },
                        suggestedHint = suggestedHintFor(uiState.grade5),
                        secureHint = secureHintFor(5)
                    )
                    Spacer(Modifier.height(10.dp))
                    GradeInput(
                        value = uiState.grade6,
                        label = stringResource(R.string.grade_6_cognitive),
                        onValueChange = { viewModel.onGradeChanged(6, it) },
                        suggestedHint = suggestedHintFor(uiState.grade6),
                        secureHint = secureHintFor(6)
                    )
                }

                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    tonalElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.default_completion_label),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$completedInputs/6",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LinearWavyProgressIndicator(
                            progress = { completionProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = totalContainerColor,
                    modifier = Modifier.animateContentSize(),
                    tonalElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.total_final_grade),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiState.total.format(),
                            style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        if (uiState.hasAnyInput) {
                            Text(
                                text = if (passed) {
                                    stringResource(R.string.passing_message)
                                } else {
                                    stringResource(R.string.failing_message)
                                },
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (uiState.predictionState != PredictionState.NONE &&
                    uiState.predictionState != PredictionState.COMPLETE
                ) {
                    val predictionContainerColor by animateColorAsState(
                        targetValue = when (uiState.predictionState) {
                            PredictionState.GUARANTEED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
                            PredictionState.IMPOSSIBLE -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.60f)
                            else -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.60f)
                        },
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "prediction_card_color"
                    )

                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = predictionContainerColor,
                        modifier = Modifier.animateContentSize(),
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.prediction_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            when (uiState.predictionState) {
                                PredictionState.POSSIBLE -> {
                                    Text(
                                        text = uiState.requiredMinGrade.formatOneDecimal(),
                                        style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.prediction_needed_format,
                                            uiState.requiredMinGrade.formatOneDecimal(),
                                            uiState.emptyFieldsCount
                                        ),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                PredictionState.GUARANTEED -> {
                                    Text(
                                        text = stringResource(R.string.prediction_guaranteed),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                PredictionState.IMPOSSIBLE -> {
                                    Text(
                                        text = stringResource(R.string.prediction_impossible),
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                else -> Unit
                            }
                        }
                    }
                }

                Spacer(Modifier.height(100.dp))
            }

            HorizontalFloatingToolbar(
                expanded = true,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                FilledIconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                FilledIconButton(
                    onClick = { viewModel.clearAll() },
                    enabled = uiState.hasAnyInput,
                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.CleaningServices,
                        contentDescription = stringResource(R.string.clear)
                    )
                }
            }
        }
    }
}

@Composable
private fun GradeSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun GradeInput(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    suggestedHint: String? = null,
    secureHint: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = if (suggestedHint != null || secureHint != null) {
            {
                Column {
                    suggestedHint?.let { Text(it) }
                    secureHint?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun Double.format(): String = String.format("%.2f", this)
private fun Double.formatOneDecimal(): String = String.format("%.1f", this)
