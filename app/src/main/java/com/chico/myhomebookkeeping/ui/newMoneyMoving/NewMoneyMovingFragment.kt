package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.annotation.SuppressLint
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
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.ui.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi

import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.*


class NewMoneyMovingFragment : Fragment() {

    private lateinit var newMoneyMovingViewModel: NewMoneyMovingViewModel
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis

    private lateinit var control: NavController

    private var firstPress: Long = 0L
    private var secondPress: Long = 0L
    private var click = 0
    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewMoneyMovingBinding.inflate(inflater, container, false)

        newMoneyMovingViewModel = ViewModelProvider(this).get(NewMoneyMovingViewModel::class.java)

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.hideKeyboard()
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        with(binding) {
            dateTimeTimeStamp.setText(currentDateTimeMillis.parseTimeFromMillis())
            dateTimeTimeStamp.isEnabled = false
            selectCashAccountButton.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
            selectCurrenciesButton.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
            }
            selectCategoryButton.setOnClickListener {
                pressSelectButton(R.id.nav_categories)
            }
            addNewMoneyMovingButton.setOnClickListener {
                pressAddButton()
//                control.popBackStack()
            }
        }
        with(newMoneyMovingViewModel) {
            selectedCashAccount.observe(viewLifecycleOwner, {
                binding.selectCashAccountButton.text = it.accountName
            })
            selectedCurrency.observe(viewLifecycleOwner, {
                binding.selectCurrenciesButton.text = it.currencyName
            })
            selectedCategory.observe(viewLifecycleOwner, {
                binding.selectCategoryButton.text = it.categoryName
            })
        }

        newMoneyMovingViewModel.checkArguments(arguments)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun pressAddButton() {
//        firstPress = System.currentTimeMillis()
//        if (firstPress > secondPress) {
        click++
//        }

        if (click == 1) {
            val dataTime = currentDateTimeMillis
            val amount: Double = binding.amount.text.toString().toDouble()
            val description: String = binding.description.text.toString()
            newMoneyMovingViewModel.saveData()
            runBlocking {
                val result =
                    newMoneyMovingViewModel.addingNewMoneyMovingInDB(dataTime, amount, description)
                if (result > 0) {
                    uiHelper.clearUiListEditText(
                        listOf(
                            binding.amount, binding.description
                        )
                    )
                    Toast.makeText(context, "запись добавлена", Toast.LENGTH_LONG).show()
                }
            }
        }
        if (click >= 2) {
            binding.addNewMoneyMovingButton.isEnabled = false
            binding.addNewMoneyMovingButton.text = "слишком много нажатий"
            Toast.makeText(context, "запись добавляется или уже добавлена", Toast.LENGTH_LONG)
                .show()
            launchUi {
                Toast.makeText(context, "кнопка временно заблокирована", Toast.LENGTH_LONG).show()
                delay(5000)
                binding.addNewMoneyMovingButton.text = "можно добавить еще одну запись"
                binding.addNewMoneyMovingButton.isEnabled = true
                click = 0
            }
        }
    }

    private fun pressSelectButton(nav: Int) {
        newMoneyMovingViewModel.saveData()
        navigateTo(nav)
    }

    private fun navigateTo(nav: Int) {
        control.navigate(nav)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}