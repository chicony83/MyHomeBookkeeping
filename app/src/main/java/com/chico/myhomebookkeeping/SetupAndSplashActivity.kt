package com.chico.myhomebookkeeping

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.chico.myhomebookkeeping.ui.firstLaunch.SelectedItemOfImageAndCheckBox
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SetupAndSplashActivity : AppCompatActivity() {

    private val viewModel: SetupAndSplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_and_splash)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.checkIsFirstLaunch()) {
            lifecycleScope.launch {

                with(viewModel) {
                    addIconCategories()
                    addIconsResources()
                    updateValues()
                }
                delay(5000)

                val listCashAccounts = getListCashAccounts()
//                val listIncomingCategories =
//                    getListSelectedIncomeCategories(getListIncomeCheckBoxes())
//                val listSpendingCategories =
//                    getListSelectedSpendingCategories(getListSpendingCheckBoxes())


                viewModel.addFirstLaunchAccounts(listCashAccounts)
                viewModel.populateParentCategories()
                viewModel.populateChildCategories()
                viewModel.populateFreeFastPayments(this@SetupAndSplashActivity)

                startActivity(Intent(this@SetupAndSplashActivity, MainActivity::class.java))
                finish()
            }
        } else {
            lifecycleScope.launch {
                delay(1500)
                startActivity(Intent(this@SetupAndSplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun getListCashAccounts() = listOf<SelectedItemOfImageAndCheckBox>(
        getItem(viewModel.cardCashAccountItem, CheckBox(this).apply {
            isChecked = true
            text = getString(R.string.quick_setup_name_Card)
        }),
        getItem(viewModel.cashCashAccountItem, CheckBox(this).apply {
            isChecked = true
            text = getString(R.string.quick_setup_name_Cash)
        })
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

    private fun getListIncomeCheckBoxes() = listOf(
        getItem(viewModel.salaryCategoryItem, CheckBox(this).apply {
            isChecked = true
            text = getString(R.string.quick_setup_name_The_Income)
        })
    )

    private fun getListSpendingCheckBoxes() = listOf(
        getItem(
            viewModel.cellularCommunicationCategoryItem,
            CheckBox(this).apply {
                isChecked = true
                text = getString(R.string.quick_setup_name_Cellular_communication)
            }
        ),
        getItem(viewModel.creditsCategoryItem, CheckBox(this).apply {
            isChecked = true
            text = getString(R.string.quick_setup_name_Credit)
        }),
        getItem(
            viewModel.fuelForCarCategoryItem,
            CheckBox(this).apply {
                isChecked = true
                text = getString(R.string.quick_setup_name_Fuel_for_the_car)
            }
        ),
        getItem(viewModel.productsCategoryItem, CheckBox(this).apply {
            isChecked = true
            text = getString(R.string.quick_setup_name_Products)
        }),
        getItem(viewModel.medicinesCategoryItem, CheckBox(this).apply {
            isChecked = true
            text = getString(R.string.quick_setup_name_Medicines)
        }),
        getItem(
            viewModel.publicTransportCategoryItem,
            CheckBox(this).apply {
                isChecked = true
                text = getString(R.string.quick_setup_name_Public_transport)
            }
        )
    )

    private fun getListSelectedSpendingCategories(listCheckBoxes: List<SelectedItemOfImageAndCheckBox>): List<SelectedItemOfImageAndCheckBox> {
        return getListSelectedItems(listCheckBoxes)
    }
}