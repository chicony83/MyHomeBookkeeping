package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ViewModelCheck
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
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import com.chico.myhomebookkeeping.utils.parseTimeToMillis
import java.util.*

class ChangeMoneyMovingViewModel(
    val app: Application,
) : AndroidViewModel(app) {
    private val argsCashAccountKey = Constants.FOR_CREATE_CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.FOR_CREATE_CURRENCY_KEY
    private val argsCategoryKey = Constants.FOR_CREATE_CATEGORY_KEY
    private val argsIdMoneyMovingForChange = Constants.SP_ID_MONEY_MOVING_FOR_CHANGE
    private val spName = Constants.SP_NAME

    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()
    private val dbCategory: CategoryDao =
        dataBase.getDataBase(app.applicationContext).categoryDao()

    private val dbMoneyMovement: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, MODE_PRIVATE)

    private val spEditor = sharedPreferences.edit()

    private val viewModelCheck = ViewModelCheck(sharedPreferences)

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

    private var idMoneyMovingForChange: Long = -1
    private var cashAccountSP = -1
    private var currencySP = -1
    private var categorySP = -1

//    var id: Long = -1

    fun getAndCheckArgsSp() {
        getSharedPreferencesArgs()
        if (idMoneyMovingForChange > 0) {

            launchIo {
                val moneyMovingForChange: MoneyMovement? =
                    MoneyMovingUseCase.getOneMoneyMoving(dbMoneyMovement, idMoneyMovingForChange)
//                id = moneyMovingForChange?.id?.toLong() ?: -1
                cashAccountSP = moneyMovingForChange?.cashAccount ?: 0
                currencySP = moneyMovingForChange?.currency ?: 0
                categorySP = moneyMovingForChange?.category ?: 0
                _amountMoney.postValue(moneyMovingForChange?.amount)
                setValuesViewModel()
                setSubmitButtonText(app.getString(R.string.change_button_text))
//                idMoneyMovingForChange = 0
//                spEditor.putLong(argsIdMoneyMovingForChange, -1)
//                spEditor.commit()
            }
        } else {
            setValuesViewModel()
            setSubmitButtonText(app.getString(R.string.add_button_text))
        }
    }

    private fun getSharedPreferencesArgs() {
        idMoneyMovingForChange = viewModelCheck.getValueSPLong(argsIdMoneyMovingForChange)
        cashAccountSP = viewModelCheck.getValueSP(argsCashAccountKey)
        currencySP = viewModelCheck.getValueSP(argsCurrencyKey)
        categorySP = viewModelCheck.getValueSP(argsCategoryKey)
    }

    private fun setValuesViewModel() {
        launchIo {
            if (viewModelCheck.isPositiveValue(cashAccountSP)) postCashAccount(cashAccountSP)
        }
        launchIo {
            if (viewModelCheck.isPositiveValue(currencySP)) postCurrency(currencySP)
        }
        launchIo {
            if (viewModelCheck.isPositiveValue(categorySP)) postCategory(categorySP)
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

    fun saveSP() {
        spEditor.putInt(argsCurrencyKey, _selectedCurrency.value?.currencyId ?: -1)
        spEditor.putInt(argsCashAccountKey, _selectedCashAccount.value?.cashAccountId ?: -1)
        spEditor.putInt(argsCategoryKey, _selectedCategory.value?.categoriesId ?: -1)
//        spEditor.putLong(argsIdMoneyMovingForChange, id ?: -1)

        spEditor.commit()
    }

    suspend fun updateDB(
//        dataTime: Long,
        amount: Double,
        description: String
    ): Long {

        val dateTime: Long = dataTime.value?.parseTimeToMillis() ?: 0
        val cashAccountValue: Int = _selectedCashAccount.value?.cashAccountId ?: 0
        val categoryValue: Int = _selectedCategory.value?.categoriesId ?: 0
        val currencyValue: Int = _selectedCurrency.value?.currencyId ?: 0
        if (idMoneyMovingForChange > 0) {
            return ChangeMoneyMovingUseCase.changeMoneyMovingLine(
                db = dbMoneyMovement,
                id = idMoneyMovingForChange,
                dateTime = dateTime,
                amount = amount,
                cashAccountId = cashAccountValue,
                categoryId = categoryValue,
                currencyId = currencyValue,
                description = description
            ).toLong()
        } else {
            val moneyMovement = MoneyMovement(
                timeStamp = dateTime,
                amount = amount,
                cashAccount = cashAccountValue,
                category = categoryValue,
                currency = currencyValue,
                description = description
            )
            return ChangeMoneyMovingUseCase.addInDataBase(dbMoneyMovement, moneyMovement)
        }

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


    fun isChangeMoneyMoving(): Boolean {
        return idMoneyMovingForChange > 0
    }

    fun getIdMoneyMovingForChange(): Long {
        return idMoneyMovingForChange
    }
}