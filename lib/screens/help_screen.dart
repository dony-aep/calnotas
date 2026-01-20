import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:grade_calculator_app/translations/translations.dart';

class HelpScreen extends StatefulWidget {
  const HelpScreen({super.key});

  @override
  State<HelpScreen> createState() => _HelpScreenState();
}

class _HelpScreenState extends State<HelpScreen> {
  bool _defaultCalculatorExpanded = false;
  bool _customCalculatorExpanded = false;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return RepaintBoundary(
      child: Scaffold(
        appBar: AppBar(
          systemOverlayStyle:
              Theme.of(context).brightness == Brightness.light
                  ? SystemUiOverlayStyle.dark.copyWith(
                    statusBarColor: Colors.transparent,
                  )
                  : SystemUiOverlayStyle.light.copyWith(
                    statusBarColor: Colors.transparent,
                  ),
          title: Text(
            t(context, 'help'),
          ),
          centerTitle: true,
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: <Widget>[
              // Calculadora por defecto - Acordeón
              Card(
                margin: const EdgeInsets.only(bottom: 16),
                color: colorScheme.surfaceContainerLow,
                elevation: 1.0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16.0),
                ),
                child: Column(
                  children: [
                    // Botón expandible para calculadora por defecto
                    ListTile(
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 20.0,
                        vertical: 8.0,
                      ),
                      title: Text(
                        t(context, 'defaultGradeCalculator'),
                        style: textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      trailing: Container(
                        width: 36,
                        height: 36,
                        decoration: BoxDecoration(
                          color: colorScheme.surfaceContainerHighest,
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          _defaultCalculatorExpanded
                              ? Icons.keyboard_arrow_up
                              : Icons.keyboard_arrow_down,
                          color: colorScheme.primary,
                        ),
                      ),
                      onTap: () {
                        setState(() {
                          _defaultCalculatorExpanded =
                              !_defaultCalculatorExpanded;
                        });
                      },
                    ),

                    // Contenido expandible
                    if (_defaultCalculatorExpanded)
                      Padding(
                        padding: const EdgeInsets.fromLTRB(
                          20.0,
                          0.0,
                          20.0,
                          20.0,
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            _buildHelpSection(text: t(context, 'enterGrades')),
                            const SizedBox(height: 16),

                            // Actualización 2025 destacada
                            Container(
                              padding: const EdgeInsets.all(16.0),
                              decoration: BoxDecoration(
                                color: colorScheme.secondaryContainer,
                                borderRadius: BorderRadius.circular(12.0),
                                border: Border.all(
                                  color: colorScheme.secondary,
                                  width: 1.0,
                                ),
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Text(
                                    Localizations.localeOf(
                                              context,
                                            ).languageCode ==
                                            'es'
                                        ? 'Actualización 2025'
                                        : '2025 Update',
                                    style: textTheme.titleMedium?.copyWith(
                                      fontWeight: FontWeight.bold,
                                      color: colorScheme.onSecondaryContainer,
                                    ),
                                  ),
                                  const SizedBox(height: 8),
                                  Text(
                                    t(context, 'update2025'),
                                    style: textTheme.bodyMedium?.copyWith(
                                      color: colorScheme.onSecondaryContainer,
                                    ),
                                  ),
                                ],
                              ),
                            ),

                            const SizedBox(height: 24),

                            // Sección de notas formativas y cognitivas
                            _buildInfoBox(
                              title: t(context, 'formativeGrade'),
                              content: t(context, 'evaluatesContinuous'),
                              icon: Icons.assignment_outlined,
                            ),

                            const SizedBox(height: 16),

                            _buildInfoBox(
                              title: t(context, 'cognitiveGrade'),
                              content: t(context, 'evaluatesAcquired'),
                              icon: Icons.psychology_outlined,
                            ),

                            const SizedBox(height: 16),

                            _buildHelpSection(
                              text: t(context, 'calculatedAutomatically'),
                              isHighlighted: true,
                            ),

                            const SizedBox(height: 24),

                            // Sección de ejemplo
                            Container(
                              padding: const EdgeInsets.all(16.0),
                              decoration: BoxDecoration(
                                color: colorScheme.surfaceContainerHigh,
                                borderRadius: BorderRadius.circular(12.0),
                                border: Border.all(
                                  color: colorScheme.outlineVariant,
                                  width: 1.0,
                                ),
                              ),
                              child: Column(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Row(
                                    children: [
                                      Icon(
                                        Icons.lightbulb_outline,
                                        color:
                                            Theme.of(
                                              context,
                                            ).colorScheme.primary,
                                      ),
                                      const SizedBox(width: 8),
                                      Text(
                                        t(context, 'example'),
                                        style: textTheme.titleSmall?.copyWith(
                                          fontWeight: FontWeight.bold,
                                        ),
                                      ),
                                    ],
                                  ),
                                  const SizedBox(height: 8),
                                  Text(
                                    t(context, 'exampleDescription'),
                                    style: textTheme.bodyMedium,
                                  ),
                                  const SizedBox(height: 12),
                                  _buildExampleItem(
                                    t(context, 'finalGrade1Example'),
                                  ),
                                  _buildExampleItem(
                                    t(context, 'finalGrade2Example'),
                                  ),
                                  _buildExampleItem(
                                    t(context, 'finalGrade3Example'),
                                  ),
                                  const SizedBox(height: 8),
                                  Text(
                                    t(context, 'totalFinalGradeExample'),
                                    style: textTheme.bodyMedium?.copyWith(
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ],
                              ),
                            ),

                            const SizedBox(height: 24),

                            // Nota importante
                            Container(
                              padding: const EdgeInsets.all(16.0),
                              decoration: BoxDecoration(
                                color: colorScheme.tertiaryContainer,
                                borderRadius: BorderRadius.circular(12.0),
                                border: Border.all(
                                  color: colorScheme.tertiary,
                                  width: 1.0,
                                ),
                              ),
                              child: Row(
                                children: [
                                  Icon(
                                    Icons.warning_amber_rounded,
                                    color: colorScheme.tertiary,
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                    child: Text(
                                      t(context, 'rememberPassing'),
                                      style: textTheme.bodyMedium?.copyWith(
                                        fontStyle: FontStyle.italic,
                                        fontWeight: FontWeight.w500,
                                        color: colorScheme.onTertiaryContainer,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                  ],
                ),
              ),

              // Calculadora personalizada - Acordeón
              Card(
                margin: const EdgeInsets.only(bottom: 16),
                color: colorScheme.surfaceContainerLow,
                elevation: 1.0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16.0),
                ),
                child: Column(
                  children: [
                    // Botón expandible para calculadora personalizada
                    ListTile(
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 20.0,
                        vertical: 8.0,
                      ),
                      title: Text(
                        t(context, 'customGradeCalculator'),
                        style: textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      trailing: Container(
                        width: 36,
                        height: 36,
                        decoration: BoxDecoration(
                          color: colorScheme.surfaceContainerHighest,
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          _customCalculatorExpanded
                              ? Icons.keyboard_arrow_up
                              : Icons.keyboard_arrow_down,
                          color: colorScheme.primary,
                        ),
                      ),
                      onTap: () {
                        setState(() {
                          _customCalculatorExpanded =
                              !_customCalculatorExpanded;
                        });
                      },
                    ),

                    // Contenido expandible
                    if (_customCalculatorExpanded)
                      Padding(
                        padding: const EdgeInsets.fromLTRB(
                          20.0,
                          0.0,
                          20.0,
                          20.0,
                        ),
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            _buildHelpSection(
                              text: t(context, 'customCalcDescription'),
                            ),
                            const SizedBox(height: 16),

                            // Lista de puntos en tarjetas
                            _buildFeatureCard(
                              t(context, 'customCalcPoint1'),
                              Icons.add_circle_outline,
                            ),
                            const SizedBox(height: 8),
                            _buildFeatureCard(
                              t(context, 'customCalcPoint2'),
                              Icons.percent_outlined,
                            ),
                            const SizedBox(height: 8),
                            _buildFeatureCard(
                              t(context, 'customCalcPoint3'),
                              Icons.calculate_outlined,
                            ),
                            const SizedBox(height: 8),
                            _buildFeatureCard(
                              t(context, 'customCalcPoint4'),
                              Icons.low_priority,
                            ),

                            const SizedBox(height: 16),

                            // Nota personalizada
                            Container(
                              padding: const EdgeInsets.all(16.0),
                              decoration: BoxDecoration(
                                color: colorScheme.primaryContainer,
                                borderRadius: BorderRadius.circular(12.0),
                                border: Border.all(
                                  color: colorScheme.primary,
                                  width: 1.0,
                                ),
                              ),
                              child: Row(
                                crossAxisAlignment: CrossAxisAlignment.start,
                                children: [
                                  Icon(
                                    Icons.info_outline,
                                    color: colorScheme.primary,
                                  ),
                                  const SizedBox(width: 12),
                                  Expanded(
                                    child: Column(
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Text(
                                          t(context, 'note'),
                                          style: textTheme.titleSmall?.copyWith(
                                            fontWeight: FontWeight.bold,
                                            color: colorScheme.primary,
                                          ),
                                        ),
                                        const SizedBox(height: 4),
                                        Text(
                                          t(context, 'customizeNote'),
                                          style: textTheme.bodyMedium,
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                  ],
                ),
              ),

              // Nota final
              Card(
                color: colorScheme.surfaceContainerLow,
                elevation: 1.0,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16.0),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Icon(
                        Icons.school_outlined,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              t(context, 'note'),
                              style: textTheme.titleSmall?.copyWith(
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              t(context, 'predefPercentages'),
                              style: textTheme.bodyMedium,
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // Widgets auxiliares para secciones de ayuda
  Widget _buildHelpSection({required String text, bool isHighlighted = false}) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    return Text(
      text,
      style: textTheme.bodyLarge?.copyWith(
        fontWeight: isHighlighted ? FontWeight.w600 : FontWeight.normal,
        color: isHighlighted ? colorScheme.primary : null,
      ),
    );
  }

  Widget _buildInfoBox({
    required String title,
    required String content,
    required IconData icon,
  }) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    return Container(
      padding: const EdgeInsets.all(12.0),
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerHighest,
        borderRadius: BorderRadius.circular(12.0),
        border: Border.all(color: colorScheme.outlineVariant, width: 1.0),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, size: 20, color: colorScheme.primary),
              const SizedBox(width: 8),
              Text(
                title,
                style: textTheme.titleSmall?.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(content, style: textTheme.bodyMedium),
        ],
      ),
    );
  }

  Widget _buildExampleItem(String text) {
    final textTheme = Theme.of(context).textTheme;
    return Padding(
      padding: const EdgeInsets.only(bottom: 6.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('• ', style: TextStyle(fontWeight: FontWeight.bold)),
          Expanded(child: Text(text, style: textTheme.bodyMedium)),
        ],
      ),
    );
  }

  Widget _buildFeatureCard(String text, IconData icon) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 10.0, horizontal: 12.0),
      decoration: BoxDecoration(
        color: colorScheme.surfaceContainerHigh,
        borderRadius: BorderRadius.circular(12.0),
        border: Border.all(color: colorScheme.outlineVariant, width: 1.0),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: 20, color: colorScheme.primary),
          const SizedBox(width: 12),
          Expanded(child: Text(text, style: textTheme.bodyMedium)),
        ],
      ),
    );
  }
}
