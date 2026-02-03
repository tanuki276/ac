package com.nyankowars.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nyankowars.domain.models.Player
import com.nyankowars.domain.models.Resource
import com.nyankowars.domain.repository.PlayerRepository
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
    
    private val _catsState = MutableStateFlow<Resource<List<com.nyankowars.domain.models.Cat>>>(Resource.Loading)
    val catsState: StateFlow<Resource<List<com.nyankowars.domain.models.Cat>>> = _catsState.asStateFlow()
    
    private val _teamCatsState = MutableStateFlow<Resource<List<com.nyankowars.domain.models.Cat>>>(Resource.Loading)
    val teamCatsState: StateFlow<Resource<List<com.nyankowars.domain.models.Cat>>> = _teamCatsState.asStateFlow()
    
    private val _upgradeState = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val upgradeState: StateFlow<Resource<Unit>> = _upgradeState.asStateFlow()
    
    private val _teamUpdateState = MutableStateFlow<Resource<Unit>>(Resource.Empty)
    val teamUpdateState: StateFlow<Resource<Unit>> = _teamUpdateState.asStateFlow()
    
    init {
        loadPlayerData()
        loadCats()
        loadTeamCats()
    }
    
    fun loadPlayerData() {
        viewModelScope.launch {
            _playerState.value = Resource.Loading
            val result = playerRepository.getPlayerData()
            _playerState.value = result.map { it.toDomain() }
        }
    }
    
    fun loadCats() {
        viewModelScope.launch {
            _catsState.value = Resource.Loading
            val result = playerRepository.getCats(false)
            _catsState.value = result.map { cats -> cats.map { it.toDomain() } }
        }
    }
    
    fun loadTeamCats() {
        viewModelScope.launch {
            _teamCatsState.value = Resource.Loading
            val result = playerRepository.getCats(true)
            _teamCatsState.value = result.map { cats -> cats.map { it.toDomain() } }
        }
    }
    
    fun upgradeCat(catId: String) {
        viewModelScope.launch {
            _upgradeState.value = Resource.Loading
            val result = playerRepository.upgradeCat(catId)
            _upgradeState.value = result.map { 
                loadPlayerData()
                loadCats()
                loadTeamCats()
                Unit
            }
        }
    }
    
    fun setCatToTeam(catId: String, position: Int) {
        viewModelScope.launch {
            _teamUpdateState.value = Resource.Loading
            val result = playerRepository.setCatToTeam(catId, position)
            _teamUpdateState.value = result.map { 
                loadCats()
                loadTeamCats()
                Unit
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