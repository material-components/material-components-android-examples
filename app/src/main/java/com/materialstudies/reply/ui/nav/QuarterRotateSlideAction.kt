package com.materialstudies.reply.ui.nav

import android.view.View
import com.materialstudies.reply.util.MathUtils

class QuarterRotateSlideAction(
    private val chevronView: View
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        chevronView.rotation = MathUtils.normalize(
            slideOffset,
            0F,
            1F,
            0F,
            180F
        )
    }
}