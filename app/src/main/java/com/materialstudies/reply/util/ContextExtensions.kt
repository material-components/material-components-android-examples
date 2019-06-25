package com.materialstudies.reply.util

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.res.use

@ColorInt
fun Context.getColorFromAttr(attr: Int): Int {
    return obtainStyledAttributes(intArrayOf(attr)).use {
        it.getColor(0, Color.BLACK)
    }
}

@StyleRes
fun Context.getStyleIdFromAttr(attr: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(attr, tv, true)
    return tv.data
}
