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

package com.materialstudies.owl.ui.onboarding

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.toRectF
import androidx.core.graphics.withScale
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * A [Drawable] that responds to `state_activated' by
 * - Drawing a tint overlay
 * - Rounding it's top left corner radius
 * - Overlaying another drawable showing the selection
 */
private const val ANIM_DURATION = 300L
class TopicThumbnailDrawable(
    @ColorInt selectedTint: Int,
    @Px private val selectedTopLeftCornerRadius: Int,
    private val selectedDrawable: Drawable
) : Drawable() {

    var bitmap: Bitmap? = null
        set(value) {
            if (value != null) {
                thumbPaint.shader = BitmapShader(value, CLAMP, CLAMP)
            }
            progress = 0f
        }
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tintPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = selectedTint
        style = FILL
    }
    private val tintAlpha = Color.alpha(selectedTint)
    private val path = Path()
    private var progress = 0f
        set(value) {
            val clamped = value.coerceIn(0f, 1f)
            if (clamped != field) {
                field = clamped
                update()
            }
        }
    private var progressAnim: ValueAnimator? = null
    private val interp = FastOutSlowInInterpolator()
    private var pivotX = 0f
    private var pivotY = 0f

    private fun update() {
        path.run {
            if (!bounds.isEmpty) {
                reset()
                val topLeftRadius = selectedTopLeftCornerRadius * progress
                addRoundRect(
                    bounds.toRectF(),
                    floatArrayOf(
                        topLeftRadius, topLeftRadius, 0f, 0f, 0f, 0f, 0f, 0f
                    ),
                    Path.Direction.CW
                )
            }
        }
        tintPaint.alpha = (progress * tintAlpha).toInt()
        callback?.invalidateDrawable(this)
    }

    override fun onStateChange(state: IntArray?): Boolean {
        val initialProgress = progress
        val newProgress = if (state?.contains(android.R.attr.state_activated) == true) {
            1f
        } else {
            0f
        }
        progressAnim?.cancel()
        progressAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener {
                progress = animatedValue as Float
            }
            // scale the duration if was already running
            duration = (Math.abs(newProgress - initialProgress) * ANIM_DURATION).toLong()
            interpolator = interp
        }
        progressAnim?.start()
        return newProgress == initialProgress
    }

    override fun isStateful() = true

    override fun onBoundsChange(bounds: Rect?) {
        if (bounds == null) return
        update()
        val dLeft = (bounds.right - selectedDrawable.intrinsicWidth) / 2
        val dTop = (bounds.bottom - selectedDrawable.intrinsicHeight) / 2
        selectedDrawable.setBounds(
            dLeft,
            dTop,
            dLeft + selectedDrawable.intrinsicWidth,
            dTop + selectedDrawable.intrinsicHeight
        )
        // scale about the 'corner' of the check mark
        pivotX = (bounds.width() / 2f) - (3f / 24f * selectedDrawable.intrinsicWidth)
        pivotY = (bounds.height() / 2f) + (5f / 24f * selectedDrawable.intrinsicWidth)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawPath(path, thumbPaint)
        if (progress > 0f) {
            canvas.drawPath(path, tintPaint)
            canvas.withScale(progress, progress, pivotX, pivotY) {
                selectedDrawable.draw(canvas)
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        thumbPaint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(filter: ColorFilter?) {
        thumbPaint.colorFilter = filter
    }
}
