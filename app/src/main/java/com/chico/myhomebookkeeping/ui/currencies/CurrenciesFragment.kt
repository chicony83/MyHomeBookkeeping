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
import com.chico.myhomebookkeeping.`interface`.OnItemSelectForSelectCallBack
import com.chico.myhomebookkeeping.`interface`.OnItemSelectedForChangeCallBack
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.`interface`.addItems.AddNewCurrencyCallBack
import com.chico.myhomebookkeeping.databinding.FragmentCurrenciesBinding
import com.chico.myhomebookkeeping.db.dao.CurrenciesDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.*
import com.chico.myhomebookkeeping.ui.currencies.dialogs.NewCurrencyDialog
import com.chico.myhomebookkeeping.ui.currencies.dialogs.SelectCurrencyDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.showKeyboard

class CurrenciesFragment : Fragment() {
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CurrenciesDao

    private var selectedCurrencyId = 0

    private val uiHelper = UiHelper()
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private lateinit var uiControl: UiControl

    //    private val showHideDialogsController = ShowHideDialogsController()
    private val uiColors = UiColors()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).currenciesDao()

        _binding = FragmentCurrenciesBinding.inflate(inflater, container, false)

        currenciesViewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)

        uiControl = UiControl(
            topButtonsHolder = binding.topButtonsHolder,
            bottomButton = binding.showHideAddCurrencyFragmentButton,
            newItemLayoutHolder = binding.newCurrencyLayoutHolder,
            confirmationLayoutHolder = binding.confirmationLayoutHolder,
            changeItemLayoutHolder = binding.changeCurrencyLayoutHolder
        )
        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(currenciesViewModel) {
            selectedCurrency.observe(viewLifecycleOwner, {
                binding.confirmationLayout.selectedItemName.text = it?.currencyName
            })
            currenciesList.observe(viewLifecycleOwner, {
                binding.currenciesHolder.adapter =
                    CurrenciesAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            showSelectCurrencyDialog(selectedId)
//                            uiControl.showSelectLayoutHolder()
//                            currenciesViewModel.loadSelectedCurrency(selectedId)
//                            selectedCurrencyId = selectedId
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

    private fun showSelectCurrencyDialog(selectedId: Int) {
        launchIo {
            val currencies: Currencies? = currenciesViewModel.loadSelectedCurrency(selectedId)
            launchUi {
                val dialog = SelectCurrencyDialog(currencies,
                    object : OnItemSelectedForChangeCallBack {
                        override fun onSelect(id: Int) {
                            Message.log("item for Changing id = $id")
                        }

                    },
                    object : OnItemSelectForSelectCallBack {
                        override fun onSelect(id: Int) {
                            Message.log("item for Select id = $id")
                        }
                    })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navControlHelper = NavControlHelper(control)

        if (navControlHelper.isPreviousFragment(R.id.nav_money_moving_query)) {
            uiHelper.hideUiElement(binding.showHideAddCurrencyFragmentButton)
            uiHelper.showUiElement(binding.selectAllButton)
        } else if (navControlHelper.isPreviousFragment(R.id.nav_money_moving)) {
            uiHelper.showUiElement(binding.selectAllButton)
        }
        view.hideKeyboard()
        with(binding) {
            selectAllButton.setOnClickListener {
                currenciesViewModel.resetCurrencyForSelect()
                currenciesViewModel.saveData(navControlHelper)
                navControlHelper.moveToMoneyMovingFragment()
            }
            showHideAddCurrencyFragmentButton.setOnClickListener {

                showNewCurrencyDialog()
            }
            with(confirmationLayout) {
                selectButton.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        currenciesViewModel.saveData(navControlHelper)
                        navControlHelper.moveToPreviousPage()
                    }
                }
                selectedItemName.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        putItemForChange()
                        view.showKeyboard()
                        with(binding.changeCurrencyLayout.itemName) {
                            requestFocus()
                            setSelection(0)
                        }
                    }
                }
                changeButton.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        putItemForChange()
                        view.showKeyboard()
                        with(binding.changeCurrencyLayout.itemName) {
                            requestFocus()
                            setSelection(0)
                        }
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
                    view.hideKeyboard()
//                    showUIControlElements()
                }
                saveChange.setOnClickListener {
                    if (uiHelper.isLengthStringMoThan(binding.changeCurrencyLayout.itemName.text)) {
                        val name: String = binding.changeCurrencyLayout.itemName.text.toString()
                        launchIo {
                            currenciesViewModel.saveChangedCurrency(name = name)
                        }
                        uiHelper.hideUiElement(binding.changeCurrencyLayoutHolder)
                        view.hideKeyboard()
//                        showUIControlElements()
                    }
                }
            }
            uiColors.setColors(
                getDialogsList(),
                getButtonsListForColorButton(),
                getButtonsListForColorButtonText()
            )
        }
    }

    private fun showNewCurrencyDialog() {
        val result = currenciesViewModel.getNamesList()
        launchUi {
            val dialog = NewCurrencyDialog(result, object : AddNewCurrencyCallBack {
                override fun add(name: String) {
                    val result = currenciesViewModel.addNewCurrency(Currencies(currencyName = name))
                    if (result > 0) {
                        showMessage("валюта добавлена")
                    }
                    if (result <= 0) {
                        showMessage("не могу добавить валюту")
                    }
                }

                override fun addAndSelect(name: String) {
                    val result = currenciesViewModel.addNewCurrency(Currencies(currencyName = name))
                    if (result > 0) {
                        showMessage("валюта добавлена")
                    }
                    if (result <= 0) {
                        showMessage("не могу добавить валюту")
                    }
                    currenciesViewModel.saveData(navControlHelper, result.toInt())
                    navControlHelper.moveToPreviousPage()
                }
            })

            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun putItemForChange() {
        uiControl.showChangeLayoutHolder()
        currenciesViewModel.selectedToChange()
        selectedCurrencyId = 0
    }

    private fun getButtonsListForColorButtonText() = listOf(
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton
    )

    private fun getDialogsList() = listOf(
        binding.changeCurrencyLayout,
//        binding.confirmationLayout
    )

    private fun getButtonsListForColorButton() = listOf(
//        binding.newCurrencyLayout.addAndSelectNewItemButton,
//        binding.newCurrencyLayout.addNewCurrencyButton,
//        binding.newCurrencyLayout.cancelCreateButton,
//        binding.confirmationLayout.changeButton,
//        binding.confirmationLayout.selectButton,
//        binding.confirmationLayout.cancelButton,
        binding.changeCurrencyLayout.saveChange,
        binding.changeCurrencyLayout.cancelChange
    )

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}