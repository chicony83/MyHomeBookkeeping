package com.chico.myhomebookkeeping.ui.newMoneyMoving

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentNewMoneyMovingBinding
import com.chico.myhomebookkeeping.db.entity.Currencies
import com.chico.myhomebookkeeping.ui.currencies.CurrenciesViewModel
import com.chico.myhomebookkeeping.utils.parseTimeFromMillis
import java.util.*


class NewMoneyMovingFragment : Fragment() {
    private var _binding: FragmentNewMoneyMovingBinding? = null
    private val binding get() = _binding!!


    private var currentDateTimeMillis: Long = Calendar.getInstance().timeInMillis
    private lateinit var currenciesViewModel: CurrenciesViewModel

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
        val control = activity?.findNavController(R.id.nav_host_fragment)

        binding.dateTimeTimeStamp.setText(currentDateTimeMillis.parseTimeFromMillis())

        binding.selectCashAccountButton.setOnClickListener{
            control?.navigate(R.id.nav_cash_account)
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