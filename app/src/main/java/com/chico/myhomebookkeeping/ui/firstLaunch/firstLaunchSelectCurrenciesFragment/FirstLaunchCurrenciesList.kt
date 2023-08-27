package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import com.chico.myhomebookkeeping.db.entity.Currencies

object FirstLaunchCurrenciesList {
    private val currenciesList = listOf<Currencies>(
        Currencies("Azərbaycan manatı", "man.", "AZN", null, false),      //Азербальджан
        Currencies("Հայկական դրամ", "֏", "AMD", null, false),          //Армения
        Currencies("Беларускі рубель", "Br.", "BYN", null, false),     //Беларусь
        Currencies("ლარი", "ლ", "GEL", null, false),                   //Грузия
        Currencies("United States dollar", "\$", "USD", null, false),    //США
        Currencies("Euro", "€", "EUR", null, false),                    //Германия,Эстония,Латвия,Литва
        Currencies("Теңге", "₸", "KZT", null, false),                   //Казахстан
        Currencies("Сом", "с", "KGS", null, false),                      //Киргизия
        Currencies("Leu moldovenesc", "L", "MDL", null, false),           //Молдавия
        Currencies("Złoty","zł","PLN",null,null),                         //Польша
        Currencies("Российский рубль", "₽", "RUB", null, false),           //Россия
        Currencies("Сомонӣ", "смн.", "SM", null, false),                    //Таджикистан
        Currencies("Türkmen Manaty", " \tm", "TMT", null, false),            //Туркменистан
        Currencies("So‘m", "So'm", "UZS", null, false),                     //узбекистан
        Currencies("Гривня", "₴", "UAH", null, false),                       //Украина
        Currencies("Franc suisse", "₣", "CFH", null, false),                    //Швейцария
        Currencies("Pound Sterling", "£", "GBP", null, false)               //Англия
    )
    fun getCurrenciesList(): List<Currencies> = currenciesList.sortedBy {
        it.iso4217
    }
}