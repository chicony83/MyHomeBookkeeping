package com.chico.myhomebookkeeping.ui.paymentPackage.changeTransfer

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.ChangeMoneyMovingUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.domain.MoneyMovingUseCase
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.PaymentTypeIds
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving.QuickPaymentSettings
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import com.chico.myhomebookkeeping.utils.parseTimeToMillis
import java.util.TimeZone

class ChangeTransferViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(Constants.SP_NAME, MODE_PRIVATE)
    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(sharedPreferences.edit())
    private val modelCheck = ModelCheck()

    private val dbMoneyMovement: MoneyMovementDao =
        dataBase.getDataBase(app.applicationContext).moneyMovementDao()
    private val dbCashAccount: CashAccountDao =
        dataBase.getDataBase(app.applicationContext).cashAccountDao()
    private val dbCurrencies: CurrenciesDao =
        dataBase.getDataBase(app.applicationContext).currenciesDao()

    private val _dateTime = MutableLiveData<String>()
    val dataTime: LiveData<String> get() = _dateTime

    private val _selectedCurrency = MutableLiveData<Currencies>()
    val selectedCurrency: LiveData<Currencies> get() = _selectedCurrency

    private val _selectedTransferCurrency = MutableLiveData<Currencies>()
    val selectedTransferCurrency: LiveData<Currencies> get() = _selectedTransferCurrency

    private val _selectedCashAccount = MutableLiveData<CashAccount>()
    val selectedCashAccount: LiveData<CashAccount> get() = _selectedCashAccount

    private val _selectedTransferCashAccount = MutableLiveData<CashAccount>()
    val selectedTransferCashAccount: LiveData<CashAccount> get() = _selectedTransferCashAccount

    private val _enteredDescription = MutableLiveData<String>()
    val enteredDescription: LiveData<String> get() = _enteredDescription

    private val _enteredAmount = MutableLiveData<Double?>()
    val enteredAmount: LiveData<Double?> get() = _enteredAmount

    private val _enteredTransferAmount = MutableLiveData<Double?>()
    val enteredTransferAmount: LiveData<Double?> get() = _enteredTransferAmount

    private val _enteredTransferRate = MutableLiveData<Double?>()
    val enteredTransferRate: LiveData<Double?> get() = _enteredTransferRate

    private val _submitButtonText = MutableLiveData<String>()
    val submitButton: LiveData<String> get() = _submitButtonText

    private var date = 0L
    private var time = 0L
    private var sourceTransferRowId = Constants.MINUS_ONE_VAL_LONG
    private var destinationTransferRowId = Constants.MINUS_ONE_VAL_LONG
    private var transferGroupIdForChange: Long? = null

    fun getTransferForChange() {
        _submitButtonText.postValue(app.getString(R.string.text_on_button_save))
        launchIo {
            val selectedId = getSP.getLong(Constants.ARGS_CHANGE_PAYMENT_ID)
            if (!modelCheck.isPositiveValue(selectedId)) return@launchIo

            val selectedTransferRow =
                MoneyMovingUseCase.getOneMoneyMoving(dbMoneyMovement, selectedId)
                    ?: return@launchIo
            val transferGroupId = selectedTransferRow.transferGroupId ?: return@launchIo
            transferGroupIdForChange = transferGroupId

            val transferRows = ChangeMoneyMovingUseCase.getTransferRows(
                dbMoneyMovement,
                transferGroupId
            )
            val sourceRow = transferRows.firstOrNull {
                it.transferDirection == PaymentTypeIds.TRANSFER_DIRECTION_FROM
            } ?: return@launchIo
            val destinationRow = transferRows.firstOrNull {
                it.transferDirection == PaymentTypeIds.TRANSFER_DIRECTION_TO
            } ?: return@launchIo

            sourceTransferRowId = sourceRow.id ?: Constants.MINUS_ONE_VAL_LONG
            destinationTransferRowId = destinationRow.id ?: Constants.MINUS_ONE_VAL_LONG

            val dateTimeSP = getSP.getLong(Constants.ARGS_NEW_PAYMENT_DATE_TIME_KEY)
            val cashAccountSP = getSP.getInt(Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY)
            val transferCashAccountSP = getSP.getInt(Constants.ARGS_NEW_PAYMENT_TRANSFER_CASH_ACCOUNT_KEY)
            val currencySP = getSP.getInt(Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY)
            val transferCurrencySP = getSP.getInt(Constants.ARGS_NEW_PAYMENT_TRANSFER_CURRENCY_KEY)
            val amountSP = getSP.getString(Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY).toString()
            val transferAmountSP =
                getSP.getString(Constants.ARGS_NEW_PAYMENT_TRANSFER_AMOUNT_KEY).toString()
            val transferRateSP =
                getSP.getString(Constants.ARGS_NEW_PAYMENT_TRANSFER_RATE_KEY).toString()
            val descriptionSP =
                getSP.getString(Constants.ARGS_NEW_PAYMENT_DESCRIPTION_KEY).toString()

            postDateTime(if (modelCheck.isPositiveValue(dateTimeSP)) dateTimeSP else sourceRow.timeStamp)
            postCashAccount(if (modelCheck.isPositiveValue(cashAccountSP)) cashAccountSP else sourceRow.cashAccount)
            postTransferCashAccount(
                if (modelCheck.isPositiveValue(transferCashAccountSP)) {
                    transferCashAccountSP
                } else {
                    destinationRow.cashAccount
                }
            )
            postCurrency(if (modelCheck.isPositiveValue(currencySP)) currencySP else sourceRow.currency)
            postTransferCurrency(
                if (modelCheck.isPositiveValue(transferCurrencySP)) {
                    transferCurrencySP
                } else {
                    destinationRow.currency
                }
            )
            postAmount(
                if (modelCheck.isPositiveValue(amountSP)) Around.double(amountSP) else sourceRow.amount
            )
            postTransferAmount(
                if (modelCheck.isPositiveValue(transferAmountSP)) {
                    Around.double(transferAmountSP)
                } else {
                    destinationRow.amount
                }
            )
            postTransferRate(
                if (modelCheck.isPositiveValue(transferRateSP)) {
                    Around.double(transferRateSP)
                } else if (sourceRow.amount > 0) {
                    destinationRow.amount / sourceRow.amount
                } else {
                    0.0
                }
            )
            postDescription(if (descriptionSP.isNotEmpty()) descriptionSP else sourceRow.description)
        }
    }

    private fun postDateTime(dateTime: Long) {
        _dateTime.postValue(dateTime.parseTimeFromMillis())
    }

    private suspend fun postCashAccount(id: Int) {
        _selectedCashAccount.postValue(CashAccountsUseCase.getOneCashAccountById(dbCashAccount, id))
    }

    private suspend fun postTransferCashAccount(id: Int) {
        _selectedTransferCashAccount.postValue(
            CashAccountsUseCase.getOneCashAccountById(dbCashAccount, id)
        )
    }

    private suspend fun postCurrency(id: Int) {
        _selectedCurrency.postValue(CurrenciesUseCase.getOneCurrency(dbCurrencies, id))
    }

    private suspend fun postTransferCurrency(id: Int) {
        _selectedTransferCurrency.postValue(CurrenciesUseCase.getOneCurrency(dbCurrencies, id))
    }

    private fun postAmount(amount: Double) {
        _enteredAmount.postValue(amount)
    }

    private fun postTransferAmount(amount: Double) {
        _enteredTransferAmount.postValue(amount)
    }

    private fun postTransferRate(rate: Double) {
        _enteredTransferRate.postValue(rate)
    }

    private fun postDescription(description: String) {
        _enteredDescription.postValue(description)
    }

    fun saveDataToSP(
        amount: Double,
        description: String,
        transferAmount: Double,
        transferRate: Double
    ) {
        with(setSP) {
            saveToSP(Constants.ARGS_NEW_PAYMENT_DATE_TIME_KEY, _dateTime.value?.parseTimeToMillis())
            saveToSP(Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY, _selectedCashAccount.value?.cashAccountId)
            saveToSP(
                Constants.ARGS_NEW_PAYMENT_TRANSFER_CASH_ACCOUNT_KEY,
                _selectedTransferCashAccount.value?.cashAccountId
            )
            saveToSP(Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY, _selectedCurrency.value?.currencyId)
            saveToSP(
                Constants.ARGS_NEW_PAYMENT_TRANSFER_CURRENCY_KEY,
                _selectedTransferCurrency.value?.currencyId
            )
            saveToSP(Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY, amount.toString())
            saveToSP(Constants.ARGS_NEW_PAYMENT_TRANSFER_AMOUNT_KEY, transferAmount.toString())
            saveToSP(Constants.ARGS_NEW_PAYMENT_TRANSFER_RATE_KEY, transferRate.toString())
            saveToSP(Constants.ARGS_NEW_PAYMENT_DESCRIPTION_KEY, description)
        }
    }

    suspend fun changeTransfer(amount: Double, transferAmount: Double, description: String): Int {
        val dateTime = dataTime.value?.parseTimeToMillis() ?: 0
        val sourceResult = ChangeMoneyMovingUseCase.changeTransferLine(
            db = dbMoneyMovement,
            id = sourceTransferRowId,
            dateTime = dateTime,
            amount = amount,
            cashAccountId = _selectedCashAccount.value?.cashAccountId ?: 0,
            currencyId = _selectedCurrency.value?.currencyId ?: 0,
            description = description
        )
        val destinationResult = ChangeMoneyMovingUseCase.changeTransferLine(
            db = dbMoneyMovement,
            id = destinationTransferRowId,
            dateTime = dateTime,
            amount = transferAmount,
            cashAccountId = _selectedTransferCashAccount.value?.cashAccountId ?: 0,
            currencyId = _selectedTransferCurrency.value?.currencyId ?: 0,
            description = description
        )
        return sourceResult + destinationResult
    }

    suspend fun deleteTransfer(): Int {
        val transferGroupId = transferGroupIdForChange ?: return 0
        return ChangeMoneyMovingUseCase.deleteTransferRows(dbMoneyMovement, transferGroupId)
    }

    fun clearSPAfterSave() {
        with(setSP) {
            saveToSP(Constants.ARGS_CHANGE_PAYMENT_ID, Constants.MINUS_ONE_VAL_LONG)
            saveToSP(Constants.ARGS_NEW_PAYMENT_DATE_TIME_KEY, Constants.MINUS_ONE_VAL_LONG)
            saveToSP(Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY, Constants.MINUS_ONE_VAL_INT)
            saveToSP(Constants.ARGS_NEW_PAYMENT_TRANSFER_CASH_ACCOUNT_KEY, Constants.MINUS_ONE_VAL_INT)
            saveToSP(Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY, Constants.MINUS_ONE_VAL_INT)
            saveToSP(Constants.ARGS_NEW_PAYMENT_TRANSFER_CURRENCY_KEY, Constants.MINUS_ONE_VAL_INT)
            saveToSP(Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY, "")
            saveToSP(Constants.ARGS_NEW_PAYMENT_TRANSFER_AMOUNT_KEY, "")
            saveToSP(Constants.ARGS_NEW_PAYMENT_TRANSFER_RATE_KEY, "")
            saveToSP(Constants.ARGS_NEW_PAYMENT_DESCRIPTION_KEY, "")
            saveToSP(
                Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_SELECT_MODE_KEY,
                Constants.CASH_ACCOUNT_SELECT_MODE_SOURCE
            )
            saveToSP(
                Constants.ARGS_NEW_PAYMENT_CURRENCY_SELECT_MODE_KEY,
                Constants.CURRENCY_SELECT_MODE_SOURCE
            )
        }
    }

    fun setDate(it: Long?) {
        date = it ?: 0
    }

    fun setTime(hour: Int, minute: Int) {
        val timeZone = TimeZone.getDefault().getOffset(System.currentTimeMillis())
        time = (((hour * 60 * 60 * 1000) - timeZone).toLong()) + ((minute * 60 * 1000).toLong())
    }

    fun setDateTimeOnButton() {
        _dateTime.postValue((date + time).parseTimeFromMillis())
    }

    fun setSourceCashAccountSelectMode() {
        setSP.saveToSP(
            Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_SELECT_MODE_KEY,
            Constants.CASH_ACCOUNT_SELECT_MODE_SOURCE
        )
    }

    fun setDestinationCashAccountSelectMode() {
        setSP.saveToSP(
            Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_SELECT_MODE_KEY,
            Constants.CASH_ACCOUNT_SELECT_MODE_DESTINATION
        )
    }

    fun setSourceCurrencySelectMode() {
        setSP.saveToSP(
            Constants.ARGS_NEW_PAYMENT_CURRENCY_SELECT_MODE_KEY,
            Constants.CURRENCY_SELECT_MODE_SOURCE
        )
    }

    fun setDestinationCurrencySelectMode() {
        setSP.saveToSP(
            Constants.ARGS_NEW_PAYMENT_CURRENCY_SELECT_MODE_KEY,
            Constants.CURRENCY_SELECT_MODE_DESTINATION
        )
    }

    fun isTransferAccountsDifferent(): Boolean {
        return _selectedCashAccount.value?.cashAccountId !=
            _selectedTransferCashAccount.value?.cashAccountId
    }

    fun isCashAccountNotNull() = selectedCashAccount.value != null
    fun isTransferCashAccountNotNull() = selectedTransferCashAccount.value != null
    fun isCurrencyNotNull() = selectedCurrency.value != null
    fun isTransferCurrencyNotNull() = selectedTransferCurrency.value != null

    fun getQuickPaymentSettings(): QuickPaymentSettings {
        return QuickPaymentSettings(
            isCurrencyScrollEnabled = false,
            isCashAccountScrollEnabled = false,
            isCalculatorButtonVisible = true,
            amountInputMode = Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS,
            amountWholeDigits = Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS,
            amountFractionDigits = Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS
        )
    }
}
