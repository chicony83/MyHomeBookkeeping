package com.chico.myhomebookkeeping.ui.paymentPackage.changeMoneyMoving

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentChangeMoneyMovingBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiColors
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ChangeMoneyMovingFragment : Fragment() {
    private lateinit var changeMoneyMovingViewModel: ChangeMoneyMovingViewModel
    private var _binding: FragmentChangeMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()
    private val uiColors = UiColors()
    private val datePicker =
        MaterialDatePicker.Builder.datePicker()
            .setSelection(
                MaterialDatePicker.todayInUtcMilliseconds()
            )
            .build()

    private val timePicker =
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChangeMoneyMovingBinding.inflate(inflater, container, false)

        changeMoneyMovingViewModel =
            ViewModelProvider(this).get(ChangeMoneyMovingViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        with(binding) {
            selectDateTimeButton.setOnClickListener {
                datePicker.show(parentFragmentManager, "TAG")
            }
            selectCashAccountButton.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
            selectCurrenciesButton.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
            }
            selectCategoryButton.setOnClickListener {
                pressSelectButton(R.id.nav_categories)
            }
            submitButton.setOnClickListener {
                pressSubmitButton()
            }
            deleteButton.setOnClickListener {
                pressDeleteButton()
            }
            with(dialogSubmitDeleteMoneyMoving) {
                submitDeleteButton.setOnClickListener {
                    deleteEntry()
                }
                cancelDeleteButton.setOnClickListener {
                    message(getString(R.string.message_deletion_canceled))
                    binding.dialogSubmitDeleteMoneyMovingHolder.visibility = View.GONE
                }
            }
        }

        with(changeMoneyMovingViewModel) {
            dataTime.observe(viewLifecycleOwner, {
                binding.selectDateTimeButton.text = it.toString()
            })
            selectedCashAccount.observe(viewLifecycleOwner, {
                binding.selectCashAccountButton.text = it.accountName
            })
            selectedCurrency.observe(viewLifecycleOwner, {
                binding.selectCurrenciesButton.text = it.currencyName
            })
            selectedCategory.observe(viewLifecycleOwner, {
                binding.selectCategoryButton.text = it.categoryName
            })
            amountMoney.observe(viewLifecycleOwner, {
                binding.amountEditText.setText(it.toString())
            })
            descriptionText.observe(viewLifecycleOwner, {
                binding.description.setText(it)
            })

        }
        datePicker.addOnPositiveButtonClickListener {
            changeMoneyMovingViewModel.setDate(it)
            timePicker.show(parentFragmentManager, "TAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val hour: Int = timePicker.hour
            val minute: Int = timePicker.minute
            with(changeMoneyMovingViewModel) {
                setTime(hour, minute)
                setDateTimeOnButton()
            }
        }
        with(changeMoneyMovingViewModel) {
            getSPForChangeMoneyMovingLine()
            getPaymentForChange()
        }

        uiColors.setColors(getDialogsList(), getButtonsListForColorButton(), getButtonsListForColorButtonText())
    }


    private fun deleteEntry() {
        runBlocking {
            val result = async { changeMoneyMovingViewModel.deleteLine() }
            if (result.await() > 0) {
                message(getString(R.string.message_entry_deleted))
                control.navigate(R.id.nav_money_moving)
            }
        }
    }


    private fun pressSubmitButton() {
        if (uiHelper.isEntered(binding.amountEditText.text)) {
            runBlocking {
                val amount: Double = binding.amountEditText.text.toString().toDouble()
                val description = binding.description.text.toString()
                changeMoneyMovingViewModel.saveDataToSp()
                val result: Int =
                    changeMoneyMovingViewModel.changeMoneyMovementInDB(amount, description)
                if (result > 0) {
                    view?.hideKeyboard()
                    message(getString(R.string.message_entry_changed))
                    control.navigate(R.id.nav_money_moving)
                }
            }
        } else {
            setBackgroundWarningColor(binding.amountEditText)
            message(getString(R.string.message_enter_amount))
        }
    }

    private fun setBackgroundWarningColor(editText: EditText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText.setBackgroundColor(resources.getColor(R.color.warning, null))
        }
    }

    private fun pressSelectButton(fragment: Int) {
        changeMoneyMovingViewModel.saveDataToSp()
//        control.navigate(fragment)
        navControlHelper.toSelectedFragment(fragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun getDialogsList() = listOf(
        binding.dialogSubmitDeleteMoneyMoving
    )

    private fun getButtonsListForColorButton() = listOf(
        binding.submitButton,
        binding.deleteButton,
        binding.dialogSubmitDeleteMoneyMoving.submitDeleteButton,
        binding.dialogSubmitDeleteMoneyMoving.cancelDeleteButton
    )

    private fun getButtonsListForColorButtonText() = listOf(
        binding.submitButton,
        binding.dialogSubmitDeleteMoneyMoving.submitDeleteButton
    )

    private fun pressDeleteButton() {
        binding.dialogSubmitDeleteMoneyMovingHolder.visibility = View.VISIBLE
    }
}