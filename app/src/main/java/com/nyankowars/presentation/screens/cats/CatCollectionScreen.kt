package com.nyankowars.presentation.screens.cats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nyankowars.domain.models.Resource
import com.nyankowars.presentation.components.CatCard
import com.nyankowars.presentation.viewmodels.PlayerViewModel

@Composable
fun CatCollectionScreen(playerViewModel: PlayerViewModel) {
    val state by playerViewModel.catsState.collectAsState()
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("にゃんこ図鑑")
        when (val value = state) {
            Resource.Loading -> CircularProgressIndicator()
            Resource.Empty -> Text("データがありません")
            is Resource.Error -> {
                Text(value.message)
                Button(onClick = playerViewModel::loadCats) { Text("再試行") }
            }
            is Resource.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(value.data, key = { it.id }) { cat ->
                    CatCard(cat = cat, onClick = {})
                }
            }
        }
    }
}
