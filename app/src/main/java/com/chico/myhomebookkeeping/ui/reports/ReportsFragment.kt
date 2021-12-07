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
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.interfaces.reports.dialogs.OnSelectedCategoriesCallBack
import com.chico.myhomebookkeeping.ui.reports.dialogs.category.ReportsSelectCategoriesDialog
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchIo
import com.chico.myhomebookkeeping.utils.launchUi
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart

class ReportsFragment : Fragment() {

    private lateinit var reportsViewModel: ReportsViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChartView: PieChart
    private lateinit var horizontalLineChartView: HorizontalBarChart
    private val charts = Charts()
    private lateinit var control: NavController
    private lateinit var navControlHelper: NavControlHelper
    private val uiHelper = UiHelper()

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

        launchIo {
            binding.recyclerView.setItemViewCacheSize(reportsViewModel.getNumbersOfCategories())
        }

        with(reportsViewModel) {
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
//            listItemsOfCategoriesForRecycler.observe(viewLifecycleOwner, {
//                binding.recyclerView.adapter = ReportsAdapter(it, object : OnItemCheckedCallBack {
//                    override fun onChecked(id: Int) {
//                        launchUi {
//                            with(reportsViewModel) {
//                                itemChecked(id)
//                                updateReports(true)
//                            }
//                        }
//                    }
//                    override fun onUnChecked(id: Int) {
//                        launchUi {
//                            with(reportsViewModel) {
//                                itemUnchecked(id)
//                                updateReports(true)
//                            }
//                        }
//                    }
//                })
//            })
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        pieChartView = binding.pieChart
        horizontalLineChartView = binding.horizontalBarChart
        with(binding) {
//            selectCashAccountButton.setOnClickListener {
//                with(reportsViewModel) {
//                    setRecyclerState(StatesReportsRecycler.ShowCashAccounts.name)
//                    postCashAccountsList()
//                }
//            }
            selectCategoryButton.setOnClickListener {
                launchUi {
                    val dialog =
                        ReportsSelectCategoriesDialog(reportsViewModel.getListOfCategories(),
                            object : OnSelectedCategoriesCallBack {
                                override fun select(categoriesSet: Set<Int>) {
                                    Message.log(" categories set in reports = ${categoriesSet.joinToString()}")
                                }

                            }
                        )


                    dialog.show(childFragmentManager, getString(R.string.tag_show_dialog))
                }

//                uiHelper.showUiElement(binding.recyclerView)
//                with(reportsViewModel) {
//                    setRecyclerState(StatesReportsRecycler.ShowCategories.name)
//                    postCategoriesList()
//                }
            }
//            selectCurrencyButton.setOnClickListener {
//                with(reportsViewModel) {
//                    setRecyclerState(StatesReportsRecycler.ShowCurrencies.name)
//                    postCurrenciesList()
//                }
//            }
            selectTimePeriodButton.setOnClickListener {
                navControlHelper.moveToSelectTimePeriod()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        launchUi {
            reportsViewModel.updateReports(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}