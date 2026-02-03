package com.nyankowars.data.models

import com.google.gson.annotations.SerializedName

// APIレスポンス基本形
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("errors") val errors: List<String>? = null
)

// エラーレスポンス
data class ErrorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("code") val code: Int? = null,
    @SerializedName("timestamp") val timestamp: String
)

// 認証
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("player") val player: PlayerResponse
)

// プレイヤー
data class PlayerResponse(
    @SerializedName("id") val id: String,
    @SerializedName("username") val username: String,
    @SerializedName("level") val level: Int,
    @SerializedName("experience") val experience: Int,
    @SerializedName("nyankoPoints") val nyankoPoints: Int,
    @SerializedName("energy") val energy: Int,
    @SerializedName("maxEnergy") val maxEnergy: Int,
    @SerializedName("gems") val gems: Int,
    @SerializedName("battleCount") val battleCount: Int,
    @SerializedName("winCount") val winCount: Int,
    @SerializedName("loseCount") val loseCount: Int,
    @SerializedName("gachaCount") val gachaCount: Int,
    @SerializedName("lastEnergyUpdate") val lastEnergyUpdate: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("settings") val settings: PlayerSettings? = null
)

data class PlayerSettings(
    @SerializedName("sound") val sound: Boolean = true,
    @SerializedName("music") val music: Boolean = true,
    @SerializedName("notifications") val notifications: Boolean = true
)

// 猫
data class CatResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("rarity") val rarity: String,
    @SerializedName("level") val level: Int,
    @SerializedName("attack") val attack: Int,
    @SerializedName("defense") val defense: Int,
    @SerializedName("health") val health: Int,
    @SerializedName("speed") val speed: Double,
    @SerializedName("criticalRate") val criticalRate: Double? = 0.05,
    @SerializedName("criticalDamage") val criticalDamage: Double? = 1.5,
    @SerializedName("element") val element: String? = "none",
    @SerializedName("isFavorite") val isFavorite: Boolean = false,
    @SerializedName("isInTeam") val isInTeam: Boolean = false,
    @SerializedName("teamPosition") val teamPosition: Int? = null,
    @SerializedName("skills") val skills: List<SkillResponse>? = emptyList(),
    @SerializedName("createdFromGacha") val createdFromGacha: Boolean = true,
    @SerializedName("battleCount") val battleCount: Int = 0,
    @SerializedName("winCount") val winCount: Int = 0,
    @SerializedName("createdAt") val createdAt: String
)

data class SkillResponse(
    @SerializedName("name") val name: String,
    @SerializedName("level") val level: Int,
    @SerializedName("damage") val damage: Int,
    @SerializedName("cooldown") val cooldown: Int,
    @SerializedName("description") val description: String
)

// ガチャ
data class GachaRequest(
    @SerializedName("type") val type: String = "normal"
)

data class GachaResponse(
    @SerializedName("cat") val cat: CatResponse,
    @SerializedName("remainingPoints") val remainingPoints: Int,
    @SerializedName("message") val message: String
)

data class MultiGachaResponse(
    @SerializedName("cats") val cats: List<CatResponse>,
    @SerializedName("remainingPoints") val remainingPoints: Int,
    @SerializedName("message") val message: String
)

// バトル
data class BattleStartRequest(
    @SerializedName("catIds") val catIds: List<String>,
    @SerializedName("mapId") val mapId: String? = null,
    @SerializedName("opponentType") val opponentType: String = "ai"
)

data class BattleAction(
    @SerializedName("roundNumber") val roundNumber: Int,
    @SerializedName("attackerId") val attackerId: String?,
    @SerializedName("targetId") val targetId: String?,
    @SerializedName("skillId") val skillId: String?,
    @SerializedName("damage") val damage: Int,
    @SerializedName("isCritical") val isCritical: Boolean = false
)

data class BattleResultResponse(
    @SerializedName("victory") val victory: Boolean,
    @SerializedName("damageDealt") val damageDealt: Int,
    @SerializedName("damageTaken") val damageTaken: Int,
    @SerializedName("rewards") val rewards: BattleRewards,
    @SerializedName("experienceGained") val experienceGained: Int,
    @SerializedName("playerLeveledUp") val playerLeveledUp: Boolean = false,
    @SerializedName("newPlayerLevel") val newPlayerLevel: Int? = null
)

data class BattleRewards(
    @SerializedName("nyankoPoints") val nyankoPoints: Int,
    @SerializedName("items") val items: List<BattleRewardItem>? = emptyList(),
    @SerializedName("newCats") val newCats: List<CatResponse>? = emptyList()
)

data class BattleRewardItem(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("quantity") val quantity: Int
)

// マップ
data class MapResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("chapter") val chapter: Int,
    @SerializedName("stage") val stage: Int,
    @SerializedName("enemyIds") val enemyIds: List<String>,
    @SerializedName("enemyLevel") val enemyLevel: Int,
    @SerializedName("expReward") val expReward: Int,
    @SerializedName("pointReward") val pointReward: Int,
    @SerializedName("clearCondition") val clearCondition: String,
    @SerializedName("turnLimit") val turnLimit: Int,
    @SerializedName("description") val description: String? = null,
    @SerializedName("backgroundImage") val backgroundImage: String? = null
)

// アイテム
data class ItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("effectValue") val effectValue: String,
    @SerializedName("price") val price: Int,
    @SerializedName("description") val description: String,
    @SerializedName("maxStack") val maxStack: Int = 99,
    @SerializedName("usableIn") val usableIn: String = "any"
)

// クエスト
data class QuestResponse(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("condition") val condition: QuestCondition,
    @SerializedName("rewards") val rewards: QuestRewards,
    @SerializedName("timeLimit") val timeLimit: String? = null,
    @SerializedName("repeatable") val repeatable: Boolean = false,
    @SerializedName("progress") val progress: Int? = 0,
    @SerializedName("completed") val completed: Boolean = false
)

data class QuestCondition(
    @SerializedName("type") val type: String,
    @SerializedName("target") val target: String,
    @SerializedName("amount") val amount: Int
)

data class QuestRewards(
    @SerializedName("experience") val experience: Int,
    @SerializedName("nyankoPoints") val nyankoPoints: Int,
    @SerializedName("items") val items: List<QuestRewardItem>? = emptyList()
)

data class QuestRewardItem(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("quantity") val quantity: Int
)

// アチーブメント
data class AchievementResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("condition") val condition: AchievementCondition,
    @SerializedName("rewards") val rewards: AchievementRewards,
    @SerializedName("secret") val secret: Boolean = false,
    @SerializedName("unlocked") val unlocked: Boolean = false,
    @SerializedName("unlockedAt") val unlockedAt: String? = null
)

data class AchievementCondition(
    @SerializedName("type") val type: String,
    @SerializedName("target") val target: String,
    @SerializedName("value") val value: Int
)

data class AchievementRewards(
    @SerializedName("nyankoPoints") val nyankoPoints: Int,
    @SerializedName("items") val items: List<AchievementRewardItem>? = emptyList()
)

data class AchievementRewardItem(
    @SerializedName("itemId") val itemId: String,
    @SerializedName("quantity") val quantity: Int
)
