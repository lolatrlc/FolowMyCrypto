package com.bvic.followmycrypto.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bvic.followmycrypto.data.CryptoRepository
import com.bvic.followmycrypto.model.Currency
import com.bvic.followmycrypto.data.FavoriRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CryptoListViewModel(
    private val repository: CryptoRepository,
    private val favRepo: FavoriRepository,
) : ViewModel() {
    private val _favTri = MutableStateFlow<Boolean>(false)
    // Version modifiable, privée : seul le ViewModel peut changer l'état
    private val _loadState = MutableStateFlow<CryptoListUiState>(CryptoListUiState.Loading)

    // Version en lecture seule, exposée à l'interface
    val uiState: StateFlow<CryptoListUiState> = _uiState.asStateFlow()
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

    fun toggleFavori(symbol: String) = favRepo.toggle(symbol)

    fun toggleFiltre() = _favTri.update { !it }
    private fun load() {
        _loadState.value = CryptoListUiState.Loading
        viewModelScope.launch {
            try {
                _loadState.value = CryptoListUiState.Success(repository.getCryptos(currency), currency)
            } catch (e: Exception) {
                _loadState.value = CryptoListUiState.Error("Impossible de charger les cours.")
            }
        }
    }

    fun changeCurrency(newCurrency: Currency){
        currency = newCurrency
        load()
    }
}
}
