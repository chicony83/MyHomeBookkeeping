package com.chico.myhomebookkeeping.ui.paymentPackage.queryMoneyMoving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingQueryBinding

class QueryMoneyMovingFragment : Fragment() {

    private lateinit var queryMoneyMovingViewModel: QueryMoneyMovingViewModel
    private var _binding: FragmentMoneyMovingQueryBinding? = null
    private val binding get() = _binding!!

    private lateinit var textAll: String

    private lateinit var control: NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyMovingQueryBinding.inflate(inflater, container, false)
        queryMoneyMovingViewModel =
            ViewModelProvider(this).get(QueryMoneyMovingViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textAll = resources.getString(R.string.text_on_button_all_text)

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(binding) {
            selectCurrency.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
            }
            selectCashAccount.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
            selectCategory.setOnClickListener {
                pressSelectButton(R.id.nav_categories)
            }
            applyButton.setOnClickListener {
                pressApplyButton()
            }
            resetParams.setOnClickListener {
                queryMoneyMovingViewModel.reset()
            }
        }
        with(queryMoneyMovingViewModel) {
            selectedCashAccount.observe(viewLifecycleOwner, {
                binding.nameSelectedCashAccount.text = it?.accountName ?: textAll
            })
            selectedCurrency.observe(viewLifecycleOwner, {
                binding.nameSelectedCurrency.text = it?.currencyName ?: textAll
            })
            selectedCategory.observe(viewLifecycleOwner, {
                binding.nameSelectedCategory.text = it?.categoryName ?: textAll
            })
        }

        queryMoneyMovingViewModel.checkArguments(arguments)

    }

    private fun pressApplyButton() {
        queryMoneyMovingViewModel.saveData()
        control.navigate(R.id.nav_money_moving)
    }

    private fun pressSelectButton(fragment: Int) {
        queryMoneyMovingViewModel.saveData()
        control.navigate(fragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}