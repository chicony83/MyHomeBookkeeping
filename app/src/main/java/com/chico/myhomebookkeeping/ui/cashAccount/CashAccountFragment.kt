package com.chico.myhomebookkeeping.ui.cashAccount

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListener
import com.chico.myhomebookkeeping.databinding.FragmentCashAccountBinding
import com.chico.myhomebookkeeping.db.dao.CashAccountDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.helpers.*
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForSelectCallBackInt
import com.chico.myhomebookkeeping.interfaces.cashAccounts.OnAddNewCashAccountsCallBack
import com.chico.myhomebookkeeping.interfaces.cashAccounts.OnChangeCashAccountCallBack
import com.chico.myhomebookkeeping.ui.cashAccount.dialogs.ChangeCashAccountDialog
import com.chico.myhomebookkeeping.ui.cashAccount.dialogs.NewCashAccountDialog
import com.chico.myhomebookkeeping.ui.cashAccount.dialogs.SelectCashAccountDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class CashAccountFragment : Fragment() {

    private lateinit var cashAccountViewModel: CashAccountViewModel
    private var _binding: FragmentCashAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: CashAccountDao

    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = dataBase.getDataBase(requireContext()).cashAccountDao()
        _binding = FragmentCashAccountBinding.inflate(inflater, container, false)

        cashAccountViewModel = ViewModelProvider(this).get(CashAccountViewModel::class.java)

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(cashAccountViewModel) {
            selectedCashAccount.observe(viewLifecycleOwner, {
                binding.confirmationLayout.selectedItemName.text = it?.accountName
            })
            cashAccountList.observe(viewLifecycleOwner, {
                binding.cashAccountHolder.adapter =
                    CashAccountAdapter(it, object : OnItemViewClickListener {
                        override fun onClick(selectedId: Int) {
                            showSelectCashAccountDialog(selectedId)
                        }
                    })
            })
        }
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        navControlHelper = NavControlHelper(control)

        with(binding) {
            selectAllButton.setOnClickListener {
                cashAccountViewModel.saveData(navControlHelper, -1)
                navControlHelper.moveToMoneyMovingFragment()
            }
            showHideAddCashAccountFragmentButton.setOnClickListener {
                showNewCashAccountDialog()
            }
        }
        if (navControlHelper.isPreviousFragment(
                R.id.nav_new_money_moving
            )
            or navControlHelper.isPreviousFragment(
                R.id.nav_change_money_moving
            )
        ) {
            uiHelper.hideUiElement(binding.selectAllButton)
        }
    }

    private fun showSelectCashAccountDialog(selectedId: Int) {
        launchIo {
            val cashAccount: CashAccount? = cashAccountViewModel.loadSelectedCashAccount(selectedId)
            launchUi {
                val dialog = SelectCashAccountDialog(cashAccount,
                    object : OnItemSelectForChangeCallBack {
                        override fun onSelect(id: Int) {
                            showChangeCashAccountDialog(cashAccount)
                        }
                    },
                    object : OnItemSelectForSelectCallBackInt {
                        override fun onSelect(id: Int) {
                            cashAccountViewModel.saveData(navControlHelper, id)
                            navControlHelper.moveToPreviousFragment()
                        }
                    })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
    }

    private fun showChangeCashAccountDialog(cashAccount: CashAccount?) {
        launchIo {
            val dialog = ChangeCashAccountDialog(cashAccount, object : OnChangeCashAccountCallBack {
                override fun change(id: Int, name: String, number: String) {
                    cashAccountViewModel.saveChangedCashAccount(id, name, number)
                }
            })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun showNewCashAccountDialog() {
        val result = cashAccountViewModel.getNamesList()
        launchUi {
            val dialog = NewCashAccountDialog(result, object : OnAddNewCashAccountsCallBack {
                override fun addAndSelect(name: String, number: String, isSelect: Boolean) {
                    val cashAccount = CashAccount(
                        accountName = name,
                        bankAccountNumber = number,
                        isCashAccountDefault = false,
                        icon = null
                    )
                    val result: Long = cashAccountViewModel.addNewCashAccount(cashAccount)
                    if (isSelect) {
                        cashAccountViewModel.saveData(navControlHelper, result.toInt())
                        navControlHelper.moveToPreviousFragment()
                    }
                }
            })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}