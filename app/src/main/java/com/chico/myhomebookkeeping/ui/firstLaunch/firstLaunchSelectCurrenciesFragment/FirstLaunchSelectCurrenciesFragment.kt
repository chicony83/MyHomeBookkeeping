package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchSelectCurrenciesBinding
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyByTextCallBack
import com.chico.myhomebookkeeping.ui.dialogs.selectAsDefault.SelectCurrencyAsDefaultDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi

class FirstLaunchSelectCurrenciesFragment : Fragment() {
    private var _binding: FragmentFirstLaunchSelectCurrenciesBinding? = null
    private val binding get() = _binding!!
    private lateinit var firstLaunchSelectCurrenciesViewModel: FirstLaunchSelectCurrenciesViewModel
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchSelectCurrenciesBinding.inflate(inflater, container, false)
        firstLaunchSelectCurrenciesViewModel =
            ViewModelProvider(this).get(FirstLaunchSelectCurrenciesViewModel::class.java)

        with(firstLaunchSelectCurrenciesViewModel) {
            firstLaunchCurrenciesList.observe(viewLifecycleOwner) {
                binding.currenciesForSelectHolder.adapter =
                    FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
                        it, object : OnChangeCurrencyByTextCallBack {
                            override fun onClick(string: String) {
                                firstLaunchSelectCurrenciesViewModel
                                    .moveCurrencyToSelectList(string)
                            }
                        }
                    )
//                Message.log("--- size of getFirstLaunchList ${it.size}")
            }
            selectedCurrenciesList.observe(viewLifecycleOwner) {
                binding.selectedCurrenciesHolder.adapter =
                    FirstLaunchSelectCurrencySelectedCurrencyAdapter(
                        it, object : OnChangeCurrencyByTextCallBack {
                            override fun onClick(string: String) {
                                Message.log("press to remove $string")
                                firstLaunchSelectCurrenciesViewModel
                                    .moveCurrencyToFirstLaunchCurrenciesList(string)
                            }
                        }
                    )
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)
        binding.submitButton.setOnClickListener {

            when (firstLaunchSelectCurrenciesViewModel.isCurrenciesListNotEmpty()) {
                true -> {
                    Toast.makeText(context, "select default currency", Toast.LENGTH_LONG).show()

                    launchUi {
                        val dialog = SelectCurrencyAsDefaultDialog(firstLaunchSelectCurrenciesViewModel.getSelectedCurrencies())
                        dialog.show(childFragmentManager,getString(R.string.tag_show_dialog))
                    }
//                    launchIo {
//                        firstLaunchSelectCurrenciesViewModel.addingCurrenciesToDB()
//                    }
//                    navControlHelper.toSelectedFragment(R.id.nav_first_launch_fragment)
                }
                false -> Toast.makeText(context, "list is EMPTY", Toast.LENGTH_LONG).show()
            }
        }
    }
}