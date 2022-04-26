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

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.databinding.EmailItemLayoutBinding
import com.materialstudies.reply.ui.common.EmailAttachmentAdapter

class EmailViewHolder(
    private val binding: EmailItemLayoutBinding,
    listener: EmailAdapter.EmailAdapterListener
): RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder{

    private val attachmentAdapter = object : EmailAttachmentAdapter() {
        override fun getLayoutIdForPosition(position: Int): Int
            = R.layout.email_attachment_preview_item_layout
    }

    override val reboundableView: View = binding.cardView

    init {
        binding.run {
            this.listener = listener
            attachmentRecyclerView.adapter = attachmentAdapter
        }
    }

    fun bind(email: Email) {
        binding.email = email
        binding.root.isActivated = email.isStarred

        attachmentAdapter.submitList(email.attachments)

        binding.executePendingBindings()
    }

    override fun onReboundOffsetChanged(
        currentSwipePercentage: Float,
        swipeThreshold: Float,
        currentTargetHasMetThresholdOnce: Boolean
    ) {
        // Only alter shape and activation in the forward direction once the swipe
        // threshold has been met. Undoing the swipe would require releasing the item and
        // re-initiating the swipe.
        if (currentTargetHasMetThresholdOnce) return

        val isStarred = binding.email?.isStarred ?: false

        // Start the background animation once the threshold is met.
        val thresholdMet = currentSwipePercentage >= swipeThreshold
        val shouldStar = when {
            thresholdMet && isStarred -> false
            thresholdMet && !isStarred -> true
            else -> return
        }
        binding.root.isActivated = shouldStar
    }

    override fun onRebounded() {
        val email = binding.email ?: return
        binding.listener?.onEmailStarChanged(email, !email.isStarred)
        email.notifyChange()
    }
}