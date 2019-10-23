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

package com.materialstudies.owl.util.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import androidx.transition.Visibility

private const val RECEDED_SCALE = 0.9f
private const val RECEDED_ALPHA = 0.9f

/**
 * A [androidx.transition.Transition] which animates the scale X & Y and alpha of a given view.
 */
class Recede : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        view.apply {
            alpha = RECEDED_ALPHA
            scaleX = RECEDED_SCALE
            scaleY = RECEDED_SCALE
        }
        return ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat(
                View.SCALE_X,
                RECEDED_SCALE,
                1f
            ),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, RECEDED_SCALE, 1f),
            PropertyValuesHolder.ofFloat(View.ALPHA, RECEDED_ALPHA, 1f)
        )
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ) = ObjectAnimator.ofPropertyValuesHolder(
        view,
        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, RECEDED_SCALE),
        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, RECEDED_SCALE),
        PropertyValuesHolder.ofFloat(View.ALPHA, 1f, RECEDED_ALPHA)
    )
}
