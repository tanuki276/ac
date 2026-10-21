package com.nyankowars.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
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

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(player: PlayerEntity)

    @Query("SELECT * FROM players WHERE id = :id LIMIT 1")
    suspend fun getPlayer(id: String?): PlayerEntity?

    @Query("UPDATE players SET nyankoPoints = :points WHERE id = :id")
    suspend fun updateNyankoPoints(id: String, points: Int)
}

@Dao
interface CatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cat: CatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(cats: List<CatEntity>)

    @Query("SELECT * FROM cats WHERE playerId = :playerId AND isInTeam = 1 ORDER BY teamPosition ASC")
    suspend fun getTeamCats(playerId: String?): List<CatEntity>

    @Query("SELECT * FROM cats WHERE playerId = :playerId ORDER BY createdAt DESC")
    suspend fun getAllCats(playerId: String?): List<CatEntity>

    @Query("UPDATE cats SET teamPosition = :position, isInTeam = :inTeam WHERE playerId = :playerId AND id = :catId")
    suspend fun updateTeamPosition(playerId: String, catId: String, position: Int, inTeam: Boolean)
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
