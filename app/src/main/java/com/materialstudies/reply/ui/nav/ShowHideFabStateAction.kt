package com.materialstudies.reply.ui.nav

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ShowHideFabStateAction(
    private val fab: FloatingActionButton
) : OnStateChangedAction {

    override fun onStateChanged(sheet: View, newState: Int) {
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            fab.show()
        } else {
            fab.hide()
        }
    }
}