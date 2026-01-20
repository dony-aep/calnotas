import 'package:shared_preferences/shared_preferences.dart';

/// Centralized service for managing SharedPreferences.
/// Provides a singleton instance to avoid multiple getInstance() calls.
class PreferencesService {
  static PreferencesService? _instance;
  static SharedPreferences? _prefs;

  PreferencesService._();

  /// Get the singleton instance of PreferencesService.
  /// Must call [init] before using this.
  static PreferencesService get instance {
    _instance ??= PreferencesService._();
    return _instance!;
  }

  /// Initialize the preferences service. Call this once at app startup.
  static Future<void> init() async {
    _prefs ??= await SharedPreferences.getInstance();
  }

  /// Get the SharedPreferences instance directly if needed.
  SharedPreferences get prefs {
    if (_prefs == null) {
      throw StateError(
        'PreferencesService not initialized. Call PreferencesService.init() first.',
      );
    }
    return _prefs!;
  }

  // Theme preferences
  static const String _themeKey = 'theme_preference';

  String? getTheme() => _prefs?.getString(_themeKey);

  Future<void> setTheme(String themeName) async {
    await _prefs?.setString(_themeKey, themeName);
  }

  // Language preferences
  static const String _langKey = 'language_preference';
  static const String _isFirstRunKey = 'is_first_run';

  String? getLanguage() => _prefs?.getString(_langKey);

  Future<void> setLanguage(String langCode) async {
    await _prefs?.setString(_langKey, langCode);
  }

  bool isFirstRun() => _prefs?.getBool(_isFirstRunKey) ?? true;

  Future<void> setFirstRunComplete() async {
    await _prefs?.setBool(_isFirstRunKey, false);
  }

  // Custom calculator data
  static const String _customCalculatorKey = 'customCalculator';

  String? getCustomCalculatorData() => _prefs?.getString(_customCalculatorKey);

  Future<void> setCustomCalculatorData(String jsonData) async {
    await _prefs?.setString(_customCalculatorKey, jsonData);
  }
}
