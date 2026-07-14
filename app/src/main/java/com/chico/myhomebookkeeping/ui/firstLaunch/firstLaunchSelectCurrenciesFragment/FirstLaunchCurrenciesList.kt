package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import com.chico.myhomebookkeeping.db.entity.Currencies

object FirstLaunchCurrenciesList {
    private val baseCurrenciesList = listOf(
        Currencies("Australian dollar", "A\$", "AUD", null, false),
        Currencies("Azərbaycan manatı", "man.", "AZN", null, false),
        Currencies("Bulgarian lev", "лв", "BGN", null, false),
        Currencies("Brazilian real", "R\$", "BRL", null, false),
        Currencies("Беларускі рубель", "Br.", "BYN", null, false),
        Currencies("Canadian dollar", "C\$", "CAD", null, false),
        Currencies("Franc suisse", "CHF", "CHF", null, false),
        Currencies("Chinese yuan", "¥", "CNY", null, false),
        Currencies("Czech koruna", "Kč", "CZK", null, false),
        Currencies("Danish krone", "kr.", "DKK", null, false),
        Currencies("United Arab Emirates dirham", "د.إ", "AED", null, false),
        Currencies("Euro", "€", "EUR", null, false),
        Currencies("Pound sterling", "£", "GBP", null, false),
        Currencies("ლარი", "ლ", "GEL", null, false),
        Currencies("Hong Kong dollar", "HK\$", "HKD", null, false),
        Currencies("Forint", "Ft", "HUF", null, false),
        Currencies("Հայկական դրամ", "֏", "AMD", null, false),
        Currencies("Indian rupee", "₹", "INR", null, false),
        Currencies("Israeli new shekel", "₪", "ILS", null, false),
        Currencies("Japanese yen", "¥", "JPY", null, false),
        Currencies("Сом", "с", "KGS", null, false),
        Currencies("South Korean won", "₩", "KRW", null, false),
        Currencies("Теңге", "₸", "KZT", null, false),
        Currencies("Leu moldovenesc", "L", "MDL", null, false),
        Currencies("Mexican peso", "Mex\$", "MXN", null, false),
        Currencies("Norwegian krone", "kr", "NOK", null, false),
        Currencies("New Zealand dollar", "NZ\$", "NZD", null, false),
        Currencies("Złoty", "zł", "PLN", null, false),
        Currencies("Romanian leu", "lei", "RON", null, false),
        Currencies("Saudi riyal", "﷼", "SAR", null, false),
        Currencies("Swedish krona", "kr", "SEK", null, false),
        Currencies("Singapore dollar", "S\$", "SGD", null, false),
        Currencies("Сомонӣ", "смн.", "TJS", null, false),
        Currencies("Turkish lira", "₺", "TRY", null, false),
        Currencies("Türkmen manaty", "m", "TMT", null, false),
        Currencies("Гривня", "₴", "UAH", null, false),
        Currencies("United States dollar", "\$", "USD", null, false),
        Currencies("So‘m", "So'm", "UZS", null, false),
        Currencies("South African rand", "R", "ZAR", null, false)
    )

    private val moreFiatCurrenciesList = listOf(
        Currencies("Afghani", "؋", "AFN", null, false),
        Currencies("Lek", "L", "ALL", null, false),
        Currencies("Argentine peso", "\$", "ARS", null, false),
        Currencies("Bahraini dinar", ".د.ب", "BHD", null, false),
        Currencies("Chilean peso", "\$", "CLP", null, false),
        Currencies("Colombian peso", "\$", "COP", null, false),
        Currencies("Egyptian pound", "E£", "EGP", null, false),
        Currencies("Icelandic krona", "kr", "ISK", null, false),
        Currencies("Indonesian rupiah", "Rp", "IDR", null, false),
        Currencies("Jordanian dinar", "د.ا", "JOD", null, false),
        Currencies("Kenyan shilling", "KSh", "KES", null, false),
        Currencies("Kuwaiti dinar", "د.ك", "KWD", null, false),
        Currencies("Moroccan dirham", "د.م.", "MAD", null, false),
        Currencies("Malaysian ringgit", "RM", "MYR", null, false),
        Currencies("Naira", "₦", "NGN", null, false),
        Currencies("Omani rial", "ر.ع.", "OMR", null, false),
        Currencies("Philippine peso", "₱", "PHP", null, false),
        Currencies("Pakistani rupee", "Rs", "PKR", null, false),
        Currencies("Qatari riyal", "ر.ق", "QAR", null, false),
        Currencies("Serbian dinar", "дин.", "RSD", null, false),
        Currencies("Thai baht", "฿", "THB", null, false),
        Currencies("New Taiwan dollar", "NT\$", "TWD", null, false),
        Currencies("Tanzanian shilling", "TSh", "TZS", null, false),
        Currencies("Uruguayan peso", "\$U", "UYU", null, false),
        Currencies("Vietnamese dong", "₫", "VND", null, false)
    )

    private val cryptoCurrenciesList = listOf(
        Currencies("Bitcoin", "₿", "BTC", null, false),
        Currencies("Ethereum", "Ξ", "ETH", null, false),
        Currencies("Tether", "₮", "USDT", null, false),
        Currencies("USD Coin", "USDC", "USDC", null, false),
        Currencies("BNB", "BNB", "BNB", null, false),
        Currencies("Solana", "SOL", "SOL", null, false),
        Currencies("XRP", "XRP", "XRP", null, false),
        Currencies("Dogecoin", "Ð", "DOGE", null, false),
        Currencies("TRON", "TRX", "TRX", null, false),
        Currencies("Toncoin", "TON", "TON", null, false),
        Currencies("Cardano", "ADA", "ADA", null, false),
        Currencies("Litecoin", "Ł", "LTC", null, false),
        Currencies("Bitcoin Cash", "BCH", "BCH", null, false),
        Currencies("Polkadot", "DOT", "DOT", null, false),
        Currencies("Chainlink", "LINK", "LINK", null, false),
        Currencies("Avalanche", "AVAX", "AVAX", null, false),
        Currencies("Stellar", "XLM", "XLM", null, false),
        Currencies("NEAR Protocol", "NEAR", "NEAR", null, false),
        Currencies("Aptos", "APT", "APT", null, false),
        Currencies("Ethereum Classic", "ETC", "ETC", null, false),
        Currencies("Monero", "XMR", "XMR", null, false),
        Currencies("Cosmos", "ATOM", "ATOM", null, false),
        Currencies("Algorand", "ALGO", "ALGO", null, false)
    )

    fun getCurrenciesList(): List<Currencies> = sortedByCode(baseCurrenciesList)

    fun getMoreFiatCurrenciesList(): List<Currencies> = sortedByCode(moreFiatCurrenciesList)

    fun getCryptoCurrenciesList(): List<Currencies> = sortedByCode(cryptoCurrenciesList)

    fun isCryptoCurrency(iso4217: String?): Boolean {
        return cryptoCurrenciesList.any { it.iso4217 == iso4217 }
    }

    private fun sortedByCode(currencies: List<Currencies>): List<Currencies> = currencies.sortedBy {
        it.iso4217
    }
}
