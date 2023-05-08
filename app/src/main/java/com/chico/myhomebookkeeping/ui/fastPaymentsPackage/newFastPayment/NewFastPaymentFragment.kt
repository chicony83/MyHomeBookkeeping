package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewFastPaymentBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnSelectRatingValueCallBack
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.dialogs.SelectRatingDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.google.android.material.chip.Chip
import kotlinx.coroutines.runBlocking


class NewFastPaymentFragment : Fragment() {
    private var _binding: FragmentNewFastPaymentBinding? = null
    private val binding get() = _binding!!
    private val newFastPaymentViewModel: NewFastPaymentViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewFastPaymentBinding.inflate(inflater, container, false)

        with(newFastPaymentViewModel) {
            descriptionFastPayment.observe(viewLifecycleOwner) {
                binding.nameFastPaymentEditText.setText(it.toString())
            }
            rating.observe(viewLifecycleOwner) {
                binding.ratingButton.setImageResource(it.img)
            }
            cashAccount.observe(viewLifecycleOwner) {
                binding.selectCashAccountButton.text = it.accountName
            }
            currency.observe(viewLifecycleOwner) {
//                binding.selectCurrenciesButton.text = it.currencyName
            }
            category.observe(viewLifecycleOwner) {
                binding.selectCategoryButton.text = it.categoryName
            }
            amount.observe(viewLifecycleOwner) {
                binding.amount.setText(it.toString())
            }
            description.observe(viewLifecycleOwner) {
                binding.description.setText(it)
            }
            currenciesList.observe(viewLifecycleOwner) { currenciesList ->
                buildCurrencyChips(currenciesList,selectedCurrency.value)
            }
        }

        return binding.root
    }

    private fun buildCurrencyChips(
        currenciesList: List<Currencies>,
        selectedCurrency: Currencies? = null
    ) {
        binding.selectCurrenciesCg.removeAllViews()
        val currencyModels = currenciesList.map { currency ->
            val chipView = LayoutInflater.from(requireContext()).inflate(R.layout.item_currency,binding.selectCurrenciesCg,false) as Chip

            chipView.apply {
                text = currency.iso4217
                isCheckable = true
                isChecked = if (selectedCurrency == null) currency.isCurrencyDefault
                    ?: false else selectedCurrency.currencyId == currency.currencyId
            }
        }
        currencyModels.forEach {
            binding.selectCurrenciesCg.addView(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(controller = control)

        with(binding) {
            selectCurrenciesCg.setOnCheckedChangeListener { group, checkedId ->
                newFastPaymentViewModel.postCurrency(
                    newFastPaymentViewModel.currenciesList.value?.firstOrNull {
                        it.iso4217 == group.findViewById<Chip>(checkedId)?.text.toString()
                    }?.currencyId
                )
            }

            ratingButton.setOnClickListener { showSelectRatingDialog() }
            selectCashAccountButton.setOnClickListener { pressSelectButton(R.id.nav_cash_account) }
//            selectCurrenciesButton.setOnClickListener { pressSelectButton(R.id.nav_currencies) }
            selectCategoryButton.setOnClickListener { pressSelectButton(R.id.nav_categories) }
            submitButton.setOnClickListener {
                presSubmitButton()
            }
        }
    }

    private fun presSubmitButton() {
        val isCashAccountNotNull = newFastPaymentViewModel.isCashAccountNotNull()
        val isCurrencyNotNull = newFastPaymentViewModel.isCurrencyNotNull()
        val isCategoryNotNull = newFastPaymentViewModel.isCategoryNotNull()
        if (binding.nameFastPaymentEditText.text.isNotEmpty()) {
            if (isCashAccountNotNull) {
                if (isCurrencyNotNull) {
                    if (isCategoryNotNull) {
                        addNewFastPayment()
                    } else {
                        message(getString(R.string.message_category_not_selected))
                    }
                } else {
                    message(getString(R.string.message_currency_not_selected))
                }
            } else {
                message(getString(R.string.message_cash_account_not_selected))
            }
        } else {
            message(getString(R.string.message_name_not_entered))
        }
    }

    private fun addNewFastPayment() {
        val nameFastPayment = getNameOfPayment()
        val description = getDescription()
        val amount = getAmount()
        runBlocking {
            val result = newFastPaymentViewModel.addNewFastPayment(
                nameFastPayment, description, amount
            )
            if (result > 0) {
                view?.hideKeyboard()
                message(getString(R.string.message_entry_added))
                newFastPaymentViewModel.clearSPAfterSave()
            }
            navControlHelper.toSelectedFragment(R.id.nav_fast_payments_fragment)
        }
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun pressSelectButton(fragment: Int) {
        newFastPaymentViewModel.saveDataToSP(
            descriptionFastPayment = getNameOfPayment(),
            description = getDescription(),
            amount = getAmount()
        )
        navControlHelper.toSelectedFragment(fragment)
    }

    private fun getDescription(): String {
        return binding.description.text.toString().let {
            if (it.isNotEmpty()) it
            else ""
        }
    }

    private fun getNameOfPayment(): String {
        return binding.nameFastPaymentEditText.text.toString().let {
            if (it.isNotEmpty()) it
            else ""
        }
    }

    private fun getAmount(): Double {
        return binding.amount.text.toString().let {
            if (!it.isNullOrEmpty()) {
                if (it.toDouble() > 0) {
                    Around.double(it)
                } else 0.0
            } else 0.0
        }
    }

    private fun showSelectRatingDialog() {
        launchUi {
            val dialog = SelectRatingDialog(
                newFastPaymentViewModel.rating.value?.rating,
                object : OnSelectRatingValueCallBack {
                    override fun select(value: Int) {
                        setRatingValue(value)
//                    Toast.makeText(requireContext(), "rating $value", Toast.LENGTH_SHORT).show()
                    }
                })
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun setRatingValue(value: Int) {
        newFastPaymentViewModel.postRating(value)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}