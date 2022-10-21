package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFirstLaunchSelectedCurrencyBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Message

class FirstLaunchSelectCurrencySelectedCurrencyAdapter(
    private val selectedCurrenciesList: List<Currencies>
) : RecyclerView.Adapter<FirstLaunchSelectCurrencySelectedCurrencyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemFirstLaunchSelectedCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(selectedCurrenciesList[position])
    }

    override fun getItemCount() = selectedCurrenciesList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemFirstLaunchSelectedCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currencies: Currencies) {
            with(binding) {
                nameCurrency.text = currencies.currencyName
                firstLaunchSelectedCurrencyItem.setOnClickListener {
                    currencies.iso4217?.let {
//                        it->Message.log("iso - $it")
                    }
                }
            }
        }

    }

}