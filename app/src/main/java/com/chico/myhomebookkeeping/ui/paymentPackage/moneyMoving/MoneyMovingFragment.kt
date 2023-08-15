package com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.db.entity.ChildCategory
import com.chico.myhomebookkeeping.db.full.FullFastPayment
import com.chico.myhomebookkeeping.interfaces.moneyMoving.OnNextEntryButtonClickedCallBack
import com.chico.myhomebookkeeping.ui.bottomSheet.EntryIsAddedBottomSheet
import com.chico.myhomebookkeeping.ui.categories.CategoriesViewModel
import com.chico.myhomebookkeeping.ui.categories.child.ChildCategoriesViewModel
import com.chico.myhomebookkeeping.ui.currencies.CurrenciesViewModel
import com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving.dialogs.SelectMoneyMovingDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class MoneyMovingFragment : Fragment() {

    private lateinit var plus: String
    private lateinit var minus: String
    private lateinit var db: MoneyMovementDao

    private lateinit var moneyMovingViewModel: MoneyMovingViewModel
    private var _binding: FragmentMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController

    private val currenciesViewModel: CurrenciesViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )
    private val categoriesViewModel: CategoriesViewModel by viewModels(
        ownerProducer = { requireActivity() }
    )

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        getStrings()
        db = dataBase.getDataBase(requireContext()).moneyMovementDao()
        _binding = FragmentMoneyMovingBinding.inflate(inflater, container, false)
        moneyMovingViewModel =
            ViewModelProvider(this).get(MoneyMovingViewModel::class.java)
        with(moneyMovingViewModel) {
            buttonTextOfTimePeriod.observe(viewLifecycleOwner) {
                binding.selectTimePeriod.text = it
            }
            buttonTextOfQueryCurrency.observe(viewLifecycleOwner) {
                binding.selectCurrency.text = it
            }
            buttonTextOfQueryCategory.observe(viewLifecycleOwner) {
                binding.selectCategory.text = it
            }
            buttonTextOfQueryCashAccount.observe(viewLifecycleOwner) {
                binding.selectCashAccount.text = it
            }
            moneyMovementList.observe(viewLifecycleOwner) {
                binding.moneyMovingHolder.adapter = it?.let { it1 ->
                    MoneyMovingAdapter(it1, object :
                        OnItemViewClickListenerLong {
                        override fun onClick(
                            fullFastPayment: FullFastPayment,
                            childCategory: ChildCategory
                        ) {
//                            getOneFullMoneyMoving(selectedId)
                            TODO()
//                            showSelectDialog(selectedId)
                        }

                    })
                }
            }
            incomeBalance.observe(viewLifecycleOwner) {
                binding.incomeBalance.text = it.toString()
            }
            spendingBalance.observe(viewLifecycleOwner) {
                binding.spendingBalance.text = it.toString()
            }
            totalBalance.observe(viewLifecycleOwner) {
                binding.totalBalance.text = it.toString()
            }
        }
        return binding.root
    }

    private fun showSelectDialog(selectedId: Long) {
        launchIo {
            val fullMoneyMoving = moneyMovingViewModel.loadSelectedMoneyMoving(selectedId)
            launchUi {
                val dialog = SelectMoneyMovingDialog(fullMoneyMoving,
                    object : OnItemSelectForChangeCallBack {
                        override fun onSelect(id: Int) {
//                        Message.log("changing item id = $id")
                            moneyMovingViewModel.saveIdMoneyMovingForChange(selectedId)
                            pressSelectButton(R.id.nav_change_money_moving)
                        }
                    })
                dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
            }
        }
    }

    private fun getStrings() {
        plus = requireContext().getString(R.string.sign_plus)
        minus = requireContext().getString(R.string.sign_minus)
    }

    private fun pressSelectButton(fragment: Int) {
        control.navigate(fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        currenciesViewModel.selectedCurrency.observe(viewLifecycleOwner) {
            moneyMovingViewModel.setButtonTextOfQueryCurrency(it.currencyName)
        }
        categoriesViewModel.selectedCategory.observe(viewLifecycleOwner) {
           binding.selectCategory.text = createButtonText(
                "CATEGORY", if (it?.nameRes != null) getString(it.nameRes)
                else it?.name.orEmpty()
            )
        }

        with(binding) {
            selectCategory.setOnClickListener {
                findNavController().navigate(R.id.showCategories)
            }
            selectCurrency.setOnClickListener {
                findNavController().navigate(R.id.showCurrencies)
            }
            selectCashAccount.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
            selectTimePeriod.setOnClickListener {
                pressSelectButton(R.id.nav_time_period)
            }
        }
//        checkLinesFound()
        checkIsFirstLaunch()

        moneyMovingViewModel.cleaningSP()
    }

    override fun onStart() {
        super.onStart()
        moneyMovingViewModel.getListFullMoneyMoving()
        newEntryAdded()
    }

    private fun newEntryAdded() {
        if (moneyMovingViewModel.isTheEntryOfMoneyMovingAdded()) {
            launchUi {
                val entryIsAddedBottomSheet = EntryIsAddedBottomSheet(
                    object : OnNextEntryButtonClickedCallBack {
                        override fun onClick() {
                            control.navigate(R.id.nav_fast_payments_fragment)
                        }
                    }
                )
                entryIsAddedBottomSheet.show(
                    childFragmentManager,
                    getString(R.string.tag_show_dialog)
                )
//                launchIo {
//                    delay(3500)
//                    entryIsAddedBottomSheet.dismiss()
//                }
            }
            moneyMovingViewModel.dialogOfNewEntryAddedIsShowed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun checkIsFirstLaunch() {
        if (moneyMovingViewModel.isFirstLaunch()) {
            moneyMovingViewModel.setIsFirstLaunchFalse()
            control.navigate(R.id.nav_first_launch_select_currencies_fragment)
        }
    }

    //    private fun checkLinesFound() {
//        var numFoundedLines = moneyMovingViewModel.getNumFoundLines()
//        var temp = numFoundedLines
//        launchUi {
//            while (numFoundedLines == temp) {
//                delay(500)
////                if (moneyMovingViewModel.isMoneyMovementFound()) {
////                    numFoundedLines = moneyMovingViewModel.getNumFoundLines()
////                }
//            }
//            message("найдено $numFoundedLines строк")
//        }
//    }

    private fun showMessage(s: String) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show()
    }

    fun createButtonText(text: String, name: String): String {
        val separator: String = getNewLineSeparator()
        return text + separator + name
    }

    private fun getNewLineSeparator(): String {
        return "\n"
    }
}