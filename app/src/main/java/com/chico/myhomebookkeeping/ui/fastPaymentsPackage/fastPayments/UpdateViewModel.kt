package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import com.chico.myhomebookkeeping.db.dao.IconCategoryDao
import com.chico.myhomebookkeeping.icons.AddIconCategories
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.ConstantsOfUpdate
import com.chico.myhomebookkeeping.sp.GetSP
import com.chico.myhomebookkeeping.sp.SetSP
import com.chico.myhomebookkeeping.update.Update43To44
import com.chico.myhomebookkeeping.update.Update44To45
import com.chico.myhomebookkeeping.utils.launchIo

class UpdateViewModel(
    val app: Application
) : AndroidViewModel(app) {
    private val spName = Constants.SP_NAME
    private val sharedPreferences:SharedPreferences = app.getSharedPreferences(spName,Context.MODE_PRIVATE)
    private val getSP = GetSP(sharedPreferences)
    private val setSP = SetSP(sharedPreferences.edit())

    fun update() {
        update_43_to_44_after_migration_2_to_3()
        update_44_to_45()
    }

    private fun update_44_to_45() {
        val updateKey = ConstantsOfUpdate.UPDATE_44_TO_45
        if (!getSP.getBooleanDefFalse(updateKey)){
            launchIo {
                val update44To45 = Update44To45()
                update44To45.update(app)
            }
        }
    }

    private fun update_43_to_44_after_migration_2_to_3() {
        val updateKey = ConstantsOfUpdate.UPDATE_43_TO_44_AFTER_MIGRATION_2_TO_3
        if (!getSP.getBooleanDefFalse(updateKey)){
            launchIo {
                val update43To44 = Update43To44()
                update43To44.update(app)
            }
            setSP.saveToSP(updateKey,true)
        }
    }
}