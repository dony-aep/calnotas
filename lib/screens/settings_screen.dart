import 'package:flutter/material.dart';
import 'package:grade_calculator_app/config/theme_provider.dart';
import 'package:grade_calculator_app/widgets/theme_dialog.dart';
import 'package:provider/provider.dart';
import 'package:flutter/services.dart';
import 'package:grade_calculator_app/screens/help_screen.dart';
import 'package:grade_calculator_app/screens/about_screen.dart';
import 'package:grade_calculator_app/screens/update_screen.dart';
import 'package:grade_calculator_app/translations/translations.dart';
import 'package:grade_calculator_app/config/language_provider.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return RepaintBoundary(
      child: Scaffold(
        appBar: AppBar(
          backgroundColor: colorScheme.surface,
          surfaceTintColor: Colors.transparent,
          elevation: 0,
          systemOverlayStyle:
              Theme.of(context).brightness == Brightness.light
                  ? SystemUiOverlayStyle.dark.copyWith(
                    statusBarColor: Colors.transparent,
                  )
                  : SystemUiOverlayStyle.light.copyWith(
                    statusBarColor: Colors.transparent,
                  ),
          title: Text(
            t(context, 'settings'),
            style: textTheme.titleLarge?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
        body: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
            // Sección Display
            _buildSectionHeader(context, t(context, 'display')),

            _buildThemeTile(context, colorScheme, textTheme),

            _buildLanguageTile(context, colorScheme, textTheme),

            const SizedBox(height: 8),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: Divider(color: colorScheme.outlineVariant),
            ),

            // Sección Support
            _buildSectionHeader(context, t(context, 'support')),

            ListTile(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              contentPadding: const EdgeInsets.symmetric(
                horizontal: 16.0,
                vertical: 4.0,
              ),
              leading: Icon(
                Icons.help_outline_rounded,
                color: colorScheme.primary,
              ),
              title: Text(
                t(context, 'help'),
                style: textTheme.bodyLarge?.copyWith(
                  fontWeight: FontWeight.w500,
                ),
              ),
              subtitle: Text(
                t(context, 'viewHelpInfo'),
                style: textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
              trailing: Icon(
                Icons.chevron_right_rounded,
                color: colorScheme.onSurfaceVariant,
              ),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const HelpScreen()),
                );
              },
            ),

            ListTile(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              contentPadding: const EdgeInsets.symmetric(
                horizontal: 16.0,
                vertical: 4.0,
              ),
              leading: Icon(
                Icons.info_outline_rounded,
                color: colorScheme.primary,
              ),
              title: Text(
                t(context, 'about'),
                style: textTheme.bodyLarge?.copyWith(
                  fontWeight: FontWeight.w500,
                ),
              ),
              subtitle: Text(
                t(context, 'appTitle'),
                style: textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
              trailing: Icon(
                Icons.chevron_right_rounded,
                color: colorScheme.onSurfaceVariant,
              ),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => const AboutScreen()),
                );
              },
            ),

            // Botón para actualizar la aplicación
            ListTile(
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              contentPadding: const EdgeInsets.symmetric(
                horizontal: 16.0,
                vertical: 4.0,
              ),
              leading: Icon(
                Icons.system_update_outlined,
                color: colorScheme.primary,
              ),
              title: Text(
                t(context, 'checkUpdates'),
                style: textTheme.bodyLarge?.copyWith(
                  fontWeight: FontWeight.w500,
                ),
              ),
              subtitle: Text(
                t(context, 'checkUpdatesDesc'),
                style: textTheme.bodyMedium?.copyWith(
                  color: colorScheme.onSurfaceVariant,
                ),
              ),
              trailing: Icon(
                Icons.chevron_right_rounded,
                color: colorScheme.onSurfaceVariant,
              ),
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const UpdateScreen(),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildThemeTile(
    BuildContext context,
    ColorScheme colorScheme,
    TextTheme textTheme,
  ) {
    return Consumer<ThemeProvider>(
      builder: (context, themeProvider, _) {
        final currentThemeKey = themeProvider.getCurrentThemeName();
        return ListTile(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16.0,
            vertical: 4.0,
          ),
          leading: Icon(
            Icons.palette_outlined,
            color: colorScheme.primary,
          ),
          title: Text(
            t(context, 'theme'),
            style: textTheme.bodyLarge?.copyWith(
              fontWeight: FontWeight.w500,
            ),
          ),
          subtitle: Text(
            t(context, currentThemeKey),
            style: textTheme.bodyMedium?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
          ),
          trailing: Icon(
            Icons.chevron_right_rounded,
            color: colorScheme.onSurfaceVariant,
          ),
          onTap: () {
            showThemeDialog(context, currentThemeKey, (newTheme) {
              themeProvider.setThemeModeFromString(newTheme);
            });
          },
        );
      },
    );
  }

  Widget _buildLanguageTile(
    BuildContext context,
    ColorScheme colorScheme,
    TextTheme textTheme,
  ) {
    return Consumer<LanguageProvider>(
      builder: (context, languageProvider, _) {
        final currentLanguage =
            languageProvider.getLanguageWithSystemInfo(context, t);
        return ListTile(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16.0,
            vertical: 4.0,
          ),
          leading: Icon(
            Icons.language_outlined,
            color: colorScheme.primary,
          ),
          title: Text(
            t(context, 'appLanguage'),
            style: textTheme.bodyLarge?.copyWith(
              fontWeight: FontWeight.w500,
            ),
          ),
          subtitle: Text(
            currentLanguage,
            style: textTheme.bodyMedium?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
          ),
          trailing: Icon(
            Icons.chevron_right_rounded,
            color: colorScheme.onSurfaceVariant,
          ),
          onTap: () {
            _showLanguageBottomSheet(context, languageProvider);
          },
        );
      },
    );
  }

  Widget _buildSectionHeader(BuildContext context, String title) {
    final textTheme = Theme.of(context).textTheme;
    final colorScheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.only(
        left: 16.0,
        right: 16.0,
        top: 20.0,
        bottom: 8.0,
      ),
      child: Text(
        title,
        style: textTheme.titleMedium?.copyWith(
          fontWeight: FontWeight.bold,
          color: colorScheme.primary,
        ),
      ),
    );
  }

  void _showLanguageBottomSheet(
    BuildContext context,
    LanguageProvider languageProvider,
  ) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    showModalBottomSheet(
      context: context,
      backgroundColor: colorScheme.surface,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(
          top: Radius.circular(20),
        ),
      ),
      builder: (context) {
        String selectedLanguage = languageProvider.locale.languageCode;

        return StatefulBuilder(
          builder: (context, setState) {
            final languageWidgets = <Widget>[];
            final orderedLangCodes = ['en', 'es']
              ..remove(selectedLanguage)
              ..insert(0, selectedLanguage);

            for (final langCode in orderedLangCodes) {
              final isDeviceLanguage =
                  WidgetsBinding.instance.platformDispatcher.locale.languageCode ==
                      langCode;
              final displayName = langCode == 'en' ? 'English' : 'Español';

              languageWidgets.add(
                RadioListTile<String>(
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  contentPadding: const EdgeInsets.symmetric(horizontal: 16.0),
                  title: Text(
                    '$displayName${isDeviceLanguage ? ' ${t(context, 'deviceLanguage')}' : ''}',
                    style: textTheme.bodyLarge,
                  ),
                  value: langCode,
                  activeColor: colorScheme.primary,
                ),
              );
            }

            return Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Handle bar
                Center(
                  child: Container(
                    margin: const EdgeInsets.only(top: 12),
                    width: 32,
                    height: 4,
                    decoration: BoxDecoration(
                      color: colorScheme.outlineVariant,
                      borderRadius: BorderRadius.circular(2),
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(
                    left: 20,
                    top: 20,
                    right: 20,
                    bottom: 8,
                  ),
                  child: Text(
                    t(context, 'selectLanguage'),
                    style: textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Divider(color: colorScheme.outlineVariant),
                RadioGroup<String>(
                  groupValue: selectedLanguage,
                  onChanged: (value) {
                    if (value != null) {
                      setState(() {
                        selectedLanguage = value;
                      });
                      languageProvider.setLocale(
                        Locale(value, value == 'en' ? 'US' : 'ES'),
                      );
                      Navigator.pop(context);
                    }
                  },
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: languageWidgets,
                  ),
                ),
                const SizedBox(height: 20),
              ],
            );
          },
        );
      },
    );
  }
}
