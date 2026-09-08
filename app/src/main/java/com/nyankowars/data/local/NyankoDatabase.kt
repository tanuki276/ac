package com.nyankowars.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val username: String,
    val level: Int,
    val experience: Int,
    val nyankoPoints: Int,
    val energy: Int,
    val maxEnergy: Int,
    val gems: Int,
    val battleCount: Int,
    val winCount: Int,
    val loseCount: Int,
    val gachaCount: Int,
    val lastEnergyUpdate: Long,
    val createdAt: Long
)

@Entity(tableName = "cats", primaryKeys = ["playerId", "id"])
data class CatEntity(
    val playerId: String,
    val id: String,
    val name: String,
    val rarity: String,
    val level: Int,
    val attack: Int,
    val defense: Int,
    val health: Int,
    val speed: Double,
    val criticalRate: Double,
    val criticalDamage: Double,
    val element: String,
    val isFavorite: Boolean,
    val isInTeam: Boolean,
    val teamPosition: Int?,
    val battleCount: Int,
    val winCount: Int,
    val createdAt: Long
)

class PlayerDao {
    fun insertOrUpdate(player: PlayerEntity) = Unit
    fun getPlayer(id: String?): PlayerEntity? = null
    fun updateNyankoPoints(id: String, points: Int) = Unit
}

class CatDao {
    fun insertOrUpdate(cat: CatEntity) = Unit
    fun insertOrUpdateAll(cats: List<CatEntity>) = Unit
    fun getTeamCats(playerId: String?): List<CatEntity> = emptyList()
    fun getAllCats(playerId: String?): List<CatEntity> = emptyList()
    fun updateTeamPosition(playerId: String, catId: String, position: Int, inTeam: Boolean) = Unit
}

@Database(
    entities = [PlayerEntity::class, CatEntity::class],
    version = 1,
    exportSchema = false
)
abstract class NyankoDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun catDao(): CatDao
}
