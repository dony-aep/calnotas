import 'package:flutter/material.dart';
import 'app_en.dart';
import 'app_es.dart';

class Translations {
  final Locale locale;

  Translations(this.locale);

  static Translations of(BuildContext context) {
    return Localizations.of<Translations>(context, Translations) ??
        Translations(const Locale('en', 'US'));
  }

  static const Map<String, Map<String, String>> _localizedValues = {
    'en': enUS,
    'es': esES,
  };

  String translate(String key) {
    return _localizedValues[locale.languageCode]?[key] ??
        _localizedValues['en']?[key] ??
        '** $key not found **';
  }

  static const LocalizationsDelegate<Translations> delegate =
      _TranslationsDelegate();
}

class _TranslationsDelegate extends LocalizationsDelegate<Translations> {
  const _TranslationsDelegate();

  @override
  bool isSupported(Locale locale) {
    return ['en', 'es'].contains(locale.languageCode);
  }

  @override
  Future<Translations> load(Locale locale) async {
    return Translations(locale);
  }

  @override
  bool shouldReload(_TranslationsDelegate old) => false;
}

// Función de conveniencia para traducir directamente
String t(BuildContext context, String key) {
  return Translations.of(context).translate(key);
}
