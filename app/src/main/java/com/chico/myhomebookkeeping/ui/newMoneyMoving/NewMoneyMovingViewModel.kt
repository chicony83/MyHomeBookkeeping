package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.checks.GetSP
import com.chico.myhomebookkeeping.constants.Constants
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
import com.chico.myhomebookkeeping.helpers.SetSP
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import com.chico.myhomebookkeeping.utils.parseTimeToMillis
import java.util.*

class NewMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val argsCashAccountCreateKey = Constants.FOR_CREATE_CASH_ACCOUNT_KEY
    private val argsCurrencyCreateKey = Constants.FOR_CREATE_CURRENCY_KEY
    private val argsCategoryCreateKey = Constants.FOR_CREATE_CATEGORY_KEY
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

    private val _selectedCurrency = MutableLiveData<Currencies>()
    val selectedCurrency: LiveData<Currencies>
        get() = _selectedCurrency

    private val _selectedCashAccount = MutableLiveData<CashAccount>()
    val selectedCashAccount: LiveData<CashAccount>
        get() = _selectedCashAccount

    private val _selectedCategory = MutableLiveData<Categories>()
    val selectedCategory: LiveData<Categories>
        get() = _selectedCategory

    private val _submitButtonText = MutableLiveData<String>()
    val submitButton: LiveData<String>
        get() = _submitButtonText

    private val _amountMoney = MutableLiveData<Double?>()
    val amountMoney: LiveData<Double?>
        get() = _amountMoney

    //    private var idMoneyMovingForChange: Long = -1
    private var cashAccountSP = -1
    private var currencySP = -1
    private var categorySP = -1

//    var id: Long = -1

    fun getAndCheckArgsSp() {

        getSharedPreferencesArgs()
        setSubmitButtonText(app.getString(R.string.add_button_text))
        setValuesViewModel()
    }

    private fun getSharedPreferencesArgs() {
//        idMoneyMovingForChange = viewModelCheck.getValueSPLong(argsIdMoneyMovingForChange)
        cashAccountSP = spValues.getInt(argsCashAccountCreateKey)
        currencySP = spValues.getInt(argsCurrencyCreateKey)
        categorySP = spValues.getInt(argsCategoryCreateKey)
    }

    private fun setValuesViewModel() {
        launchIo {
            if (modelCheck.isPositiveValue(cashAccountSP)) postCashAccount(cashAccountSP)
        }
        launchIo {
            if (modelCheck.isPositiveValue(currencySP)) postCurrency(currencySP)
        }
        launchIo {
            if (modelCheck.isPositiveValue(categorySP)) postCategory(categorySP)
        }
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

    fun saveDataToSP() {
        with(saveARGS) {
            saveToSP(
                argsCurrencyCreateKey,
                _selectedCurrency.value?.currencyId ?: minusOneInt
            )
            saveToSP(
                argsCashAccountCreateKey,
                _selectedCashAccount.value?.cashAccountId ?: minusOneInt
            )
            saveToSP(
                argsCategoryCreateKey,
                _selectedCategory.value?.categoriesId ?: minusOneInt
            )
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

    fun setSubmitButtonText(string: String) {
        _submitButtonText.postValue(string)
    }
}