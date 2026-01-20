import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:grade_calculator_app/app.dart';
import 'package:grade_calculator_app/services/preferences_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize preferences service
  await PreferencesService.init();

  // Garantizar que la app responda correctamente después de minimizar/restaurar
  SystemChrome.setEnabledSystemUIMode(
    SystemUiMode.manual,
    overlays: [SystemUiOverlay.top, SystemUiOverlay.bottom],
  );

  // Forzar la orientación vertical
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  runApp(const MyApp());
}
