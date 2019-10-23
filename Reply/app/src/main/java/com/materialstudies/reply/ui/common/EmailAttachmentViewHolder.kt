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

package com.materialstudies.reply.ui.common

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.reply.data.EmailAttachment

/**
 * Generic RecyclerView.ViewHolder which is able to bind layouts which expose a variable
 * for an [EmailAttachment].
 */
class EmailAttachmentViewHolder(
    private val binding: ViewDataBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(attachment: EmailAttachment) {
        binding.run {
            setVariable(BR.emailAttachment, attachment)
            executePendingBindings()
        }
    }
}