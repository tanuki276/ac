package com.nyankowars.data.repository

import com.nyankowars.data.api.NyankoApi
import com.nyankowars.data.api.TeamPositionRequest
import com.nyankowars.data.local.CatEntity
import com.nyankowars.data.local.NyankoDatabase
import com.nyankowars.data.local.PlayerEntity
import com.nyankowars.data.local.PreferencesManager
import com.nyankowars.data.models.CatResponse
import com.nyankowars.data.models.PlayerResponse
import com.nyankowars.data.models.PlayerSettings
import com.nyankowars.domain.models.Cat
import com.nyankowars.domain.models.CatType
import com.nyankowars.domain.models.Element
import com.nyankowars.domain.models.Player
import com.nyankowars.domain.models.Rarity
import com.nyankowars.domain.models.Resource
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val api: NyankoApi,
    private val database: NyankoDatabase,
    private val preferences: PreferencesManager
) {
    suspend fun getPlayerData(): Resource<Player> {
        val playerId = preferences.getPlayerId()
        val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
        return try {
            val response = api.getPlayerData("Bearer $token")
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val dto = body.data
                database.playerDao().insertOrUpdate(dto.toEntity())
                Resource.Success(dto.toDomain())
            } else {
                localPlayer(playerId) ?: Resource.Error(body?.message ?: "プレイヤーデータ取得に失敗しました", response.code())
            }
        } catch (e: Exception) {
            localPlayer(playerId) ?: Resource.Error("ネットワークエラー: ${e.message}")
        }
    }

    suspend fun getCats(teamOnly: Boolean = false): Resource<List<Cat>> {
        val playerId = preferences.getPlayerId() ?: return Resource.Error("プレイヤー情報がありません")
        val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
        return try {
            val response = api.getCats("Bearer $token", teamOnly)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val cats = body.data
                database.catDao().insertOrUpdateAll(cats.map { it.toEntity(playerId) })
                Resource.Success(cats.map { it.toDomain() })
            } else {
                Resource.Success(loadLocalCats(playerId, teamOnly))
            }
        } catch (_: Exception) {
            Resource.Success(loadLocalCats(playerId, teamOnly))
        }
    }

    suspend fun upgradeCat(catId: String): Resource<Unit> {
        val playerId = preferences.getPlayerId() ?: return Resource.Error("プレイヤー情報がありません")
        val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
        return try {
            val response = api.upgradeCat("Bearer $token", catId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val result = body.data
                database.catDao().insertOrUpdate(result.cat.toEntity(playerId))
                database.playerDao().updateNyankoPoints(playerId, result.remainingPoints)
                Resource.Success(Unit)
            } else {
                Resource.Error(body?.message ?: "強化に失敗しました", response.code())
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }

    suspend fun setCatToTeam(catId: String, position: Int): Resource<Unit> {
        if (position !in 0..4) return Resource.Error("チーム位置は0〜4で指定してください")
        val playerId = preferences.getPlayerId() ?: return Resource.Error("プレイヤー情報がありません")
        val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
        return try {
            val response = api.setCatToTeam("Bearer $token", catId, TeamPositionRequest(position))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                database.catDao().updateTeamPosition(playerId, catId, position, true)
                Resource.Success(Unit)
            } else {
                Resource.Error(body?.message ?: "チーム配置に失敗しました", response.code())
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }

    suspend fun updateSettings(settings: PlayerSettings): Resource<Player> {
        val token = preferences.getAuthToken() ?: return Resource.Error("ログインが必要です")
        return try {
            val response = api.updateSettings("Bearer $token", settings)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                val dto = body.data
                database.playerDao().insertOrUpdate(dto.toEntity())
                Resource.Success(dto.toDomain())
            } else {
                Resource.Error(body?.message ?: "設定更新に失敗しました", response.code())
            }
        } catch (e: Exception) {
            Resource.Error("ネットワークエラー: ${e.message}")
        }
    }

    private suspend fun localPlayer(playerId: String?): Resource<Player>? {
        if (playerId == null) return null
        return database.playerDao().getPlayer(playerId)?.let { Resource.Success(it.toDomain()) }
    }

    private suspend fun loadLocalCats(playerId: String, teamOnly: Boolean): List<Cat> {
        val entities = if (teamOnly) database.catDao().getTeamCats(playerId) else database.catDao().getAllCats(playerId)
        return entities.map { it.toDomain() }
    }
}

private fun PlayerResponse.toEntity() = PlayerEntity(
    id = id,
    username = username,
    level = level,
    experience = experience,
    nyankoPoints = nyankoPoints,
    energy = energy,
    maxEnergy = maxEnergy,
    gems = gems,
    battleCount = battleCount,
    winCount = winCount,
    loseCount = loseCount,
    gachaCount = gachaCount,
    lastEnergyUpdate = parseApiTime(lastEnergyUpdate),
    createdAt = parseApiTime(createdAt)
)

private fun PlayerResponse.toDomain() = Player(
    id = id,
    username = username,
    level = level,
    experience = experience,
    nyankoPoints = nyankoPoints,
    energy = energy,
    maxEnergy = maxEnergy,
    gems = gems,
    battleCount = battleCount,
    winCount = winCount,
    loseCount = loseCount,
    gachaCount = gachaCount,
    lastEnergyUpdate = parseApiTime(lastEnergyUpdate),
    createdAt = parseApiTime(createdAt),
    settings = com.nyankowars.domain.models.PlayerSettings(
        sound = settings?.sound ?: true,
        music = settings?.music ?: true,
        notifications = settings?.notifications ?: true
    )
)

private fun PlayerEntity.toDomain() = Player(
    id = id,
    username = username,
    level = level,
    experience = experience,
    nyankoPoints = nyankoPoints,
    energy = energy,
    maxEnergy = maxEnergy,
    gems = gems,
    battleCount = battleCount,
    winCount = winCount,
    loseCount = loseCount,
    gachaCount = gachaCount,
    lastEnergyUpdate = lastEnergyUpdate,
    createdAt = createdAt
)

private fun CatResponse.toEntity(playerId: String) = CatEntity(
    playerId = playerId,
    id = id,
    name = name,
    rarity = rarity,
    level = level,
    attack = attack,
    defense = defense,
    health = health,
    speed = speed,
    criticalRate = criticalRate ?: 0.05,
    criticalDamage = criticalDamage ?: 1.5,
    element = element ?: "none",
    isFavorite = isFavorite,
    isInTeam = isInTeam,
    teamPosition = teamPosition,
    battleCount = battleCount,
    winCount = winCount,
    createdAt = parseApiTime(createdAt)
)

private fun CatResponse.toDomain() = Cat(
    id = id,
    name = name,
    rarity = rarity.toRarity(),
    type = CatType.BASIC,
    level = level,
    attack = attack,
    defense = defense,
    health = health,
    speed = speed,
    criticalRate = criticalRate ?: 0.05,
    criticalDamage = criticalDamage ?: 1.5,
    element = (element ?: "none").toElement(),
    isFavorite = isFavorite,
    isInTeam = isInTeam,
    teamPosition = teamPosition,
    battleCount = battleCount,
    winCount = winCount,
    createdAt = parseApiTime(createdAt)
)

private fun CatEntity.toDomain() = Cat(
    id = id,
    name = name,
    rarity = rarity.toRarity(),
    type = CatType.BASIC,
    level = level,
    attack = attack,
    defense = defense,
    health = health,
    speed = speed,
    criticalRate = criticalRate,
    criticalDamage = criticalDamage,
    element = element.toElement(),
    isFavorite = isFavorite,
    isInTeam = isInTeam,
    teamPosition = teamPosition,
    battleCount = battleCount,
    winCount = winCount,
    createdAt = createdAt
)

private fun String.toRarity(): Rarity = when (trim().uppercase(Locale.ROOT)) {
    "RARE", "レア" -> Rarity.RARE
    "EPIC", "激レア", "エピック" -> Rarity.EPIC
    "LEGENDARY", "超激レア", "レジェンド" -> Rarity.LEGENDARY
    "MYTHICAL", "伝説レア", "神話" -> Rarity.MYTHICAL
    else -> Rarity.NORMAL
}

private fun String.toElement(): Element = when (trim().uppercase(Locale.ROOT)) {
    "FIRE", "火" -> Element.FIRE
    "WATER", "水" -> Element.WATER
    "EARTH", "土", "地" -> Element.EARTH
    "LIGHT", "光" -> Element.LIGHT
    "DARK", "闇" -> Element.DARK
    else -> Element.NONE
}

private fun parseApiTime(value: String): Long {
    val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss.SSSX", "yyyy-MM-dd'T'HH:mm:ssX")
    for (pattern in patterns) {
        try {
            return SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.parse(value)?.time
                ?: continue
        } catch (_: Exception) {
            // Try the next supported API timestamp format.
        }
    }
    return System.currentTimeMillis()
}
