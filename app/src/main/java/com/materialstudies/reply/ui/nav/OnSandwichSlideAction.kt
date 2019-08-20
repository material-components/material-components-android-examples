/*
 * Copyright 2019 Google LLC
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

package com.materialstudies.reply.ui.nav

import android.view.View
import androidx.annotation.FloatRange
import com.materialstudies.reply.util.normalize

/**
 * Callback used to run actions when the offset/progress of [BottomNavDrawerFragment]'s account
 * picker sandwich animation changes.
 */
interface OnSandwichSlideAction {

    /**
     * Called when the sandwich animation is running, either opening or closing the account picker.
     * [slideOffset] is a value between 0 and 1. 0 represents the closed, default state with the
     * account picker not visible. 1 represents the open state with the account picker visible.
     */
    fun onSlide(
        @FloatRange(
            from = 0.0,
            fromInclusive = true,
            to = 1.0,
            toInclusive = true
        ) slideOffset: Float
    )
}

/**
 * Rotate the given [view] counter-clockwise by 180 degrees.
 */
class HalfCounterClockwiseRotateSlideAction(
    private val view: View
) : OnSandwichSlideAction {
    override fun onSlide(slideOffset: Float) {
        view.rotation = slideOffset.normalize(
            0F,
            1F,
            180F,
            0F
        )
    }
}