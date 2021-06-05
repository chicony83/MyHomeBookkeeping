package com.chico.myhomebookkeeping.ui.moneyMoving

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
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

class MoneyMovingFragment : Fragment() {

    private lateinit var db: MoneyMovementDao

    private lateinit var moneyMovingViewModel: MoneyMovingViewModel
    private var _binding: FragmentMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private val uiHelper = UiHelper()
    private lateinit var control: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        db = dataBase.getDataBase(requireContext()).moneyMovementDao()
        _binding = FragmentMoneyMovingBinding.inflate(inflater, container, false)

        moneyMovingViewModel =
            ViewModelProvider(this).get(MoneyMovingViewModel::class.java)
        with(moneyMovingViewModel) {
            buttonTextOfQueryCurrency.observe(viewLifecycleOwner,{
                binding.selectCurrency.text = it
            })
            buttonTextOfQueryCategory.observe(viewLifecycleOwner,{
                binding.selectCategory.text = it
            })
            buttonTextOfQueryCashAccount.observe(viewLifecycleOwner,{
                binding.selectCashAccount.text = it
            })
            moneyMovementList.observe(viewLifecycleOwner, {
                binding.moneyMovingHolder.adapter = MoneyMovingAdapter(it)
            })
            amountMoneyOfQuery.observe(viewLifecycleOwner, {
                binding.resultCountAmountOfQuery.text = it.toString()
            })

        }
        return binding.root
    }

    private fun pressSelectButton(fragment: Int) {
        control.navigate(fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(binding) {
            selectQuerySetting.setOnClickListener {
                pressSelectButton(R.id.nav_money_moving_query)
            }
            selectCategory.setOnClickListener {
                pressSelectButton(R.id.nav_categories)
            }
            selectCurrency.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
            }
            selectCashAccount.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
        }

        checkLinesFound()
    }

    private fun checkLinesFound() {
        var numFoundedLines = moneyMovingViewModel.getNumFoundLines()
        var temp = numFoundedLines
        launchUi {
            while (numFoundedLines == temp) {
                delay(500)
                if (moneyMovingViewModel.isMoneyMovementFound()) {
                    numFoundedLines = moneyMovingViewModel.getNumFoundLines()
                }
            }
            Toast.makeText(context, "найдено $numFoundedLines записи", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}