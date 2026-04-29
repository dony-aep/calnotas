package com.donyaep.calnotas.ui.screens.update

import com.donyaep.calnotas.data.remote.dto.GitHubReleaseDto

data class UpdateUiState(
    val isLoading: Boolean = true,
    val errorKey: String? = null,
    val currentVersion: String = "",
    val latestRelease: GitHubReleaseDto? = null
)
