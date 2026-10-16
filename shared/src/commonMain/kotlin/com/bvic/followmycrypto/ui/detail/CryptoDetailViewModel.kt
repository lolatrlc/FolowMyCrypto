package com.bvic.followmycrypto.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.followmycrypto.data.CryptoRepository
import com.bvic.followmycrypto.model.Currency
import com.bvic.followmycrypto.data.FavoriRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CryptoDetailViewModel(
    private val symbol: String,
    private val currency: Currency,
    private val repository: CryptoRepository,
    private val favRepo: FavoriRepository,
) : ViewModel() {

    private val _loadState = MutableStateFlow<CryptoDetailUiState>(CryptoDetailUiState.Loading)

    val uiState: StateFlow<CryptoDetailUiState> = combine(
        _loadState,
        favRepo.favs,
    ) { chargement, favs ->
        if (chargement is CryptoDetailUiState.Success) {
            chargement.copy(estFavori = symbol in favs)
        } else {
            chargement
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CryptoDetailUiState.Loading)

    init {
        load()
    }

    fun retry() = load()

    fun toggleFavori() = favRepo.toggle(symbol)

    private fun load() {
        _loadState.value = CryptoDetailUiState.Loading
        viewModelScope.launch {
            try {
                _loadState.value = CryptoDetailUiState.Success(repository.getCrypto(symbol, currency), currency)
            } catch (e: Exception) {
                _loadState.value = CryptoDetailUiState.Error("Impossible de charger $symbol.")
            }
        }
    }
}