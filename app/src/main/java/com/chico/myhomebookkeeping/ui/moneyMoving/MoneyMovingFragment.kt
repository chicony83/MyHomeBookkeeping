package com.chico.myhomebookkeeping.ui.moneyMoving

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.chico.myhomebookkeeping.helpers.UiColors
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.properties.Delegates

class MoneyMovingFragment : Fragment() {

    private lateinit var plus: String
    private lateinit var minus: String
    private lateinit var db: MoneyMovementDao

    private lateinit var moneyMovingViewModel: MoneyMovingViewModel
    private var _binding: FragmentMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private val uiHelper = UiHelper()
    private lateinit var control: NavController
    private val uiColors = UiColors()

    private var selectedMoneyMovingId = 0

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
                binding.moneyMovingHolder.adapter = it?.let { it1 ->
                    MoneyMovingAdapter(it1, object :
                        OnItemViewClickListenerLong {
                        override fun onClick(selectedId: Long) {
                            uiHelper.showUiElement(binding.selectLayoutHolder)
                            Log.i("TAG", "---moneyMoving id $selectedId---")
                            moneyMovingViewModel.loadSelectedMoneyMoving(selectedId)
                            //                        selectedMoneyMovingId = selectedId
                        }

                    })
                }
            })
            incomeBalance.observe(viewLifecycleOwner, {
                binding.incomeBalance.text = it.toString()
            })
            spendingBalance.observe(viewLifecycleOwner, {
                binding.spendingBalance.text = it.toString()
            })
            totalBalance.observe(viewLifecycleOwner, {
                binding.totalBalance.text = it.toString()
            })
            selectedMoneyMoving.observe(viewLifecycleOwner, {
                with(binding.selectLayout) {
                    dateTimeText.text = it?.timeStamp?.parseTimeFromMillis()
                    if (it?.isIncome == true) {
                        amount.text = plus + it.amount.toString()
                        setTextColor(binding.selectLayout.amount,R.style.Description_IncomeText)
                    }
                    if (it?.isIncome == false) {
                        amount.text = minus + it.amount.toString()
                        setTextColor(binding.selectLayout.amount,R.style.Description_SpendingText)
                    }
                    currency.text = it?.currencyNameValue
                    category.text = it?.categoryNameValue
                    cashAccount.text = it?.cashAccountNameValue

                    if (!it?.description.isNullOrEmpty()) {
                        binding.selectLayout.descriptionOfDescription.visibility = View.VISIBLE
                        binding.selectLayout.description.visibility = View.VISIBLE
                        description.text = it?.description
                    }
                    if (it?.description.isNullOrEmpty()) {
                        description.text = null
                        binding.selectLayout.descriptionOfDescription.visibility = View.GONE
                        binding.selectLayout.description.visibility = View.GONE
                    }
                }
            })
        }
        return binding.root
    }

    private fun setTextColor(amount: TextView, style: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            amount.setTextAppearance(style)
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
            selectTimePeriod.setOnClickListener {
                pressSelectButton(R.id.nav_time_period)
            }
            with(selectLayout) {
                changeButton.setOnClickListener {
                    runBlocking {
                        moneyMovingViewModel.saveMoneyMovingToChange()
                        pressSelectButton(R.id.nav_change_money_moving)
                    }
                }
                cancelButton.setOnClickListener {
                    uiHelper.hideUiElement(binding.selectLayoutHolder)
                    if (selectedMoneyMovingId > 0) {
                        selectedMoneyMovingId = 0
                    }
                }
            }
            with(firstLaunchDialog) {
                submitFirstLaunchButton.setOnClickListener {
                    launchFragment(R.id.nav_first_launch_fragment)
                }
                cancelFirstLaunchButton.setOnClickListener {
                    uiHelper.hideUiElement(binding.firstLaunchDialogHolder)
                    moneyMovingViewModel.setIsFirstLaunchFalse()
                }
            }
        }
        checkUiMode()
//        checkLinesFound()
        checkFirstLaunch()
        moneyMovingViewModel.cleaningSP()
    }

    override fun onStart() {
        super.onStart()
        moneyMovingViewModel.getListFulMoneyMoving()
    }

    private fun launchFragment(fragment: Int) {
        control.navigate(fragment)
    }

    private fun checkFirstLaunch() {
        if (moneyMovingViewModel.isFirstLaunch()) {
            binding.firstLaunchDialogHolder.visibility = View.VISIBLE
            moneyMovingViewModel.setIsFirstLaunchFalse()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun checkUiMode() {

        val nightModeFlags = requireContext().resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> {
//                message("ночь")
                with(uiColors){
                    setDialogBackgroundColor(
                        getDialogsList(),
                        R.drawable.dialog_background_night
                    )
                    setButtonsBackgroundColor(
                        getButtonsList(),
                        getNightColorForButtonsBackground()
                    )
                }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
//                message("день")
                with(uiColors){
                    setDialogBackgroundColor(
                        getDialogsList(),
                        R.drawable.dialog_background_day
                    )
                    setButtonsBackgroundColor(
                        getButtonsList(),
                        getDayColorForButtonsBackground()
                    )
                    setColorTextOnButton(
                        getButtonsListForColorButtonText(),
                        getDayColorForButtonsText()
                    )
                }
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                with(uiColors){
                    setDialogBackgroundColor(
                        getDialogsList(),
                        R.drawable.dialog_background_day
                    )
                    setButtonsBackgroundColor(
                        getButtonsList(),
                        getDayColorForButtonsBackground()
                    )
                    setColorTextOnButton(
                        getButtonsListForColorButtonText(),
                        getDayColorForButtonsText()
                    )
                }
            }
        }
    }

    private fun getDayColorForButtonsText() = resources.getColor(R.color.colorPrimaryVariant)

    private fun getButtonsListForColorButtonText() = listOf(
        binding.firstLaunchDialog.submitFirstLaunchButton,
        binding.firstLaunchDialog.cancelFirstLaunchButton,
        binding.selectLayout.changeButton
    )

    private fun getDayColorForButtonsBackground(): ColorStateList {
        return getButtonsBackgroundColor(R.color.buttonDayBackground)
    }

    private fun getNightColorForButtonsBackground(): ColorStateList {
        return getButtonsBackgroundColor(R.color.buttonNightBackground)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun getButtonsBackgroundColor(color: Int): ColorStateList {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColorStateList(color, null)
        } else {
            resources.getColorStateList(color)
        }
    }

    private fun getButtonsList() = listOf(
        binding.selectLayout.changeButton,
        binding.selectLayout.cancelButton,
        binding.firstLaunchDialog.submitFirstLaunchButton,
        binding.firstLaunchDialog.cancelFirstLaunchButton
    )

    private fun getDialogsList()= listOf(
        binding.selectLayout,
        binding.firstLaunchDialog
    )

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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun message(text: String) {
        launchUi {
            delay(1000)
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}