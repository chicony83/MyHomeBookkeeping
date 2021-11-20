package com.chico.myhomebookkeeping.ui.moneyMoving.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R


class MoneyMovingSelectDialogFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Dialog")
        builder.setView(R.layout.dialog_select_money_moving)


//        my_cancel_btn = dialog!!.findViewById(R.id.datesetbtn) as Button

        return builder.create()
    }

}