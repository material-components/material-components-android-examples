package com.materialstudies.reply.util

import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.widget.TextView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.MaterialShapeDrawable

fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

/**
 * Helper method to get the MaterialShapeDrawable background of MaterialCardView. This should be
 * fixed in a future update to Material Components.
 *
 * TODO(https://issuetracker.google.com/issues/135604742) Remove once fix lands.
 */
val MaterialCardView.backgroundShapeDrawable: MaterialShapeDrawable
    get() = (this.background as InsetDrawable).drawable as MaterialShapeDrawable

/**
 * Helper method to get the MaterialShapeDrawable foreground of MaterialCardView. This should be
 * fixed in a future update to Material Components.
 *
 * TODO(https://issuetracker.google.com/issues/135604742) Remove once fix lands.
 */
val MaterialCardView.foregroundShapeDrawable: MaterialShapeDrawable
    get() = (((this.foreground as InsetDrawable).drawable as LayerDrawable)
        .getDrawable(0) as RippleDrawable)
        .getDrawable(0) as MaterialShapeDrawable

