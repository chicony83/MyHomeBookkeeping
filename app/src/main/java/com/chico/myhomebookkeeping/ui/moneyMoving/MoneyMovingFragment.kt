package com.chico.myhomebookkeeping.ui.moneyMoving

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.`interface`.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MoneyMovingFragment : Fragment() {

    private lateinit var db: MoneyMovementDao

    private lateinit var moneyMovingViewModel: MoneyMovingViewModel
    private var _binding: FragmentMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private val uiHelper = UiHelper()
    private lateinit var control: NavController

    private var selectedMoneyMovingId = 0

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
                binding.moneyMovingHolder.adapter = MoneyMovingAdapter(it, object:
                    OnItemViewClickListenerLong {
                    override fun onClick(selectedId: Long) {
                        uiHelper.showUiElement(binding.selectLayoutHolder)
                        Log.i("TAG","---moneyMoving id $selectedId---")
                        moneyMovingViewModel.loadSelectedMoneyMoving(selectedId)
//                        selectedMoneyMovingId = selectedId
                    }

                })
            })
            amountMoneyOfQuery.observe(viewLifecycleOwner, {
                binding.resultCountAmountOfQuery.text = it.toString()
            })
            selectedMoneyMoving.observe(viewLifecycleOwner,{
                with(binding.selectLayout){
                    dateTimeText.text = it?.timeStamp?.parseTimeFromMillis()
                    amount.text = it?.amount.toString()
                    currency.text = it?.currencyNameValue
                    category.text = it?.categoryNameValue
                    cashAccount.text = it?.cashAccountNameValue
                }
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
            selectCategory.setOnClickListener {
                pressSelectButton(R.id.nav_categories)
            }
            selectCurrency.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
            }
            selectCashAccount.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
            with(selectLayout){
                changeButton.setOnClickListener {
                    runBlocking {
                        moneyMovingViewModel.saveMoneyMovingToChange()
                        pressSelectButton(R.id.nav_change_money_moving)
                    }

                }
                cancelButton.setOnClickListener {
                    uiHelper.hideUiElement(binding.selectLayoutHolder)
                    if (selectedMoneyMovingId>0){
                        selectedMoneyMovingId = 0
                    }
                }
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