package com.chico.myhomebookkeeping.ui.reportsMenu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentReportsMenuBinding
import com.chico.myhomebookkeeping.enums.ReportsType
import com.chico.myhomebookkeeping.helpers.NavControlHelper

class ReportsMenuFragment : Fragment() {
    private lateinit var reportsMenuViewModel: ReportsMenuViewModel
    private var _binding: FragmentReportsMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)
        _binding = FragmentReportsMenuBinding.inflate(inflater, container, false)
        reportsMenuViewModel = ViewModelProvider(this).get(ReportsMenuViewModel::class.java)
        with(binding) {
            allIncomePieButton.setOnClickListener {
                selectIncomeCategoryPieReport()
            }
            allSpendingPieButton.setOnClickListener {
                selectSpendingCategoryPieReport()
            }
            selectTimePeriodButton.setOnClickListener {
                pressSelectButton(R.id.nav_time_period)
            }
        }
        with(reportsMenuViewModel){
            buttonTextOfTimePeriod.observe(viewLifecycleOwner,{
                binding.selectTimePeriodButton.text = it
            })
        }
        return binding.root
    }

    private fun pressSelectButton(fragment: Int) {
        navControlHelper.moveToSelectedFragment(fragment)
    }

    private fun selectSpendingCategoryPieReport() {
        reportsMenuViewModel.saveArgs(ReportsType.PieSpending.toString())
        launchPieReport()
    }

    private fun selectIncomeCategoryPieReport() {
        reportsMenuViewModel.saveArgs(ReportsType.PieIncome.toString())
        launchPieReport()
    }

    private fun launchPieReport() {
        navControlHelper.moveToSelectedFragment(R.id.nav_reports)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
