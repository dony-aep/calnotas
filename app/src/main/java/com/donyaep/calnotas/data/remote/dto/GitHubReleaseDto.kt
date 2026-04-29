package com.donyaep.calnotas.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GitHubReleaseDto(
    @SerializedName("tag_name") val tagName: String = "",
    val name: String = "",
    val body: String = "",
    @SerializedName("html_url") val htmlUrl: String = ""
)
