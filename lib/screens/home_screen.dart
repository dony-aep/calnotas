import 'package:flutter/material.dart';
import 'package:grade_calculator_app/screens/about_screen.dart';
import 'package:grade_calculator_app/screens/default_grade_calculator_screen.dart';
import 'package:grade_calculator_app/screens/help_screen.dart';
import 'package:grade_calculator_app/screens/settings_screen.dart';
import 'package:grade_calculator_app/screens/custom_grade_calculator_screen.dart';
import 'package:flutter/services.dart';
import 'package:grade_calculator_app/translations/translations.dart';

class MyHomePage extends StatelessWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

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
        title: Text(t(context, 'appTitle')),
        centerTitle: true,
        actions: [
          MenuAnchor(
            style: MenuStyle(
              shape: WidgetStatePropertyAll(
                RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
              ),
              padding: const WidgetStatePropertyAll(
                EdgeInsets.symmetric(vertical: 8),
              ),
            ),
            builder: (context, controller, child) {
              return IconButton(
                icon: const Icon(Icons.more_vert),
                onPressed: () {
                  if (controller.isOpen) {
                    controller.close();
                  } else {
                    controller.open();
                  }
                },
              );
            },
            menuChildren: [
              MenuItemButton(
                leadingIcon: Icon(
                  Icons.help_outline,
                  color: colorScheme.primary,
                ),
                style: ButtonStyle(
                  padding: WidgetStatePropertyAll(
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                ),
                child: Text(t(context, 'help')),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const HelpScreen(),
                    ),
                  );
                },
              ),
              MenuItemButton(
                leadingIcon: Icon(
                  Icons.settings_outlined,
                  color: colorScheme.primary,
                ),
                style: ButtonStyle(
                  padding: WidgetStatePropertyAll(
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                ),
                child: Text(t(context, 'settings')),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const SettingsScreen(),
                    ),
                  );
                },
              ),
              MenuItemButton(
                leadingIcon: Icon(
                  Icons.info_outline,
                  color: colorScheme.primary,
                ),
                style: ButtonStyle(
                  padding: WidgetStatePropertyAll(
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                ),
                child: Text(t(context, 'about')),
                onPressed: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const AboutScreen(),
                    ),
                  );
                },
              ),
            ],
          ),
        ],
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Spacer(flex: 1),
              // Header con icono
              Icon(
                Icons.calculate_rounded,
                size: 80,
                color: colorScheme.primary,
              ),
              const SizedBox(height: 16),
              Text(
                t(context, 'welcomeMessage'),
                style: textTheme.headlineMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: colorScheme.onSurface,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 8),
              Text(
                t(context, 'selectCalculator'),
                style: textTheme.bodyLarge?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
                textAlign: TextAlign.center,
              ),
              const Spacer(flex: 2),
              // Card para Calculadora por Defecto
              _CalculatorCard(
                icon: Icons.grid_view_rounded,
                title: t(context, 'gradeCalculator'),
                subtitle: t(context, 'gradeCalculatorDesc'),
                colorScheme: colorScheme,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const DefaultGradeCalculatorScreen(),
                    ),
                  );
                },
              ),
              const SizedBox(height: 16),
              // Card para Calculadora Personalizada
              _CalculatorCard(
                icon: Icons.tune_rounded,
                title: t(context, 'customGradeCalculator'),
                subtitle: t(context, 'customGradeCalculatorDesc'),
                colorScheme: colorScheme,
                isPrimary: false,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => const CustomGradeCalculatorScreen(),
                    ),
                  );
                },
              ),
              const Spacer(flex: 2),
            ],
          ),
        ),
      ),
    );
  }
}

class _CalculatorCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final ColorScheme colorScheme;
  final bool isPrimary;
  final VoidCallback onTap;

  const _CalculatorCard({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.colorScheme,
    required this.onTap,
    this.isPrimary = true,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: isPrimary ? 2 : 1,
      color: isPrimary 
          ? colorScheme.primaryContainer 
          : colorScheme.secondaryContainer,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(16),
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Row(
            children: [
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: isPrimary 
                      ? colorScheme.primary 
                      : colorScheme.secondary,
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  icon,
                  color: isPrimary 
                      ? colorScheme.onPrimary 
                      : colorScheme.onSecondary,
                  size: 28,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: isPrimary 
                            ? colorScheme.onPrimaryContainer 
                            : colorScheme.onSecondaryContainer,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      subtitle,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: isPrimary 
                            ? colorScheme.onPrimaryContainer.withValues(alpha: 0.8) 
                            : colorScheme.onSecondaryContainer.withValues(alpha: 0.8),
                      ),
                    ),
                  ],
                ),
              ),
              Icon(
                Icons.arrow_forward_ios_rounded,
                color: isPrimary 
                    ? colorScheme.onPrimaryContainer 
                    : colorScheme.onSecondaryContainer,
                size: 20,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
