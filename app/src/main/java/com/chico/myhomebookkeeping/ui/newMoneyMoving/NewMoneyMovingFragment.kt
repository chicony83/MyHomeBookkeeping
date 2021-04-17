package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.domain.CurrenciesUseCase
import com.chico.myhomebookkeeping.ui.currencies.CurrenciesViewModel
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.util.*


class NewMoneyMovingFragment : Fragment() {

    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private val argsCashAccountKey = Constants.CASH_ACCOUNT_KEY
    private val argsCurrencyKey = Constants.CURRENCY_KEY

//    private val cashAccountsUseCase = CashAccountsUseCase()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewMoneyMovingBinding.inflate(inflater, container, false)
        currenciesViewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)

        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val db = dataBase.getDataBase(requireContext())

        val control = activity?.findNavController(R.id.nav_host_fragment)

        binding.dateTimeTimeStamp.setText(currentDateTimeMillis.parseTimeFromMillis())

        binding.selectButton.setOnClickListener {
            control?.navigate(R.id.nav_cash_account)
        }
        binding.selectCurrenciesButton.setOnClickListener {
            control?.navigate(R.id.nav_currencies)
        }
        val cashAccountId: Int? = arguments?.getInt(argsCashAccountKey)
        if (cashAccountId != null) {
            launchUi {
                binding.selectButton.text = CashAccountsUseCase.getOneCashAccountName(
                    db.cashAccountDao(), cashAccountId
                )
            }
//            Toast.makeText(context, "$cashAccountId", Toast.LENGTH_SHORT).show()
        }
        val currenciesId: Int? = arguments?.getInt(argsCurrencyKey)
        if (currenciesId != null) {
            launchUi {
                binding.selectCurrenciesButton.text = CurrenciesUseCase.getOneCurrencyName(
                    db.currenciesDao(), currenciesId
                )
            }
        }

        binding.addNewMoneyMovingButton.setOnClickListener {
            control?.popBackStack()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}