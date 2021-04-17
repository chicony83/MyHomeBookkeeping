package com.chico.myhomebookkeeping.ui.cashAccount

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.CashAccountRecyclerViewItemBinding
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
        val binding = CashAccountRecyclerViewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cashAccountList[position]
        holder.bind(item,position)
    }

    override fun getItemCount() = cashAccountList.size

    inner class ViewHolder(
        private val binding: CashAccountRecyclerViewItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
//        init {
//            with(itemView){
//                setOnClickListener {
//                    Log.i("TAG","---text---")
//                }
//            }
//        }

        fun bind(cashAccount: CashAccount, id: Int) {
            with(binding) {
                nameCashAccount.text = cashAccount.accountName

                if (cashAccount.bankAccountNumber.toString().isBlank()){
                    numberCashAccount.text = ""
                }
                else{
                    numberCashAccount.text = cashAccount.bankAccountNumber.toString()
                }
                cashAccountItem.setOnClickListener {
                    cashAccount.id?.let { it1 -> listener.onClick(it1) }
                }
            }
        }
    }
}


