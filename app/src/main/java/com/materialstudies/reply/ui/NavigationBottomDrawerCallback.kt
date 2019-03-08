package com.materialstudies.reply.ui

import android.animation.ObjectAnimator
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A BottomSheetCallback to encapsulate coordination between [BottomNavigationDrawer]'s open
 * and closed states and the showing/hiding of the FAB and rotation of the BottomAppBar's chevron.
 */
class NavigationBottomDrawerCallback(
        private val fab: FloatingActionButton,
        private val navIcon: AppCompatImageView
) : BottomSheetBehavior.BottomSheetCallback() {

    private var showing = true

    override fun onSlide(sheet: View, slideOffset: Float) { }

    override fun onStateChanged(sheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            show()
        } else if (showing) {
            hide()
        }
    }

    /**
     * Show the FAB and rotate the BottomAppBar's chevron to the original position.
     */
    private fun show() {
        synchronized(showing) {
            showing = true
            fab.show()
            ObjectAnimator.ofFloat(navIcon, "rotation", 0F).apply {
                interpolator = FastOutSlowInInterpolator()
                duration = 100
            }.start()
        }

    }

    /**
     * Hide the FAB and rotate the BottomAppBar's chevron by 180 degrees.
     */
    private fun hide() {
        synchronized(showing) {
            showing = false
            fab.hide()
            ObjectAnimator.ofFloat(navIcon, "rotation", 180F).apply {
                interpolator = FastOutSlowInInterpolator()
                duration = 100
            }.start()
        }
    }


}