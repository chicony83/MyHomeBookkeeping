package com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewFastPaymentBinding
import com.chico.myhomebookkeeping.interfaces.fastPayments.OnSelectRatingValueCallBack
import com.chico.myhomebookkeeping.ui.fastPaymentsPackage.newFastPayment.dialogs.SelectRatingDialog
import com.chico.myhomebookkeeping.utils.launchUi


class NewFastPaymentFragment:Fragment() {
    private var _binding: FragmentNewFastPaymentBinding? = null
    private val binding get() = _binding!!
    private lateinit var newFastPaymentViewModel: NewFastPaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewFastPaymentBinding.inflate(inflater,container,false)

        newFastPaymentViewModel = ViewModelProvider(this).get(NewFastPaymentViewModel::class.java)

        with(newFastPaymentViewModel){
            descriptionFastPayment.observe(viewLifecycleOwner,{
                binding.descriptionFastPaymentEditText.setText(it.toString())
            })
            rating.observe(viewLifecycleOwner,{
                binding.ratingButton.setImageResource(it)
            })
            cashAccount.observe(viewLifecycleOwner,{
                binding.selectCashAccountButton.text = it.accountName
            })
            currency.observe(viewLifecycleOwner,{
                binding.selectCurrenciesButton.text = it.currencyName
            })
            category.observe(viewLifecycleOwner,{
                binding.selectCategoryButton.text = it.categoryName
            })
            amount.observe(viewLifecycleOwner,{
                binding.amount.setText(it.toString())
            })
            description.observe(viewLifecycleOwner,{
                binding.description.setText(it)
            })
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            ratingButton.setOnClickListener { showSelectRatingDialog() }

        }
    }

    private fun showSelectRatingDialog() {
        launchUi {
            val dialog = SelectRatingDialog(object:OnSelectRatingValueCallBack{
                override fun select(value: Int) {
                    setRatingValue(value)
                    Toast.makeText(requireContext(),"rating $value",Toast.LENGTH_SHORT).show()
                }
            })

            dialog.show(childFragmentManager,getString(R.string.tag_show_dialog))
        }
    }

    private fun setRatingValue(value: Int) {
        newFastPaymentViewModel.setRating(value)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}