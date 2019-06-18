package com.materialstudies.reply.ui.home

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.shape.MaterialShapeDrawable
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.databinding.EmailItemLayoutBinding
import com.materialstudies.reply.util.ThemeUtils
import com.materialstudies.reply.util.backgroundShapeDrawable
import com.materialstudies.reply.util.foregroundShapeDrawable
import com.materialstudies.reply.util.setTextAppearanceCompat

class EmailViewHolder(
    private val binding: EmailItemLayoutBinding,
    listener: EmailAdapter.EmailAdapterListener
): RecyclerView.ViewHolder(binding.root) {

    private val attachmentAdapter = EmailAttachmentAdapter()

    private val cardBackground: MaterialShapeDrawable = binding.cardView.backgroundShapeDrawable
    private val cardForeground: MaterialShapeDrawable = binding.cardView.foregroundShapeDrawable

    init {
        binding.listener = listener
        binding.attachmentRecyclerView.apply {
            layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = attachmentAdapter

        }
    }

    fun bind(email: Email) {
        binding.email = email

        Glide.with(binding.senderProfileImageView)
            .load(email.senderImg)
            .circleCrop()
            .into(binding.senderProfileImageView)

        // Set the subject's TextAppearance
        val textAppearance = ThemeUtils.getResourceIdFromAttr(
            binding.subjectTextView.context,
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
        cardBackground.interpolation = interpolation
        cardForeground.interpolation = interpolation

        binding.executePendingBindings()
    }

}