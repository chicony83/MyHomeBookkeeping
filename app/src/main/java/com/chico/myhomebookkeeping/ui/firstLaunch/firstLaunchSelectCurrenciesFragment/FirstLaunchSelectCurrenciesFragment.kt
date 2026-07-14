package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.chico.myhomebookkeeping.MainActivity
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchSelectCurrenciesBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyByTextCallBack
import com.chico.myhomebookkeeping.utils.hideKeyboard
import kotlinx.coroutines.flow.collect

class FirstLaunchSelectCurrenciesFragment : Fragment() {
    private var _binding: FragmentFirstLaunchSelectCurrenciesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLaunchSelectCurrenciesViewModel by viewModels()
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchSelectCurrenciesBinding.inflate(inflater, container, false)

        with(viewModel) {
            firstLaunchCurrenciesList.observe(viewLifecycleOwner) {
                renderCurrenciesList()
            }
            selectedCurrenciesList.observe(viewLifecycleOwner) {
                binding.availableCurrenciesTitle.text =
                    getString(R.string.first_launch_currencies_selected_count, it.size)
                binding.submitButton.isEnabled = it.isNotEmpty()
                renderCurrenciesList()
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.onDefaultCurrencyAdded.collect {
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
        binding.showMoreCurrenciesButton.setOnClickListener {
            viewModel.showMoreCurrencies()
            binding.showMoreCurrenciesButton.visibility = View.GONE
        }
        binding.showCryptoCurrenciesButton.setOnClickListener {
            viewModel.showCryptoCurrencies()
            binding.showCryptoCurrenciesButton.visibility = View.GONE
        }
        binding.submitButton.setOnClickListener {

            when (viewModel.isCurrenciesListNotEmpty()) {
                true -> {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.message_select_default_currency),
                        Toast.LENGTH_LONG
                    ).show()

                    MaterialDialog(requireContext()).show {
                        var defaultCurrency = viewModel.getSelectedCurrencies()[0]
                        title(R.string.message_select_default_currency)
                        listItemsSingleChoice(
                            items = viewModel.getSelectedCurrencies()
                                .map { it.currencyName },
                            initialSelection = 0,
                            waitForPositiveButton = false
                        ) { _, index, _ ->
                            defaultCurrency = viewModel.getSelectedCurrencies()[index]
                        }
                        positiveButton(R.string.text_on_button_submit) {
                            if (FirstLaunchCurrenciesList.isCryptoCurrency(defaultCurrency.iso4217)) {
                                showConfirmCryptoDefaultCurrencyDialog(defaultCurrency)
                            } else {
                                saveSelectedCurrencies(defaultCurrency)
                            }
                        }
                        negativeButton(R.string.text_on_button_cancel)
                    }
                }
                false -> Toast.makeText(
                    context,
                    R.string.first_launch_currencies_empty_message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun renderCurrenciesList() {
        binding.currenciesForSelectHolder.adapter =
            FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
                viewModel.firstLaunchCurrenciesList.value.orEmpty(),
                viewModel.getSelectedCurrencies().mapNotNull { it.iso4217 }.toSet(),
                object : OnChangeCurrencyByTextCallBack {
                    override fun onClick(string: String) {
                        viewModel.toggleCurrencySelection(string)
                    }
                }
            )
    }

    @SuppressLint("CheckResult")
    private fun showConfirmCryptoDefaultCurrencyDialog(defaultCurrency: Currencies) {
        MaterialDialog(requireContext()).show {
            title(R.string.message_confirm_crypto_default_currency)
            positiveButton(R.string.text_on_button_submit) {
                saveSelectedCurrencies(defaultCurrency)
            }
            negativeButton(R.string.text_on_button_cancel)
        }
    }

    private fun saveSelectedCurrencies(defaultCurrency: Currencies) {
        viewModel.addCurrenciesToDB(viewModel.getSelectedCurrencies().map {
            it.copy(isCurrencyDefault = it.iso4217 == defaultCurrency.iso4217)
        }.sortedByDescending { it.isCurrencyDefault })
    }
}
