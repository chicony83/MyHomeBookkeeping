package com.chico.myhomebookkeeping.ui.currencies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.chico.myhomebookkeeping.utils.hideKeyboard

class CurrenciesFragment : Fragment() {
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CurrenciesDao

    private var selectedCurrencyId = 0

    private val argsNameForSelect: String by lazy { Constants.FOR_SELECT_CURRENCY_KEY }
    private val argsNameForQuery:String by lazy { Constants.FOR_QUERY_CURRENCY_KEY }
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
            binding.currenciesHolder.adapter =
                CurrenciesAdapter(it, object : OnItemViewClickListener {
                    override fun onClick(selectedId: Int) {
                        uiHelper.showHideUIElements(selectedId, binding.layoutConfirmation)
                        selectedCurrencyId = selectedId
                        Log.i("TAG", "---2WTF---")
                    }
                })
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        with(binding) {
            showHideAddCurrencyFragment.setOnClickListener {
                uiHelper.setShowHideOnLayout(binding.addNewCurrencyFragment)
            }
            addNewCurrencyButton.setOnClickListener {
                if (uiHelper.isVisibleLayout(binding.addNewCurrencyFragment)) {
                    if (uiHelper.isLengthStringMoThan(binding.currencyName.text)) {
                        val nameCurrency: String = binding.currencyName.text.toString()
                        val addingCurrency = Currencies(currencyName = nameCurrency)
                        CurrenciesUseCase.addNewCurrencyRunBlocking(
                            db,
                            addingCurrency,
                            currenciesViewModel
                        )
                        uiHelper.clearUiElement(binding.currencyName)
                        uiHelper.hideUiElement(binding.addNewCurrencyFragment)
                        view.hideKeyboard()
                    } else showMessage(getString(R.string.too_short_name))
                }
            }
            selectButton.setOnClickListener {
                if (selectedCurrencyId > 0) {
                    val bundle = Bundle()
                    when(findNavController().previousBackStackEntry?.destination?.id){
                        R.id.nav_new_money_moving -> {
                            bundle.putInt(argsNameForSelect, selectedCurrencyId)
                            moveTo(R.id.nav_new_money_moving, bundle)
                        }
                        R.id.nav_money_moving -> {
                            bundle.putInt(argsNameForQuery,selectedCurrencyId)
                            moveTo(R.id.nav_money_moving, bundle)
                        }
                    }
                }
            }
            cancel.setOnClickListener {
                if (selectedCurrencyId > 0) {
                    selectedCurrencyId = 0
                    uiHelper.hideUiElement(binding.layoutConfirmation)
                }
            }
        }
    }
    private fun moveTo(nav: Int, bundle: Bundle) {
        findNavController().navigate(
            nav, bundle
        )
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}