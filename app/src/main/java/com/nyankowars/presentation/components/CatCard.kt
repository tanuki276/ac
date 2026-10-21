package com.nyankowars.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nyankowars.domain.models.Cat
import com.nyankowars.domain.models.Rarity

@Composable
fun CatCard(
    cat: Cat,
    onClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
    showFavoriteIcon: Boolean = true,
    showStats: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cat.name, fontWeight = FontWeight.Bold, color = getRarityColor(cat.rarity))
                    Text("Lv.${cat.level} · ${cat.rarity.getText()}", style = MaterialTheme.typography.bodySmall)
                }
                if (showFavoriteIcon && onFavoriteClick != null) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (cat.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "お気に入り"
                        )
                    }
                }
            }
            if (showStats) {
                Text("HP ${cat.health} / ATK ${cat.attack} / DEF ${cat.defense}")
                Text("戦闘力 ${cat.power}", style = MaterialTheme.typography.bodySmall)
            }
            if (cat.isInTeam) Text("チーム ${((cat.teamPosition ?: 0) + 1)}番", style = MaterialTheme.typography.labelMedium)
        }
    }
}

fun getRarityColor(rarity: Rarity): Color = Color(rarity.getColor())
