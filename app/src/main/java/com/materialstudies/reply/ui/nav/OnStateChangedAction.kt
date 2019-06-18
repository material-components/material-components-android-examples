package com.materialstudies.reply.ui.nav

import android.view.View

/**
 * An action to be performed when a bottom sheet's state is changed.
 */
interface OnStateChangedAction {
    fun onStateChanged(sheet: View, newState: Int)
}
