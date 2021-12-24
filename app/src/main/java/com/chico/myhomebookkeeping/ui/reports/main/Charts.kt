package com.chico.myhomebookkeeping.ui.reports.main

import com.chico.myhomebookkeeping.helpers.Message
import com.chico.myhomebookkeeping.obj.GenerateColor
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*


class Charts {
    private lateinit var colorsList: MutableList<Int>
    fun showPieChart(chartView: PieChart, map: Map<String, Double>) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = "отчёт"

        //initializing data

//        val typeAmountMap: MutableMap<String, Int> = HashMap()
//        val typeAmountMap: Map<String, Double> = map

        //initializing colors for the entries
        colorsList = getColors(map.size)

        //input data and fit data into pie chart entry
        for (type in map.keys) {
            pieEntries.add(PieEntry(map[type]!!.toFloat(), type))
        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
//        val pieDataSet = PieDataSet(pieEntries, "")
        //setting text size of the value
        pieDataSet.valueTextSize = 12f
        //providing color list for coloring different entries
        pieDataSet.colors = colorsList
        val legend = chartView.legend
        legend.isEnabled = false
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
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
//        colors.add(Color.parseColor("#304567"))
//        colors.add(Color.parseColor("#309967"))
//        colors.add(Color.parseColor("#476567"))
//        colors.add(Color.parseColor("#890567"))
//        colors.add(Color.parseColor("#a35567"))
//        colors.add(Color.parseColor("#ff5f67"))
//        colors.add(Color.parseColor("#3ca567"))
        return colors
    }

    fun showHorizontalBarChart(chartView: HorizontalBarChart, map: Map<String, Double>) {

        val valuesList: List<Double> = map.values.toList()
        val keysList: List<String> = map.keys.toList()

        val barWith: Float = 5f
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
//    }


}