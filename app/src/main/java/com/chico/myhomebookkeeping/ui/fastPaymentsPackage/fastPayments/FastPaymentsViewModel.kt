package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullFastPayment
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.db.simpleQuery.FastPaymentCreateSimpleQuery
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.launchForResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class FastPaymentsViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val argsIdFastPaymentForChangeKey = Constants.ARGS_CHANGE_FAST_PAYMENT_ID
    private val argsCashAccountCreateKey = Constants.ARGS_NEW_PAYMENT_CASH_ACCOUNT_KEY
    private val argsCurrencyCreateKey = Constants.ARGS_NEW_PAYMENT_CURRENCY_KEY
    private val argsCategoryCreateKey = Constants.ARGS_NEW_PAYMENT_CATEGORY_KEY
    private val argsAmountCreateKey = Constants.ARGS_NEW_PAYMENT_AMOUNT_KEY
    private val argsDescriptionCreateKey = Constants.ARGS_NEW_PAYMENT_DESCRIPTION_KEY

    private val spName = Constants.SP_NAME
    private val sharedPreferences: SharedPreferences =
        app.getSharedPreferences(spName, Context.MODE_PRIVATE)

    private val setSP = SetSP(spEditor = sharedPreferences.edit())

    private val db: FastPaymentsDao = dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    private val _fastPaymentsList = MutableLiveData<List<FullFastPayment>?>()
    val fastPaymentsList: MutableLiveData<List<FullFastPayment>?> get() = _fastPaymentsList

    internal fun getFullFastPaymentsList() {
        runBlocking {
            val listFullFastPayments: Deferred<List<FullFastPayment>?> =
                async(Dispatchers.IO) { loadListFullFastPayments() }

            postListFullFastPayments(listFullFastPayments.await())
//            val query = FastPaymentCreateSimpleQuery.createQueryList()
//            _fastPaymentsList.postValue(FastPaymentsUseCase.getListFullFastPayments(db,query))
        }
    }

    private fun postListFullFastPayments(list: List<FullFastPayment>?) {
        _fastPaymentsList.postValue(list)
    }

    private suspend fun loadListFullFastPayments() = launchForResult {
        val query = FastPaymentCreateSimpleQuery.createQueryList()

        return@launchForResult getListFullFastPaymentsFromUseCase(query)
    }

    private suspend fun getListFullFastPaymentsFromUseCase(query: SimpleSQLiteQuery): List<FullFastPayment>? {
        return FastPaymentsUseCase.getListFullFastPayments(
            db, query
        )
    }

    suspend fun loadSelectedFullFastPayment(id: Long): FullFastPayment? {
        return FastPaymentsUseCase.getOneFullFastPayment(
            db,
            FastPaymentCreateSimpleQuery.createQueryOneFullFastPayment(id)
        )
    }

    fun saveARGSForPay(fastPayment: FullFastPayment?) {
        runBlocking {
            val id = fastPayment?.id ?: 0
            val selectedFastPayment: Deferred<FastPayments?> = async(Dispatchers.IO) {
                FastPaymentsUseCase.getOneFastPayment(id, db)
            }
            saveToSp(selectedFastPayment.await())
        }
    }

    private fun saveToSp(fastPayments: FastPayments?) {
        with(setSP) {
            saveToSP(argsCashAccountCreateKey, fastPayments?.cashAccountId)
            saveToSP(argsCurrencyCreateKey,fastPayments?.currencyId)
            saveToSP(argsCategoryCreateKey,fastPayments?.categoryId)
            saveToSP(argsAmountCreateKey,fastPayments?.amount.toString())
            saveToSP(argsDescriptionCreateKey,fastPayments?.description)
        }
    }

    fun saveIdFastPaymentForChange(id: Long) {
        setSP.saveToSP(argsIdFastPaymentForChangeKey,id)
    }

//    private suspend fun loadSelectedFastPayment(id: Long): FastPayments? {
//        return FastPaymentsUseCase.getOneFastPayment(id,db)
//    }

//    private fun getFastPaymentsList() {
//        launchIo {
//            _fastPaymentsList.postValue(NewFastPaymentsUseCase.getAllFastPayments(db))
////            Message.log(" size fast payments list = ${_fastPaymentsList.value?.size}")
//        }
//    }
}