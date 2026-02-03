package com.nyankowars.data.api

import com.nyankowars.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface NyankoApi {
    
    // ==================== 認証 ====================
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>
    
    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<ProfileResponse>>
    
    // ==================== プレイヤー ====================
    @GET("player")
    suspend fun getPlayerData(@Header("Authorization") token: String): Response<ApiResponse<PlayerResponse>>
    
    @PUT("player/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body settings: PlayerSettings
    ): Response<ApiResponse<PlayerResponse>>
    
    // ==================== 猫 ====================
    @GET("player/cats")
    suspend fun getCats(
        @Header("Authorization") token: String,
        @Query("teamOnly") teamOnly: Boolean = false
    ): Response<ApiResponse<List<CatResponse>>>
    
    @GET("player/cats/{id}")
    suspend fun getCatById(
        @Header("Authorization") token: String,
        @Path("id") catId: String
    ): Response<ApiResponse<CatResponse>>
    
    @POST("player/cats/{id}/team")
    suspend fun setCatToTeam(
        @Header("Authorization") token: String,
        @Path("id") catId: String,
        @Body request: TeamPositionRequest
    ): Response<ApiResponse<Unit>>
    
    @POST("player/cats/{id}/upgrade")
    suspend fun upgradeCat(
        @Header("Authorization") token: String,
        @Path("id") catId: String
    ): Response<ApiResponse<CatUpgradeResponse>>
    
    // ==================== ガチャ ====================
    @POST("gacha/single")
    suspend fun gachaSingle(
        @Header("Authorization") token: String,
        @Body request: GachaRequest
    ): Response<ApiResponse<GachaResponse>>
    
    @POST("gacha/multi")
    suspend fun gachaMulti(
        @Header("Authorization") token: String,
        @Body request: GachaRequest
    ): Response<ApiResponse<MultiGachaResponse>>
    
    // ==================== バトル ====================
    @GET("battle/maps")
    suspend fun getMaps(
        @Header("Authorization") token: String,
        @Query("chapter") chapter: Int? = null
    ): Response<ApiResponse<List<MapResponse>>>
    
    @GET("battle/maps/{id}")
    suspend fun getMapById(
        @Header("Authorization") token: String,
        @Path("id") mapId: String
    ): Response<ApiResponse<MapResponse>>
    
    @POST("battle/start")
    suspend fun startBattle(
        @Header("Authorization") token: String,
        @Body request: BattleStartRequest
    ): Response<ApiResponse<BattleStartResponse>>
    
    @POST("battle/{id}/action")
    suspend fun submitBattleAction(
        @Header("Authorization") token: String,
        @Path("id") battleId: String,
        @Body action: BattleAction
    ): Response<ApiResponse<BattleActionResponse>>
    
    @POST("battle/{id}/complete")
    suspend fun completeBattle(
        @Header("Authorization") token: String,
        @Path("id") battleId: String
    ): Response<ApiResponse<BattleResultResponse>>
    
    // ==================== ショップ ====================
    @GET("shop/items")
    suspend fun getShopItems(
        @Header("Authorization") token: String,
        @Query("category") category: String? = null
    ): Response<ApiResponse<List<ItemResponse>>>
    
    @POST("shop/buy")
    suspend fun buyItem(
        @Header("Authorization") token: String,
        @Body request: BuyItemRequest
    ): Response<ApiResponse<BuyItemResponse>>
    
    // ==================== クエスト ====================
    @GET("quests")
    suspend fun getQuests(
        @Header("Authorization") token: String,
        @Query("type") type: String? = null
    ): Response<ApiResponse<List<QuestResponse>>>
    
    @POST("quests/{id}/claim")
    suspend fun claimQuest(
        @Header("Authorization") token: String,
        @Path("id") questId: String
    ): Response<ApiResponse<QuestClaimResponse>>
    
    // ==================== アチーブメント ====================
    @GET("achievements")
    suspend fun getAchievements(
        @Header("Authorization") token: String,
        @Query("unlocked") unlocked: Boolean? = null
    ): Response<ApiResponse<List<AchievementResponse>>>
    
    // ==================== 統計 ====================
    @GET("stats/player")
    suspend fun getPlayerStats(
        @Header("Authorization") token: String
    ): Response<ApiResponse<PlayerStatsResponse>>
    
    @GET("stats/leaderboard")
    suspend fun getLeaderboard(
        @Header("Authorization") token: String,
        @Query("type") type: String = "points",
        @Query("limit") limit: Int = 100
    ): Response<ApiResponse<List<LeaderboardEntry>>>
}

// 追加モデル
data class ProfileResponse(
    val player: PlayerResponse,
    val cats: List<CatResponse>
)

data class TeamPositionRequest(
    val position: Int
)

data class CatUpgradeResponse(
    val cat: CatResponse,
    val remainingPoints: Int,
    val message: String
)

data class BattleStartResponse(
    val battleId: String,
    val playerCats: List<CatResponse>,
    val enemyCats: List<EnemyCatResponse>,
    val map: MapResponse? = null
)

data class EnemyCatResponse(
    val id: String,
    val name: String,
    val rarity: String,
    val level: Int,
    val attack: Int,
    val defense: Int,
    val health: Int,
    val currentHealth: Int,
    val speed: Double,
    val skills: List<EnemySkillResponse>
)

data class EnemySkillResponse(
    val name: String,
    val cooldown: Int,
    val remainingCooldown: Int = 0
)

data class BattleActionResponse(
    val roundNumber: Int,
    val actions: List<BattleAction>,
    val playerCats: List<BattleCatStatus>,
    val enemyCats: List<EnemyCatStatus>,
    val battleContinues: Boolean
)

data class BattleCatStatus(
    val catId: String,
    val currentHealth: Int,
    val isAlive: Boolean,
    val statusEffects: List<StatusEffect>
)

data class EnemyCatStatus(
    val enemyId: String,
    val currentHealth: Int,
    val isAlive: Boolean,
    val statusEffects: List<StatusEffect>
)

data class StatusEffect(
    val type: String,
    val turnsRemaining: Int,
    val value: Int? = null
)

data class BuyItemRequest(
    val itemId: String,
    val quantity: Int = 1
)

data class BuyItemResponse(
    val item: ItemResponse,
    val remainingPoints: Int,
    val message: String
)

data class QuestClaimResponse(
    val quest: QuestResponse,
    val rewards: QuestRewards,
    val message: String
)

data class PlayerStatsResponse(
    val totalBattles: Int,
    val winRate: Double,
    val totalDamageDealt: Long,
    val totalDamageTaken: Long,
    val totalExperience: Long,
    val totalPoints: Long,
    val favoriteCat: FavoriteCat? = null,
    val playTime: Long // 秒
)

data class FavoriteCat(
    val catId: String,
    val name: String,
    val battles: Int,
    val winRate: Double
)

data class LeaderboardEntry(
    val rank: Int,
    val playerId: String,
    val username: String,
    val level: Int,
    val score: Long,
    val avatar: String? = null
)