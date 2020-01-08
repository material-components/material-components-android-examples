package com.materialstudies.owl.ui.lessons

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 *  A [BottomSheetBehavior] that helps to restrict touchable area depends on translationX
 */
class LessonBottomSheetBehavior<T : View>(context: Context, attrs: AttributeSet?) :
    BottomSheetBehavior<T>(context, attrs) {

    override fun onTouchEvent(parent: CoordinatorLayout, child: T, event: MotionEvent): Boolean {
        if (event.x < child.translationX) {
            return false
        }
        return super.onTouchEvent(parent, child, event)
    }
}