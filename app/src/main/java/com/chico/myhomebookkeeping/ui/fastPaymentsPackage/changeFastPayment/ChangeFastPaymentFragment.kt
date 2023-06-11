package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentChangeFastPaymentBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.helpers.Around
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.interfaces.OnItemSubmitForDeleteCallBack
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnSelectRatingValueCallBack
import com.chico.myhomebookkeeping.ui.dialogs.SubmitDeleteDialog
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.dialogs.SelectRatingDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.google.android.material.chip.Chip
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class ChangeFastPaymentFragment : Fragment() {

    private val changeFastPaymentViewModel: ChangeFastPaymentViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private var _binding: FragmentChangeFastPaymentBinding? = null
    val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeFastPaymentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        with(binding) {
            selectCurrenciesCg.setOnCheckedChangeListener { group, checkedId ->
                changeFastPaymentViewModel.postCurrency(
                    changeFastPaymentViewModel.currenciesList.value?.firstOrNull {
                        it.iso4217 == group.findViewById<Chip>(checkedId)?.text.toString()
                    }?.currencyId
                )
            }

            ratingButton.setOnClickListener {
                showSelectRatingDialog()
            }
            selectCashAccountButton.setOnClickListener { pressSelectButton(R.id.nav_cash_account) }
            selectCategoryButton.setOnClickListener { pressSelectButton(R.id.nav_categories) }
//            selectCurrenciesButton.setOnClickListener { pressSelectButton(R.id.nav_currencies) }
            submitButton.setOnClickListener { pressSubmitButton() }
            deleteButton.setOnClickListener { pressDeleteButton() }
        }

        with(changeFastPaymentViewModel) {
            paymentName.observe(viewLifecycleOwner, {
                binding.nameFastPayment.setText(it.toString())
            })
            paymentRating.observe(viewLifecycleOwner, {
                binding.ratingButton.setImageResource(it.img)
            })
            paymentCashAccount.observe(viewLifecycleOwner, {
                binding.selectCashAccountButton.text = it.accountName
            })
//            paymentCurrency.observe(viewLifecycleOwner, {
//                binding.selectCurrenciesButton.text = it.currencyName
//            })
            paymentCategory.observe(viewLifecycleOwner, {
                binding.selectCategoryButton.text = it.categoryName
            })
            paymentAmount.observe(viewLifecycleOwner, {
                binding.amount.setText(it)
            })
            paymentDescription.observe(viewLifecycleOwner, {
                binding.description.setText(it)
            })
            currenciesList.observe(viewLifecycleOwner) { currenciesList ->
                buildCurrencyChips(currenciesList, selectedCurrency.value)
            }
            loadedCurrencyId.observe(viewLifecycleOwner) { currency ->
                buildCurrencyChips(currenciesList.value ?: emptyList(), currency)
            }
        }

        with(changeFastPaymentViewModel) {
            getSPForChangeFastPayment()
            getFastPaymentForChange()
        }
    }

    private fun buildCurrencyChips(
        currenciesList: List<Currencies>,
        selectedCurrency: Currencies? = null
    ) {
        binding.selectCurrenciesCg.removeAllViews()
        val currencyModels = currenciesList.map { currency ->

            val chipView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_currency, binding.selectCurrenciesCg, false) as Chip

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

    private fun pressDeleteButton() {
        launchUi {
            val dialog = SubmitDeleteDialog(
                object : OnItemSubmitForDeleteCallBack {
                    override fun isDelete(isDelete: Boolean) {
                        if (isDelete) {
                            deleteEntry()
                        }
                    }
                }
            )
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun deleteEntry() {
        runBlocking {
            val result = async {
                changeFastPaymentViewModel.deleteLine()
            }
            if (result.await() > 0) {
                message(getString(R.string.message_entry_deleted))
                control.navigate(R.id.nav_fast_payments_fragment)
            }
        }
    }

    private fun pressSubmitButton() {
        if (binding.nameFastPayment.text.isNotEmpty()) {
            val name = binding.nameFastPayment.text.toString()
            val amount = getAmount()
            val description = getDescription()
            runBlocking {
                changeFastPaymentViewModel.saveDataToSP(
                    name = name,
                    amount = amount.toString(),
                    description = description
                )
                val result: Int =
                    changeFastPaymentViewModel.changeFastPayment(name, amount, description)
                if (result > 0) {
                    view?.hideKeyboard()
                    message(getString(R.string.message_entry_changed))
                    control.navigate(R.id.nav_fast_payments_fragment)
                }
            }
        } else {
            message(getString(R.string.message_name_not_entered))
        }
    }
//
//    private fun changeFastPayment() {
//
//    }

    private fun getDescription(): String {
        return binding.description.text.toString().let {
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

    private fun pressSelectButton(fragment: Int) {
        changeFastPaymentViewModel.saveDataToSP(
            binding.nameFastPayment.text.toString(),
            binding.amount.text.toString(),
            binding.description.text.toString()
        )
        navControlHelper.toSelectedFragment(fragment)

    }

    private fun showSelectRatingDialog() {
        launchUi {
            val dialog = SelectRatingDialog(
                ratingFromParent = changeFastPaymentViewModel.paymentRating.value?.rating ?: 0,
                object : OnSelectRatingValueCallBack {
                    override fun select(value: Int) {
                        setRatingValue(value)
                    }
                }
            )
            dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
        }
    }

    private fun setRatingValue(value: Int) {
        changeFastPaymentViewModel.postRatingValue(value)
    }

    private fun message(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}