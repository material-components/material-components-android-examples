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

import android.annotation.SuppressLint
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.materialstudies.reply.util.normalize
import kotlin.math.max

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
    private var halfExpandedSlideOffset = Float.MAX_VALUE

    override fun onSlide(sheet: View, slideOffset: Float) {
        if (halfExpandedSlideOffset == Float.MAX_VALUE)
            calculateInitialHalfExpandedSlideOffset(sheet)

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

    /**
     * Calculate the onSlideOffset which will be given when the bottom sheet is in the
     * [BottomSheetBehavior.STATE_HALF_EXPANDED] state.
     *
     * Recording the correct slide offset for the half expanded state happens in [onStateChanged].
     * Since the first time the sheet is opened, we haven't yet received a call to [onStateChanged],
     * this method is used to calculate the initial value manually so we can smoothly normalize
     * slideOffset values received between -1 and 1.
     *
     * See:
     * [BottomSheetBehavior.calculateCollapsedOffset]
     * [BottomSheetBehavior.calculateHalfExpandedOffset]
     * [BottomSheetBehavior.dispatchOnSlide]
     */
    @SuppressLint("PrivateResource")
    private fun calculateInitialHalfExpandedSlideOffset(sheet: View) {
        val parent = sheet.parent as CoordinatorLayout
        val behavior = BottomSheetBehavior.from(sheet)

        val halfExpandedOffset = parent.height * (1 - behavior.halfExpandedRatio)
        val peekHeightMin = parent.resources.getDimensionPixelSize(
            R.dimen.design_bottom_sheet_peek_height_min
        )
        val peek = max(peekHeightMin, parent.height - parent.width * 9 / 16)
        val collapsedOffset = max(
            parent.height - peek,
            max(0, parent.height - sheet.height)
        )
        halfExpandedSlideOffset =
            (collapsedOffset - halfExpandedOffset) / (parent.height - collapsedOffset)
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