package com.chico.myhomebookkeeping.ui.currencies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentCurrenciesBinding
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase

class CurrenciesFragment : Fragment() {
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CurrenciesDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).currenciesDao()
        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        currenciesViewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)

        currenciesViewModel.currenciesList.observe(viewLifecycleOwner, {
            binding.currenciesHolder.adapter = CurrenciesAdapter(it)

        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val currenciesUseCase = CurrenciesUseCase()
        binding.showHideAddCurrencyFragment.setOnClickListener {
            if (binding.addNewCurrencyFragment.visibility == View.VISIBLE) {
                binding.addNewCurrencyFragment.visibility = View.GONE
            } else binding.addNewCurrencyFragment.visibility = View.VISIBLE
        }
        binding.addNewCurrencyButton.setOnClickListener {
            if (binding.addNewCurrencyFragment.visibility == View.VISIBLE) {
                if (binding.newCurrencyEditText.text.isNotEmpty()) {
                    val nameCurrency: String = binding.newCurrencyEditText.text.toString()
                    val addingCurrency = Currencies(currencyName = nameCurrency)
                    CurrenciesUseCase.addNewCurrency(db, addingCurrency)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}