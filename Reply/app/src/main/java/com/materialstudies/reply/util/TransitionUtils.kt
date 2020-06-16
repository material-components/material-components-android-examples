/*
 * Copyright 2020 Google LLC
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

import androidx.transition.Transition
import com.google.android.material.transition.FadeProvider
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.ScaleProvider

private const val ELEVATION_START_SCALE = 0.92F

/**
 * Create an elevation scale transition that, e.g., can be used in conjunction with a container
 * transform to give the effect that the outgoing screen is receding or advancing along the z-axis.
 */
fun createMaterialElevationScale(entering: Boolean): Transition {
    return MaterialSharedAxis(MaterialSharedAxis.Z, entering).apply {
        (primaryAnimatorProvider as ScaleProvider).incomingStartScale = ELEVATION_START_SCALE
        secondaryAnimatorProvider = FadeProvider()
    }
}
