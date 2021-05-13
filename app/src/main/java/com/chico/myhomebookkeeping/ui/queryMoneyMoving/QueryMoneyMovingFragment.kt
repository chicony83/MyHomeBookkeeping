package com.chico.myhomebookkeeping.ui.queryMoneyMoving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentMoneyMovingQueryBinding

class QueryMoneyMovingFragment:Fragment() {

    private var _binding:FragmentMoneyMovingQueryBinding? = null
    private val binding get() = _binding!!

    private lateinit var control:NavController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyMovingQueryBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        control = activity?.findNavController(R.id.nav_host_fragment)!!

        with(binding){
            selectCurrency.setOnClickListener {
                pressSelectButton(R.id.nav_currencies)
            }
            selectCashAccount.setOnClickListener {
                pressSelectButton(R.id.nav_cash_account)
            }
            selectCategory.setOnClickListener {
                pressSelectButton(R.id.nav_categories)
            }
        }

    }
    private fun pressSelectButton(nav: Int) {
        control.navigate(nav)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}