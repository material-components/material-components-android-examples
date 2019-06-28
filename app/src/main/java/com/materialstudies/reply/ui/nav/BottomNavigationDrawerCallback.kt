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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.materialstudies.reply.util.normalize

/**
 * A [BottomSheetBehavior.BottomSheetCallback] which helps break apart clients who would like to
 * react to changed in either the bottom sheet's slide offset or state. Clients can dynamically
 * add or remove [OnSlideAction]s or [OnStateChangedAction]s which will be run when the
 * sheet's slideOffset or state are changed.
 *
 * This callback's behavior differs slightly in that the slideOffset passed to [OnSlideAction]s
 * in [onSlide] is corrected to guarantee that the offset 0.0 <i>always</i> be exactly at the
 * [BottomSheetBehavior.STATE_HALF_EXPANDED] state.
 */
class BottomNavigationDrawerCallback : BottomSheetBehavior.BottomSheetCallback() {

    private val onSlideActions: MutableList<OnSlideAction> = mutableListOf()
    private val onStateChangedActions: MutableList<OnStateChangedAction> = mutableListOf()

    private var lastSlideOffset = -1.0F
    private var halfExpandedSlideOffset = 0.0F

    override fun onSlide(sheet: View, slideOffset: Float) {
        lastSlideOffset = slideOffset

        // Correct for the fact that the slideOffset is not zero when half expanded
        val trueOffset = if (slideOffset <= halfExpandedSlideOffset) {
            slideOffset.normalize(-1F, halfExpandedSlideOffset, -1F, 0F)
        } else {
            slideOffset.normalize(halfExpandedSlideOffset, 1F, 0F, 1F)
        }

        onSlideActions.forEach { it.onSlide(sheet, trueOffset) }
    }

    override fun onStateChanged(sheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            halfExpandedSlideOffset = lastSlideOffset
            onSlide(sheet, lastSlideOffset)
        }

        onStateChangedActions.forEach { it.onStateChanged(sheet, newState) }
    }

    fun addOnSlideAction(action: OnSlideAction): Boolean {
        return onSlideActions.add(action)
    }

    fun removeOnSlideAction(action: OnSlideAction): Boolean {
        return onSlideActions.remove(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction): Boolean {
        return onStateChangedActions.add(action)
    }

    fun removeOnStateChangedAction(action: OnStateChangedAction): Boolean {
        return onStateChangedActions.remove(action)
    }
}