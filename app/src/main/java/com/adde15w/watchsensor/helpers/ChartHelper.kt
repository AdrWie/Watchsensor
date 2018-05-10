package com.adde15w.watchsensor.helpers

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class ChartHelper : OnChartValueSelectedListener {

    val mChart: LineChart

    constructor(chart: LineChart) {
        mChart = chart
        mChart.setOnChartValueSelectedListener(this)

        // no description text
        mChart.setNoDataText("You'll need to provide data for chart")

        // enable touch gestures
        mChart.setTouchEnabled(true)

        // enable scaling and dragging
        mChart.setDragEnabled(true)
        mChart.setScaleEnabled(true)
        mChart.setDrawGridBackground(false)

        // if disabled, scaling can be done on x- and y-axis separatel
        mChart.setPinchZoom(true)

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE)
        mChart.setBorderColor(Color.rgb(67, 164, 34))

        // set description text
        mChart.description.text = "Raspberry pi sensor telemetry"

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        mChart.setData(data)

        // get the legend (only possible after setting data)
        val legend = mChart.legend

        // modify the legend
        legend.form = Legend.LegendForm.LINE
        legend.typeface = Typeface.MONOSPACE
        legend.textColor = Color.rgb(0, 0, 0)
        legend.textSize = 12f

        val xAxis = mChart.xAxis
        xAxis.typeface = Typeface.MONOSPACE
        xAxis.textColor = Color.rgb(0, 0, 0)
        xAxis.setDrawGridLines(false)
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.isEnabled = true

        val yAxisLeft = mChart.axisLeft
        yAxisLeft.typeface = Typeface.MONOSPACE
        yAxisLeft.textColor = Color.rgb(0, 0, 0)
        yAxisLeft.setDrawGridLines(true)

        val yAxisRight = mChart.axisRight
        yAxisRight.isEnabled = false
    }

    fun addEntry(temperature : Float, humidity: Float) {

        val data = mChart.data
        if (data != null) {

            var tempSet = data.getDataSetByIndex(0)
            var humiSet = data.getDataSetByIndex(1)

            if (tempSet == null || humiSet == null) {
                tempSet = temperatureSet()
                humiSet = humiditySet()
                data.addDataSet(tempSet)
                data.addDataSet(humiSet)
            }

            data.addEntry(Entry(tempSet.entryCount.toFloat(), temperature), 0)
            data.addEntry(Entry(humiSet.entryCount.toFloat(), humidity),1)
            Log.w("Chart", tempSet.getEntryForIndex(tempSet.entryCount -1).toString() + " " +
                                    humiSet.getEntryForIndex(humiSet.entryCount - 1).toString())

            data.notifyDataChanged()

            // Let the chart now know it's data has changed
            mChart.notifyDataSetChanged()

            // Limit the number of visible entries
            mChart.setVisibleXRangeMaximum(8f)
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // Move to the latest entry
            mChart.moveViewTo(tempSet.entryCount.toFloat() - 1, data.yMax, YAxis.AxisDependency.LEFT)
            mChart.moveViewTo(humiSet.entryCount.toFloat() - 1, data.yMax, YAxis.AxisDependency.LEFT)
            // This automatically refreshes the chart (calls invalidate())
            // mChart.moveViewTo(data.getXvalCount() - 7, 55f, YAxis.AxisDependency.LEFT)
        }
    }

    private fun temperatureSet() : LineDataSet {
        val set = LineDataSet(null, "Temperature")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = Color.rgb(238, 117, 84)
        set.setCircleColor(Color.BLACK)
        set.lineWidth = 2f
        //set.circleRadius = 4f
        set.fillAlpha = 65
        set.fillColor = Color.rgb(67, 164, 34)
        set.highLightColor = Color.rgb(67, 164, 34)
        set.valueTextColor = Color.rgb(43, 53, 43)
        set.valueTextSize = 9f
        set.setDrawValues(true)

        return set
    }

    private fun humiditySet(): LineDataSet {
        val set = LineDataSet(null, "Humidity")
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.color = Color.rgb(124, 34, 249)
        set.setCircleColor(Color.BLACK)
        set.lineWidth = 2f
        //set.circleRadius = 4f
        set.fillAlpha = 65
        set.fillColor = Color.rgb(124, 34, 249)
        set.highLightColor = Color.rgb(124, 34, 249)
        set.valueTextColor = Color.rgb(43, 53, 43)
        set.valueTextSize = 9f
        set.setDrawValues(true)

        return set
    }

    override fun onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected")
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        Log.i("Entry selected", e.toString())
    }
}