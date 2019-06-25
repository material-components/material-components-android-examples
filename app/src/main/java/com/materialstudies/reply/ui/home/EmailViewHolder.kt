package com.materialstudies.reply.ui.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.databinding.EmailItemLayoutBinding
import com.materialstudies.reply.ui.common.EmailAttachmentAdapter
import com.materialstudies.reply.util.backgroundShapeDrawable
import com.materialstudies.reply.util.foregroundShapeDrawable
import com.materialstudies.reply.util.getStyleIdFromAttr
import com.materialstudies.reply.util.setTextAppearanceCompat

class EmailViewHolder(
    private val binding: EmailItemLayoutBinding,
    listener: EmailAdapter.EmailAdapterListener
): RecyclerView.ViewHolder(binding.root), ReboundingSwipeActionCallback.ReboundableViewHolder {

    private val attachmentAdapter = object : EmailAttachmentAdapter() {
        override fun getLayoutIdForPosition(position: Int): Int
            = R.layout.email_attachment_preview_item_layout
    }

    private val cardBackground: MaterialShapeDrawable = binding.cardView.backgroundShapeDrawable
    private val cardForeground: MaterialShapeDrawable = binding.cardView.foregroundShapeDrawable

    override val reboundableView: View = binding.cardView

    init {
        binding.run {
            this.listener = listener
            attachmentRecyclerView.adapter = attachmentAdapter
            root.background = EmailSwipeActionDrawable(root.context)
        }
    }

    fun bind(email: Email) {
        binding.email = email
        binding.root.isActivated = email.isStarred

        // Set the subject's TextAppearance
        val textAppearance = binding.subjectTextView.context.getStyleIdFromAttr(
            if (email.isImportant) {
                R.attr.textAppearanceHeadline4
            } else {
                R.attr.textAppearanceHeadline5
            }
        )
        binding.subjectTextView.setTextAppearanceCompat(
            binding.subjectTextView.context,
            textAppearance
        )

        attachmentAdapter.submitList(email.attachments)

        // Setting interpolation here controls whether or not we draw the top left corner as
        // rounded or squared. Since all other corners are set to 0dp rounded, they are
        // not affected.
        val interpolation = if (email.isStarred) 1F else 0F
        setCardShapeInterpolation(interpolation)

        binding.executePendingBindings()
    }

    private fun setCardShapeInterpolation(interpolation: Float) {
        cardBackground.interpolation = interpolation
        cardForeground.interpolation = interpolation
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

        // Animate the top left corner radius of the email card as swipe happens.
        val interpolation = (currentSwipePercentage / swipeThreshold).coerceIn(0F, 1F)
        val adjustedInterpolation = Math.abs((if (isStarred) 1F else 0F) - interpolation)
        setCardShapeInterpolation(adjustedInterpolation)

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
    }

}