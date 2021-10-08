package com.chico.myhomebookkeeping.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentReportsBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart

class ReportsFragment : Fragment() {

    private lateinit var reportsViewModel: ReportsViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChartView: PieChart
    private lateinit var horizontalLineChartView: HorizontalBarChart
    private val charts = Charts()
    private val uiHelper = UiHelper()
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        reportsViewModel =
            ViewModelProvider(this).get(ReportsViewModel::class.java)
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        with(binding) {
            selectCashAccountButton.setOnClickListener {
                showUiElements()
            }
            selectCategoryButton.setOnClickListener {
                showUiElements()
            }
            selectCurrencyButton.setOnClickListener {
                showUiElements()
            }
            selectTimePeriodButton.setOnClickListener {
                navControlHelper.moveToSelectTimePeriod()
            }
            hideRecyclerButton.setOnClickListener {
                hideUiElements()
            }
        }



        with(reportsViewModel) {
            getMap().observe(viewLifecycleOwner, { map ->
                map?.let { it1 ->
                    val sortedMap: MutableMap<String, Double> = LinkedHashMap()
                    it1.entries.sortedBy { it.value }.forEach { sortedMap[it.key] = it.value }
                    charts.showPieChart(chartView = pieChartView, sortedMap)
                    charts.showHorizontalBarChart(horizontalLineChartView, sortedMap)
                }
            })
        }
        return binding.root
    }

    private fun hideUiElements() {
        binding.recyclerView.visibility = View.GONE
        binding.hideRecyclerButton.visibility = View.GONE
    }

    private fun showUiElements() {
        uiHelper.showUiElement(binding.recyclerView)
        uiHelper.showUiElement(binding.hideRecyclerButton)
//        binding.recyclerView.visibility = View.VISIBLE
//        binding.hideRecyclerButton.visibility = View.VISIBLE
    }

//    private fun hideHideRecyclerViewButton() {
//        uiHelper.hideUiElement(binding.hideRecyclerButton)
//    }
//
//    private fun showHideRecyclerViewButton() {
//        uiHelper.showUiElement(binding.hideRecyclerButton)
//    }
//
//    private fun hideRecyclerView() {
//        uiHelper.hideUiElement(binding.recyclerView)
//    }
//
//    private fun showRecyclerView() {
//        uiHelper.showUiElement(binding.recyclerView)
//    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        pieChartView = binding.pieChart
        horizontalLineChartView = binding.horizontalBarChart
//        horizontalLineChartView.setDrawGridBackground(false)

    }


    override fun onStart() {
        super.onStart()
        reportsViewModel.getListFullMoneyMoving()
        reportsViewModel.setTextOnButtons()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}