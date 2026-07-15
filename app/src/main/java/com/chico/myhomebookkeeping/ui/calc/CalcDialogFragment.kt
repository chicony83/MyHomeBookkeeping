package com.chico.myhomebookkeeping.ui.calc

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.DialogCalcMainBinding
import com.chico.myhomebookkeeping.ui.paymentPackage.newMoneyMoving.NewMoneyMovingViewModel
import org.json.JSONArray
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class CalcDialogFragment : DialogFragment() {
    private val viewModel: NewMoneyMovingViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private val calculator = PaymentCalculatorEngine()
    private val decimalSeparatorSymbol =
        DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    private lateinit var binding: DialogCalcMainBinding
    private var history = listOf<HistoryEntry>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogCalcMainBinding.inflate(layoutInflater)

        val initialAmount = requireArguments().getString(INIT_AMOUNT, "")
        binding.calculatorExpression.text = initialAmount

        bindButtons()
        loadHistory()
        updateHistory()
        updatePreview()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton(R.string.text_on_button_submit, null)
            .setNegativeButton(R.string.text_on_button_cancel, null)
            .create()
    }

    override fun onStart() {
        super.onStart()
        (dialog as? AlertDialog)
            ?.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setOnClickListener { submitAmount() }
    }

    private fun bindButtons() {
        val digitButtons = mapOf(
            binding.button0 to "0",
            binding.button1 to "1",
            binding.button2 to "2",
            binding.button3 to "3",
            binding.button4 to "4",
            binding.button5 to "5",
            binding.button6 to "6",
            binding.button7 to "7",
            binding.button8 to "8",
            binding.button9 to "9"
        )
        digitButtons.forEach { (button, digit) ->
            button.setOnClickListener { append(digit, it) }
        }

        binding.buttonPoint.text = decimalSeparatorSymbol
        binding.buttonPoint.setOnClickListener { appendDecimalSeparator(it) }
        binding.buttonAdd.setOnClickListener { appendOperator("+", it) }
        binding.buttonSubtract.setOnClickListener { appendOperator("-", it) }
        binding.buttonMultiply.setOnClickListener { appendOperator("*", it) }
        binding.buttonDivide.setOnClickListener { appendOperator("/", it) }
        binding.buttonPercent.setOnClickListener { appendPercent(it) }
        binding.buttonParentheses.setOnClickListener { appendParenthesis(it) }
        binding.buttonClear.setOnClickListener { clear(it) }
        binding.buttonBackspace.setOnClickListener { backspace(it) }
        binding.buttonEquals.setOnClickListener { applyResult(it) }

        binding.historyFirst.setOnClickListener { restoreHistory(0) }
        binding.historySecond.setOnClickListener { restoreHistory(1) }
    }

    private fun append(value: String, view: View) {
        vibrate(view)
        binding.calculatorExpression.append(value)
        updatePreview()
    }

    private fun appendDecimalSeparator(view: View) {
        val currentNumber = binding.calculatorExpression.text
            .split('+', '-', '*', '/', '(', ')')
            .lastOrNull()
            .orEmpty()
        if (decimalSeparatorSymbol !in currentNumber && "." !in currentNumber) {
            append(decimalSeparatorSymbol, view)
        } else {
            vibrate(view)
        }
    }

    private fun appendOperator(operator: String, view: View) {
        vibrate(view)
        val expression = binding.calculatorExpression.text.toString()
        val nextExpression = when {
            expression.isEmpty() && operator == "-" -> operator
            expression.isEmpty() -> expression
            expression.last().isOperator() -> expression.dropLast(1) + operator
            else -> expression + operator
        }
        binding.calculatorExpression.text = nextExpression
        updatePreview()
    }

    private fun appendPercent(view: View) {
        val expression = binding.calculatorExpression.text.toString()
        if (expression.lastOrNull()?.isDigit() == true || expression.lastOrNull() == ')') {
            append("%", view)
        } else {
            vibrate(view)
        }
    }

    private fun appendParenthesis(view: View) {
        vibrate(view)
        val expression = binding.calculatorExpression.text.toString()
        val openCount = expression.count { it == '(' }
        val closeCount = expression.count { it == ')' }
        val next = if (
            expression.isEmpty() ||
            expression.last().isOperator() ||
            expression.last() == '(' ||
            openCount == closeCount
        ) {
            "("
        } else {
            ")"
        }
        binding.calculatorExpression.text = expression + next
        updatePreview()
    }

    private fun clear(view: View) {
        vibrate(view)
        binding.calculatorExpression.text = ""
        binding.calculatorResult.text = ""
        setErrorState(false)
    }

    private fun backspace(view: View) {
        vibrate(view)
        val expression = binding.calculatorExpression.text.toString()
        if (expression.isNotEmpty()) {
            binding.calculatorExpression.text = expression.dropLast(1)
            updatePreview()
        }
    }

    private fun applyResult(view: View) {
        vibrate(view)
        val expression = binding.calculatorExpression.text.toString()
        val result = evaluateDisplayExpression(expression).getOrElse {
            setErrorState(true)
            binding.calculatorResult.setText(R.string.syntax_error)
            return
        }
        val formattedResult = formatResult(result)
        saveHistory(expression, formattedResult)
        binding.calculatorExpression.text = formattedResult
        binding.calculatorResult.text = ""
        setErrorState(false)
    }

    private fun submitAmount() {
        val expression = binding.calculatorExpression.text.toString()
        if (expression.isBlank()) {
            viewModel.setCalcSelectedAmount("", decimalSeparatorSymbol)
            dismiss()
            return
        }

        val amount = evaluateDisplayExpression(expression).getOrElse {
            Toast.makeText(requireContext(), R.string.syntax_error, Toast.LENGTH_SHORT).show()
            setErrorState(true)
            return
        }
        val formattedAmount = formatResult(amount)
        saveHistory(expression, formattedAmount)
        viewModel.setCalcSelectedAmount(formattedAmount, decimalSeparatorSymbol)
        dismiss()
    }

    private fun updatePreview() {
        val expression = binding.calculatorExpression.text.toString()
        if (expression.isBlank()) {
            binding.calculatorResult.text = ""
            setErrorState(false)
            return
        }

        evaluateDisplayExpression(expression)
            .onSuccess { value ->
                val formattedValue = formatResult(value)
                binding.calculatorResult.text =
                    if (formattedValue != expression) formattedValue else ""
                setErrorState(false)
            }
            .onFailure {
                binding.calculatorResult.text = ""
                setErrorState(false)
            }
    }

    private fun evaluateDisplayExpression(expression: String): Result<Double> {
        val normalized = expression
            .replace(decimalSeparatorSymbol, ".")
            .replace(",", ".")
            .replace("x", "*", ignoreCase = true)
            .replace("×", "*")
            .replace("÷", "/")
        return calculator.evaluate(normalized)
    }

    private fun formatResult(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale.getDefault())
        val formatter = DecimalFormat("0.##########", symbols)
        return formatter.format(value)
    }

    private fun saveHistory(expression: String, result: String) {
        if (expression.isBlank() || expression == result) return

        // Keep only two rows so the dialog stays fast to scan while entering a payment.
        history = (listOf(HistoryEntry(expression, result)) + history)
            .distinctBy { "${it.expression}=${it.result}" }
            .take(HISTORY_SIZE)
        requireContext()
            .getSharedPreferences(PREFERENCES_NAME, 0)
            .edit()
            .putString(HISTORY_KEY, history.toJson().toString())
            .apply()
        updateHistory()
    }

    private fun loadHistory() {
        val json = requireContext()
            .getSharedPreferences(PREFERENCES_NAME, 0)
            .getString(HISTORY_KEY, null)
            ?: return
        history = runCatching {
            val array = JSONArray(json)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                HistoryEntry(
                    expression = item.getString("expression"),
                    result = item.getString("result")
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun updateHistory() {
        val historyViews = listOf(binding.historyFirst, binding.historySecond)
        historyViews.forEachIndexed { index, textView ->
            val item = history.getOrNull(index)
            textView.visibility = if (item == null) View.GONE else View.VISIBLE
            textView.text = item?.let { "${it.expression} = ${it.result}" }.orEmpty()
        }
    }

    private fun restoreHistory(index: Int) {
        val item = history.getOrNull(index) ?: return
        binding.calculatorExpression.text = item.result
        binding.calculatorResult.text = ""
        setErrorState(false)
    }

    private fun setErrorState(hasError: Boolean) {
        val color = ContextCompat.getColor(
            requireContext(),
            if (hasError) R.color.calculation_error_color else R.color.text_color
        )
        binding.calculatorExpression.setTextColor(color)
    }

    private fun vibrate(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    private fun Char.isOperator(): Boolean = this in listOf('+', '-', '*', '/')

    private fun List<HistoryEntry>.toJson(): JSONArray {
        val array = JSONArray()
        forEach { entry ->
            array.put(
                org.json.JSONObject()
                    .put("expression", entry.expression)
                    .put("result", entry.result)
            )
        }
        return array
    }

    private data class HistoryEntry(
        val expression: String,
        val result: String
    )

    companion object {
        private const val INIT_AMOUNT = "init amount"
        private const val HISTORY_SIZE = 2
        private const val PREFERENCES_NAME = "payment_calculator"
        private const val HISTORY_KEY = "history"

        fun newInstance(initAmount: String): CalcDialogFragment {
            return CalcDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(INIT_AMOUNT, initAmount)
                }
            }
        }
    }
}
