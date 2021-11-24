package com.chico.myhomebookkeeping.ui.moneyMoving.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemSelectedForChange
import com.chico.myhomebookkeeping.db.FullMoneyMoving
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.lang.IllegalStateException


class MoneyMovingSelectDialogFragment(
    val fullMoneyMoving: FullMoneyMoving?,
    val onItemSelectedForChange: OnItemSelectedForChange
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_money_moving,null)

            bindLayout(layout)

            builder.setView(layout)
//                .setPositiveButton(R.string.text_on_button_change) { _, _ ->
//                    run {
//                        fullMoneyMoving?.id?.toInt()
//                            ?.let { it1 -> onItemSelectedForChange.onSelect(it1) }
//                    }
////                    Message.log("---change---")
//                }
                .setNeutralButton(R.string.text_on_button_change){_,_->
                    run {
                        fullMoneyMoving?.id?.toInt()
                            ?.let { it1 -> onItemSelectedForChange.onSelect(it1) }
                    }

                }
                .setNegativeButton(R.string.text_on_button_cancel) { _, _ ->
                    dialog?.cancel()
                }

            builder.create()

        } ?: throw IllegalStateException("Activity cant be null")
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
            if (!fullMoneyMoving.description.isNullOrEmpty()){
                description.visibility = View.VISIBLE
                description.text = fullMoneyMoving.description
            }
        }
    }
}