package com.chico.myhomebookkeeping.ui.changeMoneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.checks.SharedPreferenceValues
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
import com.chico.myhomebookkeeping.helpers.SaveARGS
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import com.chico.myhomebookkeeping.utils.parseTimeToMillis
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.*

class ChangeMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val argsDateTimeChangeKey = Constants.FOR_CHANGE_DATE_TIME_KEY
    private val argsCashAccountChangeKey = Constants.FOR_CHANGE_CASH_ACCOUNT_KEY
    private val argsCurrencyChangeKey = Constants.FOR_CHANGE_CURRENCY_KEY
    private val argsCategoryChangeKey = Constants.FOR_CHANGE_CATEGORY_KEY
    private val argsDescriptionChangeKey = Constants.FOR_CHANGE_DESCRIPTION_KEY
    private val argsAmountChangeKey = Constants.FOR_CHANGE_AMOUNT_KEY

    private val minusOneInt = Constants.MINUS_ONE_VAL_INT
    private val minusOneLong = Constants.MINUS_ONE_VAL_LONG

    private var dataTimeSPLong: Long = minusOneLong
    private var cashAccountSPInt: Int = minusOneInt
    private var currencySPInt: Int = minusOneInt
    private var categorySPInt: Int = minusOneInt
    private var descriptionSPString: String = ""
    private var amountSPDouble: Double = minusOneInt.toDouble()

    private val spName = Constants.SP_NAME
    private val modelCheck = ModelCheck()
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)

    private val spValues = SharedPreferenceValues(sharedPreferences)
    private val saveARGS = SaveARGS(spEditor = sharedPreferences.edit())

    private val argsIdMoneyMovingForChange = Constants.FOR_CHANGE_ID_MONEY_MOVING
    private var idMoneyMovingForChangeLong: Long = -1

    private val dbMoneyMovement: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()
    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()


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

    private val _amountMoney = MutableLiveData<String?>()
    val amountMoney: LiveData<String?>
        get() = _amountMoney

    private val _descriptionText = MutableLiveData<String?>()
    val descriptionText: LiveData<String?>
        get() = _descriptionText

    fun getLineForChange() {
        idMoneyMovingForChangeLong = getIdLine()
        if (modelCheck.isPositiveValue(idMoneyMovingForChangeLong)) {
            launchIo {
                getMoneyMovement()
            }
        }
    }

    private suspend fun getMoneyMovement() = coroutineScope {
        val moneyMovement =
            async {
                MoneyMovingUseCase
                    .getOneMoneyMoving(
                        dbMoneyMovement, idMoneyMovingForChangeLong
                    )
            }

        postDateTime(moneyMovement.await())
        postCategory(moneyMovement.await())
        postCashAccount(moneyMovement.await())
        postCurrency(moneyMovement.await())
        postDescription(moneyMovement.await())
        postAmount(moneyMovement.await())
    }

    private fun postDateTime(moneyMovement: MoneyMovement?) {
        launchUi {
            var postingDateTime: String = ""
            Log.i("TAG"," dataTimeLong = $dataTimeSPLong")
            if (modelCheck.isPositiveValue(dataTimeSPLong)) {
                postingDateTime = dataTimeSPLong.parseTimeFromMillis()
            }
            if (!modelCheck.isPositiveValue(dataTimeSPLong)) {
                postingDateTime =
                    moneyMovement?.timeStamp?.parseTimeFromMillis() ?: "select date/time"
            }

//            messageLog(postingDateTime)
            _dateTime.postValue(postingDateTime)
        }
    }

    private fun messageLog(text: String) {
        Log.i("TAG", " dateTime $text")
    }

    private suspend fun postCategory(moneyMovement: MoneyMovement?) {
        launchUi {
            val postingId: Int = if (modelCheck.isPositiveValue(categorySPInt)) {
                categorySPInt
            } else {
                moneyMovement?.category ?: 0
            }
            _selectedCategory.postValue(
                CategoriesUseCase.getOneCategory(
                    dbCategory,
                    postingId
                )
            )
        }
    }

    private suspend fun postCashAccount(moneyMovement: MoneyMovement?) {
        val postingId: Int = if (modelCheck.isPositiveValue(cashAccountSPInt)) {
            cashAccountSPInt
        } else {
            moneyMovement?.cashAccount ?: 0
        }
        _selectedCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccount(
                dbCashAccount,
                postingId
            )
        )
    }

    private suspend fun postCurrency(moneyMovement: MoneyMovement?) {
        val postingId: Int = if (modelCheck.isPositiveValue(currencySPInt)) {
            currencySPInt
        } else {
            moneyMovement?.currency ?: 0
        }
        _selectedCurrency.postValue(
            CurrenciesUseCase.getOneCurrency(
                dbCurrencies,
                postingId
            )
        )
    }

    private fun postDescription(moneyMovement: MoneyMovement?) {
        val postingValue = if (descriptionSPString.isNotEmpty()) {
            descriptionSPString
        } else {
            moneyMovement?.description
        }
        _descriptionText.postValue(postingValue)
    }

    private fun postAmount(moneyMovement: MoneyMovement?) {
        val postingValue = if (modelCheck.isPositiveValue(amountSPDouble)) {
            amountSPDouble
        } else {
            moneyMovement?.amount
        }
        _amountMoney.postValue(postingValue.toString())
    }

    private fun getIdLine(): Long {
        return spValues.getLong(argsIdMoneyMovingForChange)
    }

    fun setDate(it: Long?) {
        date = it ?: 0
    }

    fun setTime(hour: Int, minute: Int) {
        val timeZone: Int = getTZ()
        time = (((hour * 60 * 60 * 1000) - timeZone).toLong()) + ((minute * 60 * 1000).toLong())
    }

    private fun getTZ(): Int {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis())
    }

    fun setDateTimeOnButton() {
        val dateTime: String = (date + time).parseTimeFromMillis()
        _dateTime.postValue(dateTime)
    }

    fun saveDataToSp() {
        with(saveARGS) {
            saveToSP(argsIdMoneyMovingForChange, idMoneyMovingForChangeLong)
            saveToSP(argsDateTimeChangeKey, _dateTime.value.toString().parseTimeToMillis())
            saveToSP(argsCashAccountChangeKey, _selectedCashAccount.value?.cashAccountId ?: -1)
            saveToSP(argsCurrencyChangeKey, _selectedCurrency.value?.currencyId ?: -1)
            saveToSP(argsCategoryChangeKey, _selectedCategory.value?.categoriesId ?: -1)
            saveToSP(argsDescriptionChangeKey, _descriptionText.value.toString())
            saveToSP(argsAmountChangeKey, _amountMoney.value.toString())
        }
    }

    fun getDataForChangeMoneyMovingLine() {
        idMoneyMovingForChangeLong = spValues.getLong(argsDateTimeChangeKey)
        dataTimeSPLong = spValues.getLong(argsDateTimeChangeKey)
        cashAccountSPInt = spValues.getInt(argsCashAccountChangeKey)
        currencySPInt = spValues.getInt(argsCurrencyChangeKey)
        categorySPInt = spValues.getInt(argsCategoryChangeKey)
        descriptionSPString = spValues.getString(argsDescriptionChangeKey).toString()
//        amountSPDouble = spValues.getString(argsAmountChangeKey)?.toDouble() ?: 0.0
    }

    suspend fun changeMoneyMovementInDB(amount: Double, description: String): Long {
        val dateTimeVal = _dateTime.value?.parseTimeToMillis() ?: 0
        messageLog("amount = $amount")
        return ChangeMoneyMovingUseCase.changeMoneyMovingLine(
            db = dbMoneyMovement,
            id = idMoneyMovingForChangeLong,
            dateTime = dateTimeVal,
            cashAccountId = _selectedCashAccount.value?.cashAccountId ?: 0,
            categoryId = _selectedCategory.value?.categoriesId ?: 0,
            currencyId = _selectedCurrency.value?.currencyId ?: 0,
            amount = amount,
            description = description
        ).toLong()
    }
}