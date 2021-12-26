package com.chico.myhomebookkeeping.ui.moneyMoving.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.lang.IllegalStateException


class SelectMoneyMovingDialog(
    val fullMoneyMoving: FullMoneyMoving?,
    private val onItemSelectForChangeCallBack: OnItemSelectForChangeCallBack
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_money_moving, null)

            val changeButton = layout.findViewById<Button>(R.id.change_button)
            val cancelButton = layout.findViewById<Button>(R.id.cancel_button)

            bindLayout(layout)
            builder.setView(layout)

            changeButton.setOnClickListener {
                fullMoneyMoving?.id?.toInt()
                    ?.let { it1 -> onItemSelectForChangeCallBack.onSelect(it1) }
            }
            cancelButton.setOnClickListener {
                dialogCancel()
            }

            builder.create()

        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun bindLayout(layout: View) {

        val itemId = layout.findViewById<TextView>(R.id.itemId)
        val dateTime = layout.findViewById<TextView>(R.id.date_time_text)
        val amount = layout.findViewById<TextView>(R.id.amount)
        val currency = layout.findViewById<TextView>(R.id.currency)
        val category = layout.findViewById<TextView>(R.id.category)
        val cashAccount = layout.findViewById<TextView>(R.id.cash_account)
        val description = layout.findViewById<TextView>(R.id.description)

        if (fullMoneyMoving != null) {
            itemId.text = fullMoneyMoving.id.toString()
            dateTime.text = fullMoneyMoving.timeStamp.parseTimeFromMillis()
            amount.text = fullMoneyMoving.amount.toString()
            currency.text = fullMoneyMoving.currencyNameValue
            category.text = fullMoneyMoving.categoryNameValue
            cashAccount.text = fullMoneyMoving.cashAccountNameValue
            if (!fullMoneyMoving.description.isNullOrEmpty()) {
                description.visibility = View.VISIBLE
                description.text = fullMoneyMoving.description
            }
        }
    }
}