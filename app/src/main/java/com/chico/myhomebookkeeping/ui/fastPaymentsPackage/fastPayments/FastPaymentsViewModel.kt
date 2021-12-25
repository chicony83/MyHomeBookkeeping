package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.chico.myhomebookkeeping.db.FullFastPayment
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.simpleQuery.FastPaymentCreateSimpleQuery
import com.chico.myhomebookkeeping.domain.FastPaymentsUseCase
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class FastPaymentsViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: FastPaymentsDao = dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    private val _fastPaymentsList = MutableLiveData<List<FullFastPayment>?>()
    val fastPaymentsList: MutableLiveData<List<FullFastPayment>?> get() = _fastPaymentsList

    init {
//        getFastPaymentsList()

    }

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

//    private fun getFastPaymentsList() {
//        launchIo {
//            _fastPaymentsList.postValue(NewFastPaymentsUseCase.getAllFastPayments(db))
////            Message.log(" size fast payments list = ${_fastPaymentsList.value?.size}")
//        }
//    }
}