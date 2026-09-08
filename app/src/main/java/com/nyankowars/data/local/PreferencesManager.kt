package com.nyankowars.data.local

import android.content.Context

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences("nyanko_preferences", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? = preferences.getString(KEY_AUTH_TOKEN, null)

    fun savePlayerId(id: String) {
        preferences.edit().putString(KEY_PLAYER_ID, id).apply()
    }

    fun getPlayerId(): String? = preferences.getString(KEY_PLAYER_ID, null)

    fun savePlayerName(name: String) {
        preferences.edit().putString(KEY_PLAYER_NAME, name).apply()
    }

    fun getPlayerName(): String? = preferences.getString(KEY_PLAYER_NAME, null)

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_PLAYER_ID = "player_id"
        const val KEY_PLAYER_NAME = "player_name"
    }
}
