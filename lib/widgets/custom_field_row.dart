import 'package:flutter/material.dart';
import 'package:grade_calculator_app/translations/translations.dart';

class CustomFieldRow extends StatelessWidget {
  final int index;
  final VoidCallback onRemove;
  final TextEditingController nameController;
  final TextEditingController percentageController;
  final TextEditingController gradeController;

  CustomFieldRow({
    super.key,
    required this.index,
    required this.onRemove,
    String initialName = '',
    String initialPercentage = '',
    String initialGrade = '',
  }) : nameController = TextEditingController(text: initialName),
       percentageController = TextEditingController(text: initialPercentage),
       gradeController = TextEditingController(text: initialGrade);

  String get name => nameController.text;
  String get percentage => percentageController.text;
  String get grade => gradeController.text;

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;
    
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 8),
      color: colorScheme.surfaceContainerLow,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '${t(context, 'field')} $index',
                  style: textTheme.titleMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: colorScheme.primary,
                  ),
                ),
                IconButton.filledTonal(
                  icon: const Icon(Icons.delete_outline),
                  onPressed: onRemove,
                  tooltip: t(context, 'removeField'),
                  style: IconButton.styleFrom(
                    foregroundColor: colorScheme.error,
                    backgroundColor: colorScheme.errorContainer,
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),

            // Nombre del campo
            TextField(
              controller: nameController,
              decoration: InputDecoration(
                labelText: t(context, 'fieldName'),
                hintText: t(context, 'fieldExample'),
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
            ),
            const SizedBox(height: 12),

            // Porcentaje y calificación en una fila
            Row(
              children: [
                // Porcentaje
                Expanded(
                  child: TextField(
                    controller: percentageController,
                    keyboardType: const TextInputType.numberWithOptions(decimal: true),
                    decoration: InputDecoration(
                      labelText: t(context, 'percentage'),
                      hintText: t(context, 'percentageExample'),
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
                  ),
                ),
                const SizedBox(width: 12),

                // Calificación
                Expanded(
                  child: TextField(
                    controller: gradeController,
                    keyboardType: const TextInputType.numberWithOptions(decimal: true),
                    decoration: InputDecoration(
                      labelText: t(context, 'grade'),
                      hintText: t(context, 'gradeExample'),
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
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
