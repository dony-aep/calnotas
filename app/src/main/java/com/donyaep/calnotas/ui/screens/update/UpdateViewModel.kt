package com.donyaep.calnotas.ui.screens.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donyaep.calnotas.BuildConfig
import com.donyaep.calnotas.data.AppContainer
import com.donyaep.calnotas.data.remote.GitHubApiService
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class UpdateViewModel(
    private val gitHubApiService: GitHubApiService = AppContainer.gitHubApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        UpdateUiState(
            isLoading = true,
            currentVersion = BuildConfig.VERSION_NAME
        )
    )
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    init {
        loadUpdateInfo()
    }

    fun retry() {
        loadUpdateInfo()
    }

    fun isUpdateAvailable(): Boolean {
        val latest = _uiState.value.latestRelease ?: return false
        return isNewerVersion(_uiState.value.currentVersion, latest.tagName.removePrefix("v"))
    }

    private fun loadUpdateInfo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorKey = null
                )
            }

            runCatching {
                gitHubApiService.getLatestRelease(owner = "dony-aep", repo = "calnotas")
            }.onSuccess { release ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        latestRelease = release,
                        errorKey = null
                    )
                }
            }.onFailure { throwable ->
                val errorKey = when (throwable) {
                    is HttpException -> {
                        if (throwable.code() == 404) {
                            "no_releases_found"
                        } else {
                            "error_fetching_updates"
                        }
                    }

                    is IOException -> "connection_error"
                    else -> "connection_error"
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorKey = errorKey
                    )
                }
            }
        }
    }

    private fun isNewerVersion(currentVersion: String, latestVersion: String): Boolean {
        val current = parseVersion(currentVersion)
        val latest = parseVersion(latestVersion)

        for (i in 0..2) {
            if (latest[i] > current[i]) return true
            if (latest[i] < current[i]) return false
        }
        return false
    }

    private fun parseVersion(value: String): List<Int> {
        val parts = value.split('.').map { it.toIntOrNull() ?: 0 }.toMutableList()
        while (parts.size < 3) {
            parts.add(0)
        }
        return parts.take(3)
    }
}
