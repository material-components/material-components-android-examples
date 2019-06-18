package com.materialstudies.reply.ui.home

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.materialstudies.reply.data.EmailAttachment
import com.materialstudies.reply.databinding.EmailAttachmentItemLayoutBinding

class EmailAttachmentViewHolder(
    private val binding: EmailAttachmentItemLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(attachment: EmailAttachment) {
        binding.run {
            emailAttachment = attachment
            Glide.with(attachmentImageView)
                .load(attachment.resId)
                .into(attachmentImageView)
            executePendingBindings()
        }
    }
}