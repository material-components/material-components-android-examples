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