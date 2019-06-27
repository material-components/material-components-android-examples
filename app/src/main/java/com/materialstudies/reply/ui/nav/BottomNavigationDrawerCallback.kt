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

/**
 * A BottomSheetCallback to encapsulate coordination between [BottomNavigationDrawer]'s open
 * and closed states and the showing/hiding of the FAB and rotation of the BottomAppBar's chevron.
 */
class BottomNavigationDrawerCallback : BottomSheetBehavior.BottomSheetCallback() {

    private val onSlideActions: MutableList<OnSlideAction> = mutableListOf()
    private val onStateChangedActions: MutableList<OnStateChangedAction> = mutableListOf()

    var lastSlideOffset = -1.0F

    override fun onSlide(sheet: View, slideOffset: Float) {
        println("BottomNavigationDrawerCallback - slideOffset = $slideOffset")
        lastSlideOffset = if (slideOffset < -1.0F || slideOffset.isNaN()) {
            0F
        } else {
            slideOffset
        }

        onSlideActions.forEach { it.onSlide(sheet, 1F - Math.abs(lastSlideOffset)) }
    }

    override fun onStateChanged(sheet: View, newState: Int) {
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