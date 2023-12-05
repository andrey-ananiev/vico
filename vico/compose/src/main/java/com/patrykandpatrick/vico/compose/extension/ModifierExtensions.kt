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

package com.patrykandpatrick.vico.compose.extension

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollState
import com.patrykandpatrick.vico.compose.gesture.OnZoom
import com.patrykandpatrick.vico.core.model.Point

internal fun Modifier.chartTouchEvent(
    setTouchPoint: ((Point?) -> Unit)?,
    isScrollEnabled: Boolean,
    scrollableState: ChartScrollState,
    onZoom: OnZoom?,
): Modifier = scrollable(
    state = scrollableState,
    orientation = Orientation.Horizontal,
    reverseDirection = true,
    enabled = isScrollEnabled,
)
    .then(
        if (setTouchPoint != null) {
            pointerInput(setTouchPoint) {
                awaitPointerEventScope {
                    var isDragStarted = false
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Move -> isDragStarted = true
                            PointerEventType.Scroll -> isDragStarted = true
                            PointerEventType.Release -> {
                                if (!isDragStarted) setTouchPoint(
                                    event.changes.first().position.point
                                )
                                isDragStarted = false
                            }
                        }
                    }
                }
            }
        } else {
            Modifier
        },
    )
    .then(
        if (!isScrollEnabled && setTouchPoint != null) {
            pointerInput(setTouchPoint) {
                detectHorizontalDragGestures(
                    onDragStart = {  },
                    onDragEnd = { },
                    onDragCancel = { },
                ) { _, _ -> }
            }
        } else {
            Modifier
        },
    )
    .then(
        if (isScrollEnabled && onZoom != null) {
            pointerInput(setTouchPoint, onZoom) {
                detectZoomGestures { centroid, zoom ->
                    setTouchPoint?.invoke(null)
                    onZoom(centroid, zoom)
                }
            }
        } else {
            Modifier
        },
    )

private val Offset.point: Point
    get() = Point(x, y)
