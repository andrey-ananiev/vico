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

package com.patrykandpatrick.vico.core

import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entriesOf
import org.junit.Test
import kotlin.test.assertEquals

public class ChartEntryModelProducerTest {

    private val minX = 0f
    private val maxX = 3f
    private val minY = 1f
    private val maxY = 5f

    private val entries1 = entriesOf(1 to minY, 2 to 2, maxX to 3)
    private val entries2 = entriesOf(minX to minY, 1 to maxY, maxX to minY)
    private val entries3 = entriesOf(minX to 2, 1 to 4, maxX to 3)

    @Test
    public fun `Test Min Max calculations`() {
        val entryList = ChartEntryModelProducer(entries1, entries2, entries3).requireModel()
        assertEquals(minX, entryList.minX)
        assertEquals(maxX, entryList.maxX)
        assertEquals(minY, entryList.minY)
        assertEquals(maxY, entryList.maxY)
        assertEquals(10f, entryList.stackedPositiveY)
    }
}
