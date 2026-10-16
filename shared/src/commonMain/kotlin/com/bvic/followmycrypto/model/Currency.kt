package com.bvic.followmycrypto.model


enum class Currency(val suffixeCurrency: String, val symbol: String) {
    DOLLAR("USDT", "$"),
    EURO("EUR", "€")
}