package com.chico.myhomebookkeeping.ui.reports.main

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.chico.myhomebookkeeping.R
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class Charts {
    fun showDonutChart(
        context: Context,
        chartView: PieChart,
        items: List<ReportCategoryItem>,
        centerAmountText: String,
        centerLabelText: String,
        onSliceSelected: (List<Int>) -> Unit
    ) {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = context.getString(R.string.chart_label_report)
        val visibleItems = items.withIndex().filter { it.value.percentage >= MIN_VISIBLE_PERCENT }
        val smallItems = items.withIndex().filter { it.value.percentage < MIN_VISIBLE_PERCENT }

        // Keep the donut readable: tiny categories are grouped in the chart,
        // while the breakdown list still shows every category separately.
        for ((index, item) in visibleItems) {
            pieEntries.add(
                PieEntry(
                    item.amount.toFloat(),
                    item.displayName,
                    DonutSliceTarget(listOf(index), showLabel = true)
                )
            )
        }
        if (smallItems.isNotEmpty()) {
            pieEntries.add(
                PieEntry(
                    smallItems.sumOf { it.value.amount }.toFloat(),
                    "",
                    DonutSliceTarget(smallItems.map { it.index }, showLabel = false)
                )
            )
        }

        val pieDataSet = PieDataSet(pieEntries, label)
        pieDataSet.valueTextSize = 10f
        val colors = visibleItems.map { it.value.color } + listOfNotNull(
            ContextCompat.getColor(context, R.color.categoryDivider).takeIf { smallItems.isNotEmpty() }
        )
        pieDataSet.colors = colors
        pieDataSet.setValueTextColors(colors.map { readableTextColor(it) })
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueFormatter = SmallSliceValueFormatter()

        val legend = chartView.legend
        legend.isEnabled = false
        chartView.description.isEnabled = false
        chartView.isDrawHoleEnabled = true
        chartView.holeRadius = 58f
        chartView.setHoleColor(ContextCompat.getColor(context, R.color.categoryScreenBackground))
        chartView.transparentCircleRadius = 58f
        chartView.setTransparentCircleAlpha(0)
        chartView.setDrawEntryLabels(false)
        chartView.setUsePercentValues(true)
        chartView.centerText = createCenterText(context, centerAmountText, centerLabelText)
        chartView.setCenterTextSize(if (centerAmountText.length > 14) 19f else 20f)
        chartView.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val target = (e as? PieEntry)?.data as? DonutSliceTarget ?: return
                onSliceSelected(target.breakdownIndices)
            }

            override fun onNothingSelected() = Unit
        })

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(true)
        chartView.setData(pieData)
        chartView.animateX(1000)
        chartView.invalidate()
    }

    private fun createCenterText(
        context: Context,
        centerAmountText: String,
        centerLabelText: String
    ): SpannableString {
        val centerText = "$centerAmountText\n$centerLabelText"
        val spannable = SpannableString(centerText)
        val labelStart = centerAmountText.length + 1
        spannable.setSpan(
            RelativeSizeSpan(1f),
            0,
            centerAmountText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.52f),
            labelStart,
            centerText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_color)),
            0,
            centerAmountText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.text_second_color)),
            labelStart,
            centerText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannable
    }

    private class SmallSliceValueFormatter : ValueFormatter() {
        private val percentFormatter = DecimalFormat("0.#", DecimalFormatSymbols(Locale.getDefault()))

        override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
            val target = pieEntry?.data as? DonutSliceTarget
            // The grouped tiny-slice sector is intentionally unlabeled.
            if (target?.showLabel == false || value < MIN_VISIBLE_PERCENT) return ""
            return "${percentFormatter.format(value)} %"
        }
    }

    private data class DonutSliceTarget(
        val breakdownIndices: List<Int>,
        val showLabel: Boolean
    )

    private fun readableTextColor(backgroundColor: Int): Int {
        val red = Color.red(backgroundColor)
        val green = Color.green(backgroundColor)
        val blue = Color.blue(backgroundColor)
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        return if (luminance > 0.62) Color.rgb(23, 35, 38) else Color.WHITE
    }

    companion object {
        private const val MIN_VISIBLE_PERCENT = 4f
    }
}
