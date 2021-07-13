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
import com.chico.myhomebookkeeping.databinding.FragmentChangeMoneyMovingBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper

import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.runBlocking
import java.util.*

class ChangeMoneyMovingFragment : Fragment() {

    private lateinit var changeMoneyMovingViewModel: ChangeMoneyMovingViewModel
    private var _binding: FragmentChangeMoneyMovingBinding? = null
    private val binding get() = _binding!!

    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()
    private val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("select date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()
    private val timePicker =
        MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("select Time")
            .build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeMoneyMovingBinding.inflate(inflater, container, false)

        changeMoneyMovingViewModel = ViewModelProvider(this).get(ChangeMoneyMovingViewModel::class.java)

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!

        navControlHelper = NavControlHelper(controller = control)

        with(binding) {
//            dateTimeTimeStamp.text = currentDateTimeMillis.parseTimeFromMillis()

            dateTimeTimeStamp.setOnClickListener {
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
        }
        with(changeMoneyMovingViewModel) {
            dataTime.observe(viewLifecycleOwner, {
                binding.dateTimeTimeStamp.text = it.toString()
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

            amountMoney.observe(viewLifecycleOwner,{
                binding.amount.setText(it.toString())
            })
            submitButton.observe(viewLifecycleOwner,{
                binding.submitButton.text = it.toString()
            })
        }

        datePicker.addOnPositiveButtonClickListener {
            changeMoneyMovingViewModel.setDate(it)
            timePicker.show(parentFragmentManager, "TAG")
        }

        timePicker.addOnPositiveButtonClickListener {
            val hour: Int = timePicker.hour
//            Log.i("TAG","hour = $hour")
            var minute:Int = timePicker.minute
            with(changeMoneyMovingViewModel){
                setTime(hour,minute)
                setDateTimeOnButton()
            }
        }
        changeMoneyMovingViewModel.getAndCheckArgsSp()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun pressSubmitButton() {

        if (CheckNewMoneyMoving.isEntered(binding.amount.text)) {
            val amount: Double = binding.amount.text.toString().toDouble()
            val description: String = binding.description.text.toString()
            changeMoneyMovingViewModel.saveSP()
            runBlocking {
                val result: Long =
                    changeMoneyMovingViewModel.updateDB(amount, description)
                if (result > 0) {
                    uiHelper.clearUiListEditText(
                        listOf(
                            binding.amount, binding.description
                        )
                    )
                    setBackgroundDefaultColor(binding.amount)
                    view?.hideKeyboard()
                    message("запись добавлена")
                    control.navigate(R.id.nav_money_moving)
                }
            }
        } else {
            setBackgroundWarningColor(binding.amount)
            message("введите сумму")
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

    private fun pressSelectButton(nav: Int) {
        changeMoneyMovingViewModel.saveSP()
        control.navigate(nav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}