package com.nyankowars.domain.models

enum class Rarity {
    NORMAL, RARE, EPIC, LEGENDARY, MYTHICAL;
    
    fun getColor(): Long {
        return when (this) {
            NORMAL -> 0xFF808080
            RARE -> 0xFF1E90FF
            EPIC -> 0xFFFF4500
            LEGENDARY -> 0xFFFFD700
            MYTHICAL -> 0xFF8A2BE2
        }
    }
    
    fun getText(): String {
        return when (this) {
            NORMAL -> "ノーマル"
            RARE -> "レア"
            EPIC -> "エピック"
            LEGENDARY -> "レジェンド"
            MYTHICAL -> "神話"
        }
    }
}

enum class CatType {
    BASIC, WARRIOR, MAGE, TANK, ASSASSIN, HEALER, DRAGON, ANGEL,
    PIRATE, ROBOT, UNDEAD, CYBORG, ROYAL, VAMPIRE, PRIEST,
    GIANT, PHOENIX, CYCLOPS, BEAST, GOLEM;
    
    fun getIcon(): String {
        return when (this) {
            BASIC -> "😺"
            WARRIOR -> "⚔️"
            MAGE -> "🔮"
            TANK -> "🛡️"
            ASSASSIN -> "🗡️"
            HEALER -> "❤️"
            DRAGON -> "🐉"
            ANGEL -> "👼"
            PIRATE -> "🏴‍☠️"
            ROBOT -> "🤖"
            UNDEAD -> "💀"
            CYBORG -> "🔧"
            ROYAL -> "👑"
            VAMPIRE -> "🧛"
            PRIEST -> "🙏"
            GIANT -> "👹"
            PHOENIX -> "🔥"
            CYCLOPS -> "👁️"
            BEAST -> "🐺"
            GOLEM -> "🗿"
        }
    }
}

enum class Element {
    FIRE, WATER, EARTH, LIGHT, DARK, NONE;
    
    fun getColor(): Long {
        return when (this) {
            FIRE -> 0xFFFF4500
            WATER -> 0xFF1E90FF
            EARTH -> 0xFF8B4513
            LIGHT -> 0xFFFFFF00
            DARK -> 0xFF2F4F4F
            NONE -> 0xFF808080
        }
    }
    
    fun getEffectivenessAgainst(other: Element): Double {
        return when (this) {
            FIRE -> if (other == EARTH) 1.5 else if (other == WATER) 0.5 else 1.0
            WATER -> if (other == FIRE) 1.5 else if (other == EARTH) 0.5 else 1.0
            EARTH -> if (other == WATER) 1.5 else if (other == FIRE) 0.5 else 1.0
            LIGHT -> if (other == DARK) 1.5 else if (other == LIGHT) 0.5 else 1.0
            DARK -> if (other == LIGHT) 1.5 else if (other == DARK) 0.5 else 1.0
            NONE -> 1.0
        }
    }
}

data class Cat(
    val id: String,
    val name: String,
    val rarity: Rarity,
    val type: CatType,
    val level: Int,
    val attack: Int,
    val defense: Int,
    val health: Int,
    val currentHealth: Int = health,
    val speed: Double,
    val criticalRate: Double = 0.05,
    val criticalDamage: Double = 1.5,
    val element: Element = Element.NONE,
    val skills: List<Skill> = emptyList(),
    val isFavorite: Boolean = false,
    val isInTeam: Boolean = false,
    val teamPosition: Int? = null,
    val battleCount: Int = 0,
    val winCount: Int = 0,
    val createdAt: Long
) {
    val power: Int
        get() = (attack * 2 + defense * 1.5 + health * 0.5 + speed * 50 + level * 10).toInt()
    
    val winRate: Double
        get() = if (battleCount > 0) winCount.toDouble() / battleCount * 100 else 0.0
    
    val isAlive: Boolean
        get() = currentHealth > 0
    
    val healthPercentage: Float
        get() = currentHealth.toFloat() / health
    
    fun takeDamage(damage: Int): Cat {
        val newHealth = (currentHealth - damage).coerceAtLeast(0)
        return copy(currentHealth = newHealth)
    }
    
    fun heal(amount: Int): Cat {
        val newHealth = (currentHealth + amount).coerceAtMost(health)
        return copy(currentHealth = newHealth)
    }
    
    fun calculateDamage(
        target: Cat,
        skill: Skill? = null,
        isCritical: Boolean = false
    ): Int {
        val baseDamage = skill?.damage ?: attack
        val elementMultiplier = element.getEffectivenessAgainst(target.element)
        val criticalMultiplier = if (isCritical) criticalDamage else 1.0
        val levelMultiplier = 1.0 + (level - target.level) * 0.02
        
        val rawDamage = baseDamage * elementMultiplier * criticalMultiplier * levelMultiplier
        val defenseReduction = target.defense * 0.01
        
        return (rawDamage * (1.0 - defenseReduction)).toInt().coerceAtLeast(1)
    }
    
    fun canUseSkill(skill: Skill): Boolean {
        // TODO: MPなどの実装
        return true
    }
}

data class Skill(
    val id: String,
    val name: String,
    val type: SkillType,
    val effectValue: String,
    val mpCost: Int,
    val description: String,
    val target: TargetType,
    val activationRate: Int = 100,
    val cooldown: Int = 0,
    val currentCooldown: Int = 0
) {
    val isOnCooldown: Boolean
        get() = currentCooldown > 0
    
    fun reduceCooldown(): Skill {
        return copy(currentCooldown = (currentCooldown - 1).coerceAtLeast(0))
    }
    
    fun resetCooldown(): Skill {
        return copy(currentCooldown = cooldown)
    }
}

enum class SkillType {
    ATTACK, BUFF, HEAL, DEBUFF, DOT, COUNTER, REVIVE, SPECIAL;
    
    fun getColor(): Long {
        return when (this) {
            ATTACK -> 0xFFFF0000
            BUFF -> 0xFF00FF00
            HEAL -> 0xFF00FFFF
            DEBUFF -> 0xFFFFA500
            DOT -> 0xFF800080
            COUNTER -> 0xFFFFD700
            REVIVE -> 0xFFFFFFFF
            SPECIAL -> 0xFF9400D3
        }
    }
}

enum class TargetType {
    SELF, SINGLE_ALLY, SINGLE_ENEMY, ALL_ALLIES, ALL_ENEMIES, RANDOM_ENEMY;
    
    fun getDescription(): String {
        return when (this) {
            SELF -> "自分自身"
            SINGLE_ALLY -> "味方単体"
            SINGLE_ENEMY -> "敵単体"
            ALL_ALLIES -> "味方全体"
            ALL_ENEMIES -> "敵全体"
            RANDOM_ENEMY -> "敵ランダム"
        }
    }
}