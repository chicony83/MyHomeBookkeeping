package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewFastPaymentBinding
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnSelectRatingValueCallBack
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment.dialogs.SelectRatingDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.runBlocking


class NewFastPaymentFragment : Fragment() {
    private var _binding: FragmentNewFastPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var newFastPaymentViewModel: NewFastPaymentViewModel

    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewFastPaymentBinding.inflate(inflater, container, false)

        newFastPaymentViewModel = ViewModelProvider(this).get(NewFastPaymentViewModel::class.java)

        with(newFastPaymentViewModel) {
            descriptionFastPayment.observe(viewLifecycleOwner, {
                binding.descriptionFastPaymentEditText.setText(it.toString())
            })
            rating.observe(viewLifecycleOwner, {
                binding.ratingButton.setImageResource(it.img)
            })
            cashAccount.observe(viewLifecycleOwner, {
                binding.selectCashAccountButton.text = it.accountName
            })
            currency.observe(viewLifecycleOwner, {
                binding.selectCurrenciesButton.text = it.currencyName
            })
            category.observe(viewLifecycleOwner, {
                binding.selectCategoryButton.text = it.categoryName
            })
            amount.observe(viewLifecycleOwner, {
                binding.amount.setText(it.toString())
            })
            description.observe(viewLifecycleOwner, {
                binding.description.setText(it)
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(controller = control)

        with(binding) {
            ratingButton.setOnClickListener { showSelectRatingDialog() }
            selectCashAccountButton.setOnClickListener { pressSelectButton(R.id.nav_cash_account) }
            selectCurrenciesButton.setOnClickListener { pressSelectButton(R.id.nav_currencies) }
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

    }

    private fun addNewFastPayment() {
        val descriptionFastPayment = getDescriptionOfPayment()
        val description = getDescription()
        val amount = getAmount()
//        newFastPaymentViewModel.saveDataToSP()
        runBlocking {
            val result = newFastPaymentViewModel.addNewFastPayment(
                descriptionFastPayment, description, amount
            )
            if (result > 0) {
                view?.hideKeyboard()
                message(getString(R.string.message_entry_added))
                newFastPaymentViewModel.clearSPAfterSave()
            }
            navControlHelper.toSelectedFragment(R.id.nav_fast_money_movement_fragment)
        }
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    private fun pressSelectButton(fragment: Int) {
        newFastPaymentViewModel.saveDataToSP(
            descriptionFastPayment = getDescriptionOfPayment(),
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

    private fun getDescriptionOfPayment(): String {
        return binding.descriptionFastPaymentEditText.text.toString().let {
            if (it.isNotEmpty()) it
            else ""
        }
    }

    private fun getAmount(): Double {
        return binding.amount.text.toString().let {
            if (!it.isNullOrEmpty()) Around.double(it)
            else 0.0
        }
    }

    private fun showSelectRatingDialog() {
        launchUi {
            val dialog = SelectRatingDialog(object : OnSelectRatingValueCallBack {
                override fun select(value: Int) {
                    setRatingValue(value)
                    Toast.makeText(requireContext(), "rating $value", Toast.LENGTH_SHORT).show()
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