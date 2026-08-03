package com.chico.myhomebookkeeping.ui.paymentPackage.newTransfer

import android.annotation.SuppressLint
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
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
import com.chico.myhomebookkeeping.helpers.displayName
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.textWathers.NewMoneyMovingAmountTextWatcher
import com.chico.myhomebookkeeping.ui.calc.CalcDialogFragment
import com.chico.myhomebookkeeping.ui.categories.CategoriesFragment
import com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving.QuickPaymentSettings
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
import kotlin.math.abs


class NewTransferFragment : Fragment() {

    private val viewModel: NewTransferViewModel by viewModels()
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!

    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()
    private var latestQuickPaymentSettings: QuickPaymentSettings? = null
    private var quickCurrencies: List<Currencies> = emptyList()
    private var quickCashAccounts: List<CashAccount> = emptyList()
    private val amountWholeDigitPickers = mutableListOf<DigitWheelView>()
    private val amountFractionDigitPickers = mutableListOf<DigitWheelView>()
    private var amountWholeDigitsCount = Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_WHOLE_DIGITS
    private var amountFractionDigitsCount = Constants.QUICK_PAYMENT_AMOUNT_DEFAULT_FRACTION_DIGITS
    private var isSyncingAmountPickers = false
    private var isSyncingTransferFields = false
    private var isTransferMoreExpanded = false
    private var keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

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
                viewModel.setSourceCurrencySelectMode()
                pressSelectButton(R.id.nav_currencies)
            }
            selectTransferCurrencyButton.setOnClickListener {
                viewModel.setDestinationCurrencySelectMode()
                pressSelectButton(R.id.nav_currencies)
            }
            selectCategoryButton.setOnClickListener {
                if (viewModel.isTransferMode()) {
                    viewModel.setDestinationCashAccountSelectMode()
                    pressSelectButton(R.id.nav_cash_account)
                } else {
                    pressSelectButton(
                        R.id.nav_categories,
                        CategoriesFragment.openModeArgs(CategoriesFragment.OPEN_MODE_NEW_PAYMENT)
                    )
                }
            }
            eraseButton.setOnClickListener {
                eraseAmountEditText()
            }
            submitButton.setOnClickListener {
                pressSubmitButton()
            }
            paymentTypeSwitchButton.setOnClickListener {
                pressSelectButton(R.id.nav_new_money_moving)
            }
            transferMoreButton.setOnClickListener {
                isTransferMoreExpanded = !isTransferMoreExpanded
                updateTransferMoreVisibility(viewModel.isTransferMode(), animate = true)
                if (isTransferMoreExpanded) {
                    scrollToTransferMore()
                }
            }
            calcButton.setOnClickListener {
                requireView().hideKeyboard()
                val calcFragment: CalcDialogFragment = CalcDialogFragment.newInstance(
                    amountEditText.text.toString()
                )
                calcFragment.show(childFragmentManager, "dialog")
            }
            amountEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus && isDigitsAmountInput()) {
                    scrollDescriptionIntoView()
                }
            }
            amountEditText.setOnClickListener {
                if (isDigitsAmountInput()) {
                    scrollDescriptionIntoView()
                }
            }
            amountEditText.doAfterTextChanged {
                if (viewModel.isTransferMode()) {
                    syncTransferFieldsFromSourceAmount()
                }
            }
            transferAmountEditText.doAfterTextChanged {
                if (!isSyncingTransferFields) {
                    syncTransferRateFromDestinationAmount()
                }
            }
            transferRateEditText.doAfterTextChanged {
                if (!isSyncingTransferFields) {
                    syncTransferAmountFromRate()
                }
            }
            description.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    keepViewAboveSubmitButton(description)
                }
            }
            description.setOnClickListener {
                keepViewAboveSubmitButton(description)
            }
            decreaseAmountWholeDigitsButton.setOnClickListener {
                changeAmountWholeDigits(-1)
            }
            increaseAmountWholeDigitsButton.setOnClickListener {
                changeAmountWholeDigits(1)
            }
            setupAmountPickers()
            setupKeyboardAwareSubmitButton()
        }
        with(viewModel) {
            dataTime.observe(viewLifecycleOwner) {
                binding.selectDateTimeButton.text = it.toString()
            }
            selectedCashAccount.observe(viewLifecycleOwner) {
                binding.selectCashAccountButton.text =
                    it.displayName(AppLanguage.getSelectedTag(requireContext()))
                rebuildQuickCashAccountRow()
            }
            selectedTransferCashAccount.observe(viewLifecycleOwner) {
                val transferCashAccountName =
                    it.displayName(AppLanguage.getSelectedTag(requireContext()))
                binding.selectTransferCashAccountButton.text = transferCashAccountName
                rebuildQuickTransferCashAccountRow()
            }
            selectedCurrency.observe(viewLifecycleOwner) {
                binding.selectCurrenciesButton.text = it.currencyName
                rebuildQuickCurrencyRow()
                syncSameCurrencyTransferDefaults()
            }
            selectedTransferCurrency.observe(viewLifecycleOwner) {
                binding.selectTransferCurrencyButton.text = it.currencyName
                rebuildQuickTransferCurrencyRow()
                syncSameCurrencyTransferDefaults()
            }
            selectedCategory.observe(viewLifecycleOwner) {
                if (!viewModel.isTransferMode()) {
                    lifecycleScope.launch {
                        binding.selectCategoryButton.text =
                            viewModel.getSelectedCategoryDisplayName(it)
                    }
                }
            }

            setDateTimeOnButton(currentDateTimeMillis)

            enteredAmount.observe(viewLifecycleOwner) {
                binding.amountEditText.setText(it.toString())
                syncAmountPickersFromAmountText()
            }
            enteredTransferAmount.observe(viewLifecycleOwner) {
                binding.transferAmountEditText.setText(it.toString())
            }
            enteredTransferRate.observe(viewLifecycleOwner) {
                binding.transferRateEditText.setText(it.toString())
            }
            enteredTransferFee.observe(viewLifecycleOwner) {
                binding.transferFeeEditText.setText(it?.toString() ?: "0")
            }
            enteredDescription.observe(viewLifecycleOwner) {
                binding.description.setText(it.toString())
            }
            submitButton.observe(viewLifecycleOwner) {
                binding.submitButton.text = it.toString()
            }
            isTransfer.observe(viewLifecycleOwner) {
                updateTransferModeUi(it)
            }
            quickPaymentSettings.observe(viewLifecycleOwner) {
                applyQuickPaymentSettings(it)
            }
        }
        viewModel.getAndCheckArgsSp()
        viewModel.setTransferMode(true)
        updateTransferModeUi(true)

        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            viewModel.onCalcAmountSelected.collectLatest {
                binding.amountEditText.setText(it)
            }
        }

        showHideEraseButton(binding.amountEditText, binding.eraseButton)
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
        updateTransferModeUi(viewModel.isTransferMode())
        lifecycleScope.launch {
            if (quickCurrencies.isEmpty()) quickCurrencies = viewModel.getAllCurrencies()
            if (quickCashAccounts.isEmpty()) quickCashAccounts = viewModel.getAllCashAccounts()
            rebuildQuickCurrencyRow()
            rebuildQuickTransferCurrencyRow()
            rebuildQuickCashAccountRow()
            rebuildQuickTransferCashAccountRow()
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
                    viewModel.setSourceCurrencySelectMode()
                    viewModel.selectCurrency(currency)
                }
            )
        }
        binding.currencyQuickSelectRow.addView(
            createQuickSelectButton(getString(R.string.text_on_button_more), false) {
                viewModel.setSourceCurrencySelectMode()
                pressSelectButton(R.id.nav_currencies)
            }
        )
    }

    private fun rebuildQuickCashAccountRow() {
        if (latestQuickPaymentSettings?.isCashAccountScrollEnabled != true || _binding == null) return
        binding.cashAccountQuickSelectRow.removeAllViews()
        quickCashAccounts.forEach { cashAccount ->
            val cashAccountName = cashAccount.displayName(AppLanguage.getSelectedTag(requireContext()))
            binding.cashAccountQuickSelectRow.addView(
                createQuickSelectButton(
                    text = cashAccount.bankAccountNumber.takeIf { it.isNotBlank() }?.let {
                        "$cashAccountName *${it.takeLast(4)}"
                    } ?: cashAccountName,
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

    private fun rebuildQuickTransferCurrencyRow() {
        if (latestQuickPaymentSettings?.isCurrencyScrollEnabled != true || _binding == null) return
        binding.transferCurrencyQuickSelectRow.removeAllViews()
        quickCurrencies.forEach { currency ->
            binding.transferCurrencyQuickSelectRow.addView(
                createQuickSelectButton(
                    text = currency.iso4217
                        ?.takeIf { it.isNotBlank() }
                        ?: currency.currencyNameShort
                        ?: currency.currencyName,
                    isSelected = currency.currencyId ==
                        viewModel.selectedTransferCurrency.value?.currencyId
                ) {
                    viewModel.selectTransferCurrency(currency)
                }
            )
        }
        binding.transferCurrencyQuickSelectRow.addView(
            createQuickSelectButton(getString(R.string.text_on_button_more), false) {
                viewModel.setDestinationCurrencySelectMode()
                pressSelectButton(R.id.nav_currencies)
            }
        )
    }

    private fun rebuildQuickTransferCashAccountRow() {
        if (latestQuickPaymentSettings?.isCashAccountScrollEnabled != true || _binding == null) return
        binding.transferCashAccountQuickSelectRow.removeAllViews()
        quickCashAccounts.forEach { cashAccount ->
            val cashAccountName = cashAccount.displayName(AppLanguage.getSelectedTag(requireContext()))
            binding.transferCashAccountQuickSelectRow.addView(
                createQuickSelectButton(
                    text = cashAccount.bankAccountNumber.takeIf { it.isNotBlank() }?.let {
                        "$cashAccountName *${it.takeLast(4)}"
                    } ?: cashAccountName,
                    isSelected = cashAccount.cashAccountId ==
                        viewModel.selectedTransferCashAccount.value?.cashAccountId
                ) {
                    viewModel.setDestinationCashAccountSelectMode()
                    viewModel.selectTransferCashAccount(cashAccount)
                }
            )
        }
        binding.transferCashAccountQuickSelectRow.addView(
            createQuickSelectButton(getString(R.string.text_on_button_more), false) {
                viewModel.setDestinationCashAccountSelectMode()
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
            gravity = Gravity.CENTER
            minHeight = resources.getDimensionPixelSize(R.dimen.quick_select_button_height)
            setPadding(
                resources.getDimensionPixelSize(R.dimen.margin_normal),
                0,
                resources.getDimensionPixelSize(R.dimen.margin_normal),
                0
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                resources.getDimensionPixelSize(R.dimen.quick_select_button_height)
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

    private fun createAmountDigitPicker(onValueChanged: (oldValue: Int, newValue: Int) -> Unit): DigitWheelView {
        return DigitWheelView(
            requireContext(),
            resources.getDimension(R.dimen.H2),
            resources.getDimension(R.dimen.H3)
        ).apply {
            layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.amount_digit_picker_width),
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                marginEnd = resources.getDimensionPixelSize(R.dimen.margin_half_normal)
            }
            onHorizontalTouchEvent = { event ->
                binding.amountScrollContainer.onTouchEvent(event)
            }
            onDoubleTap = {
                showAmountInputDialog()
            }
            onSingleTap = {
                val oldValue = value
                value = (value + 1) % 10
                if (oldValue == value) {
                    onValueChanged(oldValue, value)
                }
            }
            onValueChangedByScroll = { oldValue, newValue ->
                if (!isSyncingAmountPickers) {
                    onValueChanged(oldValue, newValue)
                }
            }
        }
    }

    private fun showAmountInputDialog() {
        val amountInput = EditText(requireContext()).apply {
            setText(binding.amountEditText.text.toString())
            setSelectAllOnFocus(true)
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
            setPadding(
                resources.getDimensionPixelSize(R.dimen.margin_double),
                resources.getDimensionPixelSize(R.dimen.margin_normal),
                resources.getDimensionPixelSize(R.dimen.margin_double),
                resources.getDimensionPixelSize(R.dimen.margin_normal)
            )
        }
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.description_amount))
            .setView(amountInput)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                binding.amountEditText.setText(amountInput.text.toString())
                syncAmountPickersFromAmountText()
            }
            .setNegativeButton(R.string.text_on_button_cancel, null)
            .create()
        dialog.setOnShowListener {
            amountInput.requestFocus()
            dialog.window?.setSoftInputMode(
                android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
            )
            val inputMethodManager = ContextCompat.getSystemService(
                requireContext(),
                InputMethodManager::class.java
            )
            inputMethodManager?.showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT)
        }
        dialog.show()
    }

    private fun rebuildAmountDigitPickers() {
        if (_binding == null) return
        binding.amountWholeDigitsRow.removeAllViews()
        amountWholeDigitPickers.clear()
        repeat(amountWholeDigitsCount) { index ->
            val picker = createAmountDigitPicker { oldValue, newValue ->
                handleAmountWholeDigitChanged(index, oldValue, newValue)
            }
            amountWholeDigitPickers += picker
            binding.amountWholeDigitsRow.addView(picker)
        }

        binding.amountFractionDigitsRow.removeAllViews()
        amountFractionDigitPickers.clear()
        repeat(amountFractionDigitsCount) { index ->
            val picker = createAmountDigitPicker { oldValue, newValue ->
                handleAmountFractionDigitChanged(index, oldValue, newValue)
            }
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

    private fun handleAmountWholeDigitChanged(index: Int, oldValue: Int, newValue: Int) {
        applyAmountDigitCarry(amountWholeDigitPickers, index, oldValue, newValue, null)
        updateAmountFromPickers()
    }

    private fun handleAmountFractionDigitChanged(index: Int, oldValue: Int, newValue: Int) {
        applyAmountDigitCarry(
            amountFractionDigitPickers,
            index,
            oldValue,
            newValue,
            amountWholeDigitPickers.lastOrNull()
        )
        updateAmountFromPickers()
    }

    private fun applyAmountDigitCarry(
        pickers: List<DigitWheelView>,
        index: Int,
        oldValue: Int,
        newValue: Int,
        carryTargetBeforeFirst: DigitWheelView?
    ) {
        val delta = when {
            oldValue == 9 && newValue == 0 -> 1
            oldValue == 0 && newValue == 9 -> -1
            else -> return
        }
        val carryTarget = pickers.getOrNull(index - 1) ?: carryTargetBeforeFirst ?: return
        carryAmountDigit(carryTarget, delta)
    }

    private fun carryAmountDigit(picker: DigitWheelView, delta: Int) {
        picker.value = if (delta > 0) {
            (picker.value + 1) % 10
        } else {
            if (picker.value == 0) 9 else picker.value - 1
        }
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
        val isTransferCurrencyNotNull = viewModel.isTransferCurrencyNotNull()
        val isCategoryNotNull = viewModel.isCategoryNotNull()
        val isTransferCashAccountNotNull = viewModel.isTransferCashAccountNotNull()
        val checkAmount = uiHelper.isEntered(binding.amountEditText.text)
        if (isCashAccountNotNull) {
            if (isCurrencyNotNull) {
                if (viewModel.isTransferMode()) {
                    pressSubmitTransferButton(
                        isTransferCashAccountNotNull,
                        isTransferCurrencyNotNull,
                        checkAmount
                    )
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
                if (shouldConfirmZeroAmount()) {
                    showZeroAmountConfirmDialog {
                        addNewMoneyMoving()
                    }
                } else {
                    addNewMoneyMoving()
                }
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
        isTransferCurrencyNotNull: Boolean,
        checkAmount: Boolean
    ) {
        if (!isTransferCashAccountNotNull) {
            message(getString(R.string.message_transfer_cash_account_not_selected))
            return
        }
        if (!isTransferCurrencyNotNull) {
            message(getString(R.string.message_transfer_currency_not_selected))
            return
        }
        if (!viewModel.isTransferAccountsDifferent()) {
            message(getString(R.string.message_transfer_cash_accounts_must_be_different))
            return
        }
        if (checkAmount) {
            if (shouldConfirmZeroAmount()) {
                showZeroAmountConfirmDialog {
                    addNewTransfer()
                }
            } else {
                val destinationAmount = getTransferAmount()
                if (destinationAmount != null && destinationAmount > 0) {
                    addNewTransfer()
                } else {
                    message(getString(R.string.message_enter_transfer_amount_or_rate))
                }
            }
        } else {
            setBackgroundWarningColor()
            message(getString(R.string.message_enter_amount))
        }
    }

    private fun isDigitsAmountInput(): Boolean {
        return latestQuickPaymentSettings?.amountInputMode !=
            Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL
    }

    private fun scrollDescriptionIntoView() {
        binding.newMoneyMovingScrollView.postDelayed({
            if (_binding == null || !binding.amountEditText.hasFocus()) return@postDelayed

            val bottomPadding = resources.getDimensionPixelSize(R.dimen.margin_normal)
            val descriptionBottom = binding.description.bottom + bottomPadding
            val visibleBottom =
                binding.newMoneyMovingScrollView.scrollY + binding.newMoneyMovingScrollView.height

            if (descriptionBottom > visibleBottom) {
                binding.newMoneyMovingScrollView.smoothScrollTo(
                    0,
                    descriptionBottom - binding.newMoneyMovingScrollView.height
                )
            }
        }, KEYBOARD_SCROLL_DELAY_MS)
    }

    private fun setupKeyboardAwareSubmitButton() {
        val scrollPaddingBottom = binding.newMoneyMovingScrollView.paddingBottom
        val visibleFrame = Rect()
        val submitButtonLocation = IntArray(2)

        keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (_binding == null) return@OnGlobalLayoutListener

            binding.root.getWindowVisibleDisplayFrame(visibleFrame)
            binding.submitButton.getLocationOnScreen(submitButtonLocation)

            val keyboardOffset =
                (binding.root.rootView.height - visibleFrame.bottom).coerceAtLeast(0)
            val isKeyboardVisible = keyboardOffset > binding.root.rootView.height * 0.15
            val submitButtonBottomWithoutTranslation =
                submitButtonLocation[1] + binding.submitButton.height - binding.submitButton.translationY
            val buttonOverlap =
                (submitButtonBottomWithoutTranslation - visibleFrame.bottom).coerceAtLeast(0f)
            val keyboardTranslation = if (isKeyboardVisible) -buttonOverlap else 0f

            binding.submitButton.translationY = keyboardTranslation
            binding.newMoneyMovingScrollView.setPadding(
                binding.newMoneyMovingScrollView.paddingLeft,
                binding.newMoneyMovingScrollView.paddingTop,
                binding.newMoneyMovingScrollView.paddingRight,
                scrollPaddingBottom + buttonOverlap.toInt()
            )
            if (isKeyboardVisible) {
                when {
                    binding.amountEditText.hasFocus() -> {
                        keepViewAboveSubmitButton(binding.amountInputContainer)
                    }
                    binding.description.hasFocus() -> {
                        keepViewAboveSubmitButton(binding.description)
                    }
                }
            }
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    private fun keepViewAboveSubmitButton(view: View) {
        binding.newMoneyMovingScrollView.post {
            if (_binding == null) return@post

            val viewLocation = IntArray(2)
            val submitButtonLocation = IntArray(2)
            view.getLocationOnScreen(viewLocation)
            binding.submitButton.getLocationOnScreen(submitButtonLocation)

            val bottomPadding = resources.getDimensionPixelSize(R.dimen.margin_normal)
            val viewBottom = viewLocation[1] + view.height + bottomPadding
            val submitButtonTop = submitButtonLocation[1]
            if (viewBottom > submitButtonTop) {
                binding.newMoneyMovingScrollView.smoothScrollBy(0, viewBottom - submitButtonTop)
            }
        }
    }

    private fun shouldConfirmZeroAmount(): Boolean {
        val amountText = binding.amountEditText.text.toString().trim()
        if (!isDigitsAmountInput() || amountText.isEmpty()) return false

        return parseAmountOrNull(amountText) == 0.0
    }

    private fun syncSameCurrencyTransferDefaults() {
        if (!viewModel.isTransferMode()) return
        val sourceCurrencyId = viewModel.selectedCurrency.value?.currencyId
        val destinationCurrencyId = viewModel.selectedTransferCurrency.value?.currencyId
        if (sourceCurrencyId != null && sourceCurrencyId == destinationCurrencyId) {
            setTransferFieldText(binding.transferRateEditText, "1")
            val sourceAmount = binding.amountEditText.text.toString()
            if (sourceAmount.isNotBlank()) {
                setTransferFieldText(binding.transferAmountEditText, sourceAmount)
            }
        }
    }

    private fun syncTransferFieldsFromSourceAmount() {
        if (isSyncingTransferFields) return
        val sourceAmount = parseAmountOrNull(binding.amountEditText.text.toString()) ?: return
        if (isSameTransferCurrency()) {
            setTransferFieldText(binding.transferRateEditText, "1")
            setTransferFieldText(binding.transferAmountEditText, formatAmount(sourceAmount))
            return
        }
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

    private fun isSameTransferCurrency(): Boolean {
        val sourceCurrencyId = viewModel.selectedCurrency.value?.currencyId
        val destinationCurrencyId = viewModel.selectedTransferCurrency.value?.currencyId
        return sourceCurrencyId != null && sourceCurrencyId == destinationCurrencyId
    }

    private fun setTransferFieldText(editText: EditText, value: String) {
        if (editText.text.toString() == value) return
        isSyncingTransferFields = true
        editText.setText(value)
        isSyncingTransferFields = false
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

    private fun parseAmountOrNull(amountText: String): Double? {
        return try {
            Around.double(amountText)
        } catch (_: NumberFormatException) {
            null
        }
    }

    private fun showZeroAmountConfirmDialog(onConfirm: () -> Unit) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.zero_amount_confirm_title)
            .setMessage(R.string.zero_amount_confirm_message)
            .setPositiveButton(R.string.text_on_button_add) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.text_on_button_cancel, null)
            .show()
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
            } else {
                message(getString(R.string.message_entry_add_failed))
            }
        }
    }

    private fun addNewTransfer() {
        val amount: Double = Around.double(binding.amountEditText.text.toString())
        val transferAmount = getTransferAmount() ?: amount
        val transferRate = getTransferRate()
        val transferFee = getTransferFee()
        val description = binding.description.text.toString()
        viewModel.saveDataToSP(amount, description, transferAmount, transferRate, transferFee)
        runBlocking {
            val result = viewModel.addNewTransfer(
                amount = amount,
                transferAmount = transferAmount,
                transferFee = transferFee,
                description = description
            )
            if (result.all { it > 0 }) {
                view?.hideKeyboard()
                viewModel.saveSPOfNewEntryIsAdded()
                control.navigate(R.id.nav_money_moving)
                viewModel.clearSPAfterSave()
            } else {
                message(getString(R.string.message_entry_add_failed))
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

    private fun pressSelectButton(fragment: Int, args: Bundle? = null) {
        viewModel.saveDataToSP(
            amount = getAmount(),
            description = getDescription(),
            transferAmount = getTransferAmount() ?: 0.0,
            transferRate = getTransferRate(),
            transferFee = getTransferFee()
        )
        navControlHelper.toSelectedFragment(fragment, args)
//        control.navigate(fragment)
    }

    private fun updateTransferModeUi(isTransfer: Boolean) {
        val isCurrencyScrollEnabled = latestQuickPaymentSettings?.isCurrencyScrollEnabled == true
        val isCashAccountScrollEnabled = latestQuickPaymentSettings?.isCashAccountScrollEnabled == true
        binding.paymentTypeSwitchButton.text = getString(
            if (isTransfer) R.string.text_on_button_transfer
            else R.string.text_on_button_payment
        )
        binding.sourceCashAccountLabel.text = getString(
            if (isTransfer) R.string.description_cash_account_from
            else R.string.description_cash_account
        )
        binding.currencyLabel.text = getString(
            if (isTransfer) R.string.description_currency_from
            else R.string.description_currency
        )
        binding.destinationCashAccountLabel.visibility = if (isTransfer) View.VISIBLE else View.GONE
        binding.selectTransferCashAccountButton.visibility =
            if (isTransfer && !isCashAccountScrollEnabled) View.VISIBLE else View.GONE
        binding.transferCashAccountQuickSelectScroll.visibility =
            if (isTransfer && isCashAccountScrollEnabled) View.VISIBLE else View.GONE
        binding.transferCurrencyLabel.visibility = if (isTransfer) View.VISIBLE else View.GONE
        binding.selectTransferCurrencyButton.visibility =
            if (isTransfer && !isCurrencyScrollEnabled) View.VISIBLE else View.GONE
        binding.transferCurrencyQuickSelectScroll.visibility =
            if (isTransfer && isCurrencyScrollEnabled) View.VISIBLE else View.GONE
        binding.transferAmountLabel.visibility = if (isTransfer) View.VISIBLE else View.GONE
        binding.transferAmountEditText.visibility = if (isTransfer) View.VISIBLE else View.GONE
        binding.transferMoreSheet.visibility = View.VISIBLE
        binding.transferMoreButton.visibility = if (isTransfer) View.VISIBLE else View.GONE
        updateTransferMoreSheetStyle(isTransfer)
        updateTransferMoreVisibility(isTransfer)
        if (isTransfer && binding.transferFeeEditText.text.isNullOrBlank()) {
            binding.transferFeeEditText.setText("0")
        }
        binding.categoryLabel.visibility = if (isTransfer) View.GONE else View.VISIBLE
        binding.selectCategoryButton.visibility = if (isTransfer) View.GONE else View.VISIBLE
        binding.selectCategoryButton.text = getString(R.string.text_on_button_select_category)
        if (!isTransfer) {
            viewModel.selectedCategory.value?.let { category ->
                lifecycleScope.launch {
                    binding.selectCategoryButton.text =
                        viewModel.getSelectedCategoryDisplayName(category)
                }
            }
        } else {
            syncSameCurrencyTransferDefaults()
        }
    }

    private fun updateTransferMoreVisibility(isTransfer: Boolean, animate: Boolean = false) {
        if (animate && _binding != null) {
            TransitionManager.beginDelayedTransition(
                binding.linearLayout,
                AutoTransition().apply { duration = TRANSFER_MORE_ANIMATION_DURATION_MS }
            )
        }
        val showTransferMore = isTransfer && isTransferMoreExpanded
        binding.transferMoreContainer.visibility = if (!isTransfer || showTransferMore) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.transferRateLabel.visibility = if (showTransferMore) View.VISIBLE else View.GONE
        binding.transferRateEditText.visibility = if (showTransferMore) View.VISIBLE else View.GONE
        binding.transferFeeLabel.visibility = if (showTransferMore) View.VISIBLE else View.GONE
        binding.transferFeeEditText.visibility = if (showTransferMore) View.VISIBLE else View.GONE
        binding.descriptionLabel.visibility = if (!isTransfer || showTransferMore) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.description.visibility = if (!isTransfer || showTransferMore) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.transferMoreText.text = getString(
            if (isTransferMoreExpanded) R.string.text_on_button_less
            else R.string.text_on_button_more
        )
        binding.transferMoreIcon.setImageResource(
            if (isTransferMoreExpanded) R.drawable.ic_expand_remove
            else R.drawable.ic_expand_add
        )
    }

    private fun updateTransferMoreSheetStyle(isTransfer: Boolean) {
        val normalPadding = resources.getDimensionPixelSize(R.dimen.margin_normal)
        if (isTransfer) {
            binding.transferMoreSheet.setBackgroundResource(R.drawable.button_neutral_background)
            binding.transferMoreContainer.setPadding(
                normalPadding,
                0,
                normalPadding,
                normalPadding
            )
        } else {
            binding.transferMoreSheet.background = null
            binding.transferMoreContainer.setPadding(0, 0, 0, 0)
        }
    }

    private fun scrollToTransferMore() {
        binding.transferMoreSheet.postDelayed({
            if (_binding == null || !isTransferMoreExpanded) return@postDelayed

            val bottomPadding = resources.getDimensionPixelSize(R.dimen.margin_normal)
            val targetBottom = binding.transferMoreSheet.bottom + bottomPadding
            val visibleBottom =
                binding.newMoneyMovingScrollView.scrollY + binding.newMoneyMovingScrollView.height

            if (targetBottom > visibleBottom) {
                binding.newMoneyMovingScrollView.smoothScrollTo(
                    0,
                    targetBottom - binding.newMoneyMovingScrollView.height
                )
            }
        }, TRANSFER_MORE_FOCUS_DELAY_MS)
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

    private fun getTransferAmount(): Double? {
        return parseAmountOrNull(binding.transferAmountEditText.text.toString())
            ?: parseAmountOrNull(binding.transferRateEditText.text.toString())?.let {
                getAmount() * it
            }
    }

    private fun getTransferRate(): Double {
        return parseAmountOrNull(binding.transferRateEditText.text.toString()) ?: 0.0
    }

    private fun getTransferFee(): Double {
        return parseAmountOrNull(binding.transferFeeEditText.text.toString()) ?: 0.0
    }

    override fun onDestroyView() {
        keyboardLayoutListener?.let {
            binding.root.viewTreeObserver.removeOnGlobalLayoutListener(it)
        }
        keyboardLayoutListener = null
        super.onDestroyView()
        _binding = null
    }

    private fun message(text: String) {


        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}

private class DigitWheelView(
    context: Context,
    private val selectedTextSizePx: Float,
    private val sideTextSizePx: Float
) : View(context) {

    var value: Int = 0
        set(newValue) {
            field = ((newValue % 10) + 10) % 10
            invalidate()
        }

    var onValueChangedByScroll: ((oldValue: Int, newValue: Int) -> Unit)? = null
    var onSingleTap: (() -> Unit)? = null
    var onDoubleTap: (() -> Unit)? = null
    var onHorizontalTouchEvent: ((MotionEvent) -> Unit)? = null

    private val selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFE8F5F4.toInt()
        textAlign = Paint.Align.CENTER
        textSize = selectedTextSizePx
    }
    private val sidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF889697.toInt()
        textAlign = Paint.Align.CENTER
        textSize = sideTextSizePx
    }
    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                onSingleTap?.invoke()
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                onDoubleTap?.invoke()
                return true
            }
        }
    )
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var startX = 0f
    private var startY = 0f
    private var lastY = 0f
    private var accumulatedY = 0f
    private var isHorizontalDrag = false
    private var isVerticalDrag = false
    private var settleAnimator: ValueAnimator? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        val verticalGap = digitStep()
        for (offset in -2..2) {
            val paint = if (offset == 0) selectedPaint else sidePaint
            drawCenteredText(
                canvas,
                digitAt(offset).toString(),
                centerX,
                centerY + accumulatedY + offset * verticalGap,
                paint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                settleAnimator?.cancel()
                startX = event.x
                startY = event.y
                lastY = event.y
                accumulatedY = 0f
                isHorizontalDrag = false
                isVerticalDrag = false
                parent?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - startX
                val dy = event.y - startY
                if (!isHorizontalDrag && !isVerticalDrag) {
                    isHorizontalDrag = abs(dx) > touchSlop && abs(dx) > abs(dy)
                    isVerticalDrag = abs(dy) > touchSlop && abs(dy) > abs(dx)
                    if (isHorizontalDrag) {
                        accumulatedY = 0f
                        invalidate()
                        parent?.requestDisallowInterceptTouchEvent(false)
                        onHorizontalTouchEvent?.invoke(MotionEvent.obtain(event).apply {
                            action = MotionEvent.ACTION_DOWN
                        })
                    }
                }
                if (isHorizontalDrag) {
                    onHorizontalTouchEvent?.invoke(event)
                    return true
                }
                if (isVerticalDrag) {
                    accumulatedY += event.y - lastY
                    val step = digitStep()
                    while (abs(accumulatedY) >= step) {
                        if (accumulatedY < 0f) {
                            changeBy(1)
                            accumulatedY += step
                        } else {
                            changeBy(-1)
                            accumulatedY -= step
                        }
                    }
                    invalidate()
                }
                lastY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isHorizontalDrag) {
                    onHorizontalTouchEvent?.invoke(event)
                }
                if (isVerticalDrag) {
                    settleOffsetToCenter()
                }
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    private fun changeBy(delta: Int) {
        val oldValue = value
        value += delta
        if (oldValue != value) {
            onValueChangedByScroll?.invoke(oldValue, value)
        }
    }

    private fun settleOffsetToCenter() {
        settleAnimator?.cancel()
        settleAnimator = ValueAnimator.ofFloat(accumulatedY, 0f).apply {
            duration = 120L
            addUpdateListener {
                accumulatedY = it.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun digitStep(): Float = (height / 3.4f).coerceAtLeast(1f)

    private fun digitAt(offset: Int): Int = ((value + offset) % 10 + 10) % 10

    private fun drawCenteredText(
        canvas: Canvas,
        text: String,
        centerX: Float,
        centerY: Float,
        paint: Paint
    ) {
        val baseline = centerY - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(text, centerX, baseline, paint)
    }
}

private const val KEYBOARD_SCROLL_DELAY_MS = 250L
private const val TRANSFER_MORE_ANIMATION_DURATION_MS = 180L
private const val TRANSFER_MORE_FOCUS_DELAY_MS = 190L
