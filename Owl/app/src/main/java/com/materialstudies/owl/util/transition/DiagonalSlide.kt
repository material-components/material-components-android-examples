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

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.transition.TransitionValues
import androidx.transition.Visibility

/**
 * A [androidx.transition.Transition] which animates visibility changes by sliding in/out diagonally
 * from the bottom right edge.
 */
class DiagonalSlide : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ) = animate(view, sceneRoot, true)

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ) = animate(view, sceneRoot, false)

    private fun animate(
        view: View,
        sceneRoot: ViewGroup,
        appear: Boolean
    ): ObjectAnimator {
        val tX = view.translationX
        val goneTX = (sceneRoot.width - view.left).toFloat()
        view.translationX = goneTX
        val tY = view.translationY
        val goneTY = (sceneRoot.height - view.top).toFloat()
        view.translationY = goneTY
        return if (appear) {
            ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, goneTX, tX),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, goneTY, tY)
            )
        } else {
            ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat(View.TRANSLATION_X, tX, goneTX),
                PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, tY, goneTY)
            ).apply {
                doOnEnd {
                    view.visibility = View.GONE
                }
            }
        }
    }
}
