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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.home.Mailbox

/**
 * A class which maintains and generates a navigation list to be displayed by [NavigationAdapter].
 */
object NavigationModel {

    const val INBOX_ID = 0
    const val STARRED_ID = 1
    const val SENT_ID = 2
    const val TRASH_ID = 3
    const val SPAM_ID = 4
    const val DRAFTS_ID = 5

    private var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = INBOX_ID,
            icon = R.drawable.ic_twotone_inbox,
            titleRes = R.string.navigation_inbox,
            checked = false,
            mailbox = Mailbox.INBOX
        ),
        NavigationModelItem.NavMenuItem(
            id = STARRED_ID,
            icon = R.drawable.ic_twotone_stars,
            titleRes = R.string.navigation_starred,
            checked = false,
            mailbox = Mailbox.STARRED
        ),
        NavigationModelItem.NavMenuItem(
            id = SENT_ID,
            icon = R.drawable.ic_twotone_send,
            titleRes = R.string.navigation_sent,
            checked = false,
            mailbox = Mailbox.SENT
        ),
        NavigationModelItem.NavMenuItem(
            id = TRASH_ID,
            icon = R.drawable.ic_twotone_delete,
            titleRes = R.string.navigation_trash,
            checked = false,
            mailbox = Mailbox.TRASH
        ),
        NavigationModelItem.NavMenuItem(
            id = SPAM_ID,
            icon = R.drawable.ic_twotone_error,
            titleRes = R.string.navigation_spam,
            checked = false,
            mailbox = Mailbox.SPAM
        ),
        NavigationModelItem.NavMenuItem(
            id = DRAFTS_ID,
            icon = R.drawable.ic_twotone_drafts,
            titleRes = R.string.navigation_drafts,
            checked = false,
            mailbox = Mailbox.DRAFTS
        )
    )

    private val _navigationList: MutableLiveData<List<NavigationModelItem>> = MutableLiveData()
    val navigationList: LiveData<List<NavigationModelItem>>
        get() = _navigationList

    init {
        postListUpdate()
    }

    /**
     * Set the currently selected menu item.
     *
     * @return true if the currently selected item has changed.
     */
    fun setNavigationMenuItemChecked(id: Int): Boolean {
        var updated = false
        navigationMenuItems.forEachIndexed { index, item ->
            val shouldCheck = item.id == id
            if (item.checked != shouldCheck) {
                navigationMenuItems[index] = item.copy(checked = shouldCheck)
                updated = true
            }
        }
        if (updated) postListUpdate()
        return updated
    }

    private fun postListUpdate() {
        val newList = navigationMenuItems +
            (NavigationModelItem.NavDivider("Folders")) +
            EmailStore.getAllFolders().map { NavigationModelItem.NavEmailFolder(it) }

        _navigationList.value = newList
    }
}

