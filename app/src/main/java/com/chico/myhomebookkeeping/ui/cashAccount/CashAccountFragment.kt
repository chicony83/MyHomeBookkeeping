package com.chico.myhomebookkeeping.ui.cashAccount

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListener
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.databinding.FragmentCashAccountBinding
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.ui.UiHelper

class CashAccountFragment : Fragment() {

    private lateinit var cashAccountViewModel: CashAccountViewModel
    private var _binding: FragmentCashAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CashAccountDao

    private var selectedCashAccountId = 0

    private val argsName: String by lazy { Constants.CASH_ACCOUNT_KEY }
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        db = dataBase.getDataBase(requireContext()).cashAccountDao()
        _binding = FragmentCashAccountBinding.inflate(inflater, container, false)

        cashAccountViewModel = ViewModelProvider(this).get(CashAccountViewModel::class.java)

        cashAccountViewModel.cashAccountList.observe(viewLifecycleOwner, {
            binding.cashAccountHolder.adapter =

                CashAccountAdapter(it, object : OnItemViewClickListener {
                    override fun onClick(selectedId: Int) {
                        uiHelper.showHideUIElements(selectedId, binding.layoutConfirmation)
                        selectedCashAccountId = selectedId
                    }
                })
        })
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            showHideAddCashAccountFragment.setOnClickListener {
                if (binding.addNewCashAccountFragment.visibility == View.GONE) {
                    uiHelper.showUiElement(binding.addNewCashAccountFragment)
                } else uiHelper.hideUiElement(binding.addNewCashAccountFragment)
            }
            addNewCashAccountButton.setOnClickListener {
                if (binding.addNewCashAccountFragment.visibility == View.VISIBLE) {
                    if (binding.cashAccountName.text.isNotEmpty()) {
                        val name = binding.cashAccountName.text.toString()
                        var number: Int? = null
                        if (binding.cashAccountNumber.text.isNotEmpty()) {
                            number = binding.cashAccountNumber.text.toString().toInt()
                        }
                        val newCashAccount = CashAccount(name, number)

                        CashAccountsUseCase.addNewCashAccountRunBlocking(
                            db,
                            newCashAccount,
                            cashAccountViewModel
                        )
                        uiHelper.clearUiElement(binding.cashAccountName)
                        uiHelper.clearUiElement(binding.cashAccountNumber)
                        uiHelper.hideUiElement(binding.addNewCashAccountFragment)
                    }
                }
            }
            selectButton.setOnClickListener {
                if (selectedCashAccountId > 0) {
                    val bundle = Bundle()
                    bundle.putInt(argsName, selectedCashAccountId)

                    findNavController().navigate(
                        R.id.nav_new_money_moving,
                        bundle
                    )
                }
            }
            cancel.setOnClickListener {
                if (selectedCashAccountId > 0) {
                    selectedCashAccountId = 0
                    uiHelper.hideUiElement(binding.layoutConfirmation)
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}