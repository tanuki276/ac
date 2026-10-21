package com.nyankowars.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.nyankowars.domain.models.Player
import com.nyankowars.domain.models.Resource
import com.nyankowars.presentation.screens.Screen
import com.nyankowars.presentation.viewmodels.PlayerViewModel

@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel,
    navController: NavHostController
) {
    val playerState by playerViewModel.playerState.collectAsState()
    val catsState by playerViewModel.catsState.collectAsState()
    val teamState by playerViewModel.teamCatsState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("NyankoWars", style = MaterialTheme.typography.headlineMedium)

        when (val state = playerState) {
            Resource.Loading -> CircularProgressIndicator()
            Resource.Empty -> Text("プレイヤー情報を読み込めませんでした")
            is Resource.Error -> ErrorCard(state.message) { playerViewModel.refreshAll() }
            is Resource.Success -> PlayerCard(state.data)
        }

        val catsCount = (catsState as? Resource.Success)?.data?.size
        val teamCount = (teamState as? Resource.Success)?.data?.size
        if (catsCount != null || teamCount != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("編成状況", style = MaterialTheme.typography.titleMedium)
                    Text("所持にゃんこ: ${catsCount ?: 0}体")
                    Text("チーム: ${teamCount ?: 0}/5体")
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { navController.navigate(Screen.Cats.route) }) { Text("にゃんこ") }
            Button(onClick = { navController.navigate(Screen.Battle.route) }) { Text("バトル") }
        }
        Button(onClick = { playerViewModel.refreshAll() }) { Text("再読み込み") }
    }
}

@Composable
private fun PlayerCard(player: Player) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(player.username, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(6.dp))
            Text("Lv.${player.level}  EXP ${player.experience}/${player.expNeededForNextLevel}")
            Text("NP ${player.nyankoPoints}  エネルギー ${player.energy}/${player.maxEnergy}")
            Text("戦績 ${player.winCount}勝 ${player.loseCount}敗 (${String.format("%.1f", player.winRate)}%)")
        }
    }
}

@Composable
private fun ErrorCard(message: String, retry: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(message)
            Spacer(Modifier.height(8.dp))
            Button(onClick = retry) { Text("再試行") }
        }
    }
}
