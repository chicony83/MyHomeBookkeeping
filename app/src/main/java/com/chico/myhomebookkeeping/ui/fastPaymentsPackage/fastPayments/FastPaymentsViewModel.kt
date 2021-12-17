package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chico.myhomebookkeeping.db.dao.FastPaymentsDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.FastPayments
import com.chico.myhomebookkeeping.domain.NewFastPaymentsUseCase
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.utils.launchIo

class FastPaymentsViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val db: FastPaymentsDao = dataBase.getDataBase(app.applicationContext).fastPaymentsDao()

    private val _fastPaymentsList = MutableLiveData<List<FastPayments>>()
    val fastPaymentsList: LiveData<List<FastPayments>> get() = _fastPaymentsList

    init {
        getFastPaymentsList()
    }

    private fun getFastPaymentsList() {
        launchIo {
            _fastPaymentsList.postValue(NewFastPaymentsUseCase.getAllFastPayments(db))
//            Message.log(" size fast payments list = ${_fastPaymentsList.value?.size}")
        }
    }
}