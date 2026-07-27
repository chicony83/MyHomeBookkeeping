package com.chico.myhomebookkeeping.ui.reports.main

import android.content.Context
import com.chico.myhomebookkeeping.R
import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.GenerateColor
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class Charts {
    private lateinit var colorsList: MutableList<Int>

    fun showPieChart(context: Context, chartView: PieChart, map: Map<String, Double>) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = context.getString(R.string.chart_label_report)

        colorsList = getColors(map.size)

        for (type in map.keys) {
            pieEntries.add(PieEntry(map[type]!!.toFloat(), type))
        }

        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize = 12f
        pieDataSet.colors = colorsList
        val legend = chartView.legend
        legend.isEnabled = false
        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        chartView.setData(pieData)
        chartView.animateX(1000)
        chartView.invalidate()
    }

    private fun getColors(size: Int): ArrayList<Int> {
        val colors: ArrayList<Int> = ArrayList()

        for (i in 0..size) {
            colors.add(GenerateColor.generateRandomColor())
        }
        return colors
    }

    fun showHorizontalBarChart(chartView: HorizontalBarChart, map: Map<String, Double>) {
        val valuesList: List<Double> = map.values.toList()
        val spaceForBars: Float = 2f
        val yVals: ArrayList<BarEntry> = mutableListOf<BarEntry>() as ArrayList<BarEntry>

        for (i in valuesList.indices) {
            Message.log("line List $i = ${valuesList[i]}")
            yVals.add(BarEntry(i * spaceForBars, valuesList[i].toFloat()))
        }

        val set1 = BarDataSet(yVals, "data Set 1")
        set1.colors = colorsList
        val legend = chartView.legend
        legend.isEnabled = false

        val data = BarData(set1)
        chartView.animateY(1000)
        chartView.data = data

        chartView.invalidate()
    }
}
