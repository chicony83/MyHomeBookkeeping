package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFirstLaunchForSelectCurrencyBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyByTextCallBack

class FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
    private val currenciesForSelectList: List<Currencies>,
    private val listener: OnChangeCurrencyByTextCallBack
) : RecyclerView.Adapter<FirstLaunchSelectCurrencyForSelectCurrencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemFirstLaunchForSelectCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currenciesForSelectList[position])
    }

    override fun getItemCount(): Int {
        Message.log("----size of currencies for select list = ${currenciesForSelectList.size}")
    return currenciesForSelectList.size
    }

    inner class ViewHolder(
        private val binding: RecyclerViewItemFirstLaunchForSelectCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currencies: Currencies) {
            with(binding){
                nameCurrency.text = currencies.currencyName
                firstLaunchCurrencyItem.setOnClickListener {
                    currencies.iso4217?.let {
                        it1->listener.onClick(it1)
                    }
                }
            }
        }
    }
}