package com.nyankowars.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = modifier
            .clickable(onClick = onClick)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(getRarityColor(cat.rarity))
                .height(4.dp)
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // ヘッダー部分
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = cat.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cat.rarity.getText(),
                            style = MaterialTheme.typography.labelMedium,
                            color = getRarityColor(cat.rarity)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "レベル",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(2.dp))
                        
                        Text(
                            text = "Lv.${cat.level}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 12.sp
                        )
                        
                        if (cat.isInTeam) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF4CAF50))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "TEAM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
                
                if (showFavoriteIcon && onFavoriteClick != null) {
                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (cat.isFavorite) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "お気に入り",
                            tint = if (cat.isFavorite) Color(0xFFFF4081) else Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // タイプと要素
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(getElementColor(cat.element))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = cat.type.getIcon() + " " + cat.type.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
                
                Text(
                    text = "戦闘力: ${cat.power}",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (showStats) {
                // ステータスバー
                Column {
                    // 攻撃力
                    StatBar(
                        label = "攻撃",
                        value = cat.attack,
                        maxValue = 100,
                        color = Color(0xFFFF5722)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 防御力
                    StatBar(
                        label = "防御",
                        value = cat.defense,
                        maxValue = 100,
                        color = Color(0xFF2196F3)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // 体力
                    StatBar(
                        label = "体力",
                        value = cat.health,
                        maxValue = 200,
                        color = Color(0xFF4CAF50)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // スピード
                    StatBar(
                        label = "速度",
                        value = cat.speed.toInt(),
                        maxValue = 3,
                        color = Color(0xFFFF9800)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // バトル戦績
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "戦闘: ${cat.battleCount}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp
                    )
                    
                    Text(
                        text = "勝率: ${String.format("%.1f", cat.winRate)}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = if (cat.winRate > 50) Color(0xFF4CAF50) else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StatBar(
    label: String,
    value: Int,
    maxValue: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp
            )
            Text(
                text = "$value",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
                    .width((value.toFloat() / maxValue).coerceIn(0f, 1f) * 100f.dp)
            )
        }
    }
}

@Composable
fun getRarityColor(rarity: Rarity): Color {
    return when (rarity) {
        Rarity.NORMAL -> Color(0xFF808080)
        Rarity.RARE -> Color(0xFF1E90FF)
        Rarity.EPIC -> Color(0xFFFF4500)
        Rarity.LEGENDARY -> Color(0xFFFFD700)
        Rarity.MYTHICAL -> Color(0xFF8A2BE2)
    }
}

@Composable
fun getElementColor(element: com.nyankowars.domain.models.Element): Color {
    return when (element) {
        com.nyankowars.domain.models.Element.FIRE -> Color(0xFFFF4500)
        com.nyankowars.domain.models.Element.WATER -> Color(0xFF1E90FF)
        com.nyankowars.domain.models.Element.EARTH -> Color(0xFF8B4513)
        com.nyankowars.domain.models.Element.LIGHT -> Color(0xFFFFFF00)
        com.nyankowars.domain.models.Element.DARK -> Color(0xFF2F4F4F)
        com.nyankowars.domain.models.Element.NONE -> Color(0xFF808080)
    }
}