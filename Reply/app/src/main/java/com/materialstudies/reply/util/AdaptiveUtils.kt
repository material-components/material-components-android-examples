/*
 * Copyright 2021 Google LLC
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
package com.materialstudies.reply.util

import android.content.Context
import android.util.DisplayMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AdaptiveUtils {

    enum class ScreenSize {
        SMALL, MEDIUM, LARGE, XLARGE
    }

    private const val SMALL_SCREEN_SIZE_UPPER_THRESHOLD = 700
    private const val MEDIUM_SCREEN_SIZE_UPPER_THRESHOLD = 840
    private const val LARGE_SCREEN_SIZE_UPPER_THRESHOLD = 1024

    private val _screenSizeState = MutableStateFlow(ScreenSize.SMALL)
    val screenSizeState: StateFlow<ScreenSize> = _screenSizeState

    fun updateScreenSize(context: Context) {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        _screenSizeState.value = when {
            screenWidth < SMALL_SCREEN_SIZE_UPPER_THRESHOLD -> ScreenSize.SMALL
            screenWidth in SMALL_SCREEN_SIZE_UPPER_THRESHOLD until MEDIUM_SCREEN_SIZE_UPPER_THRESHOLD -> ScreenSize.MEDIUM
            screenWidth in MEDIUM_SCREEN_SIZE_UPPER_THRESHOLD until LARGE_SCREEN_SIZE_UPPER_THRESHOLD -> ScreenSize.LARGE
            else -> ScreenSize.XLARGE
        }
    }
}