package com.nyankowars.data.repository

import com.nyankowars.data.api.NyankoApi
import com.nyankowars.data.local.NyankoDatabase
import com.nyankowars.data.local.PreferencesManager
import com.nyankowars.data.models.*
import com.nyankowars.domain.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val api: NyankoApi,
    private val database: NyankoDatabase,
    private val preferences: PreferencesManager
) {
    
    suspend fun getPlayerData(): Resource<PlayerResponse> {
        return try {
            val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
            val response = api.getPlayerData("Bearer $token")
            
            if (response.isSuccessful && response.body()?.success == true) {
                val player = response.body()!!.data!!
                // ローカルDBに保存
                database.playerDao().insertOrUpdate(player.toEntity())
                Resource.Success(player)
            } else {
                // ローカルから取得を試みる
                val localPlayer = database.playerDao().getPlayer(preferences.getPlayerId())
                if (localPlayer != null) {
                    Resource.Success(localPlayer.toDomain())
                } else {
                    Resource.Error("プレイヤーデータ取得に失敗しました")
                }
            }
        } catch (e: Exception) {
            // ローカルから取得を試みる
            val localPlayer = database.playerDao().getPlayer(preferences.getPlayerId())
            if (localPlayer != null) {
                Resource.Success(localPlayer.toDomain())
            } else {
                Resource.Error("ネットワークエラー: ${e.message}")
            }
        }
    }
    
    suspend fun getCats(teamOnly: Boolean = false): Resource<List<CatResponse>> {
        return try {
            val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
            val response = api.getCats("Bearer $token", teamOnly)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val cats = response.body()!!.data!!
                // ローカルDBに保存
                database.catDao().insertOrUpdateAll(cats.map { it.toEntity() })
                Resource.Success(cats)
            } else {
                // ローカルから取得を試みる
                val localCats = if (teamOnly) {
                    database.catDao().getTeamCats(preferences.getPlayerId())
                } else {
                    database.catDao().getAllCats(preferences.getPlayerId())
                }
                Resource.Success(localCats.map { it.toDomain() })
            }
        } catch (e: Exception) {
            // ローカルから取得を試みる
            val localCats = if (teamOnly) {
                database.catDao().getTeamCats(preferences.getPlayerId())
            } else {
                database.catDao().getAllCats(preferences.getPlayerId())
            }
            Resource.Success(localCats.map { it.toDomain() })
        }
    }
    
    suspend fun upgradeCat(catId: String): Resource<CatUpgradeResponse> {
        return try {
            val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
            val response = api.upgradeCat("Bearer $token", catId)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val result = response.body()!!.data!!
                // ローカルDBを更新
                database.catDao().insertOrUpdate(result.cat.toEntity())
                // プレイヤーデータも更新
                database.playerDao().updateNyankoPoints(
                    preferences.getPlayerId()!!,
                    result.remainingPoints
                )
                Resource.Success(result)
            } else {
                Resource.Error(response.body()?.message ?: "強化に失敗しました")
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }
    
    suspend fun setCatToTeam(catId: String, position: Int): Resource<Unit> {
        return try {
            val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
            val request = TeamPositionRequest(position)
            val response = api.setCatToTeam("Bearer $token", catId, request)
            
            if (response.isSuccessful && response.body()?.success == true) {
                // ローカルDBを更新
                database.catDao().updateTeamPosition(preferences.getPlayerId()!!, catId, position, true)
                Resource.Success(Unit)
            } else {
                Resource.Error("チーム配置に失敗しました")
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }
    
    suspend fun updateSettings(settings: PlayerSettings): Resource<PlayerResponse> {
        return try {
            val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
            val response = api.updateSettings("Bearer $token", settings)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val player = response.body()!!.data!!
                database.playerDao().insertOrUpdate(player.toEntity())
                Resource.Success(player)
            } else {
                Resource.Error("設定更新に失敗しました")
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }
}