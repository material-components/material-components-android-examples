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
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import android.util.Property
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.res.use
import androidx.core.graphics.transform
import androidx.core.view.forEach
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable
import com.materialstudies.owl.R
import com.materialstudies.owl.util.CornerRounding
import com.materialstudies.owl.util.descendantBackgroundColor
import com.materialstudies.owl.util.drawRoundedRect
import com.materialstudies.owl.util.drawToBitmap
import com.materialstudies.owl.util.findAncestorById
import com.materialstudies.owl.util.lerp
import com.materialstudies.owl.util.lerpArgb
import com.materialstudies.owl.util.toCornerRounding
import com.materialstudies.owl.util.transition.MaterialContainerTransitionDrawable.PROGRESS
import kotlin.math.roundToInt

@Px
private const val BITMAP_PADDING_BOTTOM = 1
private const val PROP_BOUNDS = "materialContainerTransition:bounds"
private const val PROP_BITMAP = "materialContainerTransition:bitmap"
private const val PROP_SHAPE_APPEARANCE = "materialContainerTransition:shapeAppearance"
private const val PROP_CONTAINER_COLOR = "materialContainerTransition:containerColor"
private val TRANSITION_PROPS = arrayOf(
    PROP_BOUNDS,
    PROP_BITMAP,
    PROP_SHAPE_APPEARANCE,
    PROP_CONTAINER_COLOR
)

/**
 * A [Transition] which implements the Material Container pattern from
 * https://medium.com/google-design/motion-design-doesnt-have-to-be-hard-33089196e6c2
 */
class MaterialContainerTransition(
    @IdRes private val drawInId: Int = android.R.id.content,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossfadeStartProgress: Float = 0.3f,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossfadeEndProgress: Float = 0.8f
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

        val startBounds = startValues.values[PROP_BOUNDS] as RectF
        val endBounds = endValues.values[PROP_BOUNDS] as RectF
        val dr = MaterialContainerTransitionDrawable(
            startValues.values[PROP_BITMAP] as Bitmap,
            startBounds,
            (startValues.values[PROP_SHAPE_APPEARANCE] as ShapeAppearanceModel?).toCornerRounding(
                startBounds
            ),
            startValues.values[PROP_CONTAINER_COLOR] as Int,
            crossfadeStartProgress,
            endValues.values[PROP_BITMAP] as Bitmap,
            endBounds,
            (endValues.values[PROP_SHAPE_APPEARANCE] as ShapeAppearanceModel?).toCornerRounding(
                endBounds
            ),
            endValues.values[PROP_CONTAINER_COLOR] as Int,
            crossfadeEndProgress
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

    @SuppressLint("Recycle")
    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view

        if (view.isLaidOut || view.width != 0 || view.height != 0) {
            // Capture location in screen co-ordinates
            val loc = IntArray(2)
            view.getLocationOnScreen(loc)
            val left = loc[0].toFloat() - view.translationX
            val top = loc[1].toFloat() - view.translationY
            transitionValues.values[PROP_BOUNDS] = RectF(
                left,
                top,
                left + view.width,
                top + view.height
            )
            // Clear any foreground e.g. a ripple in progress
            view.jumpDrawablesToCurrentState()
            // Add padding to the bitmap capture so that when we draw it later with a
            // [BitmapShader] with CLAMP [TileMode], the transparency is repeated.
            transitionValues.values[PROP_BITMAP] = view.drawToBitmap(BITMAP_PADDING_BOTTOM)

            // Store the view's shape appearance; either from a [Shapeable] view; else checking
            // the `transitionShapeAppearance` theme attr.
            if (view is Shapeable) {
                transitionValues.values[PROP_SHAPE_APPEARANCE] = view.shapeAppearanceModel
            } else {
                view.context.obtainStyledAttributes(intArrayOf(R.attr.transitionShapeAppearance))
                    .use {
                        val shapeAppId = it.getResourceId(0, -1)
                        if (shapeAppId != -1) {
                            transitionValues.values[PROP_SHAPE_APPEARANCE] = ShapeAppearanceModel
                                .builder(
                                    view.context,
                                    shapeAppId,
                                    0
                                ).build()
                        }
                    }
            }
            transitionValues.values[PROP_CONTAINER_COLOR] = view.descendantBackgroundColor()
        }
    }
}

/**
 * A [Drawable] which cross-fades between `startImage` and `endImage`, scaling between `startBounds`
 * and `endBounds`.
 *
 * Additionally it draws a scrim over non-shared elements and a background to the container.
 */
private const val scrimAlpha = 102 // 40% opacity
private const val containerShadow = 0x1a000000
private const val containerNoShadow = 0x00000000
private class MaterialContainerTransitionDrawable(
    private val startImage: Bitmap,
    private val startBounds: RectF,
    private val startRadii: CornerRounding,
    @ColorInt private val containerStartColor: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossfadeStartProgress: Float,
    private val endImage: Bitmap,
    private val endBounds: RectF,
    private val endRadii: CornerRounding,
    @ColorInt private val containerEndColor: Int,
    @FloatRange(from = 0.0, fromInclusive = true, to = 1.0, toInclusive = true)
    private val crossfadeEndProgress: Float,
    @ColorInt scrimColor: Int = 0xff000000.toInt()
) : Drawable() {

    private val imagePaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val startImageShader = BitmapShader(startImage, CLAMP, CLAMP)
    private val endImageShader = BitmapShader(endImage, CLAMP, CLAMP)
    private val scrimPaint = Paint().apply {
        style = Paint.Style.FILL
        color = scrimColor
    }
    private val containerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = containerStartColor
    }
    private val currentBounds = RectF(startBounds)
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
                // i.e. scale images to the current width, later draw call will mask them
                // to `currentBounds`
                startImageShader.transform {
                    val scale = currentBounds.width() / startImage.width.toFloat()
                    setScale(scale, scale)
                    postTranslate(currentBounds.left, currentBounds.top)
                }
                endImageShader.transform {
                    val scale = currentBounds.width() / endImage.width
                    setScale(scale, scale)
                    postTranslate(currentBounds.left, currentBounds.top)
                }
                invalidateSelf()
            }
        }

    override fun draw(canvas: Canvas) {
        // Fade in/out a scrim over non-shared elements
        scrimPaint.alpha = if (entering) {
            lerp(0, scrimAlpha, progress)
        } else {
            lerp(scrimAlpha, 0, progress)
        }
        if (scrimPaint.alpha > 0) canvas.drawRect(bounds, scrimPaint)

        // Animate corner radii over the crossfade range & use this when drawing the
        // container background & images
        val cornerRadii = lerp(
            startRadii,
            endRadii,
            crossfadeStartProgress,
            crossfadeEndProgress,
            progress
        )

        // Draw a background for the container, useful when the container size exceeds the image
        // size which it can in large start/end size changes. Also fade in/out a shadow.
        // TODO make radius configurable / density dependent
        containerPaint.setShadowLayer(
            12f, 0f, 12f,
            if (entering) {
                lerpArgb(containerNoShadow, containerShadow, progress)
            } else {
                lerpArgb(containerShadow, containerNoShadow, progress)
            }
        )
        containerPaint.color = lerpArgb(
            containerStartColor,
            containerEndColor,
            crossfadeStartProgress,
            crossfadeEndProgress,
            progress
        )
        canvas.drawRoundedRect(
            currentBounds,
            cornerRadii,
            containerPaint
        )

        // Cross-fade images of the start/end states over the crossfade range
        imagePaint.alpha = lerp(
            255,
            0,
            crossfadeStartProgress,
            crossfadeEndProgress,
            progress
        )
        if (imagePaint.alpha > 0) {
            imagePaint.shader = startImageShader
            canvas.drawRoundedRect(
                currentBounds,
                cornerRadii,
                imagePaint
            )
        }
        imagePaint.alpha = lerp(
            0,
            255,
            crossfadeStartProgress,
            crossfadeEndProgress,
            progress
        )
        if (imagePaint.alpha > 0) {
            imagePaint.shader = endImageShader
            canvas.drawRoundedRect(
                currentBounds,
                cornerRadii,
                imagePaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        imagePaint.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        imagePaint.colorFilter = colorFilter
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
