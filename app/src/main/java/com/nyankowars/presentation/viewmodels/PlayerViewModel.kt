package com.nyankowars.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyankowars.data.repository.PlayerRepository
import com.nyankowars.domain.models.Cat
import com.nyankowars.domain.models.Player
import com.nyankowars.domain.models.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository
) : ViewModel() {
    private val _playerState = MutableStateFlow<Resource<Player>>(Resource.Loading)
    val playerState: StateFlow<Resource<Player>> = _playerState.asStateFlow()

    private val _catsState = MutableStateFlow<Resource<List<Cat>>>(Resource.Loading)
    val catsState: StateFlow<Resource<List<Cat>>> = _catsState.asStateFlow()

    private val _teamCatsState = MutableStateFlow<Resource<List<Cat>>>(Resource.Loading)
    val teamCatsState: StateFlow<Resource<List<Cat>>> = _teamCatsState.asStateFlow()

    private val _upgradeState = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val upgradeState: StateFlow<Resource<Unit>> = _upgradeState.asStateFlow()

    private val _teamUpdateState = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val teamUpdateState: StateFlow<Resource<Unit>> = _teamUpdateState.asStateFlow()

    init {
        refreshAll()
    }

    fun loadPlayerData() {
        viewModelScope.launch {
            _playerState.value = Resource.Loading
            _playerState.value = playerRepository.getPlayerData()
        }
    }

    fun loadCats() {
        viewModelScope.launch {
            _catsState.value = Resource.Loading
            _catsState.value = playerRepository.getCats(false)
        }
    }

    fun loadTeamCats() {
        viewModelScope.launch {
            _teamCatsState.value = Resource.Loading
            _teamCatsState.value = playerRepository.getCats(true)
        }
    }

    fun upgradeCat(catId: String) {
        viewModelScope.launch {
            _upgradeState.value = Resource.Loading
            val result = playerRepository.upgradeCat(catId)
            _upgradeState.value = result
            if (result is Resource.Success) refreshAll()
        }
    }

    fun setCatToTeam(catId: String, position: Int) {
        viewModelScope.launch {
            _teamUpdateState.value = Resource.Loading
            val result = playerRepository.setCatToTeam(catId, position)
            _teamUpdateState.value = result
            if (result is Resource.Success) {
                loadCats()
                loadTeamCats()
            }
        }
    }

    fun refreshAll() {
        loadPlayerData()
        loadCats()
        loadTeamCats()
    }

    fun clearStates() {
        _upgradeState.value = Resource.Empty
        _teamUpdateState.value = Resource.Empty
    }
}
