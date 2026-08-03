package com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.MainActivity
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.interfaces.OnItemSelectForChangeCallBack
import com.chico.myhomebookkeeping.interfaces.OnItemViewClickListenerLong
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dao.MoneyMovementDao
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.interfaces.moneyMoving.OnNextEntryButtonClickedCallBack
import com.chico.myhomebookkeeping.obj.Constants
import com.chico.myhomebookkeeping.ui.bottomSheet.EntryIsAddedBottomSheet
import com.chico.myhomebookkeeping.ui.categories.CategoriesFragment
import com.chico.myhomebookkeeping.ui.paymentPackage.moneyMoving.dialogs.SelectMoneyMovingDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi

class MoneyMovingFragment : Fragment() {

    private lateinit var db: MoneyMovementDao

    private lateinit var moneyMovingViewModel: MoneyMovingViewModel
    private var _binding: FragmentMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController

    @SuppressLint("SetTextI18n")
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
            buttonTextOfTimePeriod.observe(viewLifecycleOwner, {
                binding.selectTimePeriod.text = it
            })
            buttonTextOfQueryCurrency.observe(viewLifecycleOwner, {
                binding.selectCurrency.text = it
            })
            buttonTextOfQueryCategory.observe(viewLifecycleOwner, {
                binding.selectCategory.text = it
            })
            buttonTextOfQueryCashAccount.observe(viewLifecycleOwner, {
                binding.selectCashAccount.text = it
            })
            moneyMovementList.observe(viewLifecycleOwner, {
                binding.emptyJournalCard.visibility =
                    if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
                binding.moneyMovingHolder.adapter = it?.let { it1 ->
                    MoneyMovingAdapter(
                        it1,
                        journalCurrencyDisplayMode(),
                        showJournalDateSeparators(),
                        object : OnItemViewClickListenerLong {
                            override fun onClick(selectedId: Long) {
//                                getOneFullMoneyMoving(selectedId)
                                showSelectDialog(selectedId)
                            }

                        }
                    )
                }
            })
            balanceRows.observe(viewLifecycleOwner, {
                showBalanceRows(it)
            })
        }
        return binding.root
    }

    private fun showBalanceRows(rows: List<MoneyMovingCountMoney.CurrencyBalance>) {
        binding.balanceRows.removeAllViews()
        rows.forEach {
            binding.balanceRows.addView(createBalanceRow(it))
        }
    }

    private fun journalCurrencyDisplayMode(): String {
        val sharedPreferences = requireContext().getSharedPreferences(
            Constants.SP_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(
            Constants.JOURNAL_CURRENCY_DISPLAY_MODE,
            Constants.JOURNAL_CURRENCY_DISPLAY_NAME
        ) ?: Constants.JOURNAL_CURRENCY_DISPLAY_NAME
    }

    private fun showJournalDateSeparators(): Boolean {
        val sharedPreferences = requireContext().getSharedPreferences(
            Constants.SP_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getBoolean(Constants.JOURNAL_SHOW_DATE_SEPARATORS, true)
    }

    private fun createBalanceRow(row: MoneyMovingCountMoney.CurrencyBalance): LinearLayout {
        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 4f
            setPadding(0, 0, 0, 0)
        }
        rowLayout.addView(
            createBalanceTextView(
                row.currencyPrefix,
                ContextCompat.getColor(requireContext(), R.color.black),
                Gravity.START,
                0.7f
            )
        )
        rowLayout.addView(
            createBalanceTextView(
                formatIncome(row.income),
                ContextCompat.getColor(requireContext(), R.color.incomeTextColor),
                Gravity.RIGHT,
                0.85f
            )
        )
        rowLayout.addView(
            createBalanceTextView(
                formatSpending(row.spending),
                ContextCompat.getColor(requireContext(), R.color.spendingTextColor),
                Gravity.RIGHT,
                0.95f
            )
        )
        rowLayout.addView(
            createBalanceTextView(
                "${getString(R.string.description_balance)} ${row.balance}",
                ContextCompat.getColor(requireContext(), R.color.black),
                Gravity.RIGHT,
                1.5f
            )
        )
        return rowLayout
    }

    private fun formatIncome(income: String): String {
        return if (income.startsWith("-")) income else "+$income"
    }

    private fun formatSpending(spending: String): String {
        return if (spending.startsWith("-")) spending else "-$spending"
    }

    private fun createBalanceTextView(
        text: String,
        textColor: Int,
        gravity: Int,
        weight: Float
    ): TextView {
        return TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                weight
            )
            this.text = text
            setTextColor(textColor)
            setTextSize(
                android.util.TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.H6)
            )
            this.gravity = gravity
        }
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

    private fun pressSelectButton(fragment: Int) {
        control.navigate(fragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(binding) {
            selectCategory.setOnClickListener {
                control.navigate(
                    R.id.nav_categories,
                    CategoriesFragment.openModeArgs(CategoriesFragment.OPEN_MODE_JOURNAL_FILTER)
                )
            }
            selectCurrency.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
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
        showCleanInstallMessageIfNeeded(view)

        moneyMovingViewModel.cleaningSP()
    }

    override fun onStart() {
        super.onStart()
        moneyMovingViewModel.getListFullMoneyMoving()
        newEntryAdded()
    }

    private fun newEntryAdded() {
        if (moneyMovingViewModel.isTheEntryOfMoneyMovingAdded()){
            launchUi {
                val entryIsAddedBottomSheet = EntryIsAddedBottomSheet(
                    object : OnNextEntryButtonClickedCallBack{
                        override fun onClick() {
                            control.navigate(
                                R.id.nav_categories,
                                CategoriesFragment.openModeArgs(
                                    CategoriesFragment.OPEN_MODE_STANDALONE
                                )
                            )
                        }
                    }
                )
                entryIsAddedBottomSheet.show(childFragmentManager,getString(R.string.tag_show_dialog))
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
            control.navigate(R.id.nav_first_launch_setup_fragment)
        }
    }

    private fun showCleanInstallMessageIfNeeded(view: View) {
        val sharedPreferences = requireContext().getSharedPreferences(
            Constants.SP_NAME,
            Context.MODE_PRIVATE
        )
        if (!sharedPreferences.getBoolean(Constants.CLEAN_INSTALL_MESSAGE_PENDING, false)) {
            showWhatsNewAfterFirstLaunchIfNeeded(view)
            return
        }

        sharedPreferences.edit()
            .putBoolean(Constants.CLEAN_INSTALL_MESSAGE_PENDING, false)
            .apply()
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(R.string.first_launch_clean_install_message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
        dialog.setOnDismissListener {
            showWhatsNewAfterFirstLaunchIfNeeded(view)
        }
        dialog.show()
    }

    private fun showWhatsNewAfterFirstLaunchIfNeeded(view: View) {
        view.post {
            (activity as? MainActivity)?.showWhatsNewAfterFirstLaunchIfNeeded()
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
}
