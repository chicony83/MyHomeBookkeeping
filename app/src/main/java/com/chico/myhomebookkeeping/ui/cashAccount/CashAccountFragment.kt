package com.chico.myhomebookkeeping.ui.cashAccount

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCashAccountBinding
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.helpers.ControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper

class CashAccountFragment : Fragment() {

    private lateinit var cashAccountViewModel: CashAccountViewModel
    private var _binding: FragmentCashAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CashAccountDao

    private var selectedCashAccountId = 0

    private val uiHelper = UiHelper()
    private lateinit var controlHelper: ControlHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).cashAccountDao()
        _binding = FragmentCashAccountBinding.inflate(inflater, container, false)

        cashAccountViewModel = ViewModelProvider(this).get(CashAccountViewModel::class.java)

        with(cashAccountViewModel) {
            selectedCashAccount.observe(viewLifecycleOwner,{
                binding.confirmationLayout.selectedItemName.text = it?.accountName
            })
//            selectedCashAccount.observe(viewLifecycleOwner,{
//                binding.selectedItem.text = it?.accountName
//            })
            cashAccountList.observe(viewLifecycleOwner, {
                binding.cashAccountHolder.adapter =

                    CashAccountAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            uiHelper.showHideUIElements(selectedId,binding.confirmationLayoutHolder)
//                            uiHelper.showHideUIElements(selectedId, binding.layoutConfirmation)
                            cashAccountViewModel.loadSelectedCashAccount(selectedId)
                            selectedCashAccountId = selectedId
                        }
                    })
            })
        }
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controlHelper = ControlHelper(findNavController())

        if (controlHelper.isPreviousFragment(R.id.nav_money_moving_query)){
            uiHelper.hideUiElement(binding.showHideAddCashAccountFragmentButton)
            uiHelper.showUiElement(binding.selectAllButton)
        }
        else if (controlHelper.isPreviousFragment(R.id.nav_money_moving)) {
            uiHelper.showUiElement(binding.selectAllButton)
        }

        view.hideKeyboard()
        with(binding) {
            selectAllButton.setOnClickListener {
                cashAccountViewModel.reset()
                cashAccountViewModel.saveData(controlHelper)
                controlHelper.moveToMoneyMovingFragment()
            }
            showHideAddCashAccountFragmentButton.setOnClickListener {
                uiHelper.setShowHideOnLayout(binding.newCashAccountLayoutHolder)
            }
            newCashAccountLayout.addNewCashAccountButton.setOnClickListener {
                if (uiHelper.isVisibleLayout(binding.newCashAccountLayoutHolder)) {
                    if (uiHelper.isLengthStringMoThan(binding.newCashAccountLayout.cashAccountName.text)) {
                        val name = binding.newCashAccountLayout.cashAccountName.text.toString()
                        var number: Int? = null
                        if (binding.newCashAccountLayout.cashAccountNumber.text.isNotEmpty()) {
                            number = binding.newCashAccountLayout.cashAccountNumber.text.toString().toInt()
                        }
                        val newCashAccount = CashAccount(name, number)

                        CashAccountsUseCase.addNewCashAccountRunBlocking(
                            db,
                            newCashAccount,
                            cashAccountViewModel
                        )
                        uiHelper.clearUiListEditText(
                            listOf(
                                binding.newCashAccountLayout.cashAccountName,
                                binding.newCashAccountLayout.cashAccountNumber
                            )
                        )
                        uiHelper.hideUiElement(binding.newCashAccountLayoutHolder)
                        view.hideKeyboard()
                    } else showMessage(getString(R.string.too_short_name))
                }
            }

            confirmationLayout.selectButton.setOnClickListener {
                if (selectedCashAccountId > 0) {
                    cashAccountViewModel.saveData(controlHelper)
                    controlHelper.moveToPreviousPage()
                }
            }
            confirmationLayout.cancelButton.setOnClickListener {
                if (selectedCashAccountId > 0) {
                    selectedCashAccountId = 0
                    uiHelper.hideUiElement(binding.confirmationLayoutHolder)
                }
            }
        }
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}