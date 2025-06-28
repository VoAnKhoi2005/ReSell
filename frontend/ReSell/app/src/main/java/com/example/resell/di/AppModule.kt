package com.example.resell.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.resell.network.ApiService
import com.example.resell.network.RefreshApiService
import com.example.resell.store.AuthTokenManager
import com.example.resell.util.AuthInterceptor
import com.example.resell.util.LocalDateAdapter
import com.example.resell.util.LocalDateTimeAdapter
import com.example.resell.util.TokenAuthenticator
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.example.resell.BuildConfig

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(LocalDateTimeAdapter())
            .add(LocalDateAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: AuthTokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenManager: AuthTokenManager,
        refreshApiService: RefreshApiService
    ): TokenAuthenticator {
        return TokenAuthenticator(tokenManager, refreshApiService)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val baseUrl = BuildConfig.BASE_URL
        Log.d("Retrofit", "Retrofit is using $baseUrl")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    //region Refresh API service
    @Provides
    @Singleton
    @Named("refreshOkHttp")
    fun provideRefreshOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    @Named("refreshRetrofit")
    fun provideRefreshRetrofit(
        moshi: Moshi,
        @Named("refreshOkHttp") refreshClient: OkHttpClient
    ): Retrofit {
        val baseUrl = BuildConfig.BASE_URL
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(refreshClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideRefreshApiService(@Named("refreshRetrofit") refreshRetrofit: Retrofit): RefreshApiService {
        return refreshRetrofit.create(RefreshApiService::class.java)
    }
    //endregion

    @Provides
    @Singleton
    fun provideAuthSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "auth_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}