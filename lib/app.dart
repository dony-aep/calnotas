import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:grade_calculator_app/config/language_provider.dart';
import 'package:grade_calculator_app/config/theme.dart';
import 'package:grade_calculator_app/config/theme_provider.dart';
import 'package:grade_calculator_app/screens/home_screen.dart';
import 'package:grade_calculator_app/translations/translations.dart';
import 'package:provider/provider.dart';

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => ThemeProvider()),
        ChangeNotifierProvider(create: (_) => LanguageProvider()),
      ],
      child: Consumer2<ThemeProvider, LanguageProvider>(
        builder: (context, themeProvider, languageProvider, child) {
          // Configura el estilo de la barra de estado según el tema
          final overlayStyle =
              themeProvider.isDarkMode
                  ? SystemUiOverlayStyle
                      .light // Iconos claros en tema oscuro
                  : SystemUiOverlayStyle.dark; // Iconos oscuros en tema claro

          // Aplica el estilo de la barra de estado
          SystemChrome.setSystemUIOverlayStyle(overlayStyle);

          return MaterialApp(
            title: 'CalNotas',
            debugShowCheckedModeBanner: false,
            theme: AppTheme.lightTheme(context),
            darkTheme: AppTheme.darkTheme(context),
            themeMode: themeProvider.themeMode,
            locale: languageProvider.locale,
            supportedLocales: LanguageProvider.supportedLocales,
            localizationsDelegates: const [
              Translations.delegate,
              GlobalMaterialLocalizations.delegate,
              GlobalWidgetsLocalizations.delegate,
              GlobalCupertinoLocalizations.delegate,
            ],
            home: const MyHomePage(title: 'Grade Calculator'),
          );
        },
      ),
    );
  }
}
