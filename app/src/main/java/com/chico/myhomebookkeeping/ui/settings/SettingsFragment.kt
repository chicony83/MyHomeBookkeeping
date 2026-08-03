package com.chico.myhomebookkeeping.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.backup.DatabaseBackupManager
import com.chico.myhomebookkeeping.backup.DatabaseRestoreManager
import com.chico.myhomebookkeeping.checks.GetVersionCode
import com.chico.myhomebookkeeping.databinding.FragmentSettingsBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.helpers.displayName
import com.chico.myhomebookkeeping.obj.AppLanguage
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.obj.QuickAccessPanel
import com.chico.myhomebookkeeping.ui.dialogs.WhatNewInLastVersionDialog
import com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving.QuickPaymentSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

class SettingsFragment : Fragment() {
    companion object {
        const val ARG_SECTION = "settingsSection"
        const val SECTION_QUICK_PAYMENT = "quickPayment"
    }

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private val uiHelper = UiHelper()
    private lateinit var getVersionCode: GetVersionCode
    private var isCurrencyScrollEnabled = Constants.QUICK_PAYMENT_DEFAULT_CURRENCY_SELECTION_SCROLL
    private var isCashAccountScrollEnabled = Constants.QUICK_PAYMENT_DEFAULT_CASH_ACCOUNT_SELECTION_SCROLL
    private var isCalculatorButtonVisible = true
    private var amountInputMode = Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS
    private var amountWholeDigits = 6
    private var amountFractionDigits = 2
    private var selectedStartFragmentValue = Constants.START_FRAGMENT_FAST_PAYMENTS
    private var selectedAppLanguageTag = Constants.APP_LANGUAGE_SYSTEM
    private var selectedJournalCurrencyDisplayMode = Constants.JOURNAL_CURRENCY_DISPLAY_NAME
    private var isJournalDateSeparatorsEnabled = true
    private var quickAccessItemKeys = emptyList<String>()
    private var isBindingSettings = false
    private var pendingBackupPassword: CharArray? = null
    private var pendingRestoreUri: Uri? = null
    private val updateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        _binding?.checkNewVersionButton?.isEnabled = true
        if (result.resultCode != Activity.RESULT_OK) {
            message(getString(R.string.message_update_not_completed))
        }
    }
    private val createBackupDocument = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        val password = pendingBackupPassword
        pendingBackupPassword = null
        if (uri == null || password == null) {
            password?.fill('\u0000')
            return@registerForActivityResult
        }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.createBackupButton.isEnabled = false
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseBackupManager.createEncryptedBackup(
                        requireContext().applicationContext,
                        uri,
                        password
                    )
                }
            }
            binding.createBackupButton.isEnabled = true
            message(
                getString(
                    if (result.isSuccess) R.string.backup_created else R.string.backup_failed
                )
            )
        }
    }
    private val openBackupDocument = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        pendingRestoreUri = uri
        if (uri != null) showRestorePasswordDialog()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        control = findNavController()
        navControlHelper = NavControlHelper(control)
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        getVersionCode = GetVersionCode(requireActivity(), updateLauncher)

        with(binding) {
//            changePasswordButton.setOnClickListener {
//                navControlHelper.moveToSelectedFragment(R.id.nav_select_password)
//            }
            defaultCurrencyRow.setOnClickListener {
                showSelectDefaultCurrencyDialog()
            }
            defaultCashAccountRow.setOnClickListener {
                showSelectDefaultCashAccountDialog()
            }
            currencySelectionModeRow.setOnClickListener {
                showBooleanChoiceDialog(
                    title = getString(R.string.quick_payment_currency_selection_mode),
                    currentValue = isCurrencyScrollEnabled,
                    trueLabel = getString(R.string.settings_value_scroll),
                    falseLabel = getString(R.string.settings_value_list)
                ) {
                    isCurrencyScrollEnabled = it
                    updateSettingsValues()
                    saveQuickPaymentSettings()
                }
            }
            cashAccountSelectionModeRow.setOnClickListener {
                showBooleanChoiceDialog(
                    title = getString(R.string.quick_payment_cash_account_selection_mode),
                    currentValue = isCashAccountScrollEnabled,
                    trueLabel = getString(R.string.settings_value_scroll),
                    falseLabel = getString(R.string.settings_value_list)
                ) {
                    isCashAccountScrollEnabled = it
                    updateSettingsValues()
                    saveQuickPaymentSettings()
                }
            }
            calculatorCheckBox.setOnCheckedChangeListener { _, isChecked ->
                isCalculatorButtonVisible = isChecked
                if (!isBindingSettings) saveQuickPaymentSettings()
            }
            amountInputModeRow.setOnClickListener {
                showChoiceDialog(
                    title = getString(R.string.quick_payment_amount_input),
                    labels = arrayOf(
                        getString(R.string.quick_payment_amount_input_digits),
                        getString(R.string.quick_payment_amount_input_scroll)
                    ),
                    selectedIndex = if (amountInputMode == Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL) 1 else 0
                ) { index ->
                    amountInputMode = if (index == 1) {
                        Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL
                    } else {
                        Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS
                    }
                    updateSettingsValues()
                    saveQuickPaymentSettings()
                }
            }
            amountWholeDigitsRow.setOnClickListener {
                showNumberChoiceDialog(
                    title = getString(R.string.quick_payment_amount_whole_digits),
                    min = 1,
                    max = 9,
                    currentValue = amountWholeDigits
                ) {
                    amountWholeDigits = it
                    updateSettingsValues()
                    saveQuickPaymentSettings()
                }
            }
            amountFractionDigitsRow.setOnClickListener {
                showNumberChoiceDialog(
                    title = getString(R.string.quick_payment_amount_fraction_digits),
                    min = 0,
                    max = 4,
                    currentValue = amountFractionDigits
                ) {
                    amountFractionDigits = it
                    updateSettingsValues()
                    saveQuickPaymentSettings()
                }
            }
            startFragmentRow.setOnClickListener {
                val values = arrayOf(
                    Constants.START_FRAGMENT_FAST_PAYMENTS,
                    Constants.START_FRAGMENT_CATEGORIES,
                    Constants.START_FRAGMENT_JOURNAL
                )
                showChoiceDialog(
                    title = getString(R.string.settings_start_fragment_title),
                    labels = arrayOf(
                        getString(R.string.first_launch_start_destination_fast_payments),
                        getString(R.string.first_launch_start_destination_categories),
                        getString(R.string.first_launch_start_destination_journal)
                    ),
                    selectedIndex = values.indexOf(selectedStartFragmentValue).coerceAtLeast(0)
                ) { index ->
                    selectedStartFragmentValue = values[index]
                    updateSettingsValues()
                    settingsViewModel.saveStartFragment(selectedStartFragmentValue)
                }
            }
            appLanguageRow.setOnClickListener {
                showAppLanguageDialog()
            }
            journalCurrencyDisplayModeRow.setOnClickListener {
                showJournalCurrencyDisplayModeDialog()
            }
            journalDateSeparatorsCheckBox.setOnCheckedChangeListener { _, isChecked ->
                isJournalDateSeparatorsEnabled = isChecked
                if (!isBindingSettings) {
                    settingsViewModel.saveJournalDateSeparatorsEnabled(isChecked)
                }
            }
            checkNewVersionButton.setOnClickListener {
                checkNewVersion()
            }
            versionHistoryButton.setOnClickListener {
                WhatNewInLastVersionDialog().show(
                    childFragmentManager,
                    getString(R.string.tag_show_dialog)
                )
            }
            createBackupButton.setOnClickListener {
                showBackupPasswordDialog()
            }
            restoreBackupButton.setOnClickListener {
                openBackupDocument.launch(arrayOf("application/octet-stream", "*/*"))
            }
        }
        with(settingsViewModel){
            appVersion.observe(viewLifecycleOwner, {
                binding.appVersion.text = it.toString()
            })
            quickPaymentSettings.observe(viewLifecycleOwner) {
                bindQuickPaymentSettings(it)
            }
            startFragment.observe(viewLifecycleOwner) {
                selectedStartFragmentValue = it
                isBindingSettings = true
                updateSettingsValues()
                isBindingSettings = false
            }
            quickAccessItems.observe(viewLifecycleOwner) {
                quickAccessItemKeys = it
                bindQuickAccessSettings()
            }
            appLanguage.observe(viewLifecycleOwner) {
                selectedAppLanguageTag = it
                binding.appLanguageValue.text = appLanguageTitle(it)
            }
            journalCurrencyDisplayMode.observe(viewLifecycleOwner) {
                selectedJournalCurrencyDisplayMode = it
                binding.journalCurrencyDisplayModeValue.text = journalCurrencyDisplayModeTitle(it)
            }
            journalDateSeparatorsEnabled.observe(viewLifecycleOwner) {
                isJournalDateSeparatorsEnabled = it
                isBindingSettings = true
                binding.journalDateSeparatorsCheckBox.isChecked = it
                isBindingSettings = false
            }
        }
        loadDefaultSelectionTitles()

        return binding.root
    }

    private fun checkNewVersion() {
        binding.checkNewVersionButton.isEnabled = false
        getVersionCode.getNewVersion {
            _binding?.checkNewVersionButton?.isEnabled = true
        }
    }

    private fun bindQuickPaymentSettings(settings: QuickPaymentSettings) {
        isBindingSettings = true
        isCurrencyScrollEnabled = settings.isCurrencyScrollEnabled
        isCashAccountScrollEnabled = settings.isCashAccountScrollEnabled
        isCalculatorButtonVisible = settings.isCalculatorButtonVisible
        amountInputMode = settings.amountInputMode
        amountWholeDigits = settings.amountWholeDigits.coerceIn(1, 9)
        amountFractionDigits = settings.amountFractionDigits.coerceIn(0, 4)
        updateSettingsValues()
        isBindingSettings = false
    }

    private fun updateSettingsValues() {
        binding.currencySelectionModeValue.text = selectionModeTitle(isCurrencyScrollEnabled)
        binding.cashAccountSelectionModeValue.text = selectionModeTitle(isCashAccountScrollEnabled)
        binding.calculatorCheckBox.isChecked = isCalculatorButtonVisible
        binding.amountInputModeValue.text = getString(
            if (amountInputMode == Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL) {
                R.string.quick_payment_amount_input_scroll
            } else {
                R.string.quick_payment_amount_input_digits
            }
        )
        binding.amountWholeDigitsValue.text = amountWholeDigits.toString()
        binding.amountFractionDigitsValue.text = amountFractionDigits.toString()
        binding.startFragmentValue.text = startFragmentTitle(selectedStartFragmentValue)
        binding.appLanguageValue.text = appLanguageTitle(selectedAppLanguageTag)
        binding.journalCurrencyDisplayModeValue.text =
            journalCurrencyDisplayModeTitle(selectedJournalCurrencyDisplayMode)
        binding.journalDateSeparatorsCheckBox.isChecked = isJournalDateSeparatorsEnabled
        binding.amountScrollDigitsContainer.visibility =
            if (amountInputMode == Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun bindQuickAccessSettings() {
        binding.quickAccessButtonsContainer.removeAllViews()
        quickAccessItemKeys.forEachIndexed { index, key ->
            val item = QuickAccessPanel.findByKey(key) ?: return@forEachIndexed
            binding.quickAccessButtonsContainer.addView(
                createQuickAccessRow(
                    label = getString(R.string.quick_access_button_title, index + 1),
                    value = getString(item.titleRes)
                ) {
                    showQuickAccessChoiceDialog(index)
                }
            )
        }
        if (quickAccessItemKeys.size < QuickAccessPanel.MAX_ITEMS) {
            binding.quickAccessButtonsContainer.addView(
                createQuickAccessRow(
                    label = getString(R.string.quick_access_button_title, quickAccessItemKeys.size + 1),
                    value = getString(R.string.quick_access_add_button)
                ) {
                    showQuickAccessChoiceDialog(quickAccessItemKeys.size)
                }
            )
        }
    }

    private fun createQuickAccessRow(
        label: String,
        value: String,
        onClick: () -> Unit
    ): View {
        val row = LinearLayout(requireContext(), null, 0, R.style.SettingsValueRow).apply {
            setOnClickListener { onClick() }
        }
        val labelView = TextView(requireContext(), null, 0, R.style.SettingsValueLabel).apply {
            text = label
        }
        val valueView = TextView(requireContext(), null, 0, R.style.SettingsValueText).apply {
            text = value
        }
        row.addView(
            labelView,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )
        row.addView(
            valueView,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = resources.getDimensionPixelSize(R.dimen.margin_double)
            }
        )
        return row
    }

    private fun showQuickAccessChoiceDialog(position: Int) {
        val isExistingPosition = position < quickAccessItemKeys.size
        val currentKey = quickAccessItemKeys.getOrNull(position)
        val canRemove = isExistingPosition && quickAccessItemKeys.size > QuickAccessPanel.MIN_ITEMS
        val items = QuickAccessPanel.availableItems
        val labels = items
            .map { getString(it.titleRes) }
            .let { if (canRemove) it + getString(R.string.quick_access_none) else it }
            .toTypedArray()
        val selectedIndex = items.indexOfFirst { it.key == currentKey }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.quick_access_select_title)
            .setSingleChoiceItems(labels, selectedIndex) { dialog, which ->
                if (canRemove && which == items.size) {
                    settingsViewModel.saveQuickAccessItems(
                        quickAccessItemKeys.toMutableList().apply { removeAt(position) }
                    )
                    dialog.dismiss()
                    return@setSingleChoiceItems
                }

                val selectedKey = items[which].key
                if (selectedKey != currentKey && quickAccessItemKeys.contains(selectedKey)) {
                    message(getString(R.string.quick_access_already_added))
                    val alertDialog = dialog as AlertDialog
                    if (selectedIndex >= 0) {
                        alertDialog.listView.setItemChecked(selectedIndex, true)
                    } else {
                        alertDialog.listView.clearChoices()
                        alertDialog.listView.requestLayout()
                    }
                    return@setSingleChoiceItems
                }

                val updatedItems = quickAccessItemKeys.toMutableList()
                if (isExistingPosition) {
                    updatedItems[position] = selectedKey
                } else {
                    updatedItems.add(selectedKey)
                }
                settingsViewModel.saveQuickAccessItems(updatedItems)
                dialog.dismiss()
            }
            .show()
    }

    private fun selectionModeTitle(isScrollEnabled: Boolean): String {
        return getString(
            if (isScrollEnabled) R.string.settings_value_scroll else R.string.settings_value_list
        )
    }

    private fun loadDefaultSelectionTitles() {
        viewLifecycleOwner.lifecycleScope.launch {
            val defaultCurrency = withContext(Dispatchers.IO) {
                settingsViewModel.getDefaultCurrency()
            }
            val defaultCashAccount = withContext(Dispatchers.IO) {
                settingsViewModel.getDefaultCashAccount()
            }
            binding.defaultCurrencyValue.text = defaultCurrency?.let(::currencyTitle)
                ?: getString(R.string.quick_payment_default_currency)
            binding.defaultCashAccountValue.text = defaultCashAccount?.let(::cashAccountTitle)
                ?: getString(R.string.quick_payment_default_cash_account)
        }
    }

    private fun showSelectDefaultCurrencyDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val currencies = withContext(Dispatchers.IO) {
                settingsViewModel.getAllCurrencies()
            }
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.quick_payment_default_currency)
                .setItems(currencies.map(::currencyTitle).toTypedArray()) { _, which ->
                    val currency = currencies[which]
                    viewLifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            settingsViewModel.setDefaultCurrency(currency)
                        }
                        binding.defaultCurrencyValue.text = currencyTitle(currency)
                    }
                }
                .show()
        }
    }

    private fun showSelectDefaultCashAccountDialog() {
        viewLifecycleOwner.lifecycleScope.launch {
            val cashAccounts = withContext(Dispatchers.IO) {
                settingsViewModel.getAllCashAccounts()
            }
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.quick_payment_default_cash_account)
                .setItems(cashAccounts.map(::cashAccountTitle).toTypedArray()) { _, which ->
                    val cashAccount = cashAccounts[which]
                    viewLifecycleOwner.lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            settingsViewModel.setDefaultCashAccount(cashAccount)
                        }
                        binding.defaultCashAccountValue.text = cashAccountTitle(cashAccount)
                    }
                }
                .show()
        }
    }

    private fun saveQuickPaymentSettings() {
        settingsViewModel.saveQuickPaymentSettings(createQuickPaymentSettings())
    }

    private fun createQuickPaymentSettings(): QuickPaymentSettings {
        return QuickPaymentSettings(
            isCurrencyScrollEnabled = isCurrencyScrollEnabled,
            isCashAccountScrollEnabled = isCashAccountScrollEnabled,
            isCalculatorButtonVisible = isCalculatorButtonVisible,
            amountInputMode = amountInputMode,
            amountWholeDigits = amountWholeDigits,
            amountFractionDigits = amountFractionDigits
        )
    }

    private fun startFragmentTitle(startFragment: String): String {
        return getString(
            when (startFragment) {
                Constants.START_FRAGMENT_CATEGORIES -> R.string.first_launch_start_destination_categories
                Constants.START_FRAGMENT_JOURNAL -> R.string.first_launch_start_destination_journal
                else -> R.string.first_launch_start_destination_fast_payments
            }
        )
    }

    private fun showAppLanguageDialog() {
        val values = AppLanguage.supportedTags.toTypedArray()
        showChoiceDialog(
            title = getString(R.string.settings_app_language_title),
            labels = values.map(::appLanguageTitle).toTypedArray(),
            selectedIndex = values.indexOf(selectedAppLanguageTag).coerceAtLeast(0)
        ) { index ->
            val languageTag = values[index]
            if (languageTag == selectedAppLanguageTag) return@showChoiceDialog
            selectedAppLanguageTag = languageTag
            settingsViewModel.saveAppLanguage(languageTag)
            AppLanguage.applyLanguageTag(languageTag)
        }
    }

    private fun showJournalCurrencyDisplayModeDialog() {
        val values = arrayOf(
            Constants.JOURNAL_CURRENCY_DISPLAY_NAME,
            Constants.JOURNAL_CURRENCY_DISPLAY_SHORT_NAME,
            Constants.JOURNAL_CURRENCY_DISPLAY_ISO
        )
        showChoiceDialog(
            title = getString(R.string.settings_journal_currency_display_title),
            labels = values.map(::journalCurrencyDisplayModeTitle).toTypedArray(),
            selectedIndex = values.indexOf(selectedJournalCurrencyDisplayMode).coerceAtLeast(0)
        ) { index ->
            selectedJournalCurrencyDisplayMode = values[index]
            binding.journalCurrencyDisplayModeValue.text =
                journalCurrencyDisplayModeTitle(selectedJournalCurrencyDisplayMode)
            settingsViewModel.saveJournalCurrencyDisplayMode(selectedJournalCurrencyDisplayMode)
        }
    }

    private fun appLanguageTitle(languageTag: String): String {
        return getString(
            when (languageTag) {
                Constants.APP_LANGUAGE_ENGLISH -> R.string.settings_app_language_english
                Constants.APP_LANGUAGE_POLISH -> R.string.settings_app_language_polish
                Constants.APP_LANGUAGE_RUSSIAN -> R.string.settings_app_language_russian
                else -> R.string.settings_app_language_system
            }
        )
    }

    private fun journalCurrencyDisplayModeTitle(displayMode: String): String {
        return getString(
            when (displayMode) {
                Constants.JOURNAL_CURRENCY_DISPLAY_SHORT_NAME ->
                    R.string.settings_journal_currency_display_short_name
                Constants.JOURNAL_CURRENCY_DISPLAY_ISO ->
                    R.string.settings_journal_currency_display_iso
                else -> R.string.settings_journal_currency_display_name
            }
        )
    }

    private fun currencyTitle(currency: Currencies): String {
        return currency.iso4217
            ?.takeIf { it.isNotBlank() }
            ?: currency.currencyNameShort
            ?: currency.currencyName
    }

    private fun cashAccountTitle(cashAccount: CashAccount): String {
        val languageTag = AppLanguage.getSelectedTag(requireContext())
        val accountName = cashAccount.displayName(languageTag)
        return cashAccount.bankAccountNumber.takeIf { it.isNotBlank() }?.let {
            "$accountName *${it.takeLast(4)}"
        } ?: accountName
    }

    private fun showBooleanChoiceDialog(
        title: String,
        currentValue: Boolean,
        trueLabel: String,
        falseLabel: String,
        onSelected: (Boolean) -> Unit
    ) {
        showChoiceDialog(
            title = title,
            labels = arrayOf(falseLabel, trueLabel),
            selectedIndex = if (currentValue) 1 else 0
        ) { index ->
            onSelected(index == 1)
        }
    }

    private fun showChoiceDialog(
        title: String,
        labels: Array<String>,
        selectedIndex: Int,
        onSelected: (Int) -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setSingleChoiceItems(labels, selectedIndex) { dialog, which ->
                onSelected(which)
                dialog.dismiss()
            }
            .show()
    }

    private fun showNumberChoiceDialog(
        title: String,
        min: Int,
        max: Int,
        currentValue: Int,
        onSelected: (Int) -> Unit
    ) {
        val picker = NumberPicker(requireContext()).apply {
            minValue = min
            maxValue = max
            wrapSelectorWheel = false
            value = currentValue.coerceIn(min, max)
        }
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(picker)
            .setNegativeButton(R.string.text_on_button_cancel, null)
            .setPositiveButton(R.string.text_on_button_submit) { _, _ ->
                onSelected(picker.value)
            }
            .show()
    }

    private fun showBackupPasswordDialog() {
        val padding = (20 * resources.displayMetrics.density).toInt()
        val password = passwordField(R.string.backup_password_hint)
        val repeatedPassword = passwordField(R.string.backup_password_repeat_hint)
        val fields = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, 0, padding, 0)
            addView(password)
            addView(repeatedPassword)
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.backup_password_title)
            .setView(fields)
            .setNegativeButton(R.string.text_on_button_cancel, null)
            .setPositiveButton(R.string.backup_create_button, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val first = password.text.toString()
                val second = repeatedPassword.text.toString()
                when {
                    first.length < 8 -> password.error = getString(R.string.backup_password_too_short)
                    first != second -> repeatedPassword.error = getString(R.string.backup_password_mismatch)
                    else -> {
                        pendingBackupPassword?.fill('\u0000')
                        pendingBackupPassword = first.toCharArray()
                        password.text.clear()
                        repeatedPassword.text.clear()
                        dialog.dismiss()
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                        val version = requireContext().packageManager
                            .getPackageInfo(requireContext().packageName, 0)
                            .versionName ?: "unknown"
                        createBackupDocument.launch(
                            getString(R.string.backup_file_name, version, date)
                        )
                    }
                }
            }
        }
        dialog.show()
    }

    private fun passwordField(hintRes: Int) = EditText(requireContext()).apply {
        hint = getString(hintRes)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
    }

    private fun showRestorePasswordDialog() {
        val password = passwordField(R.string.restore_password_hint)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.restore_password_title)
            .setView(password)
            .setNegativeButton(R.string.text_on_button_cancel) { _, _ ->
                pendingRestoreUri = null
                password.text.clear()
            }
            .setPositiveButton(R.string.text_on_button_submit) { _, _ ->
                val uri = pendingRestoreUri
                pendingRestoreUri = null
                val secret = password.text.toString().toCharArray()
                password.text.clear()
                if (uri != null) stageRestore(uri, secret)
            }
            .show()
    }

    private fun stageRestore(uri: Uri, password: CharArray) {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.restoreBackupButton.isEnabled = false
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    DatabaseRestoreManager.stageRestore(
                        requireContext().applicationContext,
                        uri,
                        password
                    )
                }
            }
            binding.restoreBackupButton.isEnabled = true
            if (result.isFailure) {
                message(getString(R.string.restore_failed))
                return@launch
            }
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.restore_ready_title)
                .setMessage(R.string.restore_ready_message)
                .setCancelable(false)
                .setPositiveButton(R.string.text_on_button_submit) { _, _ ->
                    android.os.Process.killProcess(android.os.Process.myPid())
                    exitProcess(0)
                }
                .show()
        }
    }

    private fun message(text: String) {
        Toast.makeText(requireContext(),text,Toast.LENGTH_LONG).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (arguments?.getString(ARG_SECTION)) {
            SECTION_QUICK_PAYMENT -> binding.quickPaymentSection
            else -> null
        }?.let { section ->
            binding.settingsScroll.post {
                binding.settingsScroll.smoothScrollTo(0, section.top)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::getVersionCode.isInitialized) {
            getVersionCode.completeDownloadedUpdateIfNeeded()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        pendingBackupPassword?.fill('\u0000')
        pendingBackupPassword = null
        pendingRestoreUri = null
        _binding = null
    }
}
