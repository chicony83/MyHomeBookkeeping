package com.chico.myhomebookkeeping.ui.currencies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.databinding.FragmentCurrenciesBinding
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.ui.UiHelper

class CurrenciesFragment : Fragment() {
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CurrenciesDao

    private var selectedCurrencyId = 0

    private val argsName = Constants.CURRENCY_KEY
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).currenciesDao()
        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        currenciesViewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)

        currenciesViewModel.currenciesList.observe(viewLifecycleOwner, {
            binding.currenciesHolder.adapter = CurrenciesAdapter(it, object:OnItemViewClickListener{
                override fun onClick(selectedId: Int) {
                    uiHelper.showHideUIElements(selectedId,binding.layoutConfirmation)
                    selectedCurrencyId = selectedId
                    Log.i("TAG","---2WTF---")
                }

            })

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
        binding.selectButton.setOnClickListener {
            if (selectedCurrencyId>0){
                val bundle = Bundle()
                bundle.putInt(argsName,selectedCurrencyId)
                findNavController().navigate(
                    R.id.nav_new_money_moving,bundle
                )
            }
        }
        binding.cancel.setOnClickListener {
            if (selectedCurrencyId>0){
                selectedCurrencyId = 0
                binding.layoutConfirmation.visibility = View.GONE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}