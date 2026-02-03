package com.nyankowars.presentation.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.nyankowars.R
import com.nyankowars.domain.models.Resource
import com.nyankowars.presentation.components.CatCard
import com.nyankowars.presentation.components.LoadingScreen
import com.nyankowars.presentation.components.QuickActionButton
import com.nyankowars.presentation.screens.Screen
import com.nyankowars.presentation.theme.PinkPrimary
import com.nyankowars.presentation.theme.PurplePrimary
import com.nyankowars.presentation.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel = hiltViewModel(),
    navController: NavHostController? = null
) {
    val playerState by playerViewModel.playerState.observeAsState()
    val catsState by playerViewModel.catsState.observeAsState()
    val teamCatsState by playerViewModel.teamCatsState.observeAsState()
    
    var refreshing by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        playerViewModel.refreshAll()
    }
    
    when (playerState) {
        is Resource.Loading -> {
            LoadingScreen()
        }
        is Resource.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "エラーが発生しました",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { playerViewModel.refreshAll() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "再試行"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("再読み込み")
                    }
                }
            }
        }
        is Resource.Success -> {
            val player = (playerState as Resource.Success).data
            val cats = (catsState as? Resource.Success)?.data ?: emptyList()
            val teamCats = (teamCatsState as? Resource.Success)?.data ?: emptyList()
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // プレイヤー情報カード
                    PlayerInfoCard(
                        player = player,
                        onRefresh = {
                            refreshing = true
                            playerViewModel.refreshAll()
                            // アニメーション用の遅延
                            kotlinx.coroutines.delay(500) {
                                refreshing = false
                            }
                        },
                        refreshing = refreshing
                    )
                }
                
                item {
                    // クイックアクション
                    QuickActionsRow(
                        onBattleClick = { navController?.navigate(Screen.Battle.route) },
                        onGachaClick = { navController?.navigate("gacha") },
                        onCatsClick = { navController?.navigate(Screen.Cats.route) },
                        onShopClick = { navController?.navigate(Screen.Shop.route) }
                    )
                }
                
                if (teamCats.isNotEmpty()) {
                    item {
                        // チームにゃんこ
                        TeamCatsSection(
                            cats = teamCats,
                            onCatClick = { catId ->
                                navController?.navigate("cat_detail/$catId")
                            }
                        )
                    }
                }
                
                if (cats.isNotEmpty()) {
                    item {
                        // 最近の獲得にゃんこ
                        RecentCatsSection(
                            cats = cats.take(3),
                            onCatClick = { catId ->
                                navController?.navigate("cat_detail/$catId")
                            }
                        )
                    }
                }
                
                item {
                    // クエストとアチーブメント
                    QuestsAndAchievementsSection(
                        onQuestsClick = { navController?.navigate("quests") },
                        onAchievementsClick = { navController?.navigate("achievements") }
                    )
                }
                
                item {
                    // お知らせ
                    NewsSection()
                }
            }
        }
        else -> {
            // Empty state
        }
    }
}

@Composable
fun PlayerInfoCard(
    player: com.nyankowars.domain.models.Player,
    onRefresh: () -> Unit,
    refreshing: Boolean
) {
    val (currentEnergy, timeToFull) = player.calculateRegainedEnergy()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurplePrimary.copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // ヘッダー
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = player.username,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        fontSize = 22.sp
                    )
                    
                    Text(
                        text = "冒険者 Lv.${player.level}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                
                IconButton(
                    onClick = onRefresh,
                    enabled = !refreshing
                ) {
                    if (refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "更新",
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 経験値バー
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "経験値",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${player.experience}/${player.expNeededForNextLevel}",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // 経験値バー
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(PinkPrimary)
                            .fillMaxWidth(player.expProgress)
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "次のレベルまで: ${player.expNeededForNextLevel - player.experience}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // リソース
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // にゃんこポイント
                ResourceItem(
                    icon = Icons.Default.Paid,
                    value = "${player.nyankoPoints}",
                    label = "にゃんこポイント",
                    color = Color(0xFFFFD700)
                )
                
                // ジェム
                ResourceItem(
                    icon = Icons.Default.Star,
                    value = "${player.gems}",
                    label = "ジェム",
                    color = Color(0xFF00BCD4)
                )
                
                // エネルギー
                ResourceItem(
                    icon = Icons.Default.Bolt,
                    value = "${currentEnergy}/${player.maxEnergy}",
                    label = "エネルギー",
                    color = Color(0xFF4CAF50),
                    subtitle = if (timeToFull > 0) {
                        val minutes = (timeToFull / (60 * 1000)).toInt()
                        "${minutes}分で回復"
                    } else {
                        "最大"
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 戦績
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatItem(
                    icon = Icons.Default.Gamepad,
                    value = "${player.battleCount}",
                    label = "総バトル"
                )
                
                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = String.format("%.1f", player.winRate) + "%",
                    label = "勝率"
                )
                
                StatItem(
                    icon = Icons.Default.LibraryAdd,
                    value = "${player.gachaCount}",
                    label = "ガチャ回数"
                )
            }
        }
    }
}

@Composable
fun ResourceItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    subtitle: String? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 10.sp
        )
    }
}

@Composable
fun QuickActionsRow(
    onBattleClick: () -> Unit,
    onGachaClick: () -> Unit,
    onCatsClick: () -> Unit,
    onShopClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            icon = Icons.Default.Battle,
            text = "バトル",
            color = Color(0xFFFF5722),
            onClick = onBattleClick
        )
        
        QuickActionButton(
            icon = Icons.Default.Star,
            text = "ガチャ",
            color = Color(0xFFFFD700),
            onClick = onGachaClick
        )
        
        QuickActionButton(
            icon = Icons.Default.Pets,
            text = "にゃんこ",
            color = Color(0xFF2196F3),
            onClick = onCatsClick
        )
        
        QuickActionButton(
            icon = Icons.Default.ShoppingCart,
            text = "ショップ",
            color = Color(0xFF4CAF50),
            onClick = onShopClick
        )
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(color.copy(alpha = 0.1f))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Composable
fun TeamCatsSection(
    cats: List<com.nyankowars.domain.models.Cat>,
    onCatClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "チームにゃんこ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = "編成を変更",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (cats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "にゃんこをチームに配置しましょう！",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cats.forEach { cat ->
                        TeamCatItem(
                            cat = cat,
                            onClick = { onCatClick(cat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamCatItem(
    cat: com.nyankowars.domain.models.Cat,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(getRarityColor(cat.rarity))
        ) {
            // にゃんこアイコンを表示（実装は後で）
            Icon(
                painter = painterResource(id = R.drawable.ic_cat_default),
                contentDescription = cat.name,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = cat.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = "レベル",
                modifier = Modifier.size(12.dp),
                tint = Color(0xFFFFD700)
            )
            
            Spacer(modifier = Modifier.width(2.dp))
            
            Text(
                text = "Lv.${cat.level}",
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun RecentCatsSection(
    cats: List<com.nyankowars.domain.models.Cat>,
    onCatClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最近の獲得にゃんこ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "もっと見る",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (cats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "まだにゃんこがいません",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    cats.forEach { cat ->
                        CatCard(
                            cat = cat,
                            onClick = { onCatClick(cat.id) },
                            showFavoriteIcon = false,
                            showStats = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestsAndAchievementsSection(
    onQuestsClick: () -> Unit,
    onAchievementsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // クエストカード
        Card(
            modifier = Modifier.weight(1f),
            onClick = onQuestsClick
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_quest),
                    contentDescription = "クエスト",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "クエスト",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "3件完了待ち",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // アチーブメントカード
        Card(
            modifier = Modifier.weight(1f),
            onClick = onAchievementsClick
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_achievement),
                    contentDescription = "アチーブメント",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "アチーブメント",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "5/20 達成",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun NewsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "お知らせ",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // ニュースアイテム
            NewsItem(
                title = "新章「天空の試練」実装！",
                date = "2024.01.15",
                description = "第5章「天空の試練」が追加されました。新しいにゃんこやボスが登場！"
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            NewsItem(
                title = "バレンタインイベント開催中",
                date = "2024.02.01",
                description = "期間限定イベントで特別なにゃんこをゲットしよう！"
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            NewsItem(
                title = "バグ修正と改善",
                date = "2024.01.30",
                description = "一部のバトルバランス調整とUI改善を行いました。"
            )
        }
    }
}

@Composable
fun NewsItem(
    title: String,
    date: String,
    description: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = date,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}