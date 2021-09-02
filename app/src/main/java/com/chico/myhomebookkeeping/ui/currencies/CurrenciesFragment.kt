package com.chico.myhomebookkeeping.ui.currencies

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.ShowHideDialogsController
import com.chico.myhomebookkeeping.helpers.UiControl
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo

class CurrenciesFragment : Fragment() {
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private var _binding: FragmentCurrenciesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CurrenciesDao
    private lateinit var currenciesUseCase: CurrenciesUseCase

    private var selectedCurrencyId = 0

    private val uiHelper = UiHelper()
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private lateinit var uiControl: UiControl
    private val showHideDialogsController = ShowHideDialogsController()

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
                            uiControl.showSelectLayoutHolder()
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
//                uiHelper.setShowHideOnLayout(binding.newCurrencyLayoutHolder)
                uiControl.showNewItemLayoutHolder()


            }
            with(newCurrencyLayout) {
                addNewCurrencyButton.setOnClickListener {
                    if (uiHelper.isVisibleLayout(binding.newCurrencyLayoutHolder)) {
                        if (uiHelper.isLengthStringMoThan(binding.newCurrencyLayout.currencyName.text)) {
                            val name: String =
                                binding.newCurrencyLayout.currencyName.text.toString()
                            val newCurrency = Currencies(currencyName = name)
                            currenciesViewModel.addNewCurrency(newCurrency)
                            eraseUiElements()
                            uiHelper.hideUiElement(binding.newCurrencyLayoutHolder)
                            view.hideKeyboard()
                            showUIControlElements()
                        } else showMessage(getString(R.string.too_short_name_message))
                    }
                }
                cancelCreate.setOnClickListener {
                    eraseUiElements()
                    uiHelper.hideUiElement(binding.newCurrencyLayoutHolder)
                    view.hideKeyboard()
                    showUIControlElements()
                }
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
                    }
                }
                changeButton.setOnClickListener {
                    if (selectedCurrencyId > 0) {
                        putItemForChange()
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
                    showUIControlElements()
                }
                saveChange.setOnClickListener {
                    if (uiHelper.isLengthStringMoThan(binding.changeCurrencyLayout.itemName.text)) {
                        val name: String = binding.changeCurrencyLayout.itemName.text.toString()
                        launchIo {
                            currenciesViewModel.saveChangedCurrency(name = name)
                        }
                        uiHelper.hideUiElement(binding.changeCurrencyLayoutHolder)
                        view.hideKeyboard()
                        showUIControlElements()
                    }
                }
            }
            checkUiMode()
        }
    }
    private fun showUIControlElements() {
        showHideDialogsController.showUIControlElements(
            topButtonsHolder = binding.topButtonsHolder,
            bottomButton = binding.showHideAddCurrencyFragmentButton
        )
    }

    private fun putItemForChange() {
        uiControl.showChangeLayoutHolder()
        currenciesViewModel.selectedToChange()
        selectedCurrencyId = 0
    }

    private fun eraseUiElements() {
        uiHelper.clearUiElement(binding.newCurrencyLayout.currencyName)
    }

    @SuppressLint("ResourceAsColor")
    private fun checkUiMode() {
        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
                setBackground(R.drawable.dialog_background_night)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                setBackground(R.drawable.dialog_background_day)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                setBackground(R.drawable.dialog_background_day)
            }
        }
    }

    private fun setBackground(shape: Int) {
        with(binding) {
            newCurrencyLayout.root.setBackgroundResource(shape)
            changeCurrencyLayout.root.setBackgroundResource(shape)
            confirmationLayout.root.setBackgroundResource(shape)
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