package com.chico.myhomebookkeeping.ui.reports.main

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentReportsBinding
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.github.mikephil.charting.charts.PieChart
import kotlinx.coroutines.launch

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
    private var highlightedBreakdownPositions: Set<Int> = emptySet()

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
            donutCenterLabel.observe(viewLifecycleOwner, {
                currentDonutCenterLabel = it
                updateDonutChart()
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
                highlightedBreakdownPositions = emptySet()
                updateBreakdownAdapter()
                updateDonutChart()
            })
            isEmpty.observe(viewLifecycleOwner, { isEmpty ->
                binding.reportContentGroup.visibility = if (isEmpty) View.GONE else View.VISIBLE
                binding.emptyStateGroup.visibility = if (isEmpty) View.VISIBLE else View.GONE
                hideScrollToChartButton(animate = false)
            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            scrollToChartButton.setOnClickListener {
                highlightedBreakdownPositions = emptySet()
                updateBreakdownAdapter()
                animateReportsScrollTo(0)
                hideScrollToChartButton()
            }
            breakdownRecyclerView.isNestedScrollingEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch {
            reportsMainViewModel.updateReports(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateBreakdownAdapter() {
        binding.breakdownRecyclerView.adapter =
            ReportsBreakdownAdapter(
                lastReportItems,
                currentCurrencyShortName,
                highlightedBreakdownPositions
            )
    }

    private fun updateDonutChart() {
        if (!::pieChartView.isInitialized || lastReportItems.isEmpty()) return
        charts.showDonutChart(
            requireContext(),
            chartView = pieChartView,
            items = lastReportItems,
            centerAmountText = binding.totalAmountTextView.text.toString(),
            centerLabelText = currentDonutCenterLabel,
            onSliceSelected = ::scrollToBreakdownItems
        )
    }

    private fun scrollToBreakdownItems(indices: List<Int>) {
        if (indices.isEmpty()) return
        highlightedBreakdownPositions = indices.toSet()
        updateBreakdownAdapter()
        binding.breakdownRecyclerView.post {
            val firstIndex = indices.minOrNull() ?: return@post
            val layoutManager = binding.breakdownRecyclerView.layoutManager
            val child = layoutManager?.findViewByPosition(firstIndex)
            val firstChildHeight = binding.breakdownRecyclerView.getChildAt(0)?.height ?: 0
            val itemTop = child?.top ?: (firstChildHeight * firstIndex)
            val topPadding = resources.getDimensionPixelSize(R.dimen.margin_double)
            val scrollY = binding.breakdownRecyclerView.top + itemTop - topPadding
            animateReportsScrollTo(scrollY.coerceAtLeast(0))
            showScrollToChartButton()
        }
    }

    private fun animateReportsScrollTo(targetY: Int) {
        // Use a fixed duration instead of ScrollView.smoothScrollTo so report navigation
        // feels deliberate enough for the selected breakdown row to stay understandable.
        ObjectAnimator.ofInt(binding.reportsScrollView, "scrollY", binding.reportsScrollView.scrollY, targetY)
            .apply {
                duration = REPORT_SCROLL_DURATION_MS
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
    }

    private fun showScrollToChartButton() {
        with(binding.scrollToChartButton) {
            animate().cancel()
            if (visibility != View.VISIBLE) {
                alpha = 0f
                visibility = View.VISIBLE
            }
            animate()
                .alpha(1f)
                .setDuration(SCROLL_BUTTON_FADE_DURATION_MS)
                .start()
        }
    }

    private fun hideScrollToChartButton(animate: Boolean = true) {
        with(binding.scrollToChartButton) {
            animate().cancel()
            if (!animate) {
                alpha = 0f
                visibility = View.GONE
                return
            }
            animate()
                .alpha(0f)
                .setDuration(SCROLL_BUTTON_FADE_DURATION_MS)
                .withEndAction { visibility = View.GONE }
                .start()
        }
    }

    companion object {
        private const val REPORT_SCROLL_DURATION_MS = 650L
        private const val SCROLL_BUTTON_FADE_DURATION_MS = 1000L
    }
}
