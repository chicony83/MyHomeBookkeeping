package com.chico.myhomebookkeeping.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.databinding.FragmentReportsBinding
import com.chico.myhomebookkeeping.utils.hideKeyboard
import org.eazegraph.lib.charts.BarChart
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.eazegraph.lib.models.BarModel







class ReportsFragment : Fragment() {

    private lateinit var reportsViewModel: ReportsViewModel
    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater,container,false)
        reportsViewModel =
            ViewModelProvider(this).get(ReportsViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.hideKeyboard()

//        val pieChart:PieChart = binding.pieChart
//
//        pieChart.addPieSlice(PieModel("Freetime", 15F, Color.parseColor("#FE6DA8")))
//        pieChart.addPieSlice(PieModel("Sleep", 25F, Color.parseColor("#56B7F1")))
//        pieChart.addPieSlice(PieModel("Work", 200F, Color.parseColor("#CDA67F")))
//        pieChart.addPieSlice(PieModel("Eating", 9F, Color.parseColor("#FED70E")))
//
//        pieChart.startAnimation()

//        val barChart:BarChart = binding.barChart
//
//        barChart.addBar(BarModel(2.3f, -0xedcbaa))
//        barChart.addBar(BarModel(2f, -0xcbcbaa))
//        barChart.addBar(BarModel(3.3f, -0xa9cbaa))
//        barChart.addBar(BarModel(1.1f, -0x78c0aa))
//        barChart.addBar(BarModel(2.7f, -0xa9480f))
//        barChart.addBar(BarModel(2f, -0xcbcbaa))
//        barChart.addBar(BarModel(0.4f, -0xe00b54))
//        barChart.addBar(BarModel(4f, -0xe45b1a))
//
//        barChart.startAnimation()
    }

    override fun onStart() {
        super.onStart()
        reportsViewModel.getListFullMoneyMoving()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}