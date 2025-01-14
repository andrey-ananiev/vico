/*
 * Copyright 2023 by Patryk Goworowski and Patrick Michalik.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.patrykandpatrick.vico.core.chart.values

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.AxisRenderer
import com.patrykandpatrick.vico.core.chart.Chart
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModel

/**
 * Manages the [ChartValues] used by a chart. There may be many [ChartValues], but all of them have the same
 * [ChartValues.minX] and [ChartValues.maxX] values. The following [ChartValues] instances exist in a chart:
 * - A main [ChartValues] instance, which is used by all components by default. It’s accessible with a null key and
 * always available in the drawing phase.
 * - A [ChartValues] instance for [AxisRenderer]s with [AxisPosition.Vertical.Start]. It’s available when the [Chart]
 * is configured to use [AxisPosition.Vertical.Start] as a key to update and retrieve its [ChartValues].
 * - A [ChartValues] instance for [AxisRenderer]s with [AxisPosition.Vertical.End]. It’s available when the [Chart]
 * is configured to use [AxisPosition.Vertical.End] as a key to update and retrieve its [ChartValues].
 *
 * @see ColumnChart.targetVerticalAxisPosition
 * @see LineChart.targetVerticalAxisPosition
 */
public class ChartValuesManager : ChartValuesProvider {

    internal val chartValues: MutableMap<AxisPosition.Vertical?, MutableChartValues> = mutableMapOf()

    override fun getChartValues(axisPosition: AxisPosition.Vertical?): ChartValues =
        chartValues[axisPosition]
            ?.takeIf { it.hasValuesSet }
            ?: chartValues.getOrPut(null) { MutableChartValues() }

    /**
     * Attempts to update the stored values to the provided values.
     * [MutableChartValues.minX] and [MutableChartValues.minY] can be updated to a lower value.
     * [MutableChartValues.maxX] and [MutableChartValues.maxY] can be updated to a higher value.
     * [MutableChartValues.chartEntryModel] and [MutableChartValues.xStep] are always updated.
     * If [axisPosition] is null, only the main [ChartValues] are updated. Otherwise, both the main [ChartValues]
     * and the [ChartValues] associated with the given [axisPosition] are updated.
     */
    public fun tryUpdate(
        minX: Float,
        maxX: Float,
        minY: Float,
        maxY: Float,
        xStep: Float,
        chartEntryModel: ChartEntryModel,
        axisPosition: AxisPosition.Vertical? = null,
    ) {
        chartValues.getOrPut(axisPosition) { MutableChartValues() }
            .tryUpdate(
                minX = minX,
                maxX = maxX,
                minY = minY,
                maxY = maxY,
                xStep = xStep,
                chartEntryModel = chartEntryModel,
            )

        if (axisPosition != null) {
            tryUpdate(minX, maxX, minY, maxY, xStep, chartEntryModel)
        } else {
            val mainValues = getChartValues(null)
            chartValues.forEach { (key, values) ->
                if (key != null) {
                    values.tryUpdate(minX = mainValues.minX, maxX = mainValues.maxX)
                }
            }
        }
    }

    /**
     * Resets the values stored in each of the [ChartValues] instances in the [chartValues] map.
     */
    public fun resetChartValues() {
        chartValues.values.forEach { it.reset() }
    }
}

/**
 * Creates and returns a [ChartValuesProvider] implementation with this [ChartValuesManager]’s [ChartValues]
 * instances.
 */
public fun ChartValuesManager.toChartValuesProvider(): ChartValuesProvider = object : ChartValuesProvider {
    val chartValues = this@toChartValuesProvider
        .chartValues
        .map { (axisPosition, chartValues) -> axisPosition to chartValues.toImmutable() }
        .toMap()

    override fun getChartValues(axisPosition: AxisPosition.Vertical?): ChartValues =
        chartValues[axisPosition] ?: chartValues.getValue(null)
}
