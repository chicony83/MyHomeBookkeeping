package com.chico.myhomebookkeeping.ui.moneyMoving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.ui.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import kotlinx.coroutines.delay

class MoneyMovingFragment : Fragment() {

    private lateinit var db: MoneyMovementDao

    private lateinit var moneyMovingViewModel: MoneyMovingViewModel
    private var _binding: FragmentMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        db = dataBase.getDataBase(requireContext()).moneyMovementDao()
        _binding = FragmentMoneyMovingBinding.inflate(inflater, container, false)

        moneyMovingViewModel =
            ViewModelProvider(this).get(MoneyMovingViewModel::class.java)

        moneyMovingViewModel.moneyMovementList.observe(viewLifecycleOwner, {
            binding.moneyMovingHolder.adapter = MoneyMovingAdapter(it)
        })
        moneyMovingViewModel.textDescriptionOfQueryCurrency.observe(viewLifecycleOwner, {
            binding.selectedQueryCurrency.text = it
        })
        moneyMovingViewModel.textDescriptionOfQueryCategory.observe(viewLifecycleOwner, {
            binding.selectedQueryCategory.text = it
        })
        moneyMovingViewModel.textDescriptionOfQueryCashAccount.observe(viewLifecycleOwner, {
            binding.selectedQueryCashAccount.text = it
        })
        moneyMovingViewModel.amountMoneyOfQuery.observe(viewLifecycleOwner, {
            binding.resultCountAmountOfQuery.text = it.toString()
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()
        with(binding) {
            selectQuerySetting.setOnClickListener {
                findNavController().navigate(R.id.nav_money_moving_query)
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