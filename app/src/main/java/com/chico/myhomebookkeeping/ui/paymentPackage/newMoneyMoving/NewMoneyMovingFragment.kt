package com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.textWathers.NewMoneyMovingAmountTextWatcher
import com.chico.myhomebookkeeping.ui.calc.CalcDialogFragment
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.fragment_change_money_moving.*
import kotlinx.android.synthetic.main.fragment_new_money_moving.amountEditText
import kotlinx.android.synthetic.main.fragment_new_money_moving.eraseButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import java.util.*


class NewMoneyMovingFragment : Fragment() {

    private val viewModel: NewMoneyMovingViewModel by viewModels()
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
            eraseButton.setOnClickListener {
                eraseAmountEditText()
            }
            submitButton.setOnClickListener {
                pressSubmitButton()
            }
            calcButton.setOnClickListener {
                requireView().hideKeyboard()
                val calcFragment: CalcDialogFragment = CalcDialogFragment.newInstance(
                    amountEditText.text.toString()
                )
                calcFragment.show(childFragmentManager, "dialog")
            }
        }
        with(viewModel) {
            dataTime.observe(viewLifecycleOwner) {
                binding.selectDateTimeButton.text = it.toString()
            }
            selectedCashAccount.observe(viewLifecycleOwner) {
                binding.selectCashAccountButton.text = it.accountName
            }
            selectedCurrency.observe(viewLifecycleOwner) {
                binding.selectCurrenciesButton.text = it.currencyName
            }
            selectedCategory.observe(viewLifecycleOwner) {
                binding.selectCategoryButton.text = it.categoryName
            }

            setDateTimeOnButton(currentDateTimeMillis)

            enteredAmount.observe(viewLifecycleOwner) {
                binding.amountEditText.setText(it.toString())
            }
            enteredDescription.observe(viewLifecycleOwner) {
                binding.description.setText(it.toString())
            }
            submitButton.observe(viewLifecycleOwner) {
                binding.submitButton.text = it.toString()
            }
        }
        viewModel.getAndCheckArgsSp()

        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.onCalcAmountSelected.collectLatest {
                binding.amountEditText.setText(it)
            }
        }

        showHideEraseButton()
    }

    private fun showHideEraseButton() {
        amountEditText.addTextChangedListener(
            NewMoneyMovingAmountTextWatcher(eraseButton)
        )
    }

    private fun eraseAmountEditText() {
        binding.amountEditText.setText("")
    }

    private fun launchDatePicker() {
        val builderDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.description_select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val datePicker = builderDatePicker
            .build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setDate(it)
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
            with(viewModel) {
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
        val isCashAccountNotNull = viewModel.isCashAccountNotNull()
        val isCurrencyNotNull = viewModel.isCurrencyNotNull()
        val isCategoryNotNull = viewModel.isCategoryNotNull()
        val checkAmount = uiHelper.isEntered(binding.amountEditText.text)
        if (isCashAccountNotNull) {
            if (isCurrencyNotNull) {
                if (isCategoryNotNull) {
                    if (checkAmount) {
                        addNewMoneyMoving()
                    } else {
                        setBackgroundWarningColor(binding.amountEditText)
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
        val amount: Double = Around.double(binding.amountEditText.text.toString())
        val description = binding.description.text.toString()
        viewModel.saveDataToSP(amount, description)
        runBlocking {
            val result = viewModel.addNewMoneyMoving(
                amount = amount,
                description = description
            )
            if (result > 0) {
//                uiHelper.clearUiListEditText(
//                    listOf(
//                        binding.amount, binding.description
//                    )
//                )
//                setBackgroundDefaultColor(binding.amount)
                view?.hideKeyboard()

//                Toast(context).showCustomToastWhitsButton(requireActivity())
//                message(getString(R.string.message_entry_added))
                viewModel.saveSPOfNewEntryIsAdded()
                control.navigate(R.id.nav_money_moving)
                viewModel.clearSPAfterSave()
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
        viewModel.saveDataToSP(getAmount(), getDescription())
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
        return binding.amountEditText.text.toString().let {
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