package com.chico.myhomebookkeeping.icons

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.enums.icons.CashAccountIconNames
import com.chico.myhomebookkeeping.enums.icons.CategoryIconNames
import com.chico.myhomebookkeeping.enums.icons.NoCategoryNames

class IconsMaps(private val resources: Resources, private val opPackageName: String) {
//    val app: Application = Application()

    fun getNoCategoryIconsList():Map<String,Int>{
        return noCategoryIconsList()
    }

    @SuppressLint("NewApi")
    private fun noCategoryIconsList() = mapOf (
        NoCategoryNames.NoImage.name to getDrawable(R.drawable.no_image)
    )

    fun getCashAccountIconsList(): Map<String, Int> {
        return iconsCashAccountMapList()
    }
//    @SuppressLint("NewApi")
//    fun getCashAccountIconsList() = mapOf(
//        CashAccountIconNames.Card.name to getDrawable(R.drawable.cash_account_card),
//        CashAccountIconNames.Cash.name to getDrawable(R.drawable.cash_account_cash),
//        CashAccountIconNames.CardOff.name to getDrawable(R.drawable.cash_account_credit_card_off)
//    )
    @SuppressLint("NewApi")
    private fun iconsCashAccountMapList() = mapOf(
        CashAccountIconNames.Card.name to getDrawable(R.drawable.cash_account_card),
        CashAccountIconNames.Cash.name to getDrawable(R.drawable.cash_account_cash),
        CashAccountIconNames.CardOff.name to getDrawable(R.drawable.cash_account_credit_card_off)
    )

    fun getCategoriesIconsMap():Map<String,Int>{
        return iconsCategoriesMap()
    }

    @SuppressLint("NewApi")
    fun iconsCategoriesMap() = mapOf(
        CategoryIconNames.Apartment.name to getDrawable(R.drawable.category_apartment),
        CategoryIconNames.Airplane.name to getDrawable(R.drawable.category_airplane),
        CategoryIconNames.ArrowsHorizontal.name to getDrawable(R.drawable.category_arrows_horizontal),
        CategoryIconNames.ArrowDropDown.name to getDrawable(R.drawable.category_arrow_drop_down),
        CategoryIconNames.ArrowDropUp.name to getDrawable(R.drawable.category_arrow_drop_up),
        CategoryIconNames.Bank.name to getDrawable(R.drawable.category_bank),
        CategoryIconNames.Build.name to getDrawable(R.drawable.category_build),
        CategoryIconNames.Bus.name to getDrawable(R.drawable.category_bus),
        CategoryIconNames.Cake.name to getDrawable(R.drawable.category_cake),
        CategoryIconNames.Car.name to getDrawable(R.drawable.category_car),
        CategoryIconNames.Celebration.name to getDrawable(R.drawable.category_celebration),
        CategoryIconNames.ChildFriendly.name to getDrawable(R.drawable.category_child_friendly),
        CategoryIconNames.Coffee.name to getDrawable(R.drawable.category_coffee),
        CategoryIconNames.Computer.name to getDrawable(R.drawable.category_computer),
        CategoryIconNames.GasStation.name to getDrawable(R.drawable.category_gas_station),
        CategoryIconNames.House.name to getDrawable(R.drawable.category_house),
        CategoryIconNames.Medical.name to getDrawable(R.drawable.category_medical),
        CategoryIconNames.Park.name to getDrawable(R.drawable.category_park),
        CategoryIconNames.PedalBike.name to getDrawable(R.drawable.category_pedal_bike),
        CategoryIconNames.People.name to getDrawable(R.drawable.category_people),
        CategoryIconNames.Person.name to getDrawable(R.drawable.category_person),
        CategoryIconNames.Pets.name to getDrawable(R.drawable.category_pets),
        CategoryIconNames.Phone.name to getDrawable(R.drawable.category_phone),
        CategoryIconNames.PhoneAndroid.name to getDrawable(R.drawable.category_phone_android),
        CategoryIconNames.PhoneIphone.name to getDrawable(R.drawable.category_phone_iphone),
        CategoryIconNames.Restaurant.name to getDrawable(R.drawable.category_restaurant),
        CategoryIconNames.Salon.name to getDrawable(R.drawable.category_salon),
        CategoryIconNames.School.name to getDrawable(R.drawable.category_school),
        CategoryIconNames.ShoppingCart.name to getDrawable(R.drawable.category_shopping_cart),
        CategoryIconNames.ShoppingCartAdd.name to getDrawable(R.drawable.category_shopping_cart_add),
        CategoryIconNames.Store.name to getDrawable(R.drawable.category_store),
        CategoryIconNames.Subway.name to getDrawable(R.drawable.category_subway),
        CategoryIconNames.Wallet.name to getDrawable(R.drawable.category_wallet),
        CategoryIconNames.TwoWheeler.name to getDrawable(R.drawable.category_two_wheeler)
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getDrawable(drawable: Int): Int {
        return resources.getIdentifier(
            resources.getResourceName(drawable),
            "drawable",
            opPackageName
        )
    }

}