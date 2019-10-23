/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialstudies.reply.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.graphics.applyCanvas
import androidx.core.view.ViewCompat
import androidx.core.view.forEach
import com.materialstudies.reply.R

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

/**
 * Search this view and any children for a [ColorDrawable] `background` and return it's `color`,
 * else return `colorSurface`.
 */
@ColorInt
fun View.descendantBackgroundColor(): Int {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = descendantBackgroundColorOrNull()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return context.themeColor(R.attr.colorSurface)
}

@ColorInt
private fun View.descendantBackgroundColorOrNull(): Int? {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    }  else if (this is ViewGroup) {
        forEach {
            val childBg = backgroundColor()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return null
}

/**
 * Check if this [View]'s `background` is a [ColorDrawable] and if so, return it's `color`,
 * otherwise `null`.
 */
@ColorInt
fun View.backgroundColor(): Int? {
    val bg = background
    if (bg is ColorDrawable) {
        return bg.color
    } else {
        val tint = backgroundTintList?.defaultColor
        if (tint != null && tint != -1) return tint
    }
    return null
}

/**
 * Walk up from a [View] looking for an ancestor with a given `id`.
 */
fun View.findAncestorById(@IdRes ancestorId: Int): View {
    return when {
        id == ancestorId -> this
        parent is View -> (parent as View).findAncestorById(ancestorId)
        else -> throw IllegalArgumentException("$ancestorId not a valid ancestor")
    }
}

/**
 * A copy of the KTX method, adding the ability to add extra padding the bottom of the [Bitmap];
 * useful when it will be used in a [android.graphics.BitmapShader] with
 * a [android.graphics.Shader.TileMode.CLAMP][CLAMP tile mode].
 */
fun View.drawToBitmap(@Px extraPaddingBottom: Int = 0): Bitmap {
    check(ViewCompat.isLaidOut(this)) {
        "View needs to be laid out before calling drawToBitmap()"
    }
    return Bitmap.createBitmap(width, height + extraPaddingBottom, ARGB_8888).applyCanvas {
        translate(-scrollX.toFloat(), -scrollY.toFloat())
        draw(this)
    }
}
