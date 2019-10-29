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

package com.materialstudies.reply.util.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.transition.TransitionValues
import android.util.Property
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import androidx.core.view.forEach
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable
import com.materialstudies.reply.R
import com.materialstudies.reply.util.CornerRounding
import com.materialstudies.reply.util.descendantBackgroundColor
import com.materialstudies.reply.util.drawToBitmap
import com.materialstudies.reply.util.findAncestorById
import com.materialstudies.reply.util.lerp
import com.materialstudies.reply.util.toCornerRounding
import com.materialstudies.reply.util.toFloatArray
import com.materialstudies.reply.util.transition.MaterialContainerTransitionDrawable.PROGRESS
import com.materialstudies.reply.util.withAlpha

@Px private const val BITMAP_PADDING_BOTTOM = 1
private const val PROP_BOUNDS = "materialContainerTransition:bounds"
private const val PROP_SHAPE_APPEARANCE = "materialContainerTransition:shapeAppearance"
private const val PROP_BACKGROUND_BOUNDS = "materialContainerTransition:backgroundBounds"
private const val PROP_BACKGROUND_BITMAP = "materialContainerTransition:backgroundBitmap"
private val TRANSITION_PROPS = arrayOf(PROP_BOUNDS, PROP_SHAPE_APPEARANCE)

/**
 * A [Transition] which implements the Material Container pattern from
 * https://medium.com/google-design/motion-design-doesnt-have-to-be-hard-33089196e6c2
 */
class MaterialContainerTransition(
    @IdRes private val drawInId: Int = android.R.id.content,
    private val correctForZOrdering: Boolean = false
) : Transition() {

    private var fromView: View? = null
    private var toView: View? = null

    override fun getTransitionProperties() = TRANSITION_PROPS

    fun setSharedElementViews(from: View, to: View) {
        fromView = from
        toView = to
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        if (fromView != null) transitionValues.view = fromView
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        if (toView != null) transitionValues.view = toView
        captureValues(transitionValues)
    }

    @SuppressLint("Recycle")
    private fun captureValues(
        transitionValues: TransitionValues
    ) {
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

            // Clear any ripples or effects caused by clicks, long presses, etc.
            view.jumpDrawablesToCurrentState()

            // Store the view's shape appearance; either from a [Shapeable] view; else checking
            // the `transitionShapeAppearance` theme attr.
            val shapeAppearance: ShapeAppearanceModel? = when (view) {
                is Shapeable -> view.shapeAppearanceModel
                else -> {
                    val ta = view.context.obtainStyledAttributes(
                        intArrayOf(R.attr.transitionShapeAppearance)
                    )
                    val shapeAppId = ta.getResourceId(0, -1)
                    ta.recycle()
                    if (shapeAppId != -1) {
                        ShapeAppearanceModel.builder(
                            view.context,
                            shapeAppId,
                            0
                        ).build()
                    } else {
                        null
                    }
                }
            }
            transitionValues.values[PROP_SHAPE_APPEARANCE] = shapeAppearance

            // Take a bitmap snapshot of the entire layout hierarchy of the start and end scenes.
            // This is used to draw the start layout under all in-progress animations during
            // MaterialContainerTransitionDrawable.draw(). This is used only while Fragment
            // transactions are improperly ordered (related to aosp/987385) and should be removed
            // in favor of a recede transition once fixed.

            // Exit early if this transition has not opted in for z order correction faking.
            if (!correctForZOrdering) return

            val root = view.rootView
            val rootLoc = IntArray(2)
            root.getLocationOnScreen(rootLoc)
            val rootLeft = rootLoc[0].toFloat() - root.translationX
            val rootTop = rootLoc[1].toFloat() - root.translationY
            transitionValues.values[PROP_BACKGROUND_BOUNDS] = RectF(
                rootLeft,
                rootTop,
                rootLeft + root.width,
                rootTop + root.height
            )
            transitionValues.values[PROP_BACKGROUND_BITMAP] = root.drawToBitmap(
                BITMAP_PADDING_BOTTOM
            )
        }
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (startValues == null || endValues == null || endValues.view !is ViewGroup) {
            return null
        }

        val startView = startValues.view
        val endView = endValues.view as ViewGroup
        // Draw in the given ancestor view's overlay. This allows us to draw beyond the bounds of
        // the shared element view, which we might need to do to animate hierarchy changes e.g.
        // from a full screen view to a grid item.
        val drawIn = endView.findAncestorById(drawInId) as ViewGroup

        val startBounds = startValues.values[PROP_BOUNDS] as RectF
        val endBounds = endValues.values[PROP_BOUNDS] as RectF

        // Account for location of drawIn view, which could be offset by elements
        // like the status bar
        val loc = IntArray(2)
        drawIn.getLocationOnScreen(loc)
        val drawInLeft = loc[0] - drawIn.translationX
        val drawInTop = loc[1] - drawIn.translationY
        startBounds.offset(-drawInLeft, -drawInTop)
        endBounds.offset(-drawInLeft, -drawInTop)

        val dr = MaterialContainerTransitionDrawable(
            startView,
            startValues.values[PROP_BACKGROUND_BITMAP] as? Bitmap,
            startValues.values[PROP_BACKGROUND_BOUNDS] as? RectF,
            startBounds,
            (startValues.values[PROP_SHAPE_APPEARANCE] as ShapeAppearanceModel?)
                .toCornerRounding(startBounds),
            endView,
            endValues.values[PROP_BACKGROUND_BITMAP] as? Bitmap,
            endValues.values[PROP_BACKGROUND_BOUNDS] as? RectF,
            endBounds,
            (endValues.values[PROP_SHAPE_APPEARANCE] as ShapeAppearanceModel?)
                .toCornerRounding(endBounds),
            startView.descendantBackgroundColor(),
            endView.descendantBackgroundColor()
        )

        return ObjectAnimator.ofFloat(dr, PROGRESS, 0f, 1f).apply {
            doOnStart {
                dr.setBounds(0, 0, drawIn.width, drawIn.height)
                drawIn.overlay.add(dr)
                // Hide the view during the transition
                drawIn.forEach { it.alpha = 0f }
            }
            doOnEnd {
                drawIn.forEach { it.alpha = 1f }
                drawIn.overlay.remove(dr)
            }
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
    private val startView: View,
    private val startBackgroundImage: Bitmap?,
    private val startBackgroundBounds: RectF?,
    private val startBounds: RectF,
    private val startRadii: CornerRounding,
    private val endView: View,
    private val endBackgroundImage: Bitmap?,
    private val endBackgroundBounds: RectF?,
    private val endBounds: RectF,
    private val endRadii: CornerRounding,
    @ColorInt val containerStartColor: Int = 0xffffffff.toInt(),
    @ColorInt val containerEndColor: Int = 0xffffffff.toInt()
) : Drawable() {

    private val imagePaint = Paint(Paint.FILTER_BITMAP_FLAG)

    private val startBackgroundShader: BitmapShader?
        get() = if (startBackgroundImage != null) {
            BitmapShader(startBackgroundImage, CLAMP, CLAMP)
        } else {
            null
        }

    private val endBackgroundShader: BitmapShader?
        get() = if (endBackgroundImage != null) {
            BitmapShader(endBackgroundImage, CLAMP, CLAMP)
        } else {
            null
        }
    private val startContainerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = containerStartColor
    }
    private val endContainerPaint = Paint().apply {
        style = Paint.Style.FILL
        color = containerEndColor
    }
    private val currentBounds = RectF(startBounds)
    private val currentPath = Path()
    private val entering = endBounds.height() > startBounds.height()

    // Values which define the fraction during which animations start and end for the outgoing and
    // incoming bitmaps based on a total progress of 0.0-1.0
    // The fading out of the outgoing element
    private val alphaOutStartPoint = 0.0F
    private val alphaOutEndPoint = 0.3F
    //The fading in of the incoming element
    private val alphaInStartPoint = 0.3F
    private val alphaInEndPoint = 1.0F
    // The corner shape animation of the container
    private val shapeStartPoint = 0.0F
    private val shapeEndPoint = 0.8F


    // Transition is driven by setting this property [0–1]
    private var progress = 0f
        set(value) {
            if (value != field) {
                field = value
                // Update the container bounds
                currentBounds.set(
                    lerp(startBounds.left, endBounds.left, value),
                    lerp(startBounds.top, endBounds.top, value),
                    lerp(startBounds.right, endBounds.right, value),
                    lerp(startBounds.bottom, endBounds.bottom, value)
                )

                // Update the path that is going to be used to clip all items inside the
                // container that is transforming.
                val cornerRadii = lerp(
                    startRadii,
                    endRadii,
                    shapeStartPoint,
                    shapeEndPoint,
                    progress
                )
                currentPath.apply {
                    reset()
                    addRoundRect(
                        currentBounds,
                        cornerRadii.toFloatArray(),
                        Path.Direction.CW
                    )
                }

                invalidateSelf()
            }
        }


    init {
        // Avoid the state where draw is called before progress is set for the first time and
        // has the chance to lerp, scale and translate values necessary to draw the scene correctly.
        progress = 0.000001F
    }

    override fun draw(canvas: Canvas) {
        drawRecedingBackgroundBitmap(canvas)

        // Clip all of the following draw operations to the bounds of the current path (a path
        // created using the currentBounds, the transitioning host container, and the transitioning
        // corner radii). This will clip all subsequent operations to a rounded Rect.
        canvas.clipPath(currentPath)

        // Draw a background for the start view which is able to fill the container as it expands.
        // This is useful for transitions such as FABs->Full screen cards where the FAB's rounded
        // corners fail to fill the expanding corner radii of the container.
        canvas.drawRect(currentBounds, startContainerPaint)

        // Fade out the startView while pinning it to the top of currentBounds and scaling it to
        // fit the width of currentBounds.
        val startAlpha = lerp(1F, 0F, alphaOutStartPoint, alphaOutEndPoint, progress)
        // Translate to pin to top
        if (startAlpha > 0F) {
            canvas.withTranslation(currentBounds.left, currentBounds.top) {
                // Scale to match container width
                val scale = currentBounds.width() / startBounds.width()
                scale(scale, scale)
                // Fade out all views
                withAlpha(bounds, startAlpha) {
                    startView.draw(this)
                }
            }
        }

        // Draw a background which matches the end view's background and occludes the
        // startContainerPaint as it becomes opaque.
        endContainerPaint.alpha = lerp(0, 255, alphaInStartPoint, alphaInEndPoint, progress)
        canvas.drawRect(currentBounds, endContainerPaint)

        // Fade in the endView while pinning it to the top of currentBounds and scaling it to fit
        // the width of currentBounds.
        val endAlpha = lerp(0F, 1F, alphaInStartPoint, alphaInEndPoint, progress)
        if (endAlpha > 0F) {
            // Translate to pin to top
            canvas.withTranslation(currentBounds.left, currentBounds.top) {
                // Scale to match container width
                val scale = currentBounds.width() / endBounds.width()
                scale(scale, scale)
                // Fade in all views
                withAlpha(bounds, endAlpha) {
                    endView.draw(this)
                }
            }
        }
    }

    /**
     * Draw the bitmap of the root start or end screen (depending on if this container is entering
     * or exiting) to work around Transition animation ordering.
     *
     * By default Transition exiting animations are played above entering animations. In some cases,
     * such as cross-fading, this is the desired behavior. For container transitions (or slides),
     * the entering transition should be played on top of the exiting transition. To make up for
     * this behavior, [MaterialContainerTransition] captures the start and end bitmap of the root
     * start and end scene and draws them first, at the bottom of the
     * [MaterialContainerTransitionDrawable] and avoids using seperate enter and exit transitions
     * altogether.
     *
     * If z ordering of Transitions becomes configurable, this should be removed in favor of a
     * dedicated enter or exit transition.
     */
    private fun drawRecedingBackgroundBitmap(canvas: Canvas) {
        // Fake the background of the transition by manually drawing the background bitmap of the
        // start screen or end screen.
        if (entering && startBackgroundShader != null && startBackgroundBounds != null) {
            imagePaint.alpha = lerp(255, 0, 0F, 1F, progress)
            imagePaint.shader = startBackgroundShader

            val bgScale = lerp(1F, 0.9F, progress)
            canvas.withScale(
                bgScale,
                bgScale,
                bounds.width() / 2F,
                bounds.height() / 2F
            ) {
                canvas.drawRect(
                    startBackgroundBounds,
                    imagePaint
                )
            }
        } else if (!entering && endBackgroundShader != null && endBackgroundBounds != null) {
            imagePaint.alpha = lerp(0, 255, 0F, 1F, progress)
            imagePaint.shader = endBackgroundShader

            val bgScale = lerp(0.9F, 1F, progress)
            canvas.withScale(
                bgScale,
                bgScale,
                bounds.width() / 2F,
                bounds.height() / 2F
            ) {
                canvas.drawRect(
                    endBackgroundBounds,
                    imagePaint
                )
            }

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
