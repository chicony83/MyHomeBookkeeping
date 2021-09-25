package com.chico.myhomebookkeeping.ui.cashAccount

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCashAccountBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.helpers.UiHelper

class CashAccountAdapter(
    private var cashAccountList: List<CashAccount>,
    val listener: OnItemViewClickListener
) :
    RecyclerView.Adapter<CashAccountAdapter.ViewHolder>() {
    val uiHelper = UiHelper()
    private lateinit var context: Context
    private lateinit var textCardNumber: String
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = RecyclerViewItemCashAccountBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        getStrings()
        return ViewHolder(binding)
    }

    private fun getStrings() {
        textCardNumber = context.getString(R.string.sign_number_of_account)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cashAccountList[position])
    }

    override fun getItemCount() = cashAccountList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemCashAccountBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(cashAccount: CashAccount) {
            with(binding) {
                nameCashAccount.text = cashAccount.accountName

                if (cashAccount.bankAccountNumber.isEmpty()) {
                    uiHelper.hideUiElement(numberCashAccount)
                    numberCashAccount.text = null
                } else {
                    numberCashAccount.text = "$textCardNumber  ${cashAccount.bankAccountNumber}"
                }
                cashAccountItem.setOnClickListener {
                    cashAccount.cashAccountId?.let { it1 -> listener.onClick(it1) }
                }
            }
        }
    }

}


