/*
 * Copyright (c) 2021. Patryk Goworowski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.patrykgoworowski.vico.core.entry.composed

import pl.patrykgoworowski.vico.core.entry.ChartModelProducer
import pl.patrykgoworowski.vico.core.entry.ChartEntryModel

/**
 * Combines two [ChartEntryModel] instances into a [ComposedChartEntryModelProducer].
 */
public operator fun <Model : ChartEntryModel> ChartModelProducer<Model>.plus(
    other: ChartModelProducer<Model>
): ComposedChartEntryModelProducer<Model> =
    ComposedChartEntryModelProducer(listOf(this, other))

/**
 * Combines this [ComposedChartEntryModelProducer] and a [ChartModelProducer]
 * into a single [ComposedChartEntryModelProducer].
 */
public operator fun <Model : ChartEntryModel> ComposedChartEntryModelProducer<Model>.plus(
    other: ChartModelProducer<Model>
): ComposedChartEntryModelProducer<Model> =
    ComposedChartEntryModelProducer(chartModelProducers + other)

/**
 * Combines two [ComposedChartEntryModelProducer] instances into a single one.
 */
public operator fun <Model : ChartEntryModel> ComposedChartEntryModelProducer<Model>.plus(
    other: ComposedChartEntryModelProducer<Model>
): ComposedChartEntryModelProducer<Model> =
    ComposedChartEntryModelProducer(chartModelProducers + other.chartModelProducers)
