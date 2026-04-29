package com.donyaep.calnotas.data

import android.content.Context
import com.donyaep.calnotas.data.remote.GitHubApiService
import com.donyaep.calnotas.data.repository.UserPreferencesRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {
    private lateinit var appContext: Context

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val gitHubApiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(appContext)
    }
}
