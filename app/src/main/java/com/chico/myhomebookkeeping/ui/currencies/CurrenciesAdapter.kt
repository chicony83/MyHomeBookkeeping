package com.chico.myhomebookkeeping.ui.currencies

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.CurrenciesRecyclerViewItemBinding
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
        val binding = CurrenciesRecyclerViewItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencies = currenciesList[position])
    }

    override fun getItemCount() = currenciesList.size

    inner class ViewHolder(
        private val binding: CurrenciesRecyclerViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currencies: Currencies) {
            with(binding) {
                nameCurrency.text = currencies.currencyName
                currenciesItem.setOnClickListener {
                    currencies.id?.let { it1 -> listener.onClick(it1)}
                }
            }
        }
    }
}