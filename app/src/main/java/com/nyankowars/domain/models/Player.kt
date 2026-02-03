package com.nyankowars.domain.models

data class Player(
    val id: String,
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
    val createdAt: Long,
    val settings: PlayerSettings = PlayerSettings()
) {
    val winRate: Double
        get() = if (battleCount > 0) winCount.toDouble() / battleCount * 100 else 0.0
    
    val expNeededForNextLevel: Int
        get() = level * 100
    
    val expProgress: Float
        get() = if (expNeededForNextLevel > 0) experience.toFloat() / expNeededForNextLevel else 0f
    
    fun calculateRegainedEnergy(): Pair<Int, Long> {
        val now = System.currentTimeMillis()
        val timeDiff = (now - lastEnergyUpdate) / 1000 // 秒単位
        val energyRegen = (timeDiff * 1).toInt() // 1 per minute
        val newEnergy = (energy + energyRegen).coerceAtMost(maxEnergy)
        val timeToFull = if (newEnergy < maxEnergy) {
            ((maxEnergy - newEnergy) * 60 * 1000).toLong()
        } else {
            0L
        }
        return newEnergy to timeToFull
    }
}

data class PlayerSettings(
    val sound: Boolean = true,
    val music: Boolean = true,
    val notifications: Boolean = true
)