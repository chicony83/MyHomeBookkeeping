package com.chico.myhomebookkeeping.ui.currencies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCurrenciesBinding
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.helpers.ControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard

class CurrenciesFragment : Fragment() {
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CurrenciesDao
    private lateinit var currenciesUseCase: CurrenciesUseCase

    private var selectedCurrencyId = 0

    private val uiHelper = UiHelper()
    private lateinit var controlHelper: ControlHelper
    private lateinit var control: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).currenciesDao()

        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        currenciesViewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)


        with(currenciesViewModel) {
            selectedCurrency.observe(viewLifecycleOwner, {
                binding.confirmationLayout.selectedItemName.text = it?.currencyName
            })
            currenciesList.observe(viewLifecycleOwner, {
                binding.currenciesHolder.adapter =
                    CurrenciesAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            uiHelper.showHideUIElements(
                                selectedId,
                                binding.confirmationLayoutHolder
                            )
                            currenciesViewModel.loadSelectedCurrency(selectedId)
                            selectedCurrencyId = selectedId
                            Log.i("TAG", "---$selectedId---")
                        }
                    })
            })
            changeCurrency.observe(viewLifecycleOwner, {
                binding.changeCurrencyLayout.itemName.setText(it?.currencyName)
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        controlHelper = ControlHelper(control)

        if (controlHelper.isPreviousFragment(R.id.nav_money_moving_query)) {
            uiHelper.hideUiElement(binding.showHideAddCurrencyFragmentButton)
            uiHelper.showUiElement(binding.selectAllButton)
        } else if (controlHelper.isPreviousFragment(R.id.nav_money_moving)) {
            uiHelper.showUiElement(binding.selectAllButton)
        }

        view.hideKeyboard()
        with(binding) {
            selectAllButton.setOnClickListener {
                currenciesViewModel.resetCurrencyForSelect()
                currenciesViewModel.saveData(controlHelper)
                controlHelper.moveToMoneyMovingFragment()
            }
            showHideAddCurrencyFragmentButton.setOnClickListener {
                uiHelper.setShowHideOnLayout(binding.newCurrencyLayoutHolder)
            }
            newCurrencyLayout.addNewCurrencyButton.setOnClickListener {
                if (uiHelper.isVisibleLayout(binding.newCurrencyLayoutHolder)) {
                    if (uiHelper.isLengthStringMoThan(binding.newCurrencyLayout.currencyName.text)) {
                        val name: String = binding.newCurrencyLayout.currencyName.text.toString()
                        currenciesViewModel.addNewCurrency(name = name)
                        uiHelper.clearUiElement(binding.newCurrencyLayout.currencyName)
                        uiHelper.hideUiElement(binding.newCurrencyLayoutHolder)
                        view.hideKeyboard()
                    } else showMessage(getString(R.string.too_short_name))
                }
            }
            with(confirmationLayout) {
                selectButton.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        currenciesViewModel.saveData(controlHelper)
                        controlHelper.moveToPreviousPage()
                    }
                }
                changeButton.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        uiHelper.hideUiElement(binding.confirmationLayoutHolder)
                        uiHelper.showUiElement(binding.changeCurrencyLayoutHolder)
                        currenciesViewModel.selectedToChange()
                        selectedCurrencyId = 0
                    }
                }
                cancelButton.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        selectedCurrencyId = 0
                        uiHelper.hideUiElement(binding.confirmationLayoutHolder)
                    }
                }
            }
            with(changeCurrencyLayout) {
                cancelChange.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        selectedCurrencyId = 0
                    }
                    currenciesViewModel.resetCurrencyForSelect()
                    currenciesViewModel.resetCurrencyForChange()
                    uiHelper.hideUiElement(binding.changeCurrencyLayoutHolder)
                }
                saveChange.setOnClickListener {
                    if (uiHelper.isLengthStringMoThan(binding.changeCurrencyLayout.itemName.text)) {
                        val name: String = binding.changeCurrencyLayout.itemName.text.toString()
                        currenciesViewModel.saveChangedCurrency(name = name)
                        view.hideKeyboard()
                    }
                }
            }
        }
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}