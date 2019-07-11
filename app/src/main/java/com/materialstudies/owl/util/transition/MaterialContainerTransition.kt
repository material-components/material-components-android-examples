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

package com.materialstudies.owl.util.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Property
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.drawToBitmap
import androidx.core.view.forEach
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.materialstudies.owl.util.descendantBackgroundColor
import com.materialstudies.owl.util.findAncestorById
import com.materialstudies.owl.util.lerp
import com.materialstudies.owl.util.lerpArgb
import com.materialstudies.owl.util.transition.MaterialContainerTransitionDrawable.PROGRESS
import kotlin.math.min
import kotlin.math.roundToInt

private const val PROP_BOUNDS = "materialContainerTransition:bounds"
private const val PROP_BITMAP = "materialContainerTransition:bitmap"
private val TRANSITION_PROPS = arrayOf(PROP_BOUNDS, PROP_BITMAP)

/**
 * A [Transition] which implements the Material Container pattern from
 * https://medium.com/google-design/motion-design-doesnt-have-to-be-hard-33089196e6c2
 */
class MaterialContainerTransition(
    @IdRes private val drawInId: Int = android.R.id.content
) : Transition() {

    override fun getTransitionProperties() = TRANSITION_PROPS

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null || endValues.view !is ViewGroup) {
            return null
        }

        val view = endValues.view as ViewGroup
        // Draw in the given ancestor view's overlay. This allows us to draw beyond the bounds of
        // the shared element view, which we might need to do to animate hierarchy changes e.g.
        // from a full screen view to a grid item.
        // TODO handle if drawIn is not at [0, 0] as bounds in screen co-ords
        val drawIn = view.findAncestorById(drawInId)

        val dr = MaterialContainerTransitionDrawable(
            startValues.values[PROP_BITMAP] as Bitmap,
            startValues.values[PROP_BOUNDS] as Rect,
            endValues.values[PROP_BITMAP] as Bitmap,
            endValues.values[PROP_BOUNDS] as Rect,
            view.descendantBackgroundColor()
        )

        return ObjectAnimator.ofFloat(dr, PROGRESS, 0f, 1f).apply {
            doOnStart {
                dr.setBounds(0, 0, drawIn.width, drawIn.height)
                drawIn.overlay.add(dr)
                // Hide the view during the transition
                view.forEach {
                    it.alpha = 0f
                }
            }
            doOnEnd {
                view.forEach {
                    it.alpha = 1f
                }
                drawIn.overlay.remove(dr)
            }
        }
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view

        if (view.isLaidOut || view.width != 0 || view.height != 0) {
            // Capture location in screen co-ordinates
            val loc = IntArray(2)
            view.getLocationOnScreen(loc)
            val left = loc[0] - view.translationX.roundToInt()
            val top = loc[1] - view.translationY.roundToInt()
            transitionValues.values[PROP_BOUNDS] = Rect(
                left,
                top,
                left + view.width,
                top + view.height
            )
            view.foreground = null
            transitionValues.values[PROP_BITMAP] = view.drawToBitmap()
        }
    }
}

/**
 * A [Drawable] which cross-fades between `startImage` and `endImage`, scaling between `startBounds`
 * and `endBounds`.
 *
 * Additionally it draws a scrim over non-shared elements and a background to the container.
 */
private class MaterialContainerTransitionDrawable(
    private val startImage: Bitmap,
    private val startBounds: Rect,
    private val endImage: Bitmap,
    private val endBounds: Rect,
    @ColorInt containerColor: Int = 0xffffffff.toInt(),
    @ColorInt scrimColor: Int = 0xff000000.toInt()
) : Drawable() {

    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val scrimPaint = Paint().apply {
        style = Paint.Style.FILL
        color = scrimColor
    }
    private val containerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = containerColor
    }
    private val currentBounds = Rect()
    private val startSrcBounds = Rect(0, 0, startImage.width, startImage.height)
    private val endSrcBounds = Rect(0, 0, endImage.width, endImage.height)
    private val startDstBounds = Rect(startBounds)
    private val endDstBounds = Rect(endBounds)
    private val entering = endBounds.height() > startBounds.height()

    // Transition is driven by setting this property [0–1]
    private var progress = 0f
        set(value) {
            if (value != field) {
                field = value
                currentBounds.set(
                    lerp(startBounds.left, endBounds.left, value),
                    lerp(startBounds.top, endBounds.top, value),
                    lerp(startBounds.right, endBounds.right, value),
                    lerp(startBounds.bottom, endBounds.bottom, value)
                )

                // “Elements are pinned to the top and masked inside the container”
                val aspect = currentBounds.height().toFloat() / currentBounds.width().toFloat()
                startSrcBounds.bottom = min(
                    startImage.height,
                    (startImage.width * aspect).roundToInt()
                )
                endSrcBounds.bottom = min(
                    endImage.height,
                    (endImage.width * aspect).roundToInt()
                )
                val startAspect = startImage.height.toFloat() / startImage.width.toFloat()
                startDstBounds.set(
                    currentBounds.left,
                    currentBounds.top,
                    currentBounds.right,
                    currentBounds.top + min(
                        currentBounds.height(),
                        (startAspect * currentBounds.width()).roundToInt()
                    )
                )
                val endAspect = endImage.height.toFloat() / endImage.width.toFloat()
                endDstBounds.set(
                    currentBounds.left,
                    currentBounds.top,
                    currentBounds.right,
                    currentBounds.top + min(
                        currentBounds.height(),
                        (endAspect * currentBounds.width()).roundToInt()
                    )
                )
                invalidateSelf()
            }
        }

    override fun draw(canvas: Canvas) {
        // Fade in/out 0–40% opaque scrim over non-shared elements
        // TODO make opacity configurable
        scrimPaint.alpha = if (entering) {
            lerp(0, 102, progress)
        } else {
            lerp(102, 0, progress)
        }
        if (scrimPaint.alpha > 0) canvas.drawRect(bounds, scrimPaint)

        // Draw a background for the container, useful when the container size exceeds the image
        // size which it can in large start/end size changes. Also fade in/out a shadow.
        // TODO make this configurable / density dependent
        containerPaint.setShadowLayer(
            12f, 0f, 12f,
            if (entering) {
                lerpArgb(0, 0x1a000000, progress)
            } else {
                lerpArgb(0x1a000000, 0, progress)
            }
        )
        canvas.drawRect(currentBounds, containerPaint)

        // Cross-fade images of the start/end states over 0.3–0.8 of `progress`
        paint.alpha = lerp(255, 0, 0.3f, 0.8f, progress)
        if (paint.alpha > 0) canvas.drawBitmap(startImage, startSrcBounds, startDstBounds, paint)
        paint.alpha = lerp(0, 255, 0.3f, 0.8f, progress)
        if (paint.alpha > 0) canvas.drawBitmap(endImage, endSrcBounds, endDstBounds, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    object PROGRESS : Property<MaterialContainerTransitionDrawable, Float>(
        Float::class.java,
        "progress"
    ) {
        override fun get(drawable: MaterialContainerTransitionDrawable) = drawable.progress

        override fun set(drawable: MaterialContainerTransitionDrawable, value: Float) {
            drawable.progress = value
        }
    }
}
