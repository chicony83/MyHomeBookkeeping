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
import com.chico.myhomebookkeeping.`interface`.OnItemChecked
import com.chico.myhomebookkeeping.databinding.FragmentReportsBinding
import com.chico.myhomebookkeeping.enums.StatesReportsRecycler
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.helpers.NavControlHelper
import com.chico.myhomebookkeeping.helpers.UiColors
import com.chico.myhomebookkeeping.helpers.UiHelper
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.chico.myhomebookkeeping.utils.launchUi
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import kotlinx.coroutines.runBlocking

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
    private val uiColors = UiColors()

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

        with(reportsViewModel) {
            getMap().observe(viewLifecycleOwner, { map ->
                map?.let { it1 ->
                    val sortedMap: MutableMap<String, Double> = LinkedHashMap()
                    it1.entries.sortedBy { it.value }.forEach { sortedMap[it.key] = it.value }
                    charts.showPieChart(chartView = pieChartView, sortedMap)
                    charts.showHorizontalBarChart(horizontalLineChartView, sortedMap)
                }
            })
            itemsListForRecycler.observe(viewLifecycleOwner, {
                binding.recyclerView.adapter = ReportsAdapter(it, object : OnItemChecked {
                    override fun onChecked(id: Int) {
                        Message.log("checked Item = $id")
                        launchUi {
                            with(reportsViewModel) {
                                itemChecked(id)
                                updateReports()

                            }
                        }
                    }

                    override fun onUnChecked(id: Int) {
                        Message.log("un Checked Item = $id")
                        launchUi {
                            with(reportsViewModel) {
                                itemUnchecked(id)
                                updateReports()

                            }
                        }
                    }
                })
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        pieChartView = binding.pieChart
        horizontalLineChartView = binding.horizontalBarChart
//        horizontalLineChartView.setDrawGridBackground(false)
        with(binding) {
            selectCashAccountButton.setOnClickListener {
                showUiElements()
                with(reportsViewModel) {
                    setRecyclerState(StatesReportsRecycler.ShowCashAccounts.name)
                    postCashAccountsList()
                }
            }
            selectCategoryButton.setOnClickListener {
                showUiElements()
                with(reportsViewModel) {
                    setRecyclerState(StatesReportsRecycler.ShowCategories.name)
                    postCategoriesList()
                }
            }
            selectCurrencyButton.setOnClickListener {
                showUiElements()
                with(reportsViewModel) {
                    setRecyclerState(StatesReportsRecycler.ShowCurrencies.name)
                    postCurrenciesList()
                }
            }
            selectTimePeriodButton.setOnClickListener {
                navControlHelper.moveToSelectTimePeriod()
            }
            hideRecyclerButton.setOnClickListener {
                reportsViewModel.setRecyclerState(StatesReportsRecycler.None.name)
                hideUiElements()
            }
        }

        uiColors.setButtonsColor(getButtonsListForColorButtons())

    }

    private fun getButtonsListForColorButtons() = listOf(
        binding.hideRecyclerButton
    )

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}