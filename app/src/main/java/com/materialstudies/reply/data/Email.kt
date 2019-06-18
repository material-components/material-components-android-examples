package com.materialstudies.reply.data

import androidx.recyclerview.widget.DiffUtil

/**
 * A simple data class to represent an Email.
 */
data class Email(
    val id: Int,
    val sender: String,
    val subject: String,
    val body: String,
    val senderImg: Int,
    val attachments: List<EmailAttachment> = emptyList(),
    var isImportant: Boolean = false,
    var isStarred: Boolean = false
) {
    val hasBody: Boolean = body.isNotBlank()
    val hasAttachments: Boolean = attachments.isNotEmpty()
}

object EmailDiffCallback : DiffUtil.ItemCallback<Email>() {
    override fun areItemsTheSame(oldItem: Email, newItem: Email): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Email, newItem: Email): Boolean {
        return oldItem.sender == newItem.sender &&
            oldItem.subject == newItem.subject &&
            oldItem.body == newItem.body &&
            oldItem.senderImg == newItem.senderImg &&
            oldItem.isStarred == newItem.isStarred &&
            oldItem.isImportant == newItem.isImportant &&
            oldItem.attachments.containsAll(newItem.attachments)

    }
}

