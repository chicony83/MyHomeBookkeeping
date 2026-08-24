package com.chico.myhomebookkeeping.ui.paymentPackage.changeTransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.parseTimeToMillis
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.runBlocking
import java.util.Locale

class ChangeTransferFragment : Fragment() {

    private val viewModel: ChangeTransferViewModel by viewModels()
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()
    private var isSyncingTransferFields = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewMoneyMovingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        setupUi()
        setupClicks()
        observeViewModel()
        viewModel.getTransferForChange()
    }

    private fun setupUi() {
        with(binding) {
            paymentTypeSwitchButton.visibility = View.GONE
            categoryLabel.visibility = View.GONE
            selectCategoryButton.visibility = View.GONE
            amountScrollContainer.visibility = View.GONE
            cashAccountQuickSelectScroll.visibility = View.GONE
            currencyQuickSelectScroll.visibility = View.GONE
            transferCashAccountQuickSelectScroll.visibility = View.GONE
            transferCurrencyQuickSelectScroll.visibility = View.GONE
            destinationCashAccountLabel.visibility = View.VISIBLE
            selectTransferCashAccountButton.visibility = View.VISIBLE
            transferCurrencyLabel.visibility = View.VISIBLE
            selectTransferCurrencyButton.visibility = View.VISIBLE
            transferAmountLabel.visibility = View.VISIBLE
            transferAmountEditText.visibility = View.VISIBLE
            transferMoreSheet.visibility = View.VISIBLE
            transferMoreButton.visibility = View.GONE
            transferMoreContainer.visibility = View.VISIBLE
            transferRateLabel.visibility = View.VISIBLE
            transferRateEditText.visibility = View.VISIBLE
            transferFeeLabel.visibility = View.GONE
            transferFeeEditText.visibility = View.GONE
            deleteButton.visibility = View.VISIBLE
            sourceCashAccountLabel.text = getString(R.string.description_cash_account_from)
            currencyLabel.text = getString(R.string.description_currency_from)
        }
    }

    private fun setupClicks() {
        with(binding) {
            selectDateTimeButton.setOnClickListener {
                launchDatePicker()
            }
            selectCashAccountButton.setOnClickListener {
                viewModel.setSourceCashAccountSelectMode()
                pressSelectButton(R.id.nav_cash_account)
            }
            selectTransferCashAccountButton.setOnClickListener {
                viewModel.setDestinationCashAccountSelectMode()
                pressSelectButton(R.id.nav_cash_account)
            }
            selectCurrenciesButton.setOnClickListener {
                viewModel.setSourceCurrencySelectMode()
                pressSelectButton(R.id.nav_currencies)
            }
            selectTransferCurrencyButton.setOnClickListener {
                viewModel.setDestinationCurrencySelectMode()
                pressSelectButton(R.id.nav_currencies)
            }
            eraseButton.setOnClickListener {
                amountEditText.setText("")
            }
            submitButton.setOnClickListener {
                pressSubmitButton()
            }
            deleteButton.setOnClickListener {
                pressDeleteButton()
            }
            amountEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showHideEraseButton(amountEditText, eraseButton)
            }
            amountEditText.setOnClickListener {
                showHideEraseButton(amountEditText, eraseButton)
            }
            amountEditText.addTextChangedListener(
                com.chico.myhomebookkeeping.textWathers.NewMoneyMovingAmountTextWatcher(
                    eraseButton
                )
            )
            amountEditText.doAfterTextChanged {
                syncTransferFieldsFromSourceAmount()
            }
            transferAmountEditText.doAfterTextChanged {
                if (!isSyncingTransferFields) syncTransferRateFromDestinationAmount()
            }
            transferRateEditText.doAfterTextChanged {
                if (!isSyncingTransferFields) syncTransferAmountFromRate()
            }
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            dataTime.observe(viewLifecycleOwner) {
                binding.selectDateTimeButton.text = it.toString()
            }
            selectedCashAccount.observe(viewLifecycleOwner) {
                binding.selectCashAccountButton.text = it.accountName
            }
            selectedTransferCashAccount.observe(viewLifecycleOwner) {
                binding.selectTransferCashAccountButton.text = it.accountName
            }
            selectedCurrency.observe(viewLifecycleOwner) {
                binding.selectCurrenciesButton.text = it.currencyName
            }
            selectedTransferCurrency.observe(viewLifecycleOwner) {
                binding.selectTransferCurrencyButton.text = it.currencyName
            }
            enteredAmount.observe(viewLifecycleOwner) {
                binding.amountEditText.setText(it.toString())
            }
            enteredTransferAmount.observe(viewLifecycleOwner) {
                binding.transferAmountEditText.setText(it.toString())
            }
            enteredTransferRate.observe(viewLifecycleOwner) {
                binding.transferRateEditText.setText(it.toString())
            }
            enteredDescription.observe(viewLifecycleOwner) {
                binding.description.setText(it)
            }
            submitButton.observe(viewLifecycleOwner) {
                binding.submitButton.text = it.toString()
            }
        }
    }

    private fun pressSubmitButton() {
        if (!viewModel.isCashAccountNotNull()) {
            message(getString(R.string.message_cash_account_not_selected))
            return
        }
        if (!viewModel.isCurrencyNotNull()) {
            message(getString(R.string.message_currency_not_selected))
            return
        }
        if (!viewModel.isTransferCashAccountNotNull()) {
            message(getString(R.string.message_transfer_cash_account_not_selected))
            return
        }
        if (!viewModel.isTransferCurrencyNotNull()) {
            message(getString(R.string.message_transfer_currency_not_selected))
            return
        }
        if (!viewModel.isTransferAccountsDifferent()) {
            message(getString(R.string.message_transfer_cash_accounts_must_be_different))
            return
        }
        if (!uiHelper.isEntered(binding.amountEditText.text)) {
            setBackgroundWarningColor(binding.amountEditText)
            message(getString(R.string.message_enter_amount))
            return
        }
        val destinationAmount = getTransferAmount()
        if (destinationAmount == null || destinationAmount <= 0) {
            message(getString(R.string.message_enter_transfer_amount_or_rate))
            return
        }
        changeTransfer(destinationAmount)
    }

    private fun changeTransfer(transferAmount: Double) {
        val amount = Around.double(binding.amountEditText.text.toString())
        val description = binding.description.text.toString()
        viewModel.saveDataToSP(
            amount = amount,
            description = description,
            transferAmount = transferAmount,
            transferRate = getTransferRate()
        )
        runBlocking {
            val result = viewModel.changeTransfer(amount, transferAmount, description)
            if (result >= 2) {
                viewModel.clearSPAfterSave()
                view?.hideKeyboard()
                message(getString(R.string.message_entry_changed))
                control.navigate(R.id.nav_money_moving)
            }
        }
    }

    private fun pressDeleteButton() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(R.string.description_you_want_to_delete_the_entry)
            .setPositiveButton(R.string.text_on_button_yes) { _, _ -> deleteTransfer() }
            .setNegativeButton(R.string.text_on_button_no) { _, _ ->
                message(getString(R.string.message_deletion_canceled))
            }
            .show()
    }

    private fun deleteTransfer() {
        runBlocking {
            val result = viewModel.deleteTransfer()
            if (result > 0) {
                viewModel.clearSPAfterSave()
                message(getString(R.string.message_entry_deleted))
                control.navigate(R.id.nav_money_moving)
            }
        }
    }

    private fun launchDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(binding.selectDateTimeButton.text.toString().parseTimeToMillis())
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
            viewModel.setTime(timePicker.hour, timePicker.minute)
            viewModel.setDateTimeOnButton()
        }
        timePicker.show(childFragmentManager, "TAG")
    }

    private fun pressSelectButton(fragment: Int) {
        val amount = parseAmountOrNull(binding.amountEditText.text.toString()) ?: 0.0
        val transferAmount = getTransferAmount() ?: 0.0
        viewModel.saveDataToSP(
            amount = amount,
            description = binding.description.text.toString(),
            transferAmount = transferAmount,
            transferRate = getTransferRate()
        )
        navControlHelper.toSelectedFragment(fragment)
    }

    private fun syncTransferFieldsFromSourceAmount() {
        if (isSyncingTransferFields) return
        val sourceAmount = parseAmountOrNull(binding.amountEditText.text.toString()) ?: return
        val rate = parseAmountOrNull(binding.transferRateEditText.text.toString())
        if (rate != null && rate > 0) {
            setTransferFieldText(binding.transferAmountEditText, formatAmount(sourceAmount * rate))
        }
    }

    private fun syncTransferRateFromDestinationAmount() {
        val sourceAmount = parseAmountOrNull(binding.amountEditText.text.toString()) ?: return
        val destinationAmount =
            parseAmountOrNull(binding.transferAmountEditText.text.toString()) ?: return
        if (sourceAmount <= 0) return
        setTransferFieldText(binding.transferRateEditText, formatRate(destinationAmount / sourceAmount))
    }

    private fun syncTransferAmountFromRate() {
        val sourceAmount = parseAmountOrNull(binding.amountEditText.text.toString()) ?: return
        val rate = parseAmountOrNull(binding.transferRateEditText.text.toString()) ?: return
        if (sourceAmount <= 0 || rate <= 0) return
        setTransferFieldText(binding.transferAmountEditText, formatAmount(sourceAmount * rate))
    }

    private fun setTransferFieldText(editText: EditText, value: String) {
        if (editText.text.toString() == value) return
        isSyncingTransferFields = true
        editText.setText(value)
        isSyncingTransferFields = false
    }

    private fun getTransferAmount(): Double? {
        return parseAmountOrNull(binding.transferAmountEditText.text.toString())
            ?: parseAmountOrNull(binding.transferRateEditText.text.toString())?.let {
                getAmount() * it
            }
    }

    private fun getAmount(): Double {
        return parseAmountOrNull(binding.amountEditText.text.toString()) ?: 0.0
    }

    private fun getTransferRate(): Double {
        return parseAmountOrNull(binding.transferRateEditText.text.toString()) ?: 0.0
    }

    private fun parseAmountOrNull(amountText: String): Double? {
        return try {
            Around.double(amountText)
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun formatAmount(value: Double): String {
        return String.format(Locale.US, "%.2f", value).trimTrailingZeros()
    }

    private fun formatRate(value: Double): String {
        return String.format(Locale.US, "%.6f", value).trimTrailingZeros()
    }

    private fun String.trimTrailingZeros(): String {
        return replace(Regex("(\\.\\d*?)0+$"), "$1").removeSuffix(".")
    }

    private fun setBackgroundWarningColor(editText: EditText) {
        editText.setBackgroundResource(R.drawable.input_field_error_background)
    }

    private fun showHideEraseButton(editText: EditText, eraseButton: View) {
        eraseButton.visibility = if (editText.text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
