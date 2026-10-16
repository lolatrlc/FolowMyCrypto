package com.bvic.followmycrypto.data

import com.bvic.followmycrypto.data.remote.BinanceApi
import com.bvic.followmycrypto.data.remote.TickerDto
import com.bvic.followmycrypto.model.Crypto
import com.bvic.followmycrypto.model.Currency

// Les cryptos suivies : la paire à demander à Binance, et le nom à afficher (Binance ne le fournit pas)
private val cryptoNames = mapOf(
    "BTC" to "Bitcoin",
    "ETH" to "Ethereum",
    "BNB" to "BNB",
    "SOL" to "Solana",
    "XRP" to "XRP",
    "ADA" to "Cardano",
    "DOGE" to "Dogecoin",
    "SHIB" to "Shiba Inu",
)

class RemoteCryptoRepository(private val api: BinanceApi) : CryptoRepository {

    override suspend fun getCryptos(currency: Currency): List<Crypto> {

        // 1. Les paires à demander
        //val pairs = cryptoNames.keys.toList()
        val pairs = cryptoNames.keys.map { base -> base + currency.suffixeCurrency }
        // 2. L'appel à l'API
        val tickers = api.getTickers(pairs)
        // 3. La conversion de chaque DTO en modèle
        return tickers.map { ticker -> ticker.toCrypto(currency) }
    }

    override suspend fun getCrypto(symbol: String, currency: Currency): Crypto {
        val tickers = api.getTickers(listOf(symbol + currency.suffixeCurrency))
        return tickers.first().toCrypto(currency)
    }
}

// DTO (ce que l'API envoie) → modèle (ce dont l'application a besoin)
private fun TickerDto.toCrypto(currency: Currency): Crypto {
    val nom = symbol.removeSuffix(currency.suffixeCurrency)
    return Crypto(
        symbol = nom,
        name = cryptoNames[nom] ?: nom,
        price = lastPrice.toDouble(),
        changePercent = priceChangePercent.toDouble(),
        highPrice = highPrice.toDouble(),
        lowPrice = lowPrice.toDouble(),
    )
}
