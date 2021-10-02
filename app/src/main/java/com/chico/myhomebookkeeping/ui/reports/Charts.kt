package com.chico.myhomebookkeeping.ui.reports

import android.graphics.Color
import com.chico.myhomebookkeeping.helpers.Message
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*


class Charts {

    fun showPieChart(chartView: PieChart, map: Map<String, Double>) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = "отчёт"

        //initializing data

//        val typeAmountMap: MutableMap<String, Int> = HashMap()
        val typeAmountMap = map

        //initializing colors for the entries
        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#304567"))
        colors.add(Color.parseColor("#309967"))
        colors.add(Color.parseColor("#476567"))
        colors.add(Color.parseColor("#890567"))
        colors.add(Color.parseColor("#a35567"))
        colors.add(Color.parseColor("#ff5f67"))
        colors.add(Color.parseColor("#3ca567"))

        //input data and fit data into pie chart entry
        for (type in typeAmountMap.keys) {
            pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat(), type))
        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
//        val pieDataSet = PieDataSet(pieEntries, "")
        //setting text size of the value
        pieDataSet.valueTextSize = 12f
        //providing color list for coloring different entries
        pieDataSet.colors = colors
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)
        chartView.setData(pieData)
        chartView.invalidate()
    }

    fun showHorizontalBarChart(chartView: HorizontalBarChart, map: Map<String, Double>) {
        val sortedMap:MutableMap<String,Double> = LinkedHashMap()
        map.entries.sortedBy { it.value }.forEach{sortedMap[it.key] = it.value}

        val valuesList: List<Double> = sortedMap.values.toList()
        val keysList:List<String> = sortedMap.keys.toList()

        val barWith: Float = 5f
        val spaceForBars: Float = 2f
        val yVals: ArrayList<BarEntry> = mutableListOf<BarEntry>() as ArrayList<BarEntry>

        for (i in valuesList.indices) {
            Message.log("line List $i = ${valuesList[i]}")
            yVals.add(BarEntry(i*spaceForBars,valuesList[i].toFloat()))
        }

        val set1 = BarDataSet(yVals, "data Set 1")
        val data = BarData(set1)
//        data.barWidth = barWith

        chartView.data = data
        chartView.invalidate()
    }
//    }


}