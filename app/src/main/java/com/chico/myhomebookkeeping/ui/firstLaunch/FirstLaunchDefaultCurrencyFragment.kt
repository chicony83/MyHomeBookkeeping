package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchDefaultCurrencyBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCurrencyAsDefaultDialogBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment.FirstLaunchCurrenciesList
import com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment.FirstLaunchSelectCurrenciesViewModel

class FirstLaunchDefaultCurrencyFragment : Fragment() {
    private var _binding: FragmentFirstLaunchDefaultCurrencyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchSelectCurrenciesViewModel by viewModels({ requireParentFragment() })
    private var defaultCurrency: Currencies? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchDefaultCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedCurrencies = viewModel.getSelectedCurrencies()
        defaultCurrency = selectedCurrencies.firstOrNull()
        binding.cryptoDefaultCurrencyWarning.visibility =
            if (hasCryptoCurrency(selectedCurrencies)) View.VISIBLE else View.GONE
        binding.defaultCurrencyHolder.adapter = SelectDefaultCurrencyAdapter(
            currencies = selectedCurrencies,
            selectedCurrencyIso = defaultCurrency?.iso4217
        ) {
            defaultCurrency = it
        }
    }

    fun submitStep() {
        val selectedDefaultCurrency = defaultCurrency ?: return
        saveSelectedCurrencies(selectedDefaultCurrency)
    }

    private fun saveSelectedCurrencies(defaultCurrency: Currencies) {
        viewModel.addCurrenciesToDB(viewModel.getSelectedCurrencies().map {
            it.copy(isCurrencyDefault = it.iso4217 == defaultCurrency.iso4217)
        }.sortedByDescending { it.isCurrencyDefault })
    }

    private fun hasCryptoCurrency(currencies: List<Currencies>): Boolean {
        return currencies.any { FirstLaunchCurrenciesList.isCryptoCurrency(it.iso4217) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class SelectDefaultCurrencyAdapter(
        private val currencies: List<Currencies>,
        selectedCurrencyIso: String?,
        private val onCurrencySelected: (Currencies) -> Unit
    ) : RecyclerView.Adapter<SelectDefaultCurrencyAdapter.ViewHolder>() {
        private var selectedCurrencyIso = selectedCurrencyIso

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerViewItemSelectCurrencyAsDefaultDialogBinding.inflate(
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

        inner class ViewHolder(
            private val binding: RecyclerViewItemSelectCurrencyAsDefaultDialogBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(currency: Currencies) {
                with(binding) {
                    currencySymbol.text = currency.currencyNameShort
                    nameCurrency.text = currency.currencyName
                    isoCurrency.text = currency.iso4217
                    defaultCurrencyRadioButton.isChecked = currency.iso4217 == selectedCurrencyIso
                    selectCurrencyAsDefaultItem.setOnClickListener {
                        selectCurrency(currency)
                    }
                }
            }

            private fun selectCurrency(currency: Currencies) {
                val previousIso = selectedCurrencyIso
                val currentPosition = adapterPosition
                selectedCurrencyIso = currency.iso4217
                onCurrencySelected(currency)
                currencies.indexOfFirst { it.iso4217 == previousIso }
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
                currentPosition
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
            }
        }
    }
}
