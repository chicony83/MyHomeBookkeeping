package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CategoriesUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.utils.launchForResult
import com.chico.myhomebookkeeping.utils.launchIo

import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.util.*


class NewMoneyMovingFragment : Fragment() {

    private lateinit var newMoneyMovingViewModel: NewMoneyMovingViewModel
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis

    private val argsCashAccountKey = Constants.CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.CURRENCY_KEY
    private val argsCategoryKey = Constants.CATEGORY_KEY

    private lateinit var control: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewMoneyMovingBinding.inflate(inflater, container, false)

        newMoneyMovingViewModel = ViewModelProvider(this).get(NewMoneyMovingViewModel::class.java)

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val db = dataBase.getDataBase(requireContext())

        control = activity?.findNavController(R.id.nav_host_fragment)!!
        with(binding) {
            dateTimeTimeStamp.setText(currentDateTimeMillis.parseTimeFromMillis())
            selectCashAccountButton.setOnClickListener {
                navigateTo(R.id.nav_cash_account)
            }
            selectCurrenciesButton.setOnClickListener {
                navigateTo(R.id.nav_currencies)
            }
            selectCategoryButton.setOnClickListener {
                navigateTo(R.id.nav_categories)
            }
            addNewMoneyMovingButton.setOnClickListener {
                control.popBackStack()
            }
        }
        with(newMoneyMovingViewModel){
            selectedCashAccount.observe(viewLifecycleOwner,{
                binding.selectCashAccountButton.text = it.first().accountName
            })
            selectedCategory.observe(viewLifecycleOwner,{
                binding.selectCategoryButton.text = it.first().categoryName
            })
            selectedCurrency.observe(viewLifecycleOwner,{
                binding.selectCurrenciesButton
            })
        }
        val cashAccountId: Int? = arguments?.getInt(argsCashAccountKey)
        if (cashAccountId != null) {
            launchIo {
                    newMoneyMovingViewModel.loadCashAccount(cashAccountId)
            }
        }
        val currenciesId: Int? = arguments?.getInt(argsCurrencyKey)
        if (currenciesId != null) {
        }
        val categoryId: Int? = arguments?.getInt(argsCategoryKey)
        if (categoryId != null) {
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun navigateTo(nav: Int) {
        control.navigate(nav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}