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
import com.chico.myhomebookkeeping.enums.SortingCategories
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.categories.CategoriesFragment
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
        const val SECTION_CATEGORY_SORTING = "categorySorting"
    }

    private lateinit var settingsViewModel: SettingsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navControlHelper: NavControlHelper
    private lateinit var control: NavController
    private val uiHelper = UiHelper()
    private lateinit var getVersionCode: GetVersionCode
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
            defaultCurrencyButton.setOnClickListener {
                showSelectDefaultCurrencyDialog()
            }
            defaultCashAccountButton.setOnClickListener {
                showSelectDefaultCashAccountDialog()
            }
            amountInputModeRadioGroup.setOnCheckedChangeListener { _, _ ->
                updateDigitsSettingsVisibility()
            }
            editCategoryListsButton.setOnClickListener {
                openCategoryListsEditor()
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
            submitButton.setOnClickListener {
                message(getString(R.string.message_settings_saved))
                saveSettings()
                navControlHelper.moveToPreviousFragment()
            }
        }
        with(settingsViewModel){
            appVersion.observe(viewLifecycleOwner, {
                binding.appVersion.text = it.toString()
            })
            quickPaymentSettings.observe(viewLifecycleOwner) {
                bindQuickPaymentSettings(it)
            }
            categorySorting.observe(viewLifecycleOwner) {
                binding.categorySortingRadioGroup.check(categorySortingRadioButtonId(it))
            }
            startFragment.observe(viewLifecycleOwner) {
                binding.startFragmentRadioGroup.check(startFragmentRadioButtonId(it))
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
        with(binding) {
            currencyScrollSwitch.isChecked = settings.isCurrencyScrollEnabled
            cashAccountScrollSwitch.isChecked = settings.isCashAccountScrollEnabled
            showCalculatorSwitch.isChecked = settings.isCalculatorButtonVisible
            amountInputModeRadioGroup.check(
                if (settings.amountInputMode == Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL) {
                    R.id.amountScrollRadioButton
                } else {
                    R.id.amountDigitsRadioButton
                }
            )
            setupDigitsPicker(amountWholeDigitsPicker, 1, 9, settings.amountWholeDigits)
            setupDigitsPicker(amountFractionDigitsPicker, 0, 4, settings.amountFractionDigits)
            updateDigitsSettingsVisibility()
        }
    }

    private fun setupDigitsPicker(
        picker: android.widget.NumberPicker,
        minValue: Int,
        maxValue: Int,
        value: Int
    ) {
        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.wrapSelectorWheel = false
        picker.value = value.coerceIn(minValue, maxValue)
    }

    private fun updateDigitsSettingsVisibility() {
        binding.amountScrollDigitsContainer.visibility =
            if (binding.amountInputModeRadioGroup.checkedRadioButtonId == R.id.amountScrollRadioButton) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun loadDefaultSelectionTitles() {
        viewLifecycleOwner.lifecycleScope.launch {
            val defaultCurrency = withContext(Dispatchers.IO) {
                settingsViewModel.getDefaultCurrency()
            }
            val defaultCashAccount = withContext(Dispatchers.IO) {
                settingsViewModel.getDefaultCashAccount()
            }
            binding.defaultCurrencyButton.text = defaultCurrency?.let(::currencyTitle)
                ?: getString(R.string.quick_payment_default_currency)
            binding.defaultCashAccountButton.text = defaultCashAccount?.let(::cashAccountTitle)
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
                        binding.defaultCurrencyButton.text = currencyTitle(currency)
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
                        binding.defaultCashAccountButton.text = cashAccountTitle(cashAccount)
                    }
                }
                .show()
        }
    }

    private fun saveSettings() {
        settingsViewModel.saveQuickPaymentSettings(createQuickPaymentSettings())
        settingsViewModel.saveCategorySorting(selectedCategorySorting())
        settingsViewModel.saveStartFragment(selectedStartFragment())
    }

    private fun openCategoryListsEditor() {
        saveSettings()
        control.navigate(
            R.id.nav_categories,
            Bundle().apply {
                putBoolean(CategoriesFragment.ARG_ENABLE_ORDER_EDIT_MODE, true)
            }
        )
    }

    private fun createQuickPaymentSettings(): QuickPaymentSettings {
        val amountInputMode =
            if (binding.amountInputModeRadioGroup.checkedRadioButtonId == R.id.amountScrollRadioButton) {
                Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL
            } else {
                Constants.QUICK_PAYMENT_AMOUNT_INPUT_DIGITS
            }
        return QuickPaymentSettings(
            isCurrencyScrollEnabled = binding.currencyScrollSwitch.isChecked,
            isCashAccountScrollEnabled = binding.cashAccountScrollSwitch.isChecked,
            isCalculatorButtonVisible = binding.showCalculatorSwitch.isChecked,
            amountInputMode = amountInputMode,
            amountWholeDigits = binding.amountWholeDigitsPicker.value,
            amountFractionDigits = binding.amountFractionDigitsPicker.value
        )
    }

    private fun selectedCategorySorting(): String {
        return when (binding.categorySortingRadioGroup.checkedRadioButtonId) {
            R.id.categorySortNumbersAscRadioButton -> SortingCategories.NumbersByASC.toString()
            R.id.categorySortNumbersDescRadioButton -> SortingCategories.NumbersByDESC.toString()
            R.id.categorySortAlphabetDescRadioButton -> SortingCategories.AlphabetByDESC.toString()
            else -> SortingCategories.AlphabetByASC.toString()
        }
    }

    private fun categorySortingRadioButtonId(sorting: String): Int {
        return when (sorting) {
            SortingCategories.NumbersByASC.toString() -> R.id.categorySortNumbersAscRadioButton
            SortingCategories.NumbersByDESC.toString() -> R.id.categorySortNumbersDescRadioButton
            SortingCategories.AlphabetByDESC.toString() -> R.id.categorySortAlphabetDescRadioButton
            else -> R.id.categorySortAlphabetAscRadioButton
        }
    }

    private fun selectedStartFragment(): String {
        return when (binding.startFragmentRadioGroup.checkedRadioButtonId) {
            R.id.startCategoriesRadioButton -> Constants.START_FRAGMENT_CATEGORIES
            R.id.startJournalRadioButton -> Constants.START_FRAGMENT_JOURNAL
            else -> Constants.START_FRAGMENT_FAST_PAYMENTS
        }
    }

    private fun startFragmentRadioButtonId(startFragment: String): Int {
        return when (startFragment) {
            Constants.START_FRAGMENT_CATEGORIES -> R.id.startCategoriesRadioButton
            Constants.START_FRAGMENT_JOURNAL -> R.id.startJournalRadioButton
            else -> R.id.startFastPaymentsRadioButton
        }
    }

    private fun currencyTitle(currency: Currencies): String {
        return currency.iso4217
            ?.takeIf { it.isNotBlank() }
            ?: currency.currencyNameShort
            ?: currency.currencyName
    }

    private fun cashAccountTitle(cashAccount: CashAccount): String {
        return cashAccount.bankAccountNumber.takeIf { it.isNotBlank() }?.let {
            "${cashAccount.accountName} *${it.takeLast(4)}"
        } ?: cashAccount.accountName
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
            SECTION_CATEGORY_SORTING -> binding.categorySortingSection
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
