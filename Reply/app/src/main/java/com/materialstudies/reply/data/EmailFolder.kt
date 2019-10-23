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

import androidx.recyclerview.widget.DiffUtil

/**
 * Alias to represent a folder (a String title) into which emails can be placed.
 */
typealias EmailFolder = String

object EmailFolderDiff : DiffUtil.ItemCallback<EmailFolder>() {
    override fun areItemsTheSame(oldItem: EmailFolder, newItem: EmailFolder) = oldItem == newItem
    override fun areContentsTheSame(oldItem: EmailFolder, newItem: EmailFolder) = oldItem == newItem
}
