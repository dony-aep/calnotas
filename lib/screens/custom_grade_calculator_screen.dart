import 'package:flutter/material.dart';
import 'package:grade_calculator_app/widgets/custom_field_row.dart';
import 'package:grade_calculator_app/models/field_data.dart';
import 'package:grade_calculator_app/services/preferences_service.dart';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:grade_calculator_app/translations/translations.dart';

class CustomGradeCalculatorScreen extends StatefulWidget {
  const CustomGradeCalculatorScreen({super.key});

  @override
  State<CustomGradeCalculatorScreen> createState() =>
      _CustomGradeCalculatorScreenState();
}

class _CustomGradeCalculatorScreenState
    extends State<CustomGradeCalculatorScreen> {
  final List<CustomFieldRow> _fields = [];
  final TextEditingController _minPassingGradeController =
      TextEditingController();
  double _totalFinalGrade = 0.0;
  double _totalPercentage = 0.0;
  String? _percentageMessage;
  Color? _percentageMessageColor;

  @override
  void initState() {
    super.initState();
    _loadSavedData();
    _minPassingGradeController.addListener(_calculateTotalGrade);
  }

  @override
  void dispose() {
    _minPassingGradeController.dispose();
    for (var field in _fields) {
      field.nameController.dispose();
      field.percentageController.dispose();
      field.gradeController.dispose();
    }
    super.dispose();
  }

  Future<void> _loadSavedData() async {
    final savedDataJson =
        PreferencesService.instance.getCustomCalculatorData();

    if (savedDataJson != null) {
      final savedData = jsonDecode(savedDataJson) as Map<String, dynamic>;

      // Load minimum passing grade
      setState(() {
        _minPassingGradeController.text = savedData['minPassingGrade'] ?? '';
      });

      // Load fields
      if (savedData.containsKey('fields')) {
        final fieldsList = List<Map<String, dynamic>>.from(savedData['fields']);

        setState(() {
          _fields.clear();
          for (int i = 0; i < fieldsList.length; i++) {
            final fieldData = FieldData.fromJson(fieldsList[i]);
            final newField = CustomFieldRow(
              index: i + 1,
              onRemove: () {
                setState(() {
                  _fields.removeAt(i);
                  // Update indices for remaining fields
                  for (int j = 0; j < _fields.length; j++) {
                    _fields[j] = CustomFieldRow(
                      index: j + 1,
                      onRemove: _fields[j].onRemove,
                      initialName: _fields[j].name,
                      initialPercentage: _fields[j].percentage,
                      initialGrade: _fields[j].grade,
                    );
                  }
                });
                _calculateTotalGrade();
              },
              initialName: fieldData.name,
              initialPercentage: fieldData.percentage,
              initialGrade: fieldData.grade,
            );
            // Add listeners for calculations
            newField.percentageController.addListener(_calculateTotalGrade);
            newField.gradeController.addListener(_calculateTotalGrade);
            _fields.add(newField);
          }
        });
        _calculateTotalGrade();
      }
    }
  }

  void _calculateTotalGrade() {
    double total = 0.0;
    double totalPercentage = 0.0;

    for (var field in _fields) {
      // Parse percentage and grade, default to 0 if invalid
      double percentage = double.tryParse(field.percentage) ?? 0.0;
      double grade = double.tryParse(field.grade) ?? 0.0;

      // Calculate weighted grade (percentage converted to decimal)
      if (field.percentage.isNotEmpty && field.grade.isNotEmpty) {
        totalPercentage += percentage;
        total += (percentage / 100) * grade;
      }
    }

    // Validate total percentage
    String? message;
    Color? messageColor;

    if (totalPercentage > 100) {
      message = t(
        context,
        'percentageExceeded',
      ).replaceAll('{0}', totalPercentage.toString());
      messageColor = Colors.red;
      // Reset total if percentage is not exactly 100%
      total = 0.0;
    } else if (totalPercentage < 100 && totalPercentage > 0) {
      message = t(
        context,
        'percentageInsufficient',
      ).replaceAll('{0}', totalPercentage.toString());
      messageColor = Colors.orange;
      // Reset total if percentage is not exactly 100%
      total = 0.0;
    } else {
      message = null;
      messageColor = null;
    }

    setState(() {
      _totalFinalGrade = total;
      _totalPercentage = totalPercentage;
      _percentageMessage = message;
      _percentageMessageColor = messageColor;
    });
  }

  String _format(double value) => value.toStringAsFixed(2);

  Future<void> _saveData() async {
    if (_fields.isEmpty) {
      return;
    }

    // Collect all field data
    final List<Map<String, dynamic>> fieldsData =
        _fields
            .map(
              (field) =>
                  FieldData(
                    name: field.name,
                    percentage: field.percentage,
                    grade: field.grade,
                  ).toJson(),
            )
            .toList();

    // Create a map with all calculator data
    final calculatorData = {
      'minPassingGrade': _minPassingGradeController.text,
      'fields': fieldsData,
    };

    // Save to PreferencesService
    await PreferencesService.instance
        .setCustomCalculatorData(jsonEncode(calculatorData));

    // Add this check before using context after async operations
    if (!mounted) return;

    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(t(context, 'calculatorSaved'))));
  }

  void _addNewField() {
    final int newIndex = _fields.length + 1;
    final newField = CustomFieldRow(
      index: newIndex,
      onRemove: () {
        setState(() {
          // Find and remove the field with matching index
          final indexToRemove = _fields.indexWhere(
            (field) => field.index == newIndex,
          );
          if (indexToRemove != -1) {
            _fields.removeAt(indexToRemove);
            // Update indices for all fields
            for (int i = 0; i < _fields.length; i++) {
              _fields[i] = CustomFieldRow(
                index: i + 1,
                onRemove: _fields[i].onRemove,
                initialName: _fields[i].name,
                initialPercentage: _fields[i].percentage,
                initialGrade: _fields[i].grade,
              );
            }
          }
        });
        _calculateTotalGrade();
      },
    );

    // Add listeners for calculation
    newField.percentageController.addListener(_calculateTotalGrade);
    newField.gradeController.addListener(_calculateTotalGrade);

    setState(() {
      _fields.add(newField);
    });
  }

  void _resetFields() {
    setState(() {
      _fields.clear();
      _minPassingGradeController.clear();
      _totalFinalGrade = 0.0;
    });
  }

  double getMinPassingGrade() {
    return double.tryParse(_minPassingGradeController.text) ?? 3.0;
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    final minPassingGrade = getMinPassingGrade();
    final bool hasPassedSubject = _totalFinalGrade >= minPassingGrade;
    final bool showPassingMessage =
        _fields.isNotEmpty && (_fields.any((field) => field.grade.isNotEmpty));
    final bool isPercentageValid = _totalPercentage == 100;

    return Scaffold(
      appBar: AppBar(
        systemOverlayStyle:
            Theme.of(context).brightness == Brightness.light
                ? SystemUiOverlayStyle.dark.copyWith(
                    statusBarColor: Colors.transparent,
                  )
                : SystemUiOverlayStyle.light.copyWith(
                    statusBarColor: Colors.transparent,
                  ),
        title: Text(t(context, 'customCalculator')),
        centerTitle: true,
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 100),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Lista de campos
                _fields.isEmpty
                    ? Card(
                        color: colorScheme.surfaceContainerLow,
                        child: Padding(
                          padding: const EdgeInsets.symmetric(vertical: 60, horizontal: 20),
                          child: Column(
                            children: [
                              Icon(
                                Icons.add_circle_outline,
                                size: 48,
                                color: colorScheme.outline,
                              ),
                              const SizedBox(height: 16),
                              Text(
                                t(context, 'noFieldsAdded'),
                                style: textTheme.bodyLarge?.copyWith(
                                  color: colorScheme.onSurfaceVariant,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ],
                          ),
                        ),
                      )
                    : ListView.builder(
                        shrinkWrap: true,
                        physics: const NeverScrollableScrollPhysics(),
                        itemCount: _fields.length,
                        itemBuilder: (context, index) => _fields[index],
                      ),

                const SizedBox(height: 16),

                // Campo para la nota mínima para aprobar
                Card(
                  color: colorScheme.surfaceContainerLow,
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        Text(
                          t(context, 'minPassingGrade'),
                          style: textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: colorScheme.primary,
                          ),
                        ),
                        const SizedBox(height: 12),
                        SizedBox(
                          width: 200,
                          child: TextField(
                            controller: _minPassingGradeController,
                            keyboardType: const TextInputType.numberWithOptions(decimal: true),
                            textAlign: TextAlign.center,
                            decoration: InputDecoration(
                              filled: false,
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(12),
                              ),
                              enabledBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(12),
                                borderSide: BorderSide(color: colorScheme.outline),
                              ),
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(12),
                                borderSide: BorderSide(color: colorScheme.primary, width: 2),
                              ),
                              contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                              hintText: '${t(context, 'defaultValue')}: 3.0',
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),

                const SizedBox(height: 16),

                // Card de Nota Final Total
                if (_fields.isNotEmpty)
                  Card(
                    color: isPercentageValid
                        ? (hasPassedSubject ? colorScheme.primaryContainer : colorScheme.errorContainer)
                        : colorScheme.surfaceContainerHigh,
                    child: Padding(
                      padding: const EdgeInsets.all(20),
                      child: Column(
                        children: [
                          Text(
                            t(context, 'totalFinalGrade'),
                            style: textTheme.titleMedium?.copyWith(
                              color: isPercentageValid
                                  ? (hasPassedSubject ? colorScheme.onPrimaryContainer : colorScheme.onErrorContainer)
                                  : colorScheme.onSurfaceVariant,
                            ),
                          ),
                          const SizedBox(height: 8),
                          Text(
                            _format(_totalFinalGrade),
                            style: textTheme.displaySmall?.copyWith(
                              fontWeight: FontWeight.bold,
                              color: isPercentageValid
                                  ? (hasPassedSubject ? colorScheme.onPrimaryContainer : colorScheme.onErrorContainer)
                                  : colorScheme.onSurfaceVariant,
                            ),
                          ),

                          // Mensaje de validación de porcentaje
                          if (_percentageMessage != null) ...[
                            const SizedBox(height: 12),
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                              decoration: BoxDecoration(
                                color: _percentageMessageColor == Colors.red
                                    ? colorScheme.errorContainer
                                    : colorScheme.tertiaryContainer,
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Text(
                                _percentageMessage!,
                                style: textTheme.bodyMedium?.copyWith(
                                  color: _percentageMessageColor == Colors.red
                                      ? colorScheme.onErrorContainer
                                      : colorScheme.onTertiaryContainer,
                                ),
                                textAlign: TextAlign.center,
                              ),
                            ),
                          ],

                          // Mensaje de aprobación/reprobación
                          if (showPassingMessage && isPercentageValid) ...[
                            const SizedBox(height: 8),
                            Text(
                              hasPassedSubject
                                  ? t(context, 'passingMessage')
                                  : t(context, 'failingMessage'),
                              style: textTheme.bodyMedium?.copyWith(
                                color: hasPassedSubject
                                    ? colorScheme.onPrimaryContainer
                                    : colorScheme.onErrorContainer,
                              ),
                              textAlign: TextAlign.center,
                            ),
                          ],
                        ],
                      ),
                    ),
                  ),
              ],
            ),
          ),

          // Floating Toolbar M3 Expressive (solo iconos)
          Positioned(
            left: 0,
            right: 0,
            bottom: 16,
            child: Center(
              child: Card(
                elevation: 6,
                color: colorScheme.surfaceContainerHigh,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(28),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(8),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      // Botón Volver
                      IconButton.filled(
                        onPressed: () => Navigator.pop(context),
                        icon: const Icon(Icons.arrow_back_rounded),
                        tooltip: t(context, 'back'),
                      ),
                      const SizedBox(width: 4),
                      // Botón Añadir Campo
                      IconButton.filled(
                        onPressed: _addNewField,
                        icon: const Icon(Icons.add_rounded),
                        tooltip: t(context, 'addField'),
                      ),
                      const SizedBox(width: 4),
                      // Botón Guardar
                      IconButton.filledTonal(
                        onPressed: _fields.isEmpty ? null : _saveData,
                        icon: const Icon(Icons.save_rounded),
                        tooltip: t(context, 'saveCalculator'),
                      ),
                      const SizedBox(width: 4),
                      // Botón Reset
                      IconButton.filledTonal(
                        onPressed: _fields.isEmpty ? null : _resetFields,
                        icon: const Icon(Icons.refresh_rounded),
                        tooltip: t(context, 'reset'),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
