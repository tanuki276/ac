package com.nyankowars.di

import android.content.Context
import androidx.room.Room
import com.nyankowars.data.api.NyankoApi
import com.nyankowars.data.local.NyankoDatabase
import com.nyankowars.data.local.PreferencesManager
import com.nyankowars.data.repository.*
import com.nyankowars.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.100:8080/api/") // サーバーURLに変更
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideNyankoApi(retrofit: Retrofit): NyankoApi {
        return retrofit.create(NyankoApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): NyankoDatabase {
        return Room.databaseBuilder(
            context,
            NyankoDatabase::class.java,
            "nyanko_database"
        ).fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        api: NyankoApi,
        preferences: PreferencesManager
    ): AuthRepository {
        return AuthRepository(api, preferences)
    }
    
    @Provides
    @Singleton
    fun providePlayerRepository(
        api: NyankoApi,
        database: NyankoDatabase,
        preferences: PreferencesManager
    ): PlayerRepository {
        return PlayerRepository(api, database, preferences)
    }
    
    // ドメインリポジトリインターフェースも同じ実装を使用
    @Provides
    @Singleton
    fun provideAuthRepositoryDomain(
        authRepository: AuthRepository
    ): com.nyankowars.domain.repository.AuthRepository {
        return authRepository
    }
    
    @Provides
    @Singleton
    fun providePlayerRepositoryDomain(
        playerRepository: PlayerRepository
    ): com.nyankowars.domain.repository.PlayerRepository {
        return playerRepository
    }
}