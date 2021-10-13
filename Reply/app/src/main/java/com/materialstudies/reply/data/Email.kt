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

package com.materialstudies.reply.data

import androidx.databinding.BaseObservable
import androidx.recyclerview.widget.DiffUtil
import com.materialstudies.reply.ui.home.Mailbox

/**
 * A simple data class to represent an Email.
 */
data class Email (
    val id: Long,
    val sender: Account,
    val recipients: List<Account> = emptyList(),
    val subject: String = "",
    val body: String = "",
    val attachments: List<EmailAttachment> = emptyList(),
    var isImportant: Boolean = false,
    var isStarred: Boolean = false,
    var mailbox: Mailbox = Mailbox.INBOX,
    var timeSent: String = "") : BaseObservable () {
    val hasBody: Boolean = body.isNotBlank()
    val hasAttachments: Boolean = attachments.isNotEmpty()
    val recipientsPreview: String = getRecipientPreview()
    val nonUserAccountRecipients = recipients
        .filterNot { AccountStore.isUserAccount(it.uid) }

    private fun getRecipientPreview(): String {
        var recipientsString = "";
        for (index in recipients.indices) {
            val account = recipients[index]
            recipientsString += when (index) {
              0 -> {
                  account.firstName
              }
              recipients.size - 1 -> {
                  " and " + account.firstName
              }
              else -> {
                  ", " + account.firstName
              }
            }
        }
        return recipientsString
    }

}

object EmailDiffCallback : DiffUtil.ItemCallback<Email>() {
    override fun areItemsTheSame(oldItem: Email, newItem: Email) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Email, newItem: Email) = oldItem == newItem
}

