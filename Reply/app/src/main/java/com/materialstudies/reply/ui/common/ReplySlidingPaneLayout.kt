package com.materialstudies.reply.ui.common

import android.content.Context
import android.util.AttributeSet
import androidx.slidingpanelayout.widget.SlidingPaneLayout

/** A [SlidingPaneLayout] that reports changes in the slidable state of its sliding pane.*/
class ReplySlidingPaneLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SlidingPaneLayout(context, attrs, defStyle) {

    interface SlidingPaneStateListener {
        fun onCanSlideChanged(canSlide: Boolean)
    }

    private var stateListener: SlidingPaneStateListener? = null

    fun setSlidingPaneStateListener(listener: SlidingPaneStateListener?) {
        stateListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        stateListener?.onCanSlideChanged(isSlideable)
    }
}