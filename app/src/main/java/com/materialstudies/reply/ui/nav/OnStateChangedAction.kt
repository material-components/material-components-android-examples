package com.materialstudies.reply.ui.nav

import android.view.View

/**
 * An action to be performed when a bottom sheet's state is changed.
 */
//class OnStateChangedAction(val action: (sheet: View, newState: Int) -> Unit)

interface OnStateChangedAction {
    fun onStateChanged(sheet: View, newState: Int)
}
