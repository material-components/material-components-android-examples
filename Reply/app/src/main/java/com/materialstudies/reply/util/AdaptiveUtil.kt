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

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


object AdaptiveUtil {

    // TODO: Comment to describe the each size
    // TODO: WindowMetrics
    // TODO: View changes inside fragment/activity classes
    enum class ScreenSize {
        SMALL, MEDIUM, LARGE, XLARGE
    }

    // These sizes are based off of the Figma mocks
    private const val SMALL_SCREEN_SIZE = 700
    private const val MEDIUM_SCREEN_SIZE = 840
    private const val LARGE_SCREEN_SIZE = 1024

    private val _screenSizeState = MutableStateFlow(ScreenSize.SMALL)
    val screenSizeState: StateFlow<ScreenSize> = _screenSizeState

    fun updateScreenSize(screenWidth: Int) {
        _screenSizeState.value = when {
            // Bottom Navigation
            screenWidth < SMALL_SCREEN_SIZE -> {
                ScreenSize.SMALL
            }
            // One Pane Layout with Navigation Rail
            screenWidth in SMALL_SCREEN_SIZE until MEDIUM_SCREEN_SIZE -> {
                ScreenSize.MEDIUM
            }
            // Two Pane Layout with Navigation Rail
            screenWidth in MEDIUM_SCREEN_SIZE until LARGE_SCREEN_SIZE -> {
                ScreenSize.LARGE
            }
            // Two Pane Layout with Navigation Drawer
            else -> {
                ScreenSize.XLARGE
            }
        }
    }
}