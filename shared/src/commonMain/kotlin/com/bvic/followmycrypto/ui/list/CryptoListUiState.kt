package com.bvic.followmycrypto.ui.list

import com.bvic.followmycrypto.model.Crypto
import com.bvic.followmycrypto.model.Currency

// Tout ce que l'écran peut afficher, et rien d'autre
sealed interface CryptoListUiState {
    data object Loading : CryptoListUiState
    data class Error(val message: String) : CryptoListUiState
    data class Success(val cryptos: List<Crypto>, val currency: Currency) : CryptoListUiState
}
