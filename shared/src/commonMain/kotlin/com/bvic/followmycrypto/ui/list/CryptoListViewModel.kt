package com.bvic.followmycrypto.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.followmycrypto.data.CryptoRepository
import com.bvic.followmycrypto.model.Currency
import com.bvic.followmycrypto.data.FavoriRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CryptoListViewModel(
    private val repository: CryptoRepository,
) : ViewModel() {

    // Version modifiable, privée : seul le ViewModel peut changer l'état
    private val _uiState = MutableStateFlow<CryptoListUiState>(CryptoListUiState.Loading)

    // Version en lecture seule, exposée à l'interface
    var currency = Currency.DOLLAR
        private set //pour que seul le ViewModel puisse changer état
    val uiState: StateFlow<CryptoListUiState> = combine(
        _loadState,
        favRepo.favs,
        _favTri,
    ) { chargement, favs, filtre -> if (chargement is CryptoListUiState.Success) {
            chargement.copy(
                cryptos = if (filtre) chargement.cryptos.filter { it.symbol in favs } else chargement.cryptos,
                favoris = favs,
                filtreActif = filtre,
            )
        } else {
            chargement
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CryptoListUiState.Loading)


    init {
        load()
    }

    fun refresh() = load()

    private fun load() {
        _uiState.value = CryptoListUiState.Loading
        viewModelScope.launch {
            try {
                _loadState.value = CryptoListUiState.Success(repository.getCryptos(currency), currency)
            } catch (e: Exception) {
                _uiState.value = CryptoListUiState.Error("Impossible de charger les cours.")
            }
        }
    }

    fun changeCurrency(newCurrency: Currency){
        currency = newCurrency
        load()
    }
}
