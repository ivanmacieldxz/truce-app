package com.ivanmacieldxz.truce.di

import com.ivanmacieldxz.truce.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(supabaseClient: SupabaseClient): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            // We use runBlocking carefully here because interceptors run synchronously in okhttp threads,
            // but for gotrue-kt getting the token might be suspending if it needs to refresh it.
            // A better approach in production is a custom Authenticator, but for this interceptor:
            val token = runBlocking {
                supabaseClient.auth.currentAccessTokenOrNull()
            }
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBackendApiService(retrofit: Retrofit): com.ivanmacieldxz.truce.data.remote.BackendApiService {
        return retrofit.create(com.ivanmacieldxz.truce.data.remote.BackendApiService::class.java)
    }
}
