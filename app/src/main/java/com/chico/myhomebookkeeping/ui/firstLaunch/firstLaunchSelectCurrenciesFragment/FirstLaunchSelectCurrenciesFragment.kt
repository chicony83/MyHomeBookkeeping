package com.chico.myhomebookkeeping.ui.firstLaunch.firstLaunchSelectCurrenciesFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchSelectCurrenciesBinding
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.interfaces.currencies.OnChangeCurrencyByTextCallBack

class FirstLaunchSelectCurrenciesFragment : Fragment() {
    private var _binding: FragmentFirstLaunchSelectCurrenciesBinding? = null
    private val binding get() = _binding!!
    private lateinit var firstLaunchSelectCurrenciesViewModel: FirstLaunchSelectCurrenciesViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchSelectCurrenciesBinding.inflate(inflater, container, false)
        firstLaunchSelectCurrenciesViewModel =
            ViewModelProvider(this).get(FirstLaunchSelectCurrenciesViewModel::class.java)
        Message.log("--- size of currencies list = ${firstLaunchSelectCurrenciesViewModel.firstLaunchCurrenciesList}")
        with(firstLaunchSelectCurrenciesViewModel) {
            firstLaunchCurrenciesList.observe(viewLifecycleOwner) {
                binding.currenciesForSelectHolder.adapter =
                    FirstLaunchSelectCurrencyForSelectCurrencyAdapter(
                        it, object : OnChangeCurrencyByTextCallBack {
                            override fun onClick(string: String) {
                                firstLaunchSelectCurrenciesViewModel.moveCurrencyToSelectList(string)
                            }
                        }
                    )
                Message.log("--- size of getFirstLaunchList ${it.size}")
            }
            selectedCurrenciesList.observe(viewLifecycleOwner){
                binding.selectedCurrenciesHolder.adapter =
                    FirstLaunchSelectCurrencySelectedCurrencyAdapter(it)
            }
        }


        return binding.root
    }
}