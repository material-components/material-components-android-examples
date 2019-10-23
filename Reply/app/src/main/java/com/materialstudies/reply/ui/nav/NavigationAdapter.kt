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

package com.materialstudies.reply.ui.nav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.materialstudies.reply.databinding.NavDividerItemLayoutBinding
import com.materialstudies.reply.databinding.NavEmailFolderItemLayoutBinding
import com.materialstudies.reply.databinding.NavMenuItemLayoutBinding

private const val VIEW_TYPE_NAV_MENU_ITEM = 4
private const val VIEW_TYPE_NAV_DIVIDER = 6
private const val VIEW_TYPE_NAV_EMAIL_FOLDER_ITEM = 5

class NavigationAdapter(
    private val listener: NavigationAdapterListener
) : ListAdapter<NavigationModelItem, NavigationViewHolder<NavigationModelItem>>(
    NavigationModelItem.NavModelItemDiff
) {

    interface NavigationAdapterListener {
        fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem)
        fun onNavEmailFolderClicked(folder: NavigationModelItem.NavEmailFolder)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NavigationModelItem.NavMenuItem -> VIEW_TYPE_NAV_MENU_ITEM
            is NavigationModelItem.NavDivider -> VIEW_TYPE_NAV_DIVIDER
            is NavigationModelItem.NavEmailFolder -> VIEW_TYPE_NAV_EMAIL_FOLDER_ITEM
            else -> throw RuntimeException("Unsupported ItemViewType for obj ${getItem(position)}")
        }
    }

    @Suppress("unchecked_cast")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NavigationViewHolder<NavigationModelItem> {
        return when (viewType) {
            VIEW_TYPE_NAV_MENU_ITEM -> NavigationViewHolder.NavMenuItemViewHolder(
                NavMenuItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            VIEW_TYPE_NAV_DIVIDER -> NavigationViewHolder.NavDividerViewHolder(
                NavDividerItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_NAV_EMAIL_FOLDER_ITEM -> NavigationViewHolder.EmailFolderViewHolder(
                NavEmailFolderItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            else -> throw RuntimeException("Unsupported view holder type")
        } as NavigationViewHolder<NavigationModelItem>
    }

    override fun onBindViewHolder(
        holder: NavigationViewHolder<NavigationModelItem>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}