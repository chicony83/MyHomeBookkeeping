package com.chico.myhomebookkeeping.ui.cashAccount

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.CashAccountRecyclerViewItemBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount

class CashAccountAdapter(
    private var cashAccountList: List<CashAccount>,
    cashAccountViewModel: CashAccountViewModel,

//    val listener: OnCashAccountListener
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
        holder.bind(item)

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
        fun bind(cashAccount: CashAccount) {
            with(binding) {
                nameCashAccount.text = cashAccount.accountName
                numberCashAccount.text = cashAccount.bankAccountNumber.toString()

                cashAccountItem.setOnClickListener {
                     Log.i("TAG","---text---")
                }

            }
        }

    }
    interface OnCashAccountListener {
        fun onClick()
    }

}


