package com.chico.myhomebookkeeping.ui.calc

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.checks.ModelCheck
import com.chico.myhomebookkeeping.db.dao.*
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.*
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.domain.*
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.utils.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CalcDialogViewModel(
    val app: Application
) : AndroidViewModel(app) {

    private val _onCalcAmountSelected = MutableLiveData<String?>()
    val onCalcAmountSelected: LiveData<String?>
        get() = _onCalcAmountSelected

    fun setCalcSelectedAmount(amount: String, decimalSeparatorSymbol: String) {
        val clearedAmount = removeWhitespacesAndCommas(amount, decimalSeparatorSymbol)
        viewModelScope.launch {
            if (clearedAmount.hasExpression()) {
                _onCalcAmountSelected.value = ""
            } else {
                _onCalcAmountSelected.value = clearedAmount
            }
        }
    }

    fun resetCalcSelectedAmount() {
        _onCalcAmountSelected.value = null
    }
}