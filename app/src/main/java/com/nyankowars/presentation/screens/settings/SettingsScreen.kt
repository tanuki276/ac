package com.nyankowars.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nyankowars.domain.models.Resource
import com.nyankowars.presentation.viewmodels.PlayerViewModel

@Composable
fun SettingsScreen(playerViewModel: PlayerViewModel) {
    val playerState by playerViewModel.playerState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("設定")
        when (val state = playerState) {
            is Resource.Success -> {
                Text("サウンド: ${if (state.data.settings.sound) "ON" else "OFF"}")
                Text("音楽: ${if (state.data.settings.music) "ON" else "OFF"}")
                Text("通知: ${if (state.data.settings.notifications) "ON" else "OFF"}")
            }
            is Resource.Error -> Text(state.message)
            else -> Unit
        }
        Button(onClick = playerViewModel::loadPlayerData) { Text("設定を再読み込み") }
    }
}
