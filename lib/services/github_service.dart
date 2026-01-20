import 'dart:convert';
import 'package:http/http.dart' as http;

/// Service for interacting with GitHub API.
/// Handles fetching release information from GitHub repositories.
class GitHubService {
  final String owner;
  final String repo;

  GitHubService({required this.owner, required this.repo});

  /// Fetches the latest release from GitHub.
  /// Returns the release data as a Map, or throws an exception on error.
  Future<GitHubRelease> getLatestRelease() async {
    final response = await http.get(
      Uri.parse(
        'https://api.github.com/repos/$owner/$repo/releases/latest',
      ),
      headers: {
        'Accept': 'application/vnd.github.v3+json',
      },
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body) as Map<String, dynamic>;
      return GitHubRelease.fromJson(data);
    } else if (response.statusCode == 404) {
      throw GitHubException('noReleasesFound');
    } else {
      throw GitHubException('errorFetchingUpdates');
    }
  }

  /// Compares two version strings and returns true if latest is newer than current.
  static bool isNewerVersion(String currentVersion, String latestVersion) {
    if (latestVersion.isEmpty || currentVersion.isEmpty) return false;

    try {
      final current = _parseVersion(currentVersion);
      final latest = _parseVersion(latestVersion);

      for (int i = 0; i < 3; i++) {
        if (latest[i] > current[i]) return true;
        if (latest[i] < current[i]) return false;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  static List<int> _parseVersion(String version) {
    final parts = version.split('.').map((e) => int.tryParse(e) ?? 0).toList();
    while (parts.length < 3) {
      parts.add(0);
    }
    return parts.take(3).toList();
  }
}

/// Represents a GitHub release.
class GitHubRelease {
  final String tagName;
  final String name;
  final String body;
  final String htmlUrl;
  final DateTime? publishedAt;
  final List<GitHubAsset> assets;

  GitHubRelease({
    required this.tagName,
    required this.name,
    required this.body,
    required this.htmlUrl,
    this.publishedAt,
    required this.assets,
  });

  /// Gets the version number without the 'v' prefix.
  String get version {
    if (tagName.startsWith('v')) {
      return tagName.substring(1);
    }
    return tagName;
  }

  factory GitHubRelease.fromJson(Map<String, dynamic> json) {
    return GitHubRelease(
      tagName: json['tag_name'] ?? '',
      name: json['name'] ?? '',
      body: json['body'] ?? '',
      htmlUrl: json['html_url'] ?? '',
      publishedAt: json['published_at'] != null
          ? DateTime.tryParse(json['published_at'])
          : null,
      assets: (json['assets'] as List<dynamic>?)
              ?.map((a) => GitHubAsset.fromJson(a as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }
}

/// Represents a release asset (downloadable file).
class GitHubAsset {
  final String name;
  final String browserDownloadUrl;
  final int size;
  final int downloadCount;
  final String contentType;

  GitHubAsset({
    required this.name,
    required this.browserDownloadUrl,
    required this.size,
    required this.downloadCount,
    required this.contentType,
  });

  factory GitHubAsset.fromJson(Map<String, dynamic> json) {
    return GitHubAsset(
      name: json['name'] ?? '',
      browserDownloadUrl: json['browser_download_url'] ?? '',
      size: json['size'] ?? 0,
      downloadCount: json['download_count'] ?? 0,
      contentType: json['content_type'] ?? '',
    );
  }

  /// Formats the file size in a human-readable format.
  String get formattedSize {
    if (size < 1024) return '$size B';
    if (size < 1024 * 1024) return '${(size / 1024).toStringAsFixed(1)} KB';
    return '${(size / (1024 * 1024)).toStringAsFixed(1)} MB';
  }
}

/// Exception for GitHub API errors.
class GitHubException implements Exception {
  final String code;
  GitHubException(this.code);

  @override
  String toString() => 'GitHubException: $code';
}
