package com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class NewMoneyMovingViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val argsDateTimeCreateKey = Constants.ARGS_NEW_PAYMENT_DATE_TIME_KEY
    private val argsCashAccountCreateKey = Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY
    private val argsTransferCashAccountCreateKey = Constants.ARGS_NEW_PAYMENT_TRANSFER_CASH_ACCOUNT_KEY
    private val argsIsTransferCreateKey = Constants.ARGS_NEW_PAYMENT_IS_TRANSFER_KEY
    private val argsCashAccountSelectModeCreateKey = Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_SELECT_MODE_KEY
    private val argsCurrencyCreateKey = Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY
    private val argsCategoryCreateKey = Constants.ARGS_NEW_PAYMENT_CATEGORY_KEY
    private val argsAmountCreateKey = Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY
    private val argsDescriptionCreateKey = Constants.ARGS_NEW_PAYMENT_DESCRIPTION_KEY

    private val argsNewEntryOfMoneyMovingInDbIsAdded =
        Constants.ARGS_NEW_ENTRY_OF_MONEY_MOVING_IN_DB_IS_ADDED

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

    private val setSP = SetSP(spEditor = sharedPreferences.edit())

    private val getSP = GetSP(sharedPreferences)

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

    private val _selectedTransferCashAccount = MutableLiveData<CashAccount>()
    val selectedTransferCashAccount: LiveData<CashAccount>
        get() = _selectedTransferCashAccount

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

    private var _onCalcAmountSelected = MutableStateFlow("")
    val onCalcAmountSelected: StateFlow<String> = _onCalcAmountSelected

    private val _isTransfer = MutableLiveData(false)
    val isTransfer: LiveData<Boolean>
        get() = _isTransfer

    private val _quickPaymentSettings = MutableLiveData<QuickPaymentSettings>()
    val quickPaymentSettings: LiveData<QuickPaymentSettings>
        get() = _quickPaymentSettings

    //    private var idMoneyMovingForChange: Long = -1

    private var dateTimeSPLong = minusOneLong
    private var cashAccountSPInt = minusOneInt
    private var transferCashAccountSPInt = minusOneInt
    private var currencySPInt = minusOneInt
    private var categorySPInt = minusOneInt
    private var isTransferSPBoolean = false
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
        dateTimeSPLong = getSP.getLong(argsDateTimeCreateKey)
        cashAccountSPInt = getSP.getInt(argsCashAccountCreateKey)
        transferCashAccountSPInt = getSP.getInt(argsTransferCashAccountCreateKey)
        currencySPInt = getSP.getInt(argsCurrencyCreateKey)
        categorySPInt = getSP.getInt(argsCategoryCreateKey)
        isTransferSPBoolean = getSP.getBooleanElseReturnFalse(argsIsTransferCreateKey)
        amountSPString = getSP.getString(argsAmountCreateKey).toString()
        descriptionSPString = getSP.getString(argsDescriptionCreateKey).toString()
    }

    private fun setValuesViewModel() {
        launchIo {
            if (modelCheck.isPositiveValue(dateTimeSPLong)) launchUi {
                postDateTime(
                    dateTimeSPLong
                )
            }
        }
        launchIo {
            launchUi {
                if (modelCheck.isPositiveValue(cashAccountSPInt)) {
                    postCashAccount(cashAccountSPInt)
                } else {
                    postDefaultCashAccount()
                }
            }
        }
        launchIo {
            if (modelCheck.isPositiveValue(transferCashAccountSPInt)) launchUi {
                postTransferCashAccount(
                    transferCashAccountSPInt
                )
            }
        }
        launchIo {
            launchUi {
                if (modelCheck.isPositiveValue(currencySPInt)) {
                    postCurrency(currencySPInt)
                } else {
                    postDefaultCurrency()
                }
            }
        }
        launchIo {
            if (modelCheck.isPositiveValue(categorySPInt)) launchUi { postCategory(categorySPInt) }
        }
        launchIo {
            if (modelCheck.isPositiveValue(amountSPString)) launchUi {
                postAmount(
                    Around.double(amountSPString)
                )
            }
        }
        launchIo {
            if (descriptionSPString.isNotEmpty()) launchUi {
                postDescription(
                    descriptionSPString
                )
            }
        }
        postIsTransfer(isTransferSPBoolean)
        postQuickPaymentSettings()
    }

    private fun postQuickPaymentSettings() {
        _quickPaymentSettings.postValue(getQuickPaymentSettings())
    }

    private fun postAmount(amount: Double) {
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

    private suspend fun postDefaultCurrency() {
        _selectedCurrency.postValue(
            CurrenciesUseCase.getDefaultCurrency(dbCurrencies)
        )
    }

    private suspend fun postCashAccount(idNum: Int) {
        _selectedCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccountById(dbCashAccount, idNum)
        )
    }

    private suspend fun postDefaultCashAccount() {
        _selectedCashAccount.postValue(
            CashAccountsUseCase.getDefaultCashAccount(dbCashAccount)
        )
    }

    private suspend fun postTransferCashAccount(idNum: Int) {
        _selectedTransferCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccountById(dbCashAccount, idNum)
        )
    }

    private fun postIsTransfer(isTransfer: Boolean) {
        _isTransfer.postValue(isTransfer)
    }

    fun saveDataToSP(amount: Double, description: String) {
        with(setSP) {
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
                argsTransferCashAccountCreateKey,
                _selectedTransferCashAccount.value?.cashAccountId
            )
            saveToSP(
                argsCategoryCreateKey,
                _selectedCategory.value?.categoriesId
            )
            saveToSP(argsIsTransferCreateKey, _isTransfer.value == true)
            if (amount > 0) {
                saveToSP(argsAmountCreateKey, amount.toString())
            }
            saveToSP(argsDescriptionCreateKey, description)
        }
    }

    suspend fun addNewMoneyMoving(
        amount: Double,
        description: String
    ): Long {
        val dateTime: Long = dataTime.value?.parseTimeToMillis() ?: 0
        val cashAccountValue: Int = _selectedCashAccount.value?.cashAccountId ?: 0
        val categoryValue: Int = _selectedCategory.value?.categoriesId ?: 0
        val currencyValue: Int = _selectedCurrency.value?.currencyId ?: 0
        val paymentTypeId = if (_selectedCategory.value?.isIncome == true) {
            PaymentTypeIds.INCOME
        } else {
            PaymentTypeIds.SPENDING
        }
        val moneyMovement = MoneyMovement(
            timeStamp = dateTime,
            amount = amount,
            cashAccount = cashAccountValue,
            category = categoryValue,
            paymentTypeId = paymentTypeId,
            currency = currencyValue,
            description = description
        )
        return NewMoneyMovementUseCase.addInDataBase(dbMoneyMovement, moneyMovement)
    }

    suspend fun addNewTransfer(
        amount: Double,
        description: String
    ): List<Long> {
        val dateTime: Long = dataTime.value?.parseTimeToMillis() ?: 0
        val sourceCashAccountValue: Int = _selectedCashAccount.value?.cashAccountId ?: 0
        val destinationCashAccountValue: Int = _selectedTransferCashAccount.value?.cashAccountId ?: 0
        val currencyValue: Int = _selectedCurrency.value?.currencyId ?: 0
        val transferGroupId = System.currentTimeMillis()
        val source = MoneyMovement(
            timeStamp = dateTime,
            cashAccount = sourceCashAccountValue,
            currency = currencyValue,
            category = null,
            paymentTypeId = PaymentTypeIds.TRANSFER,
            amount = amount,
            description = description,
            transferGroupId = transferGroupId,
            transferDirection = PaymentTypeIds.TRANSFER_DIRECTION_FROM
        )
        val destination = MoneyMovement(
            timeStamp = dateTime,
            cashAccount = destinationCashAccountValue,
            currency = currencyValue,
            category = null,
            paymentTypeId = PaymentTypeIds.TRANSFER,
            amount = amount,
            description = description,
            transferGroupId = transferGroupId,
            transferDirection = PaymentTypeIds.TRANSFER_DIRECTION_TO
        )
        return NewMoneyMovementUseCase.addTransferInDataBase(dbMoneyMovement, source, destination)
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

    fun isCashAccountNotNull(): Boolean {
        return selectedCashAccount.value != null
    }

    fun isCurrencyNotNull(): Boolean {
        return selectedCurrency.value != null
    }

    fun isCategoryNotNull(): Boolean {
        return selectedCategory.value != null
    }

    fun isTransferCashAccountNotNull(): Boolean {
        return selectedTransferCashAccount.value != null
    }

    fun isTransferMode(): Boolean {
        return _isTransfer.value == true
    }

    fun isTransferAccountsDifferent(): Boolean {
        return _selectedCashAccount.value?.cashAccountId != _selectedTransferCashAccount.value?.cashAccountId
    }

    fun clearSPAfterSave() {
        with(setSP) {
            saveToSP(argsDateTimeCreateKey, minusOneLong)
            saveToSP(argsAmountCreateKey, "")
            saveToSP(argsDescriptionCreateKey, "")
            saveToSP(argsIsTransferCreateKey, false)
            saveToSP(argsCashAccountSelectModeCreateKey, Constants.CASH_ACCOUNT_SELECT_MODE_SOURCE)
        }
    }

    fun saveSPOfNewEntryIsAdded() {
        setSP.saveToSP(argsNewEntryOfMoneyMovingInDbIsAdded, true)
    }

    fun setCalcSelectedAmount(amount: String, decimalSeparatorSymbol: String) {
        val clearedAmount = removeWhitespacesAndCommas(amount,decimalSeparatorSymbol)
        viewModelScope.launch {
            if (clearedAmount.hasExpression()) {
                _onCalcAmountSelected.value = ""
            } else {
                _onCalcAmountSelected.value = clearedAmount
            }
        }
    }

    fun setTransferMode(isTransfer: Boolean) {
        _isTransfer.postValue(isTransfer)
        setSP.saveToSP(argsIsTransferCreateKey, isTransfer)
    }

    fun setSourceCashAccountSelectMode() {
        setSP.saveToSP(
            argsCashAccountSelectModeCreateKey,
            Constants.CASH_ACCOUNT_SELECT_MODE_SOURCE
        )
    }

    fun setDestinationCashAccountSelectMode() {
        setSP.saveToSP(
            argsCashAccountSelectModeCreateKey,
            Constants.CASH_ACCOUNT_SELECT_MODE_DESTINATION
        )
    }

    fun selectCurrency(currency: Currencies) {
        _selectedCurrency.postValue(currency)
    }

    fun selectCashAccount(cashAccount: CashAccount) {
        _selectedCashAccount.postValue(cashAccount)
    }

    fun getQuickPaymentSettings(): QuickPaymentSettings {
        return QuickPaymentSettings(
            isCurrencyScrollEnabled = getSP.getBooleanElseReturnFalse(
                Constants.QUICK_PAYMENT_CURRENCY_SELECTION_SCROLL
            ),
            isCashAccountScrollEnabled = getSP.getBooleanElseReturnFalse(
                Constants.QUICK_PAYMENT_CASH_ACCOUNT_SELECTION_SCROLL
            ),
            isCalculatorButtonVisible = getSP.getBooleanElseReturnTrue(
                Constants.QUICK_PAYMENT_SHOW_CALCULATOR
            ),
            amountInputMode = getSP.getString(Constants.QUICK_PAYMENT_AMOUNT_INPUT_MODE)
                ?.takeIf { it.isNotBlank() }
                ?: Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS,
            amountWholeDigits = getSP.getInt(Constants.QUICK_PAYMENT_AMOUNT_WHOLE_DIGITS)
                .takeIf { it > 0 }
                ?: Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS,
            amountFractionDigits = getSP.getInt(Constants.QUICK_PAYMENT_AMOUNT_FRACTION_DIGITS)
                .takeIf { it >= 0 }
                ?: Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS
        )
    }

    fun saveQuickPaymentSettings(settings: QuickPaymentSettings) {
        with(setSP) {
            saveToSP(
                Constants.QUICK_PAYMENT_CURRENCY_SELECTION_SCROLL,
                settings.isCurrencyScrollEnabled
            )
            saveToSP(
                Constants.QUICK_PAYMENT_CASH_ACCOUNT_SELECTION_SCROLL,
                settings.isCashAccountScrollEnabled
            )
            saveToSP(
                Constants.QUICK_PAYMENT_SHOW_CALCULATOR,
                settings.isCalculatorButtonVisible
            )
            saveToSP(Constants.QUICK_PAYMENT_AMOUNT_INPUT_MODE, settings.amountInputMode)
            saveToSP(Constants.QUICK_PAYMENT_AMOUNT_WHOLE_DIGITS, settings.amountWholeDigits)
            saveToSP(Constants.QUICK_PAYMENT_AMOUNT_FRACTION_DIGITS, settings.amountFractionDigits)
        }
        _quickPaymentSettings.postValue(settings)
    }

    suspend fun getAllCurrencies(): List<Currencies> {
        return CurrenciesUseCase.getAllCurrenciesSortNameAsc(dbCurrencies)
    }

    suspend fun getAllCashAccounts(): List<CashAccount> {
        return CashAccountsUseCase.getAllCashAccountsSortNameAsc(dbCashAccount)
    }

    suspend fun getDefaultCurrency(): Currencies? {
        return CurrenciesUseCase.getDefaultCurrency(dbCurrencies)
    }

    suspend fun getDefaultCashAccount(): CashAccount? {
        return CashAccountsUseCase.getDefaultCashAccount(dbCashAccount)
    }

    fun setDefaultCurrency(currency: Currencies) {
        currency.currencyId?.let { currencyId ->
            launchIo {
                CurrenciesUseCase.setDefaultCurrency(dbCurrencies, currencyId)
                postCurrency(currencyId)
            }
        }
    }

    fun setDefaultCashAccount(cashAccount: CashAccount) {
        cashAccount.cashAccountId?.let { cashAccountId ->
            launchIo {
                CashAccountsUseCase.setDefaultCashAccount(dbCashAccount, cashAccountId)
                postCashAccount(cashAccountId)
            }
        }
    }
}
