package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.annotation.SuppressLint
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
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper

import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.runBlocking
import java.util.*

class NewMoneyMovingFragment : Fragment() {

    private lateinit var newMoneyMovingViewModel: NewMoneyMovingViewModel
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!

    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()

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
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(controller = control)

        with(binding) {

            selectDateTimeButton.setOnClickListener {
                launchDatePicker()
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
        }
        with(newMoneyMovingViewModel) {
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

            setDateTimeOnButton(currentDateTimeMillis)

            enteredAmount.observe(viewLifecycleOwner, {
                binding.amount.setText(it.toString())
            })
            enteredDescription.observe(viewLifecycleOwner, {
                binding.description.setText(it.toString())
            })
            submitButton.observe(viewLifecycleOwner, {
                binding.submitButton.text = it.toString()
            })
        }
        newMoneyMovingViewModel.getAndCheckArgsSp()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun launchDatePicker() {
        val builderDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.description_select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val datePicker = builderDatePicker
            .build()

        datePicker.addOnPositiveButtonClickListener {
            newMoneyMovingViewModel.setDate(it)
            launchTimePicker()
        }
        datePicker.show(parentFragmentManager, "TAG")
    }

    private fun launchTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(getString(R.string.description_select_time))
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour: Int = timePicker.hour
            val minute = timePicker.minute
            with(newMoneyMovingViewModel) {
                setTime(
                    hour = hour,
                    minute = minute
                )
                setDateTimeOnButton()
            }
        }
        timePicker.show(childFragmentManager, "TAG")
    }

    private fun pressSubmitButton() {
        val isCashAccountNotNull = newMoneyMovingViewModel.isCashAccountNotNull()
        val isCurrencyNotNull = newMoneyMovingViewModel.isCurrencyNotNull()
        val isCategoryNotNull = newMoneyMovingViewModel.isCategoryNotNull()
        val checkAmount = uiHelper.isEntered(binding.amount.text)
        if (isCashAccountNotNull) {
            if (isCurrencyNotNull) {
                if (isCategoryNotNull) {
                    if (checkAmount) {
                        addNewMoneyMoving()
                    } else {
                        setBackgroundWarningColor(binding.amount)
                        message(getString(R.string.message_enter_amount))
                    }
                } else {
                    message(getString(R.string.message_category_not_selected))
                }
            } else {
                message(getString(R.string.message_currency_not_selected))
            }
        } else {
            message(getString(R.string.message_cash_account_not_selected))
        }
    }

    private fun addNewMoneyMoving() {
        val amount: Double = Around.double(binding.amount.text.toString())
        val description = binding.description.text.toString()
        newMoneyMovingViewModel.saveDataToSP(amount, description)
        runBlocking {
            val result = newMoneyMovingViewModel.addNewMoneyMoving(
                amount = amount,
                description = description
            )
            if (result > 0) {
//                uiHelper.clearUiListEditText(
//                    listOf(
//                        binding.amount, binding.description
//                    )
//                )
                setBackgroundDefaultColor(binding.amount)
                view?.hideKeyboard()
                message(getString(R.string.message_entry_added))
                control.navigate(R.id.nav_money_moving)
                newMoneyMovingViewModel.clearSPAfterSave()
            }
        }
    }


    private fun setBackgroundWarningColor(editText: EditText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText.setBackgroundColor(resources.getColor(R.color.warning, null))
        }
    }

    private fun setBackgroundDefaultColor(editText: EditText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editText.setBackgroundColor(
                resources.getColor(
                    R.color.design_default_color_background,
                    null
                )
            )
        }
    }

    private fun pressSelectButton(fragment: Int) {
        newMoneyMovingViewModel.saveDataToSP(getAmount(), getDescription())
        navControlHelper.toSelectedFragment(fragment)
//        control.navigate(fragment)
    }

    private fun getDescription(): String {
        return binding.description.text.toString().let {
            if (it.isNotEmpty()) it
            else ""
        }
    }

    private fun getAmount(): Double {
        return binding.amount.text.toString().let {
            if (it.isNotEmpty()) Around.double(it)
            else 0.0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}