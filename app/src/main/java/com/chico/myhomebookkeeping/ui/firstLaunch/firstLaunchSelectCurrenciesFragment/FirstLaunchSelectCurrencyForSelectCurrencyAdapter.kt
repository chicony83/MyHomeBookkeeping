package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFirstLaunchForSelectCurrencyBinding
import com.chico.myhomebookkeeping.db.entity.Currencies

class FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
    private val currenciesForSelectList: List<Currencies>
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

            }
        }

    }


}