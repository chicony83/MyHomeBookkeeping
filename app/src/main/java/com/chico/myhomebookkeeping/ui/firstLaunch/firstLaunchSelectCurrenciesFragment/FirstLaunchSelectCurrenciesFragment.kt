package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.MainActivity
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchSelectCurrenciesBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCurrencyAsDefaultDialogBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyByTextCallBack
import com.chico.myhomebookkeeping.ui.firstLaunch.FirstLaunchSetupFragment
import com.chico.myhomebookkeeping.utils.hideKeyboard
import kotlinx.coroutines.flow.collect

class FirstLaunchSelectCurrenciesFragment : Fragment() {
    private var _binding: FragmentFirstLaunchSelectCurrenciesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchSelectCurrenciesViewModel by viewModels(
        ownerProducer = { parentFragment ?: this }
    )
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var majorCurrenciesAdapter: FirstLaunchSelectCurrencyForSelectCurrencyAdapter
    private lateinit var cryptoCurrenciesAdapter: FirstLaunchSelectCurrencyForSelectCurrencyAdapter
    private var isMajorCurrenciesExpanded = true
    private var isCryptoCurrenciesExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchSelectCurrenciesBinding.inflate(inflater, container, false)

        with(viewModel) {
            selectedCurrenciesList.observe(viewLifecycleOwner) {
                binding.availableCurrenciesTitle.text =
                    getString(R.string.first_launch_currencies_selected_count, it.size)
                binding.submitButton.isEnabled = it.isNotEmpty()
                updateSelectedCurrencies()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.onDefaultCurrencyAdded.collect {
                val setupFragment = parentFragment as? FirstLaunchSetupFragment
                if (setupFragment != null) {
                    setupFragment.showCashAccountsAndCategoriesStep()
                    return@collect
                }

                (requireActivity() as MainActivity).mainActivityViewModel.setFirstLaunchFlag(false)

                findNavController().navigate(
                    R.id.nav_first_launch_fragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.nav_first_launch_select_currencies_fragment, true).build()
                )
            }
        }

        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)
        initCurrenciesLists()
        binding.majorCurrenciesHeader.setOnClickListener {
            isMajorCurrenciesExpanded = !isMajorCurrenciesExpanded
            renderCurrenciesSections()
        }
        binding.cryptoCurrenciesHeader.setOnClickListener {
            isCryptoCurrenciesExpanded = !isCryptoCurrenciesExpanded
            renderCurrenciesSections()
        }
        if (parentFragment is FirstLaunchSetupFragment) {
            binding.submitButton.visibility = View.GONE
        } else {
            binding.submitButton.setOnClickListener {
                submitStep()
            }
        }
    }

    fun submitStep() {
        when (viewModel.isCurrenciesListNotEmpty()) {
            true -> {
                val setupFragment = parentFragment as? FirstLaunchSetupFragment
                if (setupFragment != null) {
                    setupFragment.showDefaultCurrencyStep()
                } else {
                    showSelectDefaultCurrencyDialog()
                }
            }
            false -> Toast.makeText(
                context,
                R.string.first_launch_currencies_empty_message,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initCurrenciesLists() {
        val selectedCurrenciesIso = getSelectedCurrenciesIso()
        val listener = object : OnChangeCurrencyByTextCallBack {
            override fun onClick(string: String) {
                viewModel.toggleCurrencySelection(string)
            }
        }

        majorCurrenciesAdapter = FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
            FirstLaunchCurrenciesList.getFiatCurrenciesList(),
            selectedCurrenciesIso,
            listener
        )
        cryptoCurrenciesAdapter = FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
            FirstLaunchCurrenciesList.getCryptoCurrenciesList(),
            selectedCurrenciesIso,
            listener
        )

        binding.majorCurrenciesHolder.adapter = majorCurrenciesAdapter
        binding.cryptoCurrenciesHolder.adapter = cryptoCurrenciesAdapter
        renderCurrenciesSections()
    }

    private fun updateSelectedCurrencies() {
        if (!::majorCurrenciesAdapter.isInitialized) return

        val selectedCurrenciesIso = getSelectedCurrenciesIso()
        majorCurrenciesAdapter.updateSelectedCurrencies(selectedCurrenciesIso)
        cryptoCurrenciesAdapter.updateSelectedCurrencies(selectedCurrenciesIso)
    }

    private fun renderCurrenciesSections() {
        renderCurrenciesSection(
            isMajorCurrenciesExpanded,
            binding.majorCurrenciesHolder,
            binding.majorCurrenciesExpandImageView
        )
        renderCurrenciesSection(
            isCryptoCurrenciesExpanded,
            binding.cryptoCurrenciesHolder,
            binding.cryptoCurrenciesExpandImageView
        )
    }

    private fun renderCurrenciesSection(
        isExpanded: Boolean,
        recyclerView: View,
        expandImageView: ImageView
    ) {
        recyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        expandImageView.setImageResource(
            if (isExpanded) R.drawable.ic_expand_remove
            else R.drawable.ic_expand_add
        )
    }

    private fun getSelectedCurrenciesIso(): Set<String> {
        return viewModel.getSelectedCurrencies().mapNotNull { it.iso4217 }.toSet()
    }

    @SuppressLint("CheckResult")
    private fun showSelectDefaultCurrencyDialog() {
        val layout = layoutInflater.inflate(R.layout.dialog_select_currency_as_default, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(layout)
            .create()
        var defaultCurrency = viewModel.getSelectedCurrencies()[0]
        val adapter = SelectDefaultCurrencyAdapter(
            currencies = viewModel.getSelectedCurrencies(),
            selectedCurrencyIso = defaultCurrency.iso4217
        ) {
            defaultCurrency = it
        }

        layout.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.iconsHolderLayout).adapter = adapter
        layout.findViewById<Button>(R.id.submitButton).setOnClickListener {
            dialog.dismiss()
            saveSelectedCurrencies(defaultCurrency)
        }
        layout.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun saveSelectedCurrencies(defaultCurrency: Currencies) {
        viewModel.addCurrenciesToDB(viewModel.getSelectedCurrencies().map {
            it.copy(isCurrencyDefault = it.iso4217 == defaultCurrency.iso4217)
        }.sortedByDescending { it.isCurrencyDefault })
    }

    private class SelectDefaultCurrencyAdapter(
        private val currencies: List<Currencies>,
        selectedCurrencyIso: String?,
        private val onCurrencySelected: (Currencies) -> Unit
    ) : androidx.recyclerview.widget.RecyclerView.Adapter<SelectDefaultCurrencyAdapter.ViewHolder>() {
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
        ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {
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
