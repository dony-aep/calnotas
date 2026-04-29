package com.donyaep.calnotas.data.remote

import com.donyaep.calnotas.data.remote.dto.GitHubReleaseDto
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GitHubReleaseDto
}
