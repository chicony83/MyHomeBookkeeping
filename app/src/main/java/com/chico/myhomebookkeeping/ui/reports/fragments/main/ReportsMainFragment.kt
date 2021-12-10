package com.chico.myhomebookkeeping.ui.reports.fragments.main

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
import com.chico.myhomebookkeeping.interfaces.reports.dialogs.OnSelectedCategoriesCallBack
import com.chico.myhomebookkeeping.ui.reports.fragments.categories.ReportsSelectCategoriesFragment
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart

class ReportsMainFragment : Fragment() {

    private lateinit var reportsMainViewModel: ReportsMainViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChartView: PieChart
    private lateinit var horizontalLineChartView: HorizontalBarChart
    private val charts = Charts()
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper

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

        launchIo {
            binding.recyclerView.setItemViewCacheSize(reportsMainViewModel.getNumbersOfCategories())
        }

        with(reportsMainViewModel) {
            buttonTextOfTimePeriod.observe(viewLifecycleOwner, {
                binding.selectTimePeriodButton.text = it
            })
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        pieChartView = binding.pieChart
        horizontalLineChartView = binding.horizontalBarChart
        with(binding) {
            selectCategoryButton.setOnClickListener {
                navControlHelper.moveToSelectedFragment(R.id.nav_reports_categories)
//                launchUi {
//                    val dialog =
//                        ReportsSelectCategoriesFragment(
//                            reportsMainViewModel.getListOfCategories(),
//                            object : OnSelectedCategoriesCallBack {
//                                override fun select(categoriesSet: Set<Int>) {
//                                    val result: Boolean =
//                                        reportsMainViewModel.updateSelectedCategories(categoriesSet)
////                                    Message.log(" categories set transferred in reports = ${categoriesSet.joinToString()}")
//                                    launchUi {
//                                        reportsMainViewModel.updateReports(result)
//                                    }
//                                }
//                            }
//                        )
////                    dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
//                }
            }
            selectTimePeriodButton.setOnClickListener {
                navControlHelper.moveToSelectTimePeriod()
            }
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
}