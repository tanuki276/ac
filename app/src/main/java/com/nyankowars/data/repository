package com.nyankowars.data.repository

import com.nyankowars.data.api.NyankoApi
import com.nyankowars.data.local.PreferencesManager
import com.nyankowars.data.models.*
import com.nyankowars.domain.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: NyankoApi,
    private val preferences: PreferencesManager
) {
    
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Resource<AuthResponse> {
        return try {
            val request = RegisterRequest(username, email, password)
            val response = api.register(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!.data!!
                // トークンを保存
                preferences.saveAuthToken(authResponse.token)
                preferences.savePlayerId(authResponse.player.id)
                preferences.savePlayerName(authResponse.player.username)
                
                Resource.Success(authResponse)
            } else {
                val error = response.body()?.message ?: "登録に失敗しました"
                Resource.Error(error)
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }
    
    suspend fun login(email: String, password: String): Resource<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = api.login(request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val authResponse = response.body()!!.data!!
                // トークンを保存
                preferences.saveAuthToken(authResponse.token)
                preferences.savePlayerId(authResponse.player.id)
                preferences.savePlayerName(authResponse.player.username)
                
                Resource.Success(authResponse)
            } else {
                val error = response.body()?.message ?: "ログインに失敗しました"
                Resource.Error(error)
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }
    
    suspend fun logout() {
        preferences.clearAll()
    }
    
    suspend fun getProfile(): Resource<ProfileResponse> {
        return try {
            val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
            val response = api.getProfile("Bearer $token")
            
            if (response.isSuccessful && response.body()?.success == true) {
                Resource.Success(response.body()!!.data!!)
            } else {
                Resource.Error("プロフィール取得に失敗しました")
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }
    
    fun isLoggedIn(): Boolean {
        return preferences.getAuthToken() != null
    }
    
    fun getAuthToken(): String? {
        return preferences.getAuthToken()
    }
    
    fun getPlayerId(): String? {
        return preferences.getPlayerId()
    }
    
    fun getPlayerName(): String? {
        return preferences.getPlayerName()
    }
}