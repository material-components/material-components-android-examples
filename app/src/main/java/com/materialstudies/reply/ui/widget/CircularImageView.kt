package com.materialstudies.reply.ui.widget

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView

/**
 * A simple ImageView extension to clip an ImageView to a circle.
 *
 * Clients of [CircularImageView] must set the width and height of the view to the same value.
 * Failing to do so will result in a pill shaped view.
 */
class CircularImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        clipToOutline = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Clip this view to a rounded Rect, creating a circle iff the view is a square.
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, w, h, h / 2F)
            }
        }
    }
}