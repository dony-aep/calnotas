import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:grade_calculator_app/translations/translations.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:grade_calculator_app/services/github_service.dart';

class UpdateScreen extends StatefulWidget {
  const UpdateScreen({super.key});

  @override
  State<UpdateScreen> createState() => _UpdateScreenState();
}

class _UpdateScreenState extends State<UpdateScreen> {
  bool _isLoading = true;
  String? _error;
  GitHubRelease? _latestRelease;
  String _currentVersion = '';

  static final _githubService = GitHubService(
    owner: 'dony-aep',
    repo: 'calnotas',
  );

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      // Obtener versión actual de la app
      final packageInfo = await PackageInfo.fromPlatform();
      _currentVersion = packageInfo.version;

      // Obtener última release de GitHub usando el servicio
      _latestRelease = await _githubService.getLatestRelease();
    } on GitHubException catch (e) {
      _error = e.code;
    } catch (e) {
      _error = 'connectionError';
    }

    if (mounted) {
      setState(() {
        _isLoading = false;
      });
    }
  }

  Future<void> _launchDownloadUrl(String url) async {
    final Uri uri = Uri.parse(url);
    if (!await launchUrl(uri, mode: LaunchMode.externalApplication)) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(t(context, 'errorOpeningLink')),
            backgroundColor: Theme.of(context).colorScheme.error,
          ),
        );
      }
    }
  }

  bool _isUpdateAvailable() {
    if (_latestRelease == null) return false;
    return GitHubService.isNewerVersion(
      _currentVersion,
      _latestRelease!.version,
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return Scaffold(
      appBar: AppBar(
        backgroundColor: colorScheme.surface,
        surfaceTintColor: Colors.transparent,
        elevation: 0,
        systemOverlayStyle: Theme.of(context).brightness == Brightness.light
            ? SystemUiOverlayStyle.dark.copyWith(
                statusBarColor: Colors.transparent,
              )
            : SystemUiOverlayStyle.light.copyWith(
                statusBarColor: Colors.transparent,
              ),
        title: Text(
          t(context, 'checkUpdates'),
          style: textTheme.titleLarge?.copyWith(
            fontWeight: FontWeight.bold,
          ),
        ),
      ),
      body: _isLoading
          ? _buildLoadingState(colorScheme, textTheme)
          : _error != null
              ? _buildErrorState(colorScheme, textTheme)
              : _buildContentState(colorScheme, textTheme),
    );
  }

  Widget _buildLoadingState(ColorScheme colorScheme, TextTheme textTheme) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircularProgressIndicator(
            color: colorScheme.primary,
          ),
          const SizedBox(height: 24),
          Text(
            t(context, 'checkingForUpdates'),
            style: textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildErrorState(ColorScheme colorScheme, TextTheme textTheme) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: colorScheme.errorContainer.withValues(alpha: 0.3),
                shape: BoxShape.circle,
              ),
              child: Icon(
                _error == 'noReleasesFound'
                    ? Icons.info_outline_rounded
                    : Icons.cloud_off_rounded,
                size: 64,
                color: _error == 'noReleasesFound'
                    ? colorScheme.primary
                    : colorScheme.error,
              ),
            ),
            const SizedBox(height: 24),
            Text(
              t(context, _error!),
              style: textTheme.titleMedium?.copyWith(
                color: colorScheme.onSurface,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              t(context, 'tryAgainLater'),
              style: textTheme.bodyMedium?.copyWith(
                color: colorScheme.onSurfaceVariant,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 32),
            FilledButton.icon(
              onPressed: _loadData,
              icon: const Icon(Icons.refresh_rounded),
              label: Text(t(context, 'retry')),
              style: FilledButton.styleFrom(
                backgroundColor: colorScheme.primary,
                foregroundColor: colorScheme.onPrimary,
                padding: const EdgeInsets.symmetric(
                  horizontal: 24,
                  vertical: 12,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContentState(ColorScheme colorScheme, TextTheme textTheme) {
    final isUpdateAvailable = _isUpdateAvailable();
    final latestVersion = _latestRelease?.version ?? '';

    return SingleChildScrollView(
      padding: const EdgeInsets.all(24.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          // Icono de estado
          Container(
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              color: isUpdateAvailable
                  ? colorScheme.primaryContainer.withValues(alpha: 0.3)
                  : colorScheme.tertiaryContainer.withValues(alpha: 0.3),
              shape: BoxShape.circle,
            ),
            child: Icon(
              isUpdateAvailable
                  ? Icons.system_update_rounded
                  : Icons.check_circle_rounded,
              size: 72,
              color: isUpdateAvailable
                  ? colorScheme.primary
                  : colorScheme.tertiary,
            ),
          ),
          const SizedBox(height: 24),

          // Título de estado
          Text(
            isUpdateAvailable
                ? t(context, 'updateAvailable')
                : t(context, 'upToDate'),
            style: textTheme.headlineSmall?.copyWith(
              fontWeight: FontWeight.bold,
              color: colorScheme.onSurface,
            ),
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 8),
          Text(
            isUpdateAvailable
                ? t(context, 'newVersionAvailable')
                : t(context, 'youHaveLatestVersion'),
            style: textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurfaceVariant,
            ),
            textAlign: TextAlign.center,
          ),

          const SizedBox(height: 32),

          // Card de versiones
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(
              color: colorScheme.surfaceContainerLow,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(
                color: colorScheme.outlineVariant,
                width: 1,
              ),
            ),
            child: Column(
              children: [
                _buildVersionRow(
                  context,
                  t(context, 'currentVersion'),
                  'v$_currentVersion',
                  colorScheme,
                  textTheme,
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 12),
                  child: Divider(color: colorScheme.outlineVariant),
                ),
                _buildVersionRow(
                  context,
                  t(context, 'latestVersion'),
                  latestVersion.isNotEmpty ? 'v$latestVersion' : '--',
                  colorScheme,
                  textTheme,
                  highlight: isUpdateAvailable,
                ),
              ],
            ),
          ),

          if (isUpdateAvailable && _latestRelease != null) ...[
            const SizedBox(height: 24),

            // Release notes
            if (_latestRelease!.body.isNotEmpty) ...[
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: colorScheme.surfaceContainerLow,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(
                    color: colorScheme.outlineVariant,
                    width: 1,
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Icon(
                          Icons.notes_rounded,
                          size: 20,
                          color: colorScheme.primary,
                        ),
                        const SizedBox(width: 8),
                        Text(
                          t(context, 'releaseNotes'),
                          style: textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                            color: colorScheme.onSurface,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    Text(
                      _latestRelease!.body,
                      style: textTheme.bodyMedium?.copyWith(
                        color: colorScheme.onSurfaceVariant,
                        height: 1.5,
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),
            ],

            // Botón de descarga
            SizedBox(
              width: double.infinity,
              child: FilledButton.icon(
                onPressed: () {
                  if (_latestRelease!.htmlUrl.isNotEmpty) {
                    _launchDownloadUrl(_latestRelease!.htmlUrl);
                  }
                },
                icon: const Icon(Icons.download_rounded),
                label: Text(t(context, 'downloadUpdate')),
                style: FilledButton.styleFrom(
                  backgroundColor: colorScheme.primary,
                  foregroundColor: colorScheme.onPrimary,
                  padding: const EdgeInsets.symmetric(vertical: 16),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(16),
                  ),
                ),
              ),
            ),
          ],

          const SizedBox(height: 16),

          // Botón de reintentar (siempre visible)
          TextButton.icon(
            onPressed: _loadData,
            icon: const Icon(Icons.refresh_rounded),
            label: Text(t(context, 'checkAgain')),
            style: TextButton.styleFrom(
              foregroundColor: colorScheme.primary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildVersionRow(
    BuildContext context,
    String label,
    String version,
    ColorScheme colorScheme,
    TextTheme textTheme, {
    bool highlight = false,
  }) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: textTheme.bodyMedium?.copyWith(
            color: colorScheme.onSurfaceVariant,
          ),
        ),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
          decoration: BoxDecoration(
            color: highlight
                ? colorScheme.primaryContainer
                : colorScheme.surfaceContainerHighest,
            borderRadius: BorderRadius.circular(20),
          ),
          child: Text(
            version,
            style: textTheme.labelLarge?.copyWith(
              fontWeight: FontWeight.w600,
              color: highlight
                  ? colorScheme.onPrimaryContainer
                  : colorScheme.onSurfaceVariant,
            ),
          ),
        ),
      ],
    );
  }
}
