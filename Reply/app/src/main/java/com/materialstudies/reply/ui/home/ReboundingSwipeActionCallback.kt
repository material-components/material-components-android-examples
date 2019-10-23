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

package com.materialstudies.reply.ui.home

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.ln

// The intensity at which dX of a swipe should be decreased as we approach the swipe
// threshold.
private const val swipeReboundingElasticity = 0.8F

// The 'true' percentage of total swipe distance needed to consider a view as 'swiped'. This
// is used in favor of getSwipeThreshold since that has been overridden to return an impossible
// to reach value.
private const val trueSwipeThreshold = 0.4F

class ReboundingSwipeActionCallback : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.RIGHT
) {

    interface ReboundableViewHolder {

        /**
         * A view from the view holder which should be translated for swipe events.
         */
        val reboundableView: View

        /**
         * Called as a view holder is actively being swiped/rebounded.
         *
         * @param currentSwipePercentage The total percentage the view has been swiped.
         * @param swipeThreshold The percentage needed to consider a swipe as "rebounded"
         *  or "swiped"
         * @param currentTargetHasMetThresholdOnce Whether or not during a contiguous interaction
         *  with a single view holder, the swipe percentage has ever been greater than the swipe
         *  threshold.
         */
        fun onReboundOffsetChanged(
            currentSwipePercentage: Float,
            swipeThreshold: Float,
            currentTargetHasMetThresholdOnce: Boolean
        )

        /**
         * Called once all interaction (user initiated swiping and animations) has ended and this
         * view holder has been swiped passed the swipe threshold.
         */
        fun onRebounded()
    }

    // Track the view holder currently being swiped.
    private var currentTargetPosition: Int = -1
    private var currentTargetHasMetThresholdOnce: Boolean = false

    // Never dismiss.
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = Float.MAX_VALUE

    // Never dismiss.
    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return Float.MAX_VALUE
    }

    // Never dismiss.
    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return Float.MAX_VALUE
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // After animations to replace view have run, notify viewHolders that they have
        // been swiped. This waits for animations to finish so RecyclerView's DefaultItemAnimator
        // doesn't try to run updating animations while swipe animations are still running.
        if (currentTargetHasMetThresholdOnce && viewHolder is ReboundableViewHolder){
            currentTargetHasMetThresholdOnce = false
            viewHolder.onRebounded()
        }
        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder !is ReboundableViewHolder) return
        if (currentTargetPosition != viewHolder.adapterPosition) {
            currentTargetPosition = viewHolder.adapterPosition
            currentTargetHasMetThresholdOnce = false
        }

        val itemView = viewHolder.itemView
        val currentSwipePercentage = abs(dX) / itemView.width
        viewHolder.onReboundOffsetChanged(
            currentSwipePercentage,
            trueSwipeThreshold,
            currentTargetHasMetThresholdOnce
        )
        translateReboundingView(itemView, viewHolder, dX)

        if (currentSwipePercentage >= trueSwipeThreshold &&
            !currentTargetHasMetThresholdOnce) {
            currentTargetHasMetThresholdOnce = true
        }
    }

    private fun translateReboundingView(
        itemView: View,
        viewHolder: ReboundableViewHolder,
        dX: Float
    ) {
        // Progressively decrease the amount by which the view is translated to give a 'spring'
        // affect to the item.
        val swipeDismissDistanceHorizontal = itemView.width * trueSwipeThreshold
        val dragFraction = ln(
            (1 + (dX / swipeDismissDistanceHorizontal)).toDouble()) / ln(3.toDouble()
        )
        val dragTo = dragFraction * swipeDismissDistanceHorizontal *
            swipeReboundingElasticity

        viewHolder.reboundableView.translationX = dragTo.toFloat()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Do nothing. Overriding getSwipeThreshold to an impossible number means this will
        // never be called.
    }
}