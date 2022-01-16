package com.chico.myhomebookkeeping.ui.firstLaunch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class FirstLaunchFragment : Fragment() {
    private lateinit var firstLaunchViewModel: FirstLaunchViewModel
    private var _binding: FragmentFirstLaunchBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private var noImage = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchBinding.inflate(inflater, container, false)
        firstLaunchViewModel = ViewModelProvider(this).get(FirstLaunchViewModel::class.java)
        noImage = firstLaunchViewModel.getNoImageImage()
        with(firstLaunchViewModel) {
            cardCashAccountItem.observe(viewLifecycleOwner, {
                setImageResourceOnIcon(binding.cardCashAccountIcon, cardCashAccountItem)
            })
            cashCashAccountItem.observe(viewLifecycleOwner, {
                setImageResourceOnIcon(binding.cashCashAccountIcon, cashCashAccountItem)
            })
            salaryCategoryItem.observe(viewLifecycleOwner, {
                setImageResourceOnIcon(binding.incomeMoneyIcon,salaryCategoryItem)
            //                binding.incomeMoneyIcon.setImageResource(it.imageResource ?: noImage)
            })
        }

        return binding.root
    }

    private fun setImageResourceOnIcon(
        imageView: ImageView,
        item: LiveData<FirstLaunchViewModel.FirstLaunchItem>
    ) {
        imageView.setImageResource(item.value?.imageResource ?: noImage)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        binding.submitButton.setOnClickListener {
            launchUi {
                val listCashAccounts = getListCashAccounts()
                val listCurrencies = getListCurrencies()
                val listIncomingCategories = getListIncomeCategories(getListIncomeCheckBoxes())
                val listSpendingCategories = getListSpendingCategories(getListSpendingCheckBoxes())

                launchIo {
                    firstLaunchViewModel.addFirstLaunchElements(
                        listCashAccounts,
                        listCurrencies,
                        listIncomingCategories,
                        listSpendingCategories
                    )
                }
            }

            firstLaunchViewModel.setIsFirstLaunchFalse()
            control.navigate(R.id.nav_fast_payments_fragment)
        }
    }


    override fun onStart() {
        super.onStart()
        launchIo {
            with(firstLaunchViewModel) {
                addIconCategories()
                addIconsResources()
                updateValues()
            }
        }
    }

    private fun getListSpendingCategories(listCheckBoxes: List<CheckBox>): List<CheckBox> {
        return getListSelectedItems(listCheckBoxes)
    }

    private fun getListIncomeCategories(listCheckBoxes: List<CheckBox>): List<CheckBox> {
        return getListSelectedItems(listCheckBoxes)
    }

    private fun getListSelectedItems(listCheckBoxes: List<CheckBox>): List<CheckBox> {
        val listSelectedItems = mutableListOf<CheckBox>()
        for (i in listCheckBoxes.indices) {
            if (listCheckBoxes[i].isChecked) {
                listSelectedItems.add(listCheckBoxes[i])
            }
        }
        return listSelectedItems.toList()
    }

    private fun getListIncomeCheckBoxes() = listOf(binding.addCategoryTheSalary)

    private fun getListSpendingCheckBoxes() = listOf(
        binding.addCategoryCellularCommunication,
        binding.addCategoryCredit,
        binding.addCategoryFuelForTheCar,
        binding.addCategoryProducts,
        binding.addCategoryMedicines,
        binding.addCategoryPublicTransport
    )

    private fun getListCurrencies() = listOf(binding.addDefaultCurrency)

    private fun getListCashAccounts() = listOf(
        binding.addCashAccountsCard,
        binding.addCashAccountsCash
    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}