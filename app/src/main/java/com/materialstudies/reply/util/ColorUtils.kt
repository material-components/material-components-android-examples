package com.materialstudies.reply.util

import android.content.Context
import android.util.TypedValue

/**
 * Common color helper functions.
 */
object ColorUtils {

    fun getColorFromAttr(context: Context, attr: Int): Int {
        val tv = TypedValue()
        context.theme.resolveAttribute(attr, tv, true)
        return tv.data
    }

}