package com.chico.myhomebookkeeping.icons

import android.annotation.SuppressLint
import android.content.res.Resources
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.enums.icon.names.CashAccountIconNames
import com.chico.myhomebookkeeping.enums.icon.names.CategoryIconNames
import com.chico.myhomebookkeeping.enums.icon.names.NoCategoryNames

class IconsMaps(private val resources: Resources, private val opPackageName: String) {
//    val app: Application = Application()

    fun getNoCategoryIconsList(): Map<String, Int> {
        return noCategoryIconsList()
    }

    @SuppressLint("NewApi")
    private fun noCategoryIconsList() = mapOf(
        NoCategoryNames.NoImage.name to getDrawable(R.drawable.no_image)
    )

    fun getCashAccountIconsList(): Map<String, Int> {
        return iconsCashAccountMap()
    }

    //    @SuppressLint("NewApi")
//    fun getCashAccountIconsList() = mapOf(
//        CashAccountIconNames.Card.name to getDrawable(R.drawable.cash_account_card),
//        CashAccountIconNames.Cash.name to getDrawable(R.drawable.cash_account_cash),
//        CashAccountIconNames.CardOff.name to getDrawable(R.drawable.cash_account_credit_card_off)
//    )
    @SuppressLint("NewApi")
    private fun iconsCashAccountMap() = mapOf(
        CashAccountIconNames.Card.name to getDrawable(R.drawable.cash_account_card),
        CashAccountIconNames.Cash.name to getDrawable(R.drawable.cash_account_cash),
        CashAccountIconNames.CardOff.name to getDrawable(R.drawable.cash_account_credit_card_off)
    )

    fun getCategoriesIconsMap(): Map<String, Int> {
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
        CategoryIconNames.CardGiftcard.name to getDrawable(R.drawable.category_card_giftcard),
        CategoryIconNames.Car.name to getDrawable(R.drawable.category_car),
        CategoryIconNames.Celebration.name to getDrawable(R.drawable.category_celebration),
        CategoryIconNames.ChildFriendly.name to getDrawable(R.drawable.category_child_friendly),
        CategoryIconNames.Checkroom.name to getDrawable(R.drawable.category_checkroom),
        CategoryIconNames.CleaningServices.name to getDrawable(R.drawable.category_cleaning_services),
        CategoryIconNames.Cloud.name to getDrawable(R.drawable.category_cloud),
        CategoryIconNames.Coffee.name to getDrawable(R.drawable.category_coffee),
        CategoryIconNames.Computer.name to getDrawable(R.drawable.category_computer),
        CategoryIconNames.Description.name to getDrawable(R.drawable.category_description),
        CategoryIconNames.GasStation.name to getDrawable(R.drawable.category_gas_station),
        CategoryIconNames.Gavel.name to getDrawable(R.drawable.category_gavel),
        CategoryIconNames.House.name to getDrawable(R.drawable.category_house),
        CategoryIconNames.Kitchen.name to getDrawable(R.drawable.category_kitchen),
        CategoryIconNames.LocalParking.name to getDrawable(R.drawable.category_local_parking),
        CategoryIconNames.LocalTaxi.name to getDrawable(R.drawable.category_local_taxi),
        CategoryIconNames.Medical.name to getDrawable(R.drawable.category_medical),
        CategoryIconNames.MusicNote.name to getDrawable(R.drawable.category_music_note),
        CategoryIconNames.Park.name to getDrawable(R.drawable.category_park),
        CategoryIconNames.PedalBike.name to getDrawable(R.drawable.category_pedal_bike),
        CategoryIconNames.People.name to getDrawable(R.drawable.category_people),
        CategoryIconNames.Person.name to getDrawable(R.drawable.category_person),
        CategoryIconNames.Pets.name to getDrawable(R.drawable.category_pets),
        CategoryIconNames.Phone.name to getDrawable(R.drawable.category_phone),
        CategoryIconNames.PhoneAndroid.name to getDrawable(R.drawable.category_phone_android),
        CategoryIconNames.PhoneIphone.name to getDrawable(R.drawable.category_phone_iphone),
        CategoryIconNames.Policy.name to getDrawable(R.drawable.category_policy),
        CategoryIconNames.Receipt.name to getDrawable(R.drawable.category_receipt),
        CategoryIconNames.Restaurant.name to getDrawable(R.drawable.category_restaurant),
        CategoryIconNames.Salon.name to getDrawable(R.drawable.category_salon),
        CategoryIconNames.School.name to getDrawable(R.drawable.category_school),
        CategoryIconNames.ShoppingCart.name to getDrawable(R.drawable.category_shopping_cart),
        CategoryIconNames.ShoppingCartAdd.name to getDrawable(R.drawable.category_shopping_cart_add),
        CategoryIconNames.Sports.name to getDrawable(R.drawable.category_sports),
        CategoryIconNames.Store.name to getDrawable(R.drawable.category_store),
        CategoryIconNames.Subscriptions.name to getDrawable(R.drawable.category_subscriptions),
        CategoryIconNames.Subway.name to getDrawable(R.drawable.category_subway),
        CategoryIconNames.Train.name to getDrawable(R.drawable.category_train),
        CategoryIconNames.Apps.name to getDrawable(R.drawable.category_apps),
        CategoryIconNames.Videocam.name to getDrawable(R.drawable.category_videocam),
        CategoryIconNames.VideogameAsset.name to getDrawable(R.drawable.category_videogame_asset),
        CategoryIconNames.VolunteerActivism.name to getDrawable(R.drawable.category_volunteer_activism),
        CategoryIconNames.Wallet.name to getDrawable(R.drawable.category_wallet),
        CategoryIconNames.Book.name to getDrawable(R.drawable.category_book),
        CategoryIconNames.BusinessCenter.name to getDrawable(R.drawable.category_business_center),
        CategoryIconNames.TwoWheeler.name to getDrawable(R.drawable.category_two_wheeler)
    )

    private fun getDrawable(drawable: Int): Int = drawable
}
