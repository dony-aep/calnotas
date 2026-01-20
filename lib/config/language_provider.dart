import 'package:flutter/material.dart';
import 'package:grade_calculator_app/services/preferences_service.dart';

class LanguageProvider extends ChangeNotifier {
  Locale _locale = const Locale('en', 'US');
  bool _isSystemLanguage = false;

  LanguageProvider() {
    _loadFromPrefs();
  }

  void _loadFromPrefs() {
    final prefs = PreferencesService.instance;
    final isFirstRun = prefs.isFirstRun();

    if (isFirstRun) {
      // Es la primera ejecución, detectar el idioma del sistema
      final systemLocale = WidgetsBinding.instance.platformDispatcher.locale;
      final systemLangCode = systemLocale.languageCode;

      // Verificar si el idioma del sistema está soportado
      final isSupported = supportedLocales.any(
        (locale) => locale.languageCode == systemLangCode,
      );

      // Usar el idioma del sistema si está soportado, de lo contrario usar español
      final langCode = isSupported ? systemLangCode : 'es';

      // Guardar la configuración y marcar como primera ejecución completada
      prefs.setLanguage(langCode);
      prefs.setFirstRunComplete();

      _locale = Locale(langCode, langCode == 'en' ? 'US' : 'ES');
      _isSystemLanguage = true;
    } else {
      // No es primera ejecución, cargar idioma guardado
      final langCode = prefs.getLanguage() ?? 'en';
      _locale = Locale(langCode, langCode == 'en' ? 'US' : 'ES');

      // Verificar si coincide con el idioma del sistema
      final systemLocale = WidgetsBinding.instance.platformDispatcher.locale;
      _isSystemLanguage = systemLocale.languageCode == langCode;
    }

    notifyListeners();
  }

  Future<void> _saveToPrefs(String langCode) async {
    await PreferencesService.instance.setLanguage(langCode);
  }

  Locale get locale => _locale;
  bool get isSystemLanguage => _isSystemLanguage;

  void setLocale(Locale locale) {
    if (!supportedLocales.contains(locale)) return;

    _locale = locale;

    // Verificar si coincide con el idioma del sistema
    final systemLocale = WidgetsBinding.instance.platformDispatcher.locale;
    _isSystemLanguage = systemLocale.languageCode == locale.languageCode;

    _saveToPrefs(locale.languageCode);
    notifyListeners();
  }

  // Versión básica que no usa traducción (para compatibilidad)
  String getCurrentLanguageName() {
    final String baseName =
        _locale.languageCode == 'en' ? 'English' : 'Español';
    return baseName;
  }

  // Nueva versión con soporte para traducción
  String getLanguageWithSystemInfo(
    BuildContext context,
    Function translationFunction,
  ) {
    final String baseName =
        _locale.languageCode == 'en' ? 'English' : 'Español';
    if (_isSystemLanguage) {
      // Usar la función de traducción para traducir el texto
      return '$baseName ${translationFunction(context, 'deviceLanguage')}';
    } else {
      return baseName;
    }
  }

  static const List<Locale> supportedLocales = [
    Locale('en', 'US'),
    Locale('es', 'ES'),
  ];
}
