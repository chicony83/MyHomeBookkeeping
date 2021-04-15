package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.constants.Constants
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.db.dataBase
import com.chico.myhomebookkeeping.domain.CashAccountsUseCase
import com.chico.myhomebookkeeping.ui.currencies.CurrenciesViewModel
import com.chico.myhomebookkeeping.utils.launchUi
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.util.*


class NewMoneyMovingFragment : Fragment() {

    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!
    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis
    private lateinit var currenciesViewModel: CurrenciesViewModel
    private val argsCashAccount = Constants.CASH_ACCOUNT_KEY

    private val cashAccountsUseCase = CashAccountsUseCase()


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

        val cashAccountId: Int? = arguments?.getInt(argsCashAccount)

        val control = activity?.findNavController(R.id.nav_host_fragment)

        binding.dateTimeTimeStamp.setText(currentDateTimeMillis.parseTimeFromMillis())

        binding.selectCashAccountButton.setOnClickListener {
            control?.navigate(R.id.nav_cash_account)
        }
        if (cashAccountId != null) {
            launchUi {
                val text = cashAccountsUseCase
                    .getOneCashAccount(
                        dataBase
                            .getDataBase(
                                requireContext()
                            )
                            .cashAccountDao(), cashAccountId
                    )
                binding.selectCashAccountButton.text = text
            }

            Toast.makeText(context, "$cashAccountId", Toast.LENGTH_SHORT).show()
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