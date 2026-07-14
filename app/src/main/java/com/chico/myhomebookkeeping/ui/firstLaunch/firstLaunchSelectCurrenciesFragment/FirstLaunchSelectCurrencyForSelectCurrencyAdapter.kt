package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFirstLaunchForSelectCurrencyBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyByTextCallBack

class FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
    private val currenciesForSelectList: List<Currencies>,
    private val selectedCurrenciesIso: Set<String>,
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

    override fun getItemCount() = currenciesForSelectList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemFirstLaunchForSelectCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currencies: Currencies) {
            with(binding){
                nameCurrency.text = currencies.currencyName
                isoCurrency.text = currencies.iso4217
                currencyCheckBox.isChecked = selectedCurrenciesIso.contains(currencies.iso4217)
                firstLaunchCurrencyItem.setOnClickListener {
                    currencies.iso4217?.let {
                        it1->listener.onClick(it1)
                    }
                }
                currencyCheckBox.setOnClickListener {
                    currencies.iso4217?.let {
                        it1->listener.onClick(it1)
                    }
                }
            }
        }
    }
}
