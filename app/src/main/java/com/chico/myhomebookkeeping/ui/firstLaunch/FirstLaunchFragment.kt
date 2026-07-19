package com.chico.myhomebookkeeping.ui.firstLaunch

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentFirstLaunchBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCashAccountAsDefaultDialogBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLaunchBinding.inflate(inflater, container, false)
        firstLaunchViewModel = ViewModelProvider(parentFragment ?: this).get(FirstLaunchViewModel::class.java)
//        noImage = firstLaunchViewModel.getNoImageImage()!!
        with(firstLaunchViewModel) {
            cardCashAccountItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(binding.cardCashAccountIcon, cardCashAccountItem)
            }
            cashCashAccountItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(binding.cashCashAccountIcon, cashCashAccountItem)
            }
            salaryCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(binding.incomeMoneyIcon, salaryCategoryItem)
            }
            productsCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(binding.productsCategoryIcon, productsCategoryItem)
            }
            fuelForCarCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(binding.fuelCategoryIcon, fuelForCarCategoryItem)
            }
            cellularCommunicationCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(
                    binding.cellularCommunicationCategoryIcon,
                    cellularCommunicationCategoryItem
                )
            }
            creditsCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(
                    binding.creditCategoryIcon, creditsCategoryItem
                )
            }
            medicinesCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(
                    binding.medicalCategoryIcon, medicinesCategoryItem
                )
            }
            publicTransportCategoryItem.observe(viewLifecycleOwner) {
                setImageResourceOnIcon(
                    binding.publicTransportCategoryIcon, publicTransportCategoryItem
                )
            }
        }

        return binding.root
    }

    private fun setImageResourceOnIcon(
        imageView: ImageView,
        item: LiveData<Int>
    ) {
        imageView.setImageResource(item.value ?: R.drawable.no_image)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        if (parentFragment is FirstLaunchSetupFragment) {
            binding.submitButton.visibility = View.GONE
        } else {
            binding.submitButton.setOnClickListener {
                submitStep()
            }
        }
    }

    fun submitStep() {
        val setupFragment = parentFragment as? FirstLaunchSetupFragment
        if (setupFragment != null) {
            firstLaunchViewModel.saveFirstLaunchSelections(
                getSelectedSetupItems(getListCashAccounts()),
                getSelectedSetupItems(getListIncomeCheckBoxes()),
                getSelectedSetupItems(getListSpendingCheckBoxes())
            )
            setupFragment.showDefaultCashAccountStep()
        } else {
            showSelectDefaultCashAccountDialog()
        }
    }

    private fun showSelectDefaultCashAccountDialog() {
        val selectedCashAccounts = getListSelectedItems(getListCashAccounts())
        if (selectedCashAccounts.isEmpty()) return

        val layout = layoutInflater.inflate(R.layout.dialog_select_currency_as_default, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(layout)
            .create()
        var defaultCashAccount = selectedCashAccounts[0]
        val adapter = SelectDefaultCashAccountAdapter(
            cashAccounts = selectedCashAccounts,
            selectedCashAccountName = getCashAccountName(defaultCashAccount)
        ) {
            defaultCashAccount = it
        }

        layout.findViewById<TextView>(R.id.dialogTitle)
            .setText(R.string.message_select_default_cash_account)
        layout.findViewById<RecyclerView>(R.id.iconsHolderLayout).adapter = adapter
        layout.findViewById<Button>(R.id.submitButton).setOnClickListener {
            dialog.dismiss()
            addFirstLaunchElements(getCashAccountName(defaultCashAccount))
        }
        layout.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addFirstLaunchElements(defaultCashAccountName: String) {
        launchUi {
            val listCashAccounts = getListCashAccounts()
            val listIncomingCategories =
                getListSelectedIncomeCategories(getListIncomeCheckBoxes())
            val listSpendingCategories =
                getListSelectedSpendingCategories(getListSpendingCheckBoxes())

            launchIo {
                firstLaunchViewModel.addFirstLaunchElements(
                    listCashAccounts,
                    listIncomingCategories,
                    listSpendingCategories,
                    defaultCashAccountName
                )
            }
        }

        firstLaunchViewModel.setIsFirstLaunchFalse()
        val setupFragment = parentFragment as? FirstLaunchSetupFragment
        if (setupFragment != null) {
            setupFragment.finishFirstLaunch()
        } else {
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

    private fun getListSelectedSpendingCategories(listCheckBoxes: List<SelectedItemOfImageAndCheckBox>): List<SelectedItemOfImageAndCheckBox> {
        return getListSelectedItems(listCheckBoxes)
    }

    private fun getListSelectedIncomeCategories(listCheckBoxes: List<SelectedItemOfImageAndCheckBox>): List<SelectedItemOfImageAndCheckBox> {
        return getListSelectedItems(listCheckBoxes)
    }

    private fun getListSelectedItems(listOfItems: List<SelectedItemOfImageAndCheckBox>): List<SelectedItemOfImageAndCheckBox> {
        val listSelectedItems = mutableListOf<SelectedItemOfImageAndCheckBox>()
        for (i in listOfItems.indices) {
            if (listOfItems[i].checkBox.isChecked) {
                listSelectedItems.add(listOfItems[i])
            }
        }
        return listSelectedItems.toList()
    }

    private fun getSelectedSetupItems(listOfItems: List<SelectedItemOfImageAndCheckBox>): List<FirstLaunchSetupItem> {
        return getListSelectedItems(listOfItems).map {
            FirstLaunchSetupItem(it.img, it.checkBox.text.toString())
        }
    }

    private fun getListIncomeCheckBoxes() = listOf(
        getItem(firstLaunchViewModel.salaryCategoryItem, binding.addCategoryTheSalary)
    )

    private fun getListSpendingCheckBoxes() = listOf(
        getItem(
            firstLaunchViewModel.cellularCommunicationCategoryItem,
            binding.addCategoryCellularCommunicationCheckBox
        ),
        getItem(firstLaunchViewModel.creditsCategoryItem, binding.addCategoryCreditCheckBox),
        getItem(
            firstLaunchViewModel.fuelForCarCategoryItem,
            binding.addCategoryFuelForTheCarCheckBox
        ),
        getItem(firstLaunchViewModel.productsCategoryItem, binding.addCategoryProductsCheckBox),
        getItem(firstLaunchViewModel.medicinesCategoryItem, binding.addCategoryMedicinesCheckBox),
        getItem(
            firstLaunchViewModel.publicTransportCategoryItem,
            binding.addCategoryPublicTransportCheckBox
        )
    )

    private fun getListCashAccounts() = listOf<SelectedItemOfImageAndCheckBox>(
        getItem(firstLaunchViewModel.cardCashAccountItem, binding.addCashAccountsCardCheckBox),
        getItem(firstLaunchViewModel.cashCashAccountItem, binding.addCashAccountsCashCheckBox)
    )

    private fun getItem(
        item: LiveData<Int>,
        checkBox: CheckBox
    ): SelectedItemOfImageAndCheckBox {
        return SelectedItemOfImageAndCheckBox(
            item.value ?: R.drawable.no_image,
            checkBox
        )
    }

    private fun getCashAccountName(item: SelectedItemOfImageAndCheckBox): String {
        return item.checkBox.text.toString()
    }

    private class SelectDefaultCashAccountAdapter(
        private val cashAccounts: List<SelectedItemOfImageAndCheckBox>,
        selectedCashAccountName: String,
        private val onCashAccountSelected: (SelectedItemOfImageAndCheckBox) -> Unit
    ) : RecyclerView.Adapter<SelectDefaultCashAccountAdapter.ViewHolder>() {
        private var selectedCashAccountName = selectedCashAccountName

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerViewItemSelectCashAccountAsDefaultDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(cashAccounts[position])
        }

        override fun getItemCount() = cashAccounts.size

        inner class ViewHolder(
            private val binding: RecyclerViewItemSelectCashAccountAsDefaultDialogBinding
        ) : RecyclerView.ViewHolder(binding.root) {
            fun bind(cashAccount: SelectedItemOfImageAndCheckBox) {
                val cashAccountName = cashAccount.checkBox.text.toString()
                with(binding) {
                    iconImg.setImageResource(cashAccount.img)
                    nameCashAccount.text = cashAccountName
                    defaultCashAccountRadioButton.isChecked =
                        cashAccountName == selectedCashAccountName
                    selectCashAccountAsDefaultItem.setOnClickListener {
                        selectCashAccount(cashAccount)
                    }
                }
            }

            private fun selectCashAccount(cashAccount: SelectedItemOfImageAndCheckBox) {
                val previousName = selectedCashAccountName
                val currentPosition = adapterPosition
                selectedCashAccountName = cashAccount.checkBox.text.toString()
                onCashAccountSelected(cashAccount)
                cashAccounts.indexOfFirst { it.checkBox.text.toString() == previousName }
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
                currentPosition
                    .takeIf { it >= 0 }
                    ?.let { notifyItemChanged(it) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
