package com.materialstudies.reply.util

import android.view.animation.Interpolator
import androidx.core.view.animation.PathInterpolatorCompat

/**
 * A custom Interpolator that dramatically slows as an animation end, avoiding sudden motion
 * stops for large moving components (ie. shared element cards).
 */
class FastOutUltraSlowIn : Interpolator {

    private val pathInterpolator = PathInterpolatorCompat.create(
        0.185F,
        0.770F,
        0.135F,
        0.975F
    )

    override fun getInterpolation(fraction: Float): Float {
        return pathInterpolator.getInterpolation(fraction)
    }
}