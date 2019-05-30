package com.materialstudies.reply.ui.nav

import android.view.View

/**
 * An action to be performed when a bottom sheet's slide offset is changed.
 *
 * 'slideOffset' will always be a float between the values of 0.0 and 1.0, 0.0 being hidden
 * and 1.0 being expanded.
 */
//class OnSlideAction(val action: (sheet: View, slideOffset: Float) -> Unit)

interface OnSlideAction {
    fun onSlide(sheet: View, slideOffset: Float)
}
