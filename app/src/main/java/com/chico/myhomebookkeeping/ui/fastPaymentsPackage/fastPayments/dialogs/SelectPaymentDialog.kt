package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.fastPayments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.db.FullFastPayment
import java.lang.IllegalStateException

class SelectPaymentDialog(
    private val fastPayment: FullFastPayment?
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_select_fast_payment, null)

            buildLayout(layout)
            builder.setView(layout)

            val selectButton = layout.findViewById<Button>(R.id.selectButton)
            val changeButton = layout.findViewById<Button>(R.id.changeButton)
            val cancelButton = layout.findViewById<Button>(R.id.cancelButton)

            cancelButton.setOnClickListener { dialogCancel() }

            builder.create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun dialogCancel() {
        dialog?.cancel()
    }

    private fun buildLayout(layout: View) {
        val name = layout.findViewById<TextView>(R.id.name_fast_payment)
        val cashAccount = layout.findViewById<TextView>(R.id.cashAccountName)
        val currency = layout.findViewById<TextView>(R.id.currencyName)
        val category = layout.findViewById<TextView>(R.id.categoryName)
        val rating = layout.findViewById<ImageView>(R.id.ratingImg)
        val description = layout.findViewById<TextView>(R.id.description)
        val amount = layout.findViewById<TextView>(R.id.amount)

        if (fastPayment != null) {
            name.text = fastPayment.nameFastPayment
            cashAccount.text = fastPayment.cashAccountNameValue
            currency.text = fastPayment.currencyNameValue
            category.text = fastPayment.categoryNameValue
            rating.setImageResource(getRatingImage())

            description.text = fastPayment.description
            if (fastPayment.description.isNullOrEmpty()){
                description.visibility = View.GONE
            }
            if (fastPayment.amount.toString().isNotEmpty()){
                val number = fastPayment.amount?:0.0
                if (number>0){
                    amount.text = fastPayment.amount.toString()
                }
                if (number<=0){
                    amount.text = "-"
                }
            }
        }
    }

    private fun getRatingImage(): Int {
        return when (fastPayment?.rating) {
            0 -> R.drawable.rating1
            1 -> R.drawable.rating2
            2 -> R.drawable.rating3
            3 -> R.drawable.rating4
            4 -> R.drawable.rating5
            else -> R.drawable.rating1

        }
    }
}