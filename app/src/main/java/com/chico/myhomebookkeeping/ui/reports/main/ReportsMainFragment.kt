package com.chico.myhomebookkeeping.ui.reports.main

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
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.github.mikephil.charting.charts.PieChart

class ReportsMainFragment : Fragment() {

    private lateinit var reportsMainViewModel: ReportsMainViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChartView: PieChart
    private val charts = Charts()
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private var lastReportItems: List<ReportCategoryItem> = emptyList()
    private var currentCurrencyShortName: String? = null
    private var currentDonutCenterLabel: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        reportsMainViewModel =
            ViewModelProvider(this).get(ReportsMainViewModel::class.java)
        control = activity?.findNavController(R.id.nav_host_fragment)!!
        navControlHelper = NavControlHelper(control)

        with(reportsMainViewModel) {
            buttonTextOfTimePeriod.observe(viewLifecycleOwner, {
                binding.periodFilterTextView.text = it
            })
            reportTitle.observe(viewLifecycleOwner, {
                binding.reportTitleTextView.text = it
            })
            donutCenterLabel.observe(viewLifecycleOwner, {
                currentDonutCenterLabel = it
                updateDonutChart()
            })
            periodText.observe(viewLifecycleOwner, {
                binding.periodTextView.text = it
            })
            totalAmountText.observe(viewLifecycleOwner, {
                binding.totalAmountTextView.text = it
                updateDonutChart()
            })
            summaryDetailsText.observe(viewLifecycleOwner, {
                binding.summaryDetailsTextView.text = it
            })
            currencyShortName.observe(viewLifecycleOwner, {
                currentCurrencyShortName = it
                updateBreakdownAdapter()
            })
            categoriesFilterText.observe(viewLifecycleOwner, {
                binding.categoryFilterTextView.text = it
            })
            reportCategoryItems.observe(viewLifecycleOwner, { items ->
                lastReportItems = items
                updateBreakdownAdapter()
                updateDonutChart()
            })
            isEmpty.observe(viewLifecycleOwner, { isEmpty ->
                binding.reportContentGroup.visibility = if (isEmpty) View.GONE else View.VISIBLE
                binding.emptyStateGroup.visibility = if (isEmpty) View.VISIBLE else View.GONE
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        pieChartView = binding.pieChart
        with(binding) {
            categoryFilterButton.setOnClickListener {
                navControlHelper.toSelectedFragment(R.id.nav_reports_categories_fragment)
            }
            periodFilterButton.setOnClickListener {
                navControlHelper.moveToSelectTimePeriod()
            }
            emptyChangePeriodButton.setOnClickListener {
                navControlHelper.moveToSelectTimePeriod()
            }
            emptySelectCategoriesButton.setOnClickListener {
                navControlHelper.toSelectedFragment(R.id.nav_reports_categories_fragment)
            }
            breakdownRecyclerView.isNestedScrollingEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        launchUi {
            reportsMainViewModel.updateReports(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateBreakdownAdapter() {
        binding.breakdownRecyclerView.adapter =
            ReportsBreakdownAdapter(lastReportItems, currentCurrencyShortName)
    }

    private fun updateDonutChart() {
        if (!::pieChartView.isInitialized || lastReportItems.isEmpty()) return
        charts.showDonutChart(
            requireContext(),
            chartView = pieChartView,
            items = lastReportItems,
            centerAmountText = binding.totalAmountTextView.text.toString(),
            centerLabelText = currentDonutCenterLabel
        )
    }
}
