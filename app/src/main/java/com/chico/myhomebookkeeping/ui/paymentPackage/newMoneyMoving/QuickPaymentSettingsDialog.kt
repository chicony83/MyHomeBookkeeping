package com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.DialogQuickPaymentSettingsBinding
import com.chico.myhomebookkeeping.databinding.DialogSelectCurrencyAsDefaultBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCashAccountAsDefaultDialogBinding
import com.chico.myhomebookkeeping.databinding.RecyclerViewItemSelectCurrencyAsDefaultDialogBinding
import com.chico.myhomebookkeeping.db.entity.CashAccount
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.obj.Constants
import java.lang.IllegalStateException

class QuickPaymentSettingsDialog(
    private val settings: QuickPaymentSettings,
    private val currencies: List<Currencies>,
    private val cashAccounts: List<CashAccount>,
    private val defaultCurrency: Currencies?,
    private val defaultCashAccount: CashAccount?,
    private val onSettingsSubmitted: (QuickPaymentSettings) -> Unit,
    private val onDefaultCurrencySubmitted: (Currencies) -> Unit,
    private val onDefaultCashAccountSubmitted: (CashAccount) -> Unit
) : DialogFragment() {

    private var selectedDefaultCurrency = defaultCurrency
    private var selectedDefaultCashAccount = defaultCashAccount

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val binding = DialogQuickPaymentSettingsBinding.inflate(requireActivity().layoutInflater)
            bindSettings(binding)

            binding.defaultCurrencyButton.setOnClickListener {
                showSelectCurrencyDialog(binding)
            }
            binding.defaultCashAccountButton.setOnClickListener {
                showSelectCashAccountDialog(binding)
            }
            binding.submitButton.setOnClickListener {
                onSettingsSubmitted(createSettings(binding))
                dialog?.cancel()
            }
            binding.cancelButton.setOnClickListener {
                dialog?.cancel()
            }

            AlertDialog.Builder(it)
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException(getString(R.string.exceptions_activity_cant_be_null))
    }

    private fun bindSettings(binding: DialogQuickPaymentSettingsBinding) {
        binding.defaultCurrencyButton.text = selectedDefaultCurrency?.let(::currencyTitle)
            ?: getString(R.string.quick_payment_default_currency)
        binding.defaultCashAccountButton.text = selectedDefaultCashAccount?.let(::cashAccountTitle)
            ?: getString(R.string.quick_payment_default_cash_account)
        binding.currencyScrollSwitch.isChecked = settings.isCurrencyScrollEnabled
        binding.cashAccountScrollSwitch.isChecked = settings.isCashAccountScrollEnabled
        binding.showCalculatorSwitch.isChecked = settings.isCalculatorButtonVisible
        binding.amountInputModeRadioGroup.check(
            if (settings.amountInputMode == Constants.QUICK_PAYMENT_AMOUNT_INPUT_SCROLL) {
                R.id.amountScrollRadioButton
            } else {
                R.id.amountDigitsRadioButton
            }
        )
        setupDigitsPicker(binding.amountWholeDigitsPicker, 1, 9, settings.amountWholeDigits)
        setupDigitsPicker(binding.amountFractionDigitsPicker, 0, 4, settings.amountFractionDigits)
        updateDigitsSettingsVisibility(binding)
        binding.amountInputModeRadioGroup.setOnCheckedChangeListener { _, _ ->
            updateDigitsSettingsVisibility(binding)
        }
    }

    private fun createSettings(binding: DialogQuickPaymentSettingsBinding): QuickPaymentSettings {
        val amountInputMode = if (
            binding.amountInputModeRadioGroup.checkedRadioButtonId == R.id.amountScrollRadioButton
        ) {
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

    private fun setupDigitsPicker(
        picker: NumberPicker,
        minValue: Int,
        maxValue: Int,
        value: Int
    ) {
        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.wrapSelectorWheel = false
        picker.value = value.coerceIn(minValue, maxValue)
    }

    private fun updateDigitsSettingsVisibility(binding: DialogQuickPaymentSettingsBinding) {
        binding.amountScrollDigitsContainer.visibility =
            if (binding.amountInputModeRadioGroup.checkedRadioButtonId == R.id.amountScrollRadioButton) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun showSelectCurrencyDialog(settingsBinding: DialogQuickPaymentSettingsBinding) {
        val binding = DialogSelectCurrencyAsDefaultBinding.inflate(requireActivity().layoutInflater)
        var pendingCurrency = selectedDefaultCurrency
        val adapter = SelectDefaultCurrencyAdapter(
            currencies = currencies,
            selectedCurrencyId = pendingCurrency?.currencyId,
            onCurrencySelected = {
                pendingCurrency = it
            }
        )
        binding.iconsHolderLayout.adapter = adapter
        val childDialog = createChildDialog(binding.root)
        binding.submitButton.setOnClickListener {
            pendingCurrency?.let {
                selectedDefaultCurrency = it
                settingsBinding.defaultCurrencyButton.text = currencyTitle(it)
                onDefaultCurrencySubmitted(it)
            }
            childDialog.cancel()
        }
        binding.cancelButton.setOnClickListener {
            childDialog.cancel()
        }
        childDialog.show()
    }

    private fun showSelectCashAccountDialog(settingsBinding: DialogQuickPaymentSettingsBinding) {
        val binding = DialogSelectCurrencyAsDefaultBinding.inflate(requireActivity().layoutInflater)
        binding.dialogTitle.text = getString(R.string.quick_payment_default_cash_account)
        var pendingCashAccount = selectedDefaultCashAccount
        val adapter = SelectDefaultCashAccountAdapter(
            cashAccounts = cashAccounts,
            selectedCashAccountId = pendingCashAccount?.cashAccountId,
            onCashAccountSelected = {
                pendingCashAccount = it
            }
        )
        binding.iconsHolderLayout.adapter = adapter
        val childDialog = createChildDialog(binding.root)
        binding.submitButton.setOnClickListener {
            pendingCashAccount?.let {
                selectedDefaultCashAccount = it
                settingsBinding.defaultCashAccountButton.text = cashAccountTitle(it)
                onDefaultCashAccountSubmitted(it)
            }
            childDialog.cancel()
        }
        binding.cancelButton.setOnClickListener {
            childDialog.cancel()
        }
        childDialog.show()
    }

    private fun createChildDialog(view: android.view.View): AlertDialog {
        return AlertDialog.Builder(requireActivity())
            .setView(view)
            .create()
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

    private class SelectDefaultCurrencyAdapter(
        private val currencies: List<Currencies>,
        selectedCurrencyId: Int?,
        private val onCurrencySelected: (Currencies) -> Unit
    ) : RecyclerView.Adapter<SelectDefaultCurrencyAdapter.ViewHolder>() {

        private var selectedCurrencyId = selectedCurrencyId

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerViewItemSelectCurrencyAsDefaultDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(currencies[position])
        }

        override fun getItemCount(): Int = currencies.size

        inner class ViewHolder(
            private val binding: RecyclerViewItemSelectCurrencyAsDefaultDialogBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(currency: Currencies) {
                binding.currencySymbol.text = currency.currencyNameShort
                binding.nameCurrency.text = currency.currencyName
                binding.isoCurrency.text = currency.iso4217
                binding.defaultCurrencyRadioButton.isChecked = currency.currencyId == selectedCurrencyId
                binding.selectCurrencyAsDefaultItem.setOnClickListener {
                    val oldSelectedCurrencyId = selectedCurrencyId
                    selectedCurrencyId = currency.currencyId
                    onCurrencySelected(currency)
                    oldSelectedCurrencyId?.let(::notifyCurrencyChanged)
                    selectedCurrencyId?.let(::notifyCurrencyChanged)
                }
            }

            private fun notifyCurrencyChanged(currencyId: Int) {
                val index = currencies.indexOfFirst { it.currencyId == currencyId }
                if (index >= 0) notifyItemChanged(index)
            }
        }
    }

    private class SelectDefaultCashAccountAdapter(
        private val cashAccounts: List<CashAccount>,
        selectedCashAccountId: Int?,
        private val onCashAccountSelected: (CashAccount) -> Unit
    ) : RecyclerView.Adapter<SelectDefaultCashAccountAdapter.ViewHolder>() {

        private var selectedCashAccountId = selectedCashAccountId

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RecyclerViewItemSelectCashAccountAsDefaultDialogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(cashAccounts[position])
        }

        override fun getItemCount(): Int = cashAccounts.size

        inner class ViewHolder(
            private val binding: RecyclerViewItemSelectCashAccountAsDefaultDialogBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(cashAccount: CashAccount) {
                cashAccount.icon?.let { binding.iconImg.setImageResource(it) }
                binding.nameCashAccount.text = cashAccount.accountName
                binding.defaultCashAccountRadioButton.isChecked =
                    cashAccount.cashAccountId == selectedCashAccountId
                binding.selectCashAccountAsDefaultItem.setOnClickListener {
                    val oldSelectedCashAccountId = selectedCashAccountId
                    selectedCashAccountId = cashAccount.cashAccountId
                    onCashAccountSelected(cashAccount)
                    oldSelectedCashAccountId?.let(::notifyCashAccountChanged)
                    selectedCashAccountId?.let(::notifyCashAccountChanged)
                }
            }

            private fun notifyCashAccountChanged(cashAccountId: Int) {
                val index = cashAccounts.indexOfFirst { it.cashAccountId == cashAccountId }
                if (index >= 0) notifyItemChanged(index)
            }
        }
    }
}
