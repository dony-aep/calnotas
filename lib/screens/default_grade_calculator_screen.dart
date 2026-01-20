import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:grade_calculator_app/translations/translations.dart';

class DefaultGradeCalculatorScreen extends StatefulWidget {
  const DefaultGradeCalculatorScreen({super.key});

  @override
  State<DefaultGradeCalculatorScreen> createState() =>
      _DefaultGradeCalculatorScreenState();
}

class _DefaultGradeCalculatorScreenState
    extends State<DefaultGradeCalculatorScreen> {
  final TextEditingController _controller1 = TextEditingController();
  final TextEditingController _controller2 = TextEditingController();
  final TextEditingController _controller3 = TextEditingController();
  final TextEditingController _controller4 = TextEditingController();
  final TextEditingController _controller5 = TextEditingController();
  final TextEditingController _controller6 = TextEditingController();

  IconData _clearIcon = Icons.cleaning_services_outlined;

  double _finalGrade1 = 0.0;
  double _finalGrade2 = 0.0;
  double _finalGrade3 = 0.0;
  double _totalFinalGrade = 0.0;

  bool get _hasAnyInput =>
      _controller1.text.isNotEmpty ||
      _controller2.text.isNotEmpty ||
      _controller3.text.isNotEmpty ||
      _controller4.text.isNotEmpty ||
      _controller5.text.isNotEmpty ||
      _controller6.text.isNotEmpty;

  @override
  void initState() {
    super.initState();

    // Add listeners for grade recalculation
    _controller1.addListener(_recalculateGrades);
    _controller2.addListener(_recalculateGrades);
    _controller3.addListener(_recalculateGrades);
    _controller4.addListener(_recalculateGrades);
    _controller5.addListener(_recalculateGrades);
    _controller6.addListener(_recalculateGrades);

    // Add listeners for input validation
    _controller1.addListener(
      () => _validateAndUpdateGrade(_controller1, _controller1.text),
    );
    _controller2.addListener(
      () => _validateAndUpdateGrade(_controller2, _controller2.text),
    );
    _controller3.addListener(
      () => _validateAndUpdateGrade(_controller3, _controller3.text),
    );
    _controller4.addListener(
      () => _validateAndUpdateGrade(_controller4, _controller4.text),
    );
    _controller5.addListener(
      () => _validateAndUpdateGrade(_controller5, _controller5.text),
    );
    _controller6.addListener(
      () => _validateAndUpdateGrade(_controller6, _controller6.text),
    );
  }

  @override
  void dispose() {
    _controller1.dispose();
    _controller2.dispose();
    _controller3.dispose();
    _controller4.dispose();
    _controller5.dispose();
    _controller6.dispose();
    super.dispose();
  }

  void _clearFields() {
    _controller1.clear();
    _controller2.clear();
    _controller3.clear();
    _controller4.clear();
    _controller5.clear();
    _controller6.clear();
    setState(() {
      _clearIcon = Icons.check_outlined;
      _finalGrade1 = 0.0;
      _finalGrade2 = 0.0;
      _finalGrade3 = 0.0;
      _totalFinalGrade = 0.0;
    });
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) {
        setState(() {
          _clearIcon = Icons.cleaning_services_outlined;
        });
      }
    });
  }

  void _recalculateGrades() {
    double f1 = double.tryParse(_controller1.text) ?? 0.0;
    double c1 = double.tryParse(_controller2.text) ?? 0.0;
    double f2 = double.tryParse(_controller3.text) ?? 0.0;
    double c2 = double.tryParse(_controller4.text) ?? 0.0;
    double f3 = double.tryParse(_controller5.text) ?? 0.0;
    double c3 = double.tryParse(_controller6.text) ?? 0.0;

    double final1 = (f1 * 0.15) + (c1 * 0.15);
    double final2 = (f2 * 0.15) + (c2 * 0.15);
    double final3 = (f3 * 0.20) + (c3 * 0.20);
    double total = final1 + final2 + final3;

    setState(() {
      _finalGrade1 = final1;
      _finalGrade2 = final2;
      _finalGrade3 = final3;
      _totalFinalGrade = total;
    });
  }

  String _format(double value) => value.toStringAsFixed(2);

  void _validateAndUpdateGrade(TextEditingController controller, String text) {
    if (text.isEmpty) return;

    double? grade = double.tryParse(text);
    if (grade == null) {
      controller.text = '';
      _showInvalidGradeDialog(t(context, 'invalidNumber'));
      return;
    }

    if (grade < 0 || grade > 5) {
      controller.text = '';
      _showInvalidGradeDialog(t(context, 'invalidRange'));
    }
  }

  void _showInvalidGradeDialog(String message) {
    final colorScheme = Theme.of(context).colorScheme;
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        icon: Icon(Icons.error_outline, color: colorScheme.error, size: 32),
        title: Text(t(context, 'invalidGrade')),
        content: Text(message),
        actions: [
          FilledButton(
            onPressed: () => Navigator.pop(context),
            child: Text(t(context, 'ok')),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    
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
        title: Text(t(context, 'gradeCalculator')),
        centerTitle: true,
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 100),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                // Corte 1
                _GradeCard(
                  title: '${t(context, 'finalGrade1')}: ${_format(_finalGrade1)}',
                  colorScheme: colorScheme,
                  textTheme: textTheme,
                  children: [
                    _GradeTextField(
                      controller: _controller1,
                      label: t(context, 'grade1Formative'),
                      hint: t(context, 'grade1'),
                      colorScheme: colorScheme,
                    ),
                    const SizedBox(height: 12),
                    _GradeTextField(
                      controller: _controller2,
                      label: t(context, 'grade2Cognitive'),
                      hint: t(context, 'grade2'),
                      colorScheme: colorScheme,
                    ),
                  ],
                ),
                const SizedBox(height: 16),

                // Corte 2
                _GradeCard(
                  title: '${t(context, 'finalGrade2')}: ${_format(_finalGrade2)}',
                  colorScheme: colorScheme,
                  textTheme: textTheme,
                  children: [
                    _GradeTextField(
                      controller: _controller3,
                      label: t(context, 'grade3Formative'),
                      hint: t(context, 'grade3'),
                      colorScheme: colorScheme,
                    ),
                    const SizedBox(height: 12),
                    _GradeTextField(
                      controller: _controller4,
                      label: t(context, 'grade4Cognitive'),
                      hint: t(context, 'grade4'),
                      colorScheme: colorScheme,
                    ),
                  ],
                ),
                const SizedBox(height: 16),

                // Corte 3
                _GradeCard(
                  title: '${t(context, 'finalGrade3')}: ${_format(_finalGrade3)}',
                  colorScheme: colorScheme,
                  textTheme: textTheme,
                  children: [
                    _GradeTextField(
                      controller: _controller5,
                      label: t(context, 'grade5Formative'),
                      hint: t(context, 'grade5'),
                      colorScheme: colorScheme,
                    ),
                    const SizedBox(height: 12),
                    _GradeTextField(
                      controller: _controller6,
                      label: t(context, 'grade6Cognitive'),
                      hint: t(context, 'grade6'),
                      colorScheme: colorScheme,
                    ),
                  ],
                ),
                const SizedBox(height: 16),

                // Card de Nota Final Total (movido al final)
                Card(
                  color: _totalFinalGrade >= 3.0 
                      ? colorScheme.primaryContainer 
                      : colorScheme.errorContainer,
                  child: Padding(
                    padding: const EdgeInsets.all(20),
                    child: Column(
                      children: [
                        Text(
                          t(context, 'totalFinalGrade'),
                          style: textTheme.titleMedium?.copyWith(
                            color: _totalFinalGrade >= 3.0
                                ? colorScheme.onPrimaryContainer
                                : colorScheme.onErrorContainer,
                          ),
                        ),
                        const SizedBox(height: 8),
                        Text(
                          _format(_totalFinalGrade),
                          style: textTheme.displaySmall?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: _totalFinalGrade >= 3.0
                                ? colorScheme.onPrimaryContainer
                                : colorScheme.onErrorContainer,
                          ),
                        ),
                        if (_controller1.text.isNotEmpty ||
                            _controller2.text.isNotEmpty ||
                            _controller3.text.isNotEmpty ||
                            _controller4.text.isNotEmpty ||
                            _controller5.text.isNotEmpty ||
                            _controller6.text.isNotEmpty) ...[
                          const SizedBox(height: 8),
                          Text(
                            _totalFinalGrade >= 3.0
                                ? t(context, 'passingMessage')
                                : t(context, 'failingMessage'),
                            style: textTheme.bodyMedium?.copyWith(
                              color: _totalFinalGrade >= 3.0
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
                      const SizedBox(width: 8),
                      // Botón Limpiar
                      IconButton.filledTonal(
                        onPressed: _hasAnyInput ? _clearFields : null,
                        icon: Icon(_clearIcon),
                        tooltip: t(context, 'clear'),
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

// Widget para las cards de cada corte
class _GradeCard extends StatelessWidget {
  final String title;
  final ColorScheme colorScheme;
  final TextTheme textTheme;
  final List<Widget> children;

  const _GradeCard({
    required this.title,
    required this.colorScheme,
    required this.textTheme,
    required this.children,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: colorScheme.surfaceContainerLow,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: textTheme.titleMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: colorScheme.primary,
              ),
            ),
            const SizedBox(height: 16),
            ...children,
          ],
        ),
      ),
    );
  }
}

// Widget para los campos de texto (Outlined sin fondo)
class _GradeTextField extends StatelessWidget {
  final TextEditingController controller;
  final String label;
  final String hint;
  final ColorScheme colorScheme;

  const _GradeTextField({
    required this.controller,
    required this.label,
    required this.hint,
    required this.colorScheme,
  });

  @override
  Widget build(BuildContext context) {
    return TextField(
      controller: controller,
      keyboardType: const TextInputType.numberWithOptions(decimal: true),
      decoration: InputDecoration(
        labelText: label,
        hintText: hint,
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
      ),
    );
  }
}
