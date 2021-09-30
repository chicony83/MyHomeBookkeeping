package com.chico.myhomebookkeeping.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.databinding.FragmentReportsBinding
import com.chico.myhomebookkeeping.utils.hideKeyboard
import com.github.mikephil.charting.charts.PieChart

class ReportsFragment : Fragment() {

    private lateinit var reportsViewModel: ReportsViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChartView: PieChart
    private val charts = Charts()
    private val converter = Converter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        reportsViewModel =
            ViewModelProvider(this).get(ReportsViewModel::class.java)

        with(reportsViewModel) {
            getMap().observe(viewLifecycleOwner, Observer { map ->
                map?.let {
                    charts.showPieChart(pieChartView = pieChartView,it)
                }
            })
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

        pieChartView = binding.pieChart

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