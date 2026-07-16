package com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.textWathers.NewMoneyMovingAmountTextWatcher
import com.chico.myhomebookkeeping.ui.calc.CalcDialogFragment
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
//import kotlinx.android.synthetic.main.fragment_change_money_moving.*
//import kotlinx.android.synthetic.main.fragment_new_money_moving.amountEditText
//import kotlinx.android.synthetic.main.fragment_new_money_moving.eraseButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private var latestQuickPaymentSettings: QuickPaymentSettings? = null
    private var quickCurrencies: List<Currencies> = emptyList()
    private var quickCashAccounts: List<CashAccount> = emptyList()
    private val amountWholeDigitPickers = mutableListOf<NumberPicker>()
    private val amountFractionDigitPickers = mutableListOf<NumberPicker>()
    private var amountWholeDigitsCount = Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS
    private var amountFractionDigitsCount = Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS
    private var isSyncingAmountPickers = false

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
                viewModel.setSourceCashAccountSelectMode()
                pressSelectButton(R.id.nav_cash_account)
            }
            selectTransferCashAccountButton.setOnClickListener {
                viewModel.setDestinationCashAccountSelectMode()
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
            paymentTypeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val isTransfer = checkedId == R.id.transferModeButton
                    viewModel.setTransferMode(isTransfer)
                    updateTransferModeUi(isTransfer)
                }
            }
            calcButton.setOnClickListener {
                requireView().hideKeyboard()
                val calcFragment: CalcDialogFragment = CalcDialogFragment.newInstance(
                    amountEditText.text.toString()
                )
                calcFragment.show(childFragmentManager, "dialog")
            }
            decreaseAmountWholeDigitsButton.setOnClickListener {
                changeAmountWholeDigits(-1)
            }
            increaseAmountWholeDigitsButton.setOnClickListener {
                changeAmountWholeDigits(1)
            }
            setupAmountPickers()
        }
        with(viewModel) {
            dataTime.observe(viewLifecycleOwner) {
                binding.selectDateTimeButton.text = it.toString()
            }
            selectedCashAccount.observe(viewLifecycleOwner) {
                binding.selectCashAccountButton.text = it.accountName
                rebuildQuickCashAccountRow()
            }
            selectedTransferCashAccount.observe(viewLifecycleOwner) {
                binding.selectTransferCashAccountButton.text = it.accountName
            }
            selectedCurrency.observe(viewLifecycleOwner) {
                binding.selectCurrenciesButton.text = it.currencyName
                rebuildQuickCurrencyRow()
            }
            selectedCategory.observe(viewLifecycleOwner) {
                binding.selectCategoryButton.text = it.categoryName
            }

            setDateTimeOnButton(currentDateTimeMillis)

            enteredAmount.observe(viewLifecycleOwner) {
                binding.amountEditText.setText(it.toString())
                syncAmountPickersFromAmountText()
            }
            enteredDescription.observe(viewLifecycleOwner) {
                binding.description.setText(it.toString())
            }
            submitButton.observe(viewLifecycleOwner) {
                binding.submitButton.text = it.toString()
            }
            isTransfer.observe(viewLifecycleOwner) {
                binding.paymentTypeToggleGroup.check(
                    if (it) R.id.transferModeButton else R.id.paymentModeButton
                )
                updateTransferModeUi(it)
            }
            quickPaymentSettings.observe(viewLifecycleOwner) {
                applyQuickPaymentSettings(it)
            }
        }
        viewModel.getAndCheckArgsSp()

        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.onCalcAmountSelected.collectLatest {
                binding.amountEditText.setText(it)
            }
        }

        showHideEraseButton(binding.amountEditText, binding.eraseButton)
    }

    fun openQuickPaymentSettings() {
        lifecycleScope.launch {
            val dialog = QuickPaymentSettingsDialog(
                settings = viewModel.getQuickPaymentSettings(),
                currencies = viewModel.getAllCurrencies(),
                cashAccounts = viewModel.getAllCashAccounts(),
                defaultCurrency = viewModel.getDefaultCurrency(),
                defaultCashAccount = viewModel.getDefaultCashAccount(),
                onSettingsSubmitted = {
                    viewModel.saveQuickPaymentSettings(it)
                },
                onDefaultCurrencySubmitted = {
                    viewModel.setDefaultCurrency(it)
                },
                onDefaultCashAccountSubmitted = {
                    viewModel.setDefaultCashAccount(it)
                }
            )
            dialog.show(childFragmentManager, "quickPaymentSettingsDialog")
        }
    }

    private fun applyQuickPaymentSettings(settings: QuickPaymentSettings) {
        latestQuickPaymentSettings = settings
        val isScrollAmountInput =
            settings.amountInputMode == Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL
        configureAmountPickers(settings)
        binding.amountInputContainer.visibility = if (isScrollAmountInput) {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.amountScrollContainer.visibility = if (isScrollAmountInput) {
            View.VISIBLE
        } else {
            View.GONE
        }
        if (isScrollAmountInput) {
            syncAmountPickersFromAmountText()
        }
        binding.calcButton.visibility = if (settings.isCalculatorButtonVisible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.selectCurrenciesButton.visibility = if (settings.isCurrencyScrollEnabled) {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.currencyQuickSelectScroll.visibility = if (settings.isCurrencyScrollEnabled) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.selectCashAccountButton.visibility = if (settings.isCashAccountScrollEnabled) {
            View.GONE
        } else {
            View.VISIBLE
        }
        binding.cashAccountQuickSelectScroll.visibility = if (settings.isCashAccountScrollEnabled) {
            View.VISIBLE
        } else {
            View.GONE
        }
        lifecycleScope.launch {
            if (quickCurrencies.isEmpty()) quickCurrencies = viewModel.getAllCurrencies()
            if (quickCashAccounts.isEmpty()) quickCashAccounts = viewModel.getAllCashAccounts()
            rebuildQuickCurrencyRow()
            rebuildQuickCashAccountRow()
        }
    }

    private fun rebuildQuickCurrencyRow() {
        if (latestQuickPaymentSettings?.isCurrencyScrollEnabled != true || _binding == null) return
        binding.currencyQuickSelectRow.removeAllViews()
        quickCurrencies.forEach { currency ->
            binding.currencyQuickSelectRow.addView(
                createQuickSelectButton(
                    text = currency.iso4217
                        ?.takeIf { it.isNotBlank() }
                        ?: currency.currencyNameShort
                        ?: currency.currencyName,
                    isSelected = currency.currencyId == viewModel.selectedCurrency.value?.currencyId
                ) {
                    viewModel.selectCurrency(currency)
                }
            )
        }
        binding.currencyQuickSelectRow.addView(
            createQuickSelectButton(getString(R.string.text_on_button_more), false) {
                pressSelectButton(R.id.nav_currencies)
            }
        )
    }

    private fun rebuildQuickCashAccountRow() {
        if (latestQuickPaymentSettings?.isCashAccountScrollEnabled != true || _binding == null) return
        binding.cashAccountQuickSelectRow.removeAllViews()
        quickCashAccounts.forEach { cashAccount ->
            binding.cashAccountQuickSelectRow.addView(
                createQuickSelectButton(
                    text = cashAccount.bankAccountNumber.takeIf { it.isNotBlank() }?.let {
                        "${cashAccount.accountName} *${it.takeLast(4)}"
                    } ?: cashAccount.accountName,
                    isSelected = cashAccount.cashAccountId ==
                        viewModel.selectedCashAccount.value?.cashAccountId
                ) {
                    viewModel.selectCashAccount(cashAccount)
                }
            )
        }
        binding.cashAccountQuickSelectRow.addView(
            createQuickSelectButton(getString(R.string.text_on_button_more), false) {
                viewModel.setSourceCashAccountSelectMode()
                pressSelectButton(R.id.nav_cash_account)
            }
        )
    }

    private fun createQuickSelectButton(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ): Button {
        return Button(requireContext()).apply {
            this.text = text
            isAllCaps = false
            maxLines = 1
            setPadding(
                resources.getDimensionPixelSize(R.dimen.margin_normal),
                0,
                resources.getDimensionPixelSize(R.dimen.margin_normal),
                0
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginEnd = resources.getDimensionPixelSize(R.dimen.margin_half_normal)
            }
            if (isSelected) {
                setBackgroundResource(R.drawable.button_primary_background)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.buttonPrimaryText))
            } else {
                setBackgroundResource(R.drawable.button_neutral_background)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.buttonNeutralText))
            }
            setOnClickListener { onClick() }
        }
    }

    private fun setupAmountPickers() {
        rebuildAmountDigitPickers()
    }

    private fun createAmountDigitPicker(onValueChanged: () -> Unit): NumberPicker {
        return NumberPicker(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                marginEnd = resources.getDimensionPixelSize(R.dimen.margin_half_normal)
            }
            minValue = 0
            maxValue = 9
            wrapSelectorWheel = true
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            setOnValueChangedListener { _, _, _ ->
                if (!isSyncingAmountPickers) onValueChanged()
            }
            setOnClickListener {
                value = (value + 1) % 10
                onValueChanged()
            }
        }
    }

    private fun rebuildAmountDigitPickers() {
        if (_binding == null) return
        binding.amountWholeDigitsRow.removeAllViews()
        amountWholeDigitPickers.clear()
        repeat(amountWholeDigitsCount) {
            val picker = createAmountDigitPicker(::updateAmountFromPickers)
            amountWholeDigitPickers += picker
            binding.amountWholeDigitsRow.addView(picker)
        }

        binding.amountFractionDigitsRow.removeAllViews()
        amountFractionDigitPickers.clear()
        repeat(amountFractionDigitsCount) {
            val picker = createAmountDigitPicker(::updateAmountFromPickers)
            amountFractionDigitPickers += picker
            binding.amountFractionDigitsRow.addView(picker)
        }
        binding.amountFractionGroup.visibility = if (amountFractionDigitsCount > 0) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.amountDecimalSeparator.visibility = binding.amountFractionGroup.visibility
        syncAmountPickersFromAmountText()
    }

    private fun configureAmountPickers(settings: QuickPaymentSettings) {
        val newWholeDigitsCount = settings.amountWholeDigits.coerceIn(1, 9)
        val newFractionDigitsCount = settings.amountFractionDigits.coerceIn(0, 4)
        if (
            newWholeDigitsCount != amountWholeDigitsCount ||
            newFractionDigitsCount != amountFractionDigitsCount ||
            amountWholeDigitPickers.isEmpty()
        ) {
            amountWholeDigitsCount = newWholeDigitsCount
            amountFractionDigitsCount = newFractionDigitsCount
            rebuildAmountDigitPickers()
        } else {
            syncAmountPickersFromAmountText()
        }
    }

    private fun changeAmountWholeDigits(delta: Int) {
        amountWholeDigitsCount = (amountWholeDigitsCount + delta).coerceIn(1, 9)
        rebuildAmountDigitPickers()
    }

    private fun syncAmountPickersFromAmountText() {
        val amountText = binding.amountEditText.text.toString()
        val parts = amountText.split(".", limit = 2)
        val wholeDigits = parts.getOrNull(0)
            .orEmpty()
            .filter(Char::isDigit)
            .takeLast(amountWholeDigitsCount)
            .padStart(amountWholeDigitsCount, '0')
        val fractionDigits = parts.getOrNull(1)
            .orEmpty()
            .filter(Char::isDigit)
            .padEnd(amountFractionDigitsCount, '0')
            .take(amountFractionDigitsCount)

        isSyncingAmountPickers = true
        amountWholeDigitPickers.forEachIndexed { index, picker ->
            picker.value = wholeDigits.getOrNull(index)?.digitToIntOrNull() ?: 0
        }
        amountFractionDigitPickers.forEachIndexed { index, picker ->
            picker.value = fractionDigits.getOrNull(index)?.digitToIntOrNull() ?: 0
        }
        isSyncingAmountPickers = false
    }

    private fun updateAmountFromPickers() {
        val wholeText = amountWholeDigitPickers
            .joinToString("") { it.value.toString() }
            .trimStart('0')
        val fractionText = amountFractionDigitPickers.joinToString("") { it.value.toString() }
        val amount = when {
            wholeText.isBlank() && fractionText.all { it == '0' } -> ""
            amountFractionDigitsCount == 0 -> wholeText.ifBlank { "0" }
            else -> "${wholeText.ifBlank { "0" }}.$fractionText"
        }
        if (binding.amountEditText.text.toString() != amount) {
            binding.amountEditText.setText(amount)
        }
    }

    private fun showHideEraseButton(amountEditText: EditText, eraseButton: ImageButton) {
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
        val isTransferCashAccountNotNull = viewModel.isTransferCashAccountNotNull()
        val checkAmount = uiHelper.isEntered(binding.amountEditText.text)
        if (isCashAccountNotNull) {
            if (isCurrencyNotNull) {
                if (viewModel.isTransferMode()) {
                    pressSubmitTransferButton(isTransferCashAccountNotNull, checkAmount)
                } else {
                    pressSubmitPaymentButton(isCategoryNotNull, checkAmount)
                }
            } else {
                message(getString(R.string.message_currency_not_selected))
            }
        } else {
            message(getString(R.string.message_cash_account_not_selected))
        }
    }

    private fun pressSubmitPaymentButton(isCategoryNotNull: Boolean, checkAmount: Boolean) {
        if (isCategoryNotNull) {
            if (checkAmount) {
                addNewMoneyMoving()
            } else {
                setBackgroundWarningColor()
                message(getString(R.string.message_enter_amount))
            }
        } else {
            message(getString(R.string.message_category_not_selected))
        }
    }

    private fun pressSubmitTransferButton(
        isTransferCashAccountNotNull: Boolean,
        checkAmount: Boolean
    ) {
        if (!isTransferCashAccountNotNull) {
            message(getString(R.string.message_transfer_cash_account_not_selected))
            return
        }
        if (!viewModel.isTransferAccountsDifferent()) {
            message(getString(R.string.message_transfer_cash_accounts_must_be_different))
            return
        }
        if (checkAmount) {
            addNewTransfer()
        } else {
            setBackgroundWarningColor()
            message(getString(R.string.message_enter_amount))
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

    private fun addNewTransfer() {
        val amount: Double = Around.double(binding.amountEditText.text.toString())
        val description = binding.description.text.toString()
        viewModel.saveDataToSP(amount, description)
        runBlocking {
            val result = viewModel.addNewTransfer(
                amount = amount,
                description = description
            )
            if (result.all { it > 0 }) {
                view?.hideKeyboard()
                viewModel.saveSPOfNewEntryIsAdded()
                control.navigate(R.id.nav_money_moving)
                viewModel.clearSPAfterSave()
            }
        }
    }


    private fun setBackgroundWarningColor() {
        binding.amountInputContainer.setBackgroundResource(R.drawable.input_field_error_background)
    }

//    private fun setBackgroundDefaultColor(editText: EditText) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            editText.setBackgroundColor(
//                resources.getColor(
//                    R.color.design_default_color_background,
//                    null
//                )
//            )
//        }
//    }

    private fun pressSelectButton(fragment: Int) {
        viewModel.saveDataToSP(getAmount(), getDescription())
        navControlHelper.toSelectedFragment(fragment)
//        control.navigate(fragment)
    }

    private fun updateTransferModeUi(isTransfer: Boolean) {
        binding.sourceCashAccountLabel.text = getString(
            if (isTransfer) R.string.description_cash_account_from
            else R.string.description_cash_account
        )
        val transferVisibility = if (isTransfer) View.VISIBLE else View.GONE
        val paymentVisibility = if (isTransfer) View.GONE else View.VISIBLE
        binding.destinationCashAccountLabel.visibility = transferVisibility
        binding.selectTransferCashAccountButton.visibility = transferVisibility
        binding.categoryLabel.visibility = paymentVisibility
        binding.selectCategoryButton.visibility = paymentVisibility
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
