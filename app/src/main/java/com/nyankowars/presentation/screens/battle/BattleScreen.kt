package com.nyankowars.presentation.screens.battle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nyankowars.domain.models.Resource
import com.nyankowars.presentation.viewmodels.PlayerViewModel

@Composable
fun BattleScreen(playerViewModel: PlayerViewModel) {
    val teamState by playerViewModel.teamCatsState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("バトル")
        when (val state = teamState) {
            Resource.Loading -> CircularProgressIndicator()
            Resource.Empty -> Text("チーム情報がありません")
            is Resource.Error -> Text(state.message)
            is Resource.Success -> {
                Text("現在のチーム: ${state.data.size}/5体")
                state.data.forEach { cat -> Text("${(cat.teamPosition ?: 0) + 1}. ${cat.name} Lv.${cat.level}") }
                if (state.data.isEmpty()) Text("にゃんこ画面からチームを編成してください")
            }
        }
        Button(onClick = playerViewModel::loadTeamCats) { Text("チームを更新") }
        Text("バトル本体は次の安定化対象です。現在は不正な空編成で開始しないようサーバー側を修正済みです。")
    }
}
