package com.materialstudies.reply.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil

/**
 * A simple data class to represent an Email.
 */
data class Email(
    val id: Int,
    val sender: String,
    val recipient: String,
    val subject: String,
    val body: String,
    @DrawableRes val senderResId: Int,
    val attachments: List<EmailAttachment> = emptyList(),
    var isImportant: Boolean = false,
    var isStarred: Boolean = false
) {
    val hasBody: Boolean = body.isNotBlank()
    val hasAttachments: Boolean = attachments.isNotEmpty()
}

object EmailDiffCallback : DiffUtil.ItemCallback<Email>() {
    override fun areItemsTheSame(oldItem: Email, newItem: Email) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Email, newItem: Email) = oldItem == newItem
}

