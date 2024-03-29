package com.chico.myhomebookkeeping.ui.currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemCurrenciesBinding
import com.chico.myhomebookkeeping.db.entity.Currencies

class CurrenciesAdapter(
    private val currenciesList: List<Currencies>,
    val listener: OnItemViewClickListener
) :
    RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = RecyclerViewItemCurrenciesBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencies = currenciesList[position])
    }

    override fun getItemCount() = currenciesList.size

    inner class ViewHolder(
        private val binding: RecyclerViewItemCurrenciesBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currencies: Currencies) {
            with(binding) {

                root.contentDescription = currencies.currencyName
                nameCurrency.text = currencies.currencyName
                nameShortCurrency.text = currencies.currencyNameShort
                iSOCurrency.text = currencies.iso4217

                currenciesItem.setOnLongClickListener {
                    currencies.currencyId?.let { it1 -> listener.onLongClick(it1) }
                    true
                }
                currenciesItem.setOnClickListener {
                    currencies.currencyId?.let { it1 -> listener.onShortClick(it1) }
                }
            }
        }
    }
}