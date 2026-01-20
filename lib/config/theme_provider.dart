import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:grade_calculator_app/services/preferences_service.dart';

class ThemeProvider extends ChangeNotifier {
  ThemeMode _themeMode = ThemeMode.system;

  ThemeProvider() {
    _loadFromPrefs();
  }

  // Inicializa las preferencias y carga el tema guardado
  void _loadFromPrefs() {
    final themeName =
        PreferencesService.instance.getTheme() ?? 'System default';
    setThemeModeFromString(themeName, notify: false);
    notifyListeners();
  }

  // Guarda la preferencia de tema
  Future<void> _saveToPrefs(String themeName) async {
    await PreferencesService.instance.setTheme(themeName);
  }

  ThemeMode get themeMode => _themeMode;

  bool get isDarkMode {
    if (_themeMode == ThemeMode.system) {
      final brightness =
          SchedulerBinding.instance.platformDispatcher.platformBrightness;
      return brightness == Brightness.dark;
    }
    return _themeMode == ThemeMode.dark;
  }

  void setThemeMode(ThemeMode themeMode, {bool notify = true}) {
    _themeMode = themeMode;
    if (notify) notifyListeners();
  }

  void setThemeModeFromString(String themeName, {bool notify = true}) {
    ThemeMode newMode;

    switch (themeName) {
      case 'Light':
        newMode = ThemeMode.light;
        break;
      case 'Dark':
        newMode = ThemeMode.dark;
        break;
      default:
        newMode = ThemeMode.system;
    }

    _themeMode = newMode;
    _saveToPrefs(themeName);

    if (notify) notifyListeners();
  }

  String getCurrentThemeName() {
    switch (_themeMode) {
      case ThemeMode.light:
        return 'light';
      case ThemeMode.dark:
        return 'dark';
      default:
        return 'systemDefault';
    }
  }
}
