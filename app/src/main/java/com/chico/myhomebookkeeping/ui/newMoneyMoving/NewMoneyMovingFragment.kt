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
import com.chico.myhomebookkeeping.utils.launchIo

import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.util.*


class NewMoneyMovingFragment : Fragment() {

    private lateinit var newMoneyMovingViewModel: NewMoneyMovingViewModel
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis


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
            selectedCurrency.observe(viewLifecycleOwner,{
                binding.selectCurrenciesButton.text = it.first().currencyName
            })
            selectedCategory.observe(viewLifecycleOwner,{
                binding.selectCategoryButton.text = it.first().categoryName
            })
        }

        newMoneyMovingViewModel.checkArguments(arguments)

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