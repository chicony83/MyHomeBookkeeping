package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CategoryDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Categories
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.db.entity.MoneyMovement
import com.chico.myhomebookkeeping.domain.*
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import com.chico.myhomebookkeeping.utils.parseTimeToMillis
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class NewMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val argsDateTimeCreateKey = Constants.FOR_CREATE_DATE_TIME_KEY
    private val argsCashAccountCreateKey = Constants.FOR_CREATE_CASH_ACCOUNT_KEY
    private val argsCurrencyCreateKey = Constants.FOR_CREATE_CURRENCY_KEY
    private val argsCategoryCreateKey = Constants.FOR_CREATE_CATEGORY_KEY
    private val argsAmountCreateKey = Constants.FOR_CREATE_AMOUNT_KEY
    private val argsDescriptionCreateKey = Constants.FOR_CREATE_DESCRIPTION_KEY


    private val modelCheck = ModelCheck()

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private val spName = Constants.SP_NAME

    private val dbMoneyMovement: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()
    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()

    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)

    //    private val spEditor = sharedPreferences.edit()
    private val saveARGS = SetSP(spEditor = sharedPreferences.edit())

    private val spValues = GetSP(sharedPreferences)

    private val _dateTime = MutableLiveData<String>()
    val dataTime: LiveData<String>
        get() = _dateTime

    private var date: Long = 0
    private var time: Long = 0

    private val _selectedDateTime = MutableLiveData<String>()
    val selectedDateTime: LiveData<String>
        get() = _selectedDateTime

    private val _selectedCurrency = MutableLiveData<Currencies>()
    val selectedCurrency: LiveData<Currencies>
        get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<CashAccount>()
    val selectedCashAccount: LiveData<CashAccount>
        get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<Categories>()
    val selectedCategory: LiveData<Categories>
        get() = _selectedCategory

    private val _enteredDescription = MutableLiveData<String>()
    val enteredDescription: LiveData<String>
        get() = _enteredDescription

    private val _enteredAmount = MutableLiveData<Double?>()
    val enteredAmount: LiveData<Double?>
        get() = _enteredAmount

    private val _submitButtonText = MutableLiveData<String>()
    val submitButton: LiveData<String>
        get() = _submitButtonText

    //    private var idMoneyMovingForChange: Long = -1

    private var dateTimeSPLong = minusOneLong
    private var cashAccountSPInt = minusOneInt
    private var currencySPInt = minusOneInt
    private var categorySPInt = minusOneInt
    private var amountSPString = ""
    private var descriptionSPString = ""
//    var id: Long = -1

    fun getAndCheckArgsSp() {

        getSharedPreferencesArgs()
        setSubmitButtonText(app.getString(R.string.text_on_button_add))
        setValuesViewModel()
    }

    private fun getSharedPreferencesArgs() {
//        idMoneyMovingForChange = viewModelCheck.getValueSPLong(argsIdMoneyMovingForChange)
        dateTimeSPLong = spValues.getLong(argsDateTimeCreateKey)
        cashAccountSPInt = spValues.getInt(argsCashAccountCreateKey)
        currencySPInt = spValues.getInt(argsCurrencyCreateKey)
        categorySPInt = spValues.getInt(argsCategoryCreateKey)
        amountSPString = spValues.getString(argsAmountCreateKey).toString()
        descriptionSPString = spValues.getString(argsDescriptionCreateKey).toString()
    }

    private fun setValuesViewModel() {
        launchIo {
            if (modelCheck.isPositiveValue(dateTimeSPLong)) postDateTime(dateTimeSPLong)
        }
        launchIo {
            if (modelCheck.isPositiveValue(cashAccountSPInt)) postCashAccount(cashAccountSPInt)
        }
        launchIo {
            if (modelCheck.isPositiveValue(currencySPInt)) postCurrency(currencySPInt)
        }
        launchIo {
            if (modelCheck.isPositiveValue(categorySPInt)) postCategory(categorySPInt)
        }
        launchIo {
            if (modelCheck.isPositiveValue(amountSPString)) postAmount(
                aroundDouble(amountSPString)
            )
        }
        launchIo {
            if (descriptionSPString.isNotEmpty()) postDescription(descriptionSPString)
        }
    }

    private fun postAmount(amount:Double) {
        _enteredAmount.postValue(amount)
    }

    private fun postDescription(descriptionSPString: String) {
        _enteredDescription.postValue(descriptionSPString)
    }

    private fun postDateTime(dateTimeSPLong: Long) {
        _dateTime.postValue(dateTimeSPLong.parseTimeFromMillis())
    }

    private suspend fun postCategory(idNum: Int) {
        _selectedCategory.postValue(
            CategoriesUseCase.getOneCategory(dbCategory, idNum)
        )
    }

    private suspend fun postCurrency(idNum: Int) {
        _selectedCurrency.postValue(
            CurrenciesUseCase.getOneCurrency(dbCurrencies, idNum)
        )
    }

    private suspend fun postCashAccount(idNum: Int) {
        _selectedCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccount(dbCashAccount, idNum)
        )
    }

    fun saveDataToSP(amount: Double, description: String) {
        with(saveARGS) {
            saveToSP(argsDateTimeCreateKey, _dateTime.value?.parseTimeToMillis())

            saveToSP(
                argsCurrencyCreateKey,
                _selectedCurrency.value?.currencyId
            )
            saveToSP(
                argsCashAccountCreateKey,
                _selectedCashAccount.value?.cashAccountId
            )
            saveToSP(
                argsCategoryCreateKey,
                _selectedCategory.value?.categoriesId
            )
            if (amount>0){
                saveToSP(argsAmountCreateKey, amount.toString())
            }
            saveToSP(argsDescriptionCreateKey, description)
        }
    }

    suspend fun addInMoneyMovingDB(
        amount: Double,
        description: String
    ): Long {
        val dateTime: Long = dataTime.value?.parseTimeToMillis() ?: 0
        val cashAccountValue: Int = _selectedCashAccount.value?.cashAccountId ?: 0
        val categoryValue: Int = _selectedCategory.value?.categoriesId ?: 0
        val currencyValue: Int = _selectedCurrency.value?.currencyId ?: 0
        val moneyMovement = MoneyMovement(
            timeStamp = dateTime,
            amount = amount,
            cashAccount = cashAccountValue,
            category = categoryValue,
            currency = currencyValue,
            description = description
        )
        return NewMoneyMovementUseCase.addInDataBase(dbMoneyMovement, moneyMovement)
    }

    fun setDate(it: Long?) {
        date = it ?: 0
    }

    fun setTime(hour: Int, minute: Int) {
        val timeZone: Int = getTZ()
        time = (((hour * 60 * 60 * 1000) - timeZone).toLong()) + ((minute * 60 * 1000).toLong())

//        Log.i("TAG", "hour = $hour, minute = $minute, time = $time")
    }

    fun setDateTimeOnButton() {
        val dateTime: String = (date + time).parseTimeFromMillis()
        _dateTime.postValue(dateTime)
    }

    fun setDateTimeOnButton(currentDateTimeMillis: Long) {
        val dateTime: String = currentDateTimeMillis.parseTimeFromMillis()
        _dateTime.postValue(dateTime)
    }

    private fun getTZ(): Int {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis())
    }

    private fun setSubmitButtonText(string: String) {
        _submitButtonText.postValue(string)
    }

    fun checkIsCashAccountSelected(): Boolean {
        return selectedCashAccount.value != null
    }

    fun checkIsCurrencySelected(): Boolean {
        return selectedCurrency.value != null
    }

    fun checkIsCategorySelected(): Boolean {
        return selectedCategory.value != null
    }

    fun clearSPAfterSave() {
        with(saveARGS) {
            saveToSP(argsDateTimeCreateKey, minusOneLong)
            saveToSP(argsAmountCreateKey, "")
            saveToSP(argsDescriptionCreateKey, "")
        }
    }

    fun aroundDouble(text: String): Double {
        return BigDecimal(text)
            .setScale(2, RoundingMode.HALF_EVEN)
            .toDouble()
    }
}