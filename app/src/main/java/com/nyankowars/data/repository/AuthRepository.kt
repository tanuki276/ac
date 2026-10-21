package com.nyankowars.data.repository

import com.nyankowars.data.api.NyankoApi
import com.nyankowars.data.local.PreferencesManager
import com.nyankowars.data.models.LoginRequest
import com.nyankowars.data.models.RegisterRequest
import com.nyankowars.domain.models.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: NyankoApi,
    private val preferences: PreferencesManager
) {
    suspend fun register(username: String, email: String, password: String): Resource<Unit> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val auth = body.data
                preferences.saveAuthToken(auth.token)
                preferences.savePlayerId(auth.player.id)
                preferences.savePlayerName(auth.player.username)
                Resource.Success(Unit)
            } else {
                Resource.Error(body?.message ?: "登録に失敗しました", response.code())
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): Resource<Unit> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val auth = body.data
                preferences.saveAuthToken(auth.token)
                preferences.savePlayerId(auth.player.id)
                preferences.savePlayerName(auth.player.username)
                Resource.Success(Unit)
            } else {
                Resource.Error(body?.message ?: "ログインに失敗しました", response.code())
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }

    suspend fun logout() {
        preferences.clearAll()
    }

    fun isLoggedIn(): Boolean = !preferences.getAuthToken().isNullOrBlank()
    fun getAuthToken(): String? = preferences.getAuthToken()
    fun getPlayerId(): String? = preferences.getPlayerId()
    fun getPlayerName(): String? = preferences.getPlayerName()
}
