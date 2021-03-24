package com.chico.myhomebookkeeping.ui.cashAccount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.CashAccountRecyclerViewItemBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount

class CashAccountAdapter(private val cashAccountList:List<CashAccount>):
RecyclerView.Adapter<CashAccountAdapter.ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding = CashAccountRecyclerViewItemBinding
            .inflate(LayoutInflater.from(parent.context),parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cashAccountList[position])
    }

    override fun getItemCount() = cashAccountList.size

    class ViewHolder(private val binding: CashAccountRecyclerViewItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(cashAccount: CashAccount){
            with(binding){
                nameCashAccount.text = cashAccount.accountName
                numberCashAccount.text = cashAccount.bankAccountNumber.toString()
            }
        }
    }
}