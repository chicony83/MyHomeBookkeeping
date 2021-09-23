package com.chico.myhomebookkeeping.ui.cashAccount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCashAccountBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount

class CashAccountAdapter(
    private var cashAccountList: List<CashAccount>,
    val listener: OnItemViewClickListener
) :
    RecyclerView.Adapter<CashAccountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = RecyclerViewItemCashAccountBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cashAccountList[position])
    }

    override fun getItemCount() = cashAccountList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemCashAccountBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cashAccount: CashAccount) {
            with(binding) {
                nameCashAccount.text = cashAccount.accountName

                if (cashAccount.bankAccountNumber.toString().isBlank()){
                    numberCashAccount.text = ""
                }
                else{
                    numberCashAccount.text = cashAccount.bankAccountNumber.toString()
                }
                cashAccountItem.setOnClickListener {
                    cashAccount.cashAccountId?.let { it1 -> listener.onClick(it1) }
                }
            }
        }
    }
}


