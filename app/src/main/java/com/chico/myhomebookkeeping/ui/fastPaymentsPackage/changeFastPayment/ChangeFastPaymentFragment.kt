package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentChangeFastPaymentBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnSelectRatingValueCallBack
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.dialogs.SelectRatingDialog
import com.chico.myhomebookkeeping.utils.launchUi

class ChangeFastPaymentFragment : Fragment() {

    private lateinit var changeFastPaymentViewModel: ChangeFastPaymentViewModel
    private var _binding: FragmentChangeFastPaymentBinding? = null
    val binding get() = _binding!!
    private lateinit var control:NavController
    private lateinit var navControlHelper: NavControlHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangeFastPaymentBinding.inflate(inflater, container, false)
        changeFastPaymentViewModel =
            ViewModelProvider(this).get(ChangeFastPaymentViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        with(binding){
            ratingButton.setOnClickListener {
                showSelectRatingDialog()
            }
            selectCashAccountButton.setOnClickListener { pressSelectButton(R.id.nav_cash_account) }
            selectCategoryButton.setOnClickListener { pressSelectButton(R.id.nav_categories) }
            selectCurrenciesButton.setOnClickListener { pressSelectButton(R.id.nav_currencies) }
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
            paymentCurrency.observe(viewLifecycleOwner, {
                binding.selectCurrenciesButton.text = it.currencyName
            })
            paymentCategory.observe(viewLifecycleOwner, {
                binding.selectCategoryButton.text = it.categoryName
            })
            paymentAmount.observe(viewLifecycleOwner, {
                binding.amount.setText(it)
            })
            paymentDescription.observe(viewLifecycleOwner, {
                binding.description.setText(it)
            })
        }

        with(changeFastPaymentViewModel) {
            getSPForChangeFastPayment()
            getFastPaymentForChange()
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
                ratingFromParent = changeFastPaymentViewModel.paymentRating.value?.rating?:0,
                object:OnSelectRatingValueCallBack{
                    override fun select(value: Int) {
                        setRatingValue(value)
                    }
                }
            )
            dialog.show(childFragmentManager,getString(R.string.tag_show_dialog))
        }
    }

    private fun setRatingValue(value: Int) {
        changeFastPaymentViewModel.postRatingValue(value)
    }
}