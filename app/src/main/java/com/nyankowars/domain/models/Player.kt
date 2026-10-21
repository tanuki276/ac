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
        if (energy >= maxEnergy) return maxEnergy to 0L

        val now = System.currentTimeMillis()
        val elapsedSeconds = ((now - lastEnergyUpdate).coerceAtLeast(0L) / 1000L)
        val regenerated = (elapsedSeconds / 60L).toInt()
        val newEnergy = (energy + regenerated).coerceAtMost(maxEnergy)

        if (newEnergy >= maxEnergy) return newEnergy to 0L

        val remainderSeconds = elapsedSeconds % 60L
        val secondsUntilNextEnergy = if (remainderSeconds == 0L) 60L else 60L - remainderSeconds
        val missingAfterNext = (maxEnergy - newEnergy - 1).coerceAtLeast(0)
        val timeToFullMs = (secondsUntilNextEnergy + missingAfterNext * 60L) * 1000L
        return newEnergy to timeToFullMs
    }
}

data class PlayerSettings(
    val sound: Boolean = true,
    val music: Boolean = true,
    val notifications: Boolean = true
)
