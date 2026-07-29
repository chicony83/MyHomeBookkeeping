package com.chico.myhomebookkeeping.ui.currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemFirstLaunchForSelectCurrencyBinding
import com.chico.myhomebookkeeping.db.entity.Currencies

class CurrencyCatalogAdapter(
    private val currencies: List<Currencies>,
    private val addedCurrencyKeys: Set<String>,
    selectedCurrencyKeys: Set<String>,
    private val onCurrencyClicked: (String) -> Unit
) : RecyclerView.Adapter<CurrencyCatalogAdapter.ViewHolder>() {
    private var selectedCurrencyKeys: Set<String> = selectedCurrencyKeys

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewItemFirstLaunchForSelectCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currencies[position])
    }

    override fun getItemCount() = currencies.size

    fun updateSelectedCurrencies(newSelectedCurrencyKeys: Set<String>) {
        val oldSelectedCurrencyKeys = selectedCurrencyKeys
        selectedCurrencyKeys = newSelectedCurrencyKeys

        currencies.forEachIndexed { index, currency ->
            val key = currency.catalogKey()
            if (oldSelectedCurrencyKeys.contains(key) != newSelectedCurrencyKeys.contains(key)) {
                notifyItemChanged(index)
            }
        }
    }

    inner class ViewHolder(
        private val binding: RecyclerViewItemFirstLaunchForSelectCurrencyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currency: Currencies) {
            val currencyKey = currency.catalogKey()
            val isAdded = addedCurrencyKeys.contains(currencyKey)
            val isChecked = isAdded || selectedCurrencyKeys.contains(currencyKey)

            with(binding) {
                currencySymbol.text = currency.currencyNameShort
                nameCurrency.text = currency.currencyName
                isoCurrency.text = currency.iso4217
                currencyCheckBox.isChecked = isChecked
                currencyCheckBox.isEnabled = !isAdded
                firstLaunchCurrencyItem.isEnabled = !isAdded
                firstLaunchCurrencyItem.alpha = if (isAdded) ADDED_CURRENCY_ALPHA else 1f
                firstLaunchCurrencyItem.setOnClickListener {
                    if (!isAdded) onCurrencyClicked(currencyKey)
                }
                currencyCheckBox.setOnClickListener {
                    if (!isAdded) onCurrencyClicked(currencyKey)
                }
            }
        }
    }

    private companion object {
        const val ADDED_CURRENCY_ALPHA = 0.62f
    }
}

fun Currencies.catalogKey(): String {
    return iso4217
        ?.takeIf { it.isNotBlank() }
        ?.uppercase()
        ?: currencyName.lowercase()
}
