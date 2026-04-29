package com.donyaep.calnotas.ui.screens.customcalculator

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun CustomCalculatorScreen(
    onBack: () -> Unit,
    viewModel: CustomCalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.custom_calculator_title)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
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
                                    text = stringResource(R.string.custom_calculator_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.home_custom_description),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item {
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.min_passing_grade),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = uiState.minPassingGrade,
                                onValueChange = viewModel::onMinPassingGradeChanged,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                label = { Text(stringResource(R.string.default_value_hint)) },
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                if (uiState.fields.isEmpty()) {
                    item {
                        Surface(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            tonalElevation = 1.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = stringResource(R.string.no_fields_added),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                items(uiState.fields, key = { it.id }) { field ->
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        tonalElevation = 1.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.field_prefix, field.id),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                FilledIconButton(
                                    onClick = { viewModel.removeField(field.id) },
                                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = stringResource(R.string.remove_field)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = field.name,
                                onValueChange = { viewModel.onNameChanged(field.id, it) },
                                label = { Text(stringResource(R.string.field_name)) },
                                singleLine = true,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = field.percentage,
                                    onValueChange = { viewModel.onPercentageChanged(field.id, it) },
                                    label = { Text(stringResource(R.string.percentage)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = field.grade,
                                    onValueChange = { viewModel.onGradeChanged(field.id, it) },
                                    label = { Text(stringResource(R.string.grade)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    val percentageProgress = (uiState.totalPercentage / 100.0).toFloat().coerceIn(0f, 1f)

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
                                text = stringResource(R.string.custom_percentage_progress_label),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${uiState.totalPercentage.format()}%",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            LinearWavyProgressIndicator(
                                progress = { percentageProgress },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    val minPassing = uiState.minPassingGrade.toDoubleOrNull() ?: 3.0
                    val passed = uiState.totalFinalGrade >= minPassing

                    val targetContainerColor = when {
                        uiState.percentageValidation != PercentageValidation.NONE -> MaterialTheme.colorScheme.surfaceContainerHigh
                        passed -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.60f)
                        else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.60f)
                    }
                    val totalContainerColor by animateColorAsState(
                        targetValue = targetContainerColor,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "result_card_color"
                    )

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
                                text = uiState.totalFinalGrade.format(),
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (uiState.percentageValidation != PercentageValidation.NONE) {
                                val percentageText = when (uiState.percentageValidation) {
                                    PercentageValidation.EXCEEDED -> stringResource(
                                        R.string.percentage_exceeded_format,
                                        uiState.totalPercentage.format()
                                    )

                                    PercentageValidation.INSUFFICIENT -> stringResource(
                                        R.string.percentage_insufficient_format,
                                        uiState.totalPercentage.format()
                                    )

                                    PercentageValidation.NONE -> ""
                                }

                                Text(
                                    text = percentageText,
                                    color = if (uiState.percentageValidation == PercentageValidation.EXCEEDED) {
                                        MaterialTheme.colorScheme.error
                                    } else {
                                        Color(0xFFB26A00)
                                    },
                                    textAlign = TextAlign.Center
                                )
                            } else if (uiState.fields.any { it.grade.isNotBlank() }) {
                                Text(
                                    text = if (passed) {
                                        stringResource(R.string.passing_message)
                                    } else {
                                        stringResource(R.string.failing_message)
                                    },
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
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
                FilledIconButton(onClick = { viewModel.addField() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_field)
                    )
                }
                FilledIconButton(
                    onClick = { viewModel.saveCalculator() },
                    enabled = uiState.fields.isNotEmpty(),
                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = stringResource(R.string.save_calculator)
                    )
                }
                FilledIconButton(
                    onClick = { viewModel.resetAll() },
                    enabled = uiState.fields.isNotEmpty(),
                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.reset)
                    )
                }
            }
        }
    }
}

private fun Double.format(): String = String.format("%.2f", this)
