package com.bvic.followmycrypto.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoriRepository{
    private val _favs = MutableStateFlow<Set<String>>(emptySet())
    val favs: StateFlow<Set<String>> = _favs.asStateFlow()

    fun toggle(symbol: String){
        _favs.update {
            if(symbol in it) {
                it-symbol
            }else{
                it+symbol
            }
        }
    }
}