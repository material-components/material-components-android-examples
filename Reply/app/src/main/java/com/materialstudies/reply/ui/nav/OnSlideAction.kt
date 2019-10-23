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

package com.materialstudies.reply.ui.nav

import android.view.View
import android.widget.ImageView
import androidx.annotation.FloatRange
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.materialstudies.reply.R
import com.materialstudies.reply.util.normalize

/**
 * An action to be performed when a bottom sheet's slide offset is changed.
 */
interface OnSlideAction {
    /**
     * Called when the bottom sheet's [slideOffset] is changed. [slideOffset] will always be a
     * value between -1.0 and 1.0. -1.0 is equal to [BottomSheetBehavior.STATE_HIDDEN], 0.0
     * is equal to [BottomSheetBehavior.STATE_HALF_EXPANDED] and 1.0 is equal to
     * [BottomSheetBehavior.STATE_EXPANDED].
     */
    fun onSlide(
        sheet: View,
        @FloatRange(
            from = -1.0,
            fromInclusive = true,
            to = 1.0,
            toInclusive = true
        ) slideOffset: Float
    )
}

/**
 * A slide action which rotates a view counterclockwise by 180 degrees between the hidden state
 * and the half expanded state.
 */
class HalfClockwiseRotateSlideAction(
    private val view: View
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        view.rotation = slideOffset.normalize(
            -1F,
            0F,
            0F,
            180F
        )
    }
}

/**
 * A slide action which acts on the nav drawer between the half expanded state and
 * expanded state by:
 * - Scaling the user avatar
 * - Translating the foreground sheet
 * - Removing the foreground sheets rounded corners/edge treatment
 */
class ForegroundSheetTransformSlideAction(
    private val foregroundView: View,
    private val foregroundShapeDrawable: MaterialShapeDrawable,
    private val profileImageView: ImageView
) : OnSlideAction {

    private val foregroundMarginTop = foregroundView.marginTop
    private var systemTopInset: Int = 0
    private val foregroundZ = foregroundView.z
    private val profileImageOriginalZ = profileImageView.z

    private fun getPaddingTop(): Int {
        // This view's tag might not be set immediately as it needs to wait for insets to be
        // applied. Lazily evaluate to ensure we get a value, even if we've already started slide
        // changes.
        if (systemTopInset == 0) {
            systemTopInset = foregroundView.getTag(R.id.tag_system_window_inset_top) as? Int? ?: 0
        }
        return systemTopInset
    }

    override fun onSlide(sheet: View, slideOffset: Float) {
        val progress = slideOffset.normalize(0F, 0.25F, 1F, 0F)
        profileImageView.scaleX = progress
        profileImageView.scaleY = progress
        foregroundShapeDrawable.interpolation = progress

        foregroundView.translationY = -(1 - progress) * foregroundMarginTop
        val topPaddingProgress = slideOffset.normalize(0F, 0.9F, 0F, 1F)
        foregroundView.updatePadding(top = (getPaddingTop() * topPaddingProgress).toInt())

        // Modify the z ordering of the profileImage to make it easier to click when half-expanded.
        // Reset the z order if the sheet is expanding so the profile image slides under the
        // foreground sheet.
        if (slideOffset > 0 && foregroundZ <= profileImageView.z) {
            profileImageView.z = profileImageOriginalZ
        } else if (slideOffset <= 0 && foregroundZ >= profileImageView.z) {
            profileImageView.z = foregroundZ + 1
        }
    }
}

/**
 * Change the alpha of [view] when a bottom sheet is slid.
 *
 * @param reverse Setting reverse to true will cause the view's alpha to approach 0.0 as the sheet
 *  slides up. The default behavior, false, causes the view's alpha to approach 1.0 as the sheet
 *  slides up.
 */
class AlphaSlideAction(
    private val view: View,
    private val reverse: Boolean = false
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        val alpha = slideOffset.normalize(-1F, 0F, 0F, 1F)
        view.alpha = if (!reverse) alpha else 1F - alpha
    }
}
