import 'package:flutter/material.dart';
import 'package:grade_calculator_app/translations/translations.dart';

void showThemeDialog(
  BuildContext context,
  String currentThemeKey,
  Function(String) onThemeChanged,
) {
  final colorScheme = Theme.of(context).colorScheme;
  final textTheme = Theme.of(context).textTheme;

  // Convierte la clave del tema actual al valor interno para selección
  String selectedTheme;
  switch (currentThemeKey) {
    case 'light':
      selectedTheme = 'Light';
      break;
    case 'dark':
      selectedTheme = 'Dark';
      break;
    default:
      selectedTheme = 'System default';
  }

  showDialog(
    context: context,
    builder: (context) {
      return StatefulBuilder(
        builder: (context, setState) {
          return AlertDialog(
            backgroundColor: colorScheme.surface,
            surfaceTintColor: Colors.transparent,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(20),
            ),
            contentPadding: const EdgeInsets.symmetric(
              horizontal: 8.0,
              vertical: 16.0,
            ),
            title: Text(
              t(context, 'chooseTheme'),
              style: textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            content: RadioGroup<String>(
              groupValue: selectedTheme,
              onChanged: (value) {
                setState(() {
                  selectedTheme = value!;
                });
              },
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  RadioListTile<String>(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                    title: Text(
                      t(context, 'systemDefault'),
                      style: textTheme.bodyLarge,
                    ),
                    value: 'System default',
                    activeColor: colorScheme.primary,
                  ),
                  RadioListTile<String>(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                    title: Text(
                      t(context, 'light'),
                      style: textTheme.bodyLarge,
                    ),
                    value: 'Light',
                    activeColor: colorScheme.primary,
                  ),
                  RadioListTile<String>(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                    title: Text(
                      t(context, 'dark'),
                      style: textTheme.bodyLarge,
                    ),
                    value: 'Dark',
                    activeColor: colorScheme.primary,
                  ),
                ],
              ),
            ),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                },
                child: Text(
                  t(context, 'cancel'),
                  style: textTheme.labelLarge?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurfaceVariant,
                  ),
                ),
              ),
              FilledButton(
                onPressed: () {
                  onThemeChanged(selectedTheme);
                  Navigator.pop(context);
                },
                style: FilledButton.styleFrom(
                  backgroundColor: colorScheme.primary,
                  foregroundColor: colorScheme.onPrimary,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
                child: Text(
                  t(context, 'ok'),
                  style: textTheme.labelLarge?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onPrimary,
                  ),
                ),
              ),
            ],
          );
        },
      );
    },
  );
}
