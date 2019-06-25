package com.materialstudies.reply.ui.nav

import android.view.View
import com.materialstudies.reply.util.lerp

class QuarterRotateSlideAction(
    private val chevronView: View
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        chevronView.rotation = lerp(
            0F,
            180F,
            slideOffset
        )
    }
}