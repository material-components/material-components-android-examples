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

package com.materialstudies.reply.util

import android.view.animation.Interpolator
import androidx.core.view.animation.PathInterpolatorCompat

/**
 * A custom Interpolator that dramatically slows as an animation end, avoiding sudden motion
 * stops for large moving components (ie. shared element cards).
 */
class FastOutUltraSlowIn : Interpolator {

    private val pathInterpolator = PathInterpolatorCompat.create(
        0.185F,
        0.770F,
        0.135F,
        0.975F
    )

    override fun getInterpolation(fraction: Float): Float {
        return pathInterpolator.getInterpolation(fraction)
    }
}