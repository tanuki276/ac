package com.nyankowars.domain.models

val Skill.damage: Int
    get() = effectValue.toIntOrNull() ?: 0
