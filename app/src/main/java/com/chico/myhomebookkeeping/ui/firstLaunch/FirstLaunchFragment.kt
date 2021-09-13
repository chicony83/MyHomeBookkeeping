package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiControl
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard

class FirstLaunchFragment : Fragment() {
    private lateinit var firstLaunchViewModel: FirstLaunchViewModel
    private var _binding: FragmentFirstLaunchBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchBinding.inflate(inflater, container, false)
        firstLaunchViewModel = ViewModelProvider(this).get(FirstLaunchViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)
        binding.submitButton.setOnClickListener {
            with(firstLaunchViewModel) {
                addCashAccountCard(binding.addCashAccountsCard)
                addCashAccountCash(binding.addCashAccountsCash)
                addDefaultCurrency(binding.addDefaultCurrency)
                addCategoryTheSalary(binding.addCategoryTheSalary)
                addCategoryProducts(binding.addCategoryProducts)
                addCategoryFuelForTheCar(binding.addCategoryFuelForTheCar)
                addCategoryCellularCommunication(binding.addCategoryCellularCommunication)
                addCategoryCredit(binding.addCategoryCredit)
            }
            firstLaunchViewModel.setIsFirstLaunchFalse()
            navControlHelper.moveToPreviousPage()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun launchFragment(fragment: Int) {
        control.navigate(fragment)
    }
}