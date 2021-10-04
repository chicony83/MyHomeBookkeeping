package com.chico.myhomebookkeeping.ui.cashAccount

import android.annotation.SuppressLint
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
import com.chico.myhomebookkeeping.EditNameTextWatcher
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCashAccountBinding
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.helpers.*
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.showKeyboard

class CashAccountFragment : Fragment() {

    private lateinit var cashAccountViewModel: CashAccountViewModel
    private var _binding: FragmentCashAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CashAccountDao

    private var selectedCashAccountId: Int = 0

    private val uiHelper = UiHelper()
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController

    private lateinit var uiControl: UiControl
    private val showHideDialogsController = ShowHideDialogsController()
    private val uiColors = UiColors()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).cashAccountDao()
        _binding = FragmentCashAccountBinding.inflate(inflater, container, false)

        cashAccountViewModel = ViewModelProvider(this).get(CashAccountViewModel::class.java)

        with(cashAccountViewModel) {
            selectedCashAccount.observe(viewLifecycleOwner, {
                binding.confirmationLayout.selectedItemName.text = it?.accountName
            })
            cashAccountList.observe(viewLifecycleOwner, {
                binding.cashAccountHolder.adapter =

                    CashAccountAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            uiControl.showSelectLayoutHolder()
                            cashAccountViewModel.loadSelectedCashAccount(selectedId)
                            selectedCashAccountId = selectedId
                        }
                    })
            })
            changeCashAccount.observe(viewLifecycleOwner, {
                binding.changeCashAccountLayout.cashAccountName.setText(it?.accountName)
                binding.changeCashAccountLayout.cashAccountNumber.setText(it?.bankAccountNumber.toString())
            }
            )
        }
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        uiControl = UiControl(
            topButtonsHolder = binding.topButtonsHolder,
            bottomButton = binding.showHideAddCashAccountFragmentButton,
            newItemLayoutHolder = binding.newCashAccountLayoutHolder,
            confirmationLayoutHolder = binding.confirmationLayoutHolder,
            changeItemLayoutHolder = binding.changeCashAccountLayoutHolder
        )
        control = activity?.findNavController(R.id.nav_host_fragment)!!

        navControlHelper = NavControlHelper(control)

        if (navControlHelper.isPreviousFragment(R.id.nav_money_moving_query)) {
            uiHelper.hideUiElement(binding.showHideAddCashAccountFragmentButton)
            uiHelper.showUiElement(binding.selectAllButton)
        } else if (navControlHelper.isPreviousFragment(R.id.nav_money_moving)) {
            uiHelper.showUiElement(binding.selectAllButton)
        }
        with(binding) {
            selectAllButton.setOnClickListener {
                cashAccountViewModel.resetCashAccountForChange()
                cashAccountViewModel.saveData(navControlHelper)
                navControlHelper.moveToMoneyMovingFragment()
            }
            showHideAddCashAccountFragmentButton.setOnClickListener {
                uiControl.showNewItemLayoutHolder()
                view.showKeyboard()

                val result: Any = cashAccountViewModel.getNamesList()
//                if (result is Int) {
//                    showMessage("список имён пуст")
//                }
                if (result is List<*>) {
                    val namesList: List<String> = result as List<String>
                    binding.newCashAccountLayout.cashAccountName.addTextChangedListener(
                        EditNameTextWatcher(
                            namesList,
                            binding.newCashAccountLayout.addNewCashAccountButton,
                            binding.newCashAccountLayout.errorThisNameIsTaken

                        )
                    )
//                    showMessage("список имен отправлен на проверку")
                }
                with(binding.newCashAccountLayout.cashAccountName) {
                    requestFocus()
                    setSelection(0)
                }
            }
            with(newCashAccountLayout) {
                addAndSelectNewItemButton.setOnClickListener {
                    selectedCashAccountId = addNewCashAccount(view)
                    if (selectedCashAccountId > 0) {
//                        cashAccountViewModel.loadSelectedCashAccount(selectedCashAccountId)
                        Message.log("selected cash Account ID = $selectedCashAccountId")
                        cashAccountViewModel.saveData(navControlHelper,selectedCashAccountId)
//                        cashAccountViewModel.saveData(navControlHelper)
                        navControlHelper.moveToPreviousPage()
                    }
                }
                addNewCashAccountButton.setOnClickListener {
                    addNewCashAccount(view)
                }
                cancelCreateButton.setOnClickListener {
                    eraseUiElements()
                    uiHelper.hideUiElement(binding.newCashAccountLayoutHolder)
                    view.hideKeyboard()
                    showUIControlElements()
                }
            }
            with(confirmationLayout) {
                selectButton.setOnClickListener {
                    if (selectedCashAccountId > 0) {
                        cashAccountViewModel.saveData(navControlHelper)
                        navControlHelper.moveToPreviousPage()
                    }
                }
                selectedItemName.setOnClickListener {
                    if (selectedCashAccountId > 0) {
                        putItemForChange()
                        view.showKeyboard()
                        with(binding.changeCashAccountLayout.cashAccountName) {
                            requestFocus()
                            setSelection(0)
                        }
                    }
                }
                changeButton.setOnClickListener {
                    if (selectedCashAccountId > 0) {
                        putItemForChange()
                        view.showKeyboard()
                        with(binding.changeCashAccountLayout.cashAccountName) {
                            requestFocus()
                            setSelection(0)
                        }
                    }
                }
                cancelButton.setOnClickListener {
                    if (selectedCashAccountId > 0) {
                        selectedCashAccountId = 0
                        uiHelper.hideUiElement(binding.confirmationLayoutHolder)
                    }
                }
            }
            with(changeCashAccountLayout) {
                cancelChange.setOnClickListener {
                    if (selectedCashAccountId > 0) {
                        selectedCashAccountId = 0
                    }
                    cashAccountViewModel.resetCashAccountForChange()
                    cashAccountViewModel.resetCashAccountForSelect()
                    uiHelper.hideUiElement(binding.changeCashAccountLayoutHolder)
                    view.hideKeyboard()
                    showUIControlElements()
                }
                saveChange.setOnClickListener {
                    if (uiHelper.isLengthStringMoThan(binding.changeCashAccountLayout.cashAccountName.text)) {
                        val name = binding.changeCashAccountLayout.cashAccountName.text.toString()
                        val number =
                            binding.changeCashAccountLayout.cashAccountNumber.text.toString()
                        Log.i("TAG", " click name = $name, number = $number")
                        launchIo {
                            cashAccountViewModel.saveChangedCashAccount(
                                name = name,
                                number = number
                            )
                        }
                        uiHelper.hideUiElement(changeCashAccountLayoutHolder)
                        view.hideKeyboard()
                        showUIControlElements()
                    }
                }
            }
        }
        uiColors.setColors(
            getDialogsList(),
            getButtonsListForColorButton(),
            getButtonsListForColorButtonText()
        )
    }

    private fun addNewCashAccount(view: View): Int {
        if (uiHelper.isVisibleLayout(binding.newCashAccountLayoutHolder)) {
            if (uiHelper.isLengthStringMoThan(binding.newCashAccountLayout.cashAccountName.text)) {
                val name = binding.newCashAccountLayout.cashAccountName.text.toString()
                val number: String =
                    binding.newCashAccountLayout.cashAccountNumber.text.toString()
                val newCashAccount =
                    CashAccount(accountName = name, bankAccountNumber = number)
                Message.log("adding new CAsh Account = $newCashAccount")
                eraseUiElements()
                uiHelper.hideUiElement(binding.newCashAccountLayoutHolder)
                view.hideKeyboard()
                showUIControlElements()
                return cashAccountViewModel.addNewCashAccount(newCashAccount).toInt()
            } else {
                showMessage(getString(R.string.message_too_short_name))
                return -1
            }
        } else return -1
    }

    private fun showUIControlElements() {
        showHideDialogsController.showUIControlElements(
            topButtonsHolder = binding.topButtonsHolder,
            bottomButton = binding.showHideAddCashAccountFragmentButton
        )
    }

    private fun putItemForChange() {
        uiControl.showChangeLayoutHolder()
        cashAccountViewModel.selectedToChange()
        selectedCashAccountId = 0
    }

    private fun eraseUiElements() {
        uiHelper.clearUiListEditText(
            listOf(
                binding.newCashAccountLayout.cashAccountName,
                binding.newCashAccountLayout.cashAccountNumber
            )
        )
    }


    private fun getButtonsListForColorButtonText() = listOf(
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton
    )

    private fun getButtonsListForColorButton() = listOf(
        binding.newCashAccountLayout.addAndSelectNewItemButton,
        binding.newCashAccountLayout.addNewCashAccountButton,
        binding.newCashAccountLayout.cancelCreateButton,
        binding.confirmationLayout.changeButton,
        binding.confirmationLayout.selectButton,
        binding.confirmationLayout.cancelButton,
        binding.changeCashAccountLayout.saveChange,
        binding.changeCashAccountLayout.cancelChange
    )

    private fun getDialogsList() = listOf(
        binding.newCashAccountLayout,
        binding.changeCashAccountLayout,
        binding.confirmationLayout
    )

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}