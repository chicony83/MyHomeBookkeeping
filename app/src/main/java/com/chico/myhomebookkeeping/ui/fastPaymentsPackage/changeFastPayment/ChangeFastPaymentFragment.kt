package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.changeFastPayment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentChangeFastPaymentBinding

class ChangeFastPaymentFragment : Fragment() {

    private lateinit var changeFastPaymentViewModel: ChangeFastPaymentViewModel
    private var _binding: FragmentChangeFastPaymentBinding? = null
    val binding get() = _binding!!

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

        with(changeFastPaymentViewModel) {
            paymentName.observe(viewLifecycleOwner, {
                binding.nameFastPayment.setText(it.toString())
            })
            paymentRating.observe(viewLifecycleOwner, {
                binding.ratingButton.setImageResource(getRatingImage(it))
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
            getFastPaymentForChange()
        }
    }

    private fun getRatingImage(rating: Int?): Int {
        return when (rating) {
            0 -> R.drawable.rating1
            1 -> R.drawable.rating2
            2 -> R.drawable.rating3
            3 -> R.drawable.rating4
            4 -> R.drawable.rating5
            else -> R.drawable.rating1
        }
    }
}