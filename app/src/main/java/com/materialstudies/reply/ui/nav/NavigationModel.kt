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

/**
 * A class which maintains and generates a navigation list to be displayed by [NavigationAdapter].
 */
object NavigationModel {

    private var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = 0,
            icon = R.drawable.ic_twotone_inbox_on_branded,
            titleRes = R.string.navigation_inbox,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 1,
            icon = R.drawable.ic_twotone_stars_on_branded,
            titleRes = R.string.navigation_starred,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 2,
            icon = R.drawable.ic_twotone_send_on_branded,
            titleRes = R.string.navigation_sent,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 3,
            icon = R.drawable.ic_twotone_delete_on_branded,
            titleRes = R.string.navigation_trash,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 4,
            icon = R.drawable.ic_twotone_error_on_branded,
            titleRes = R.string.navigation_spam,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = 5,
            icon = R.drawable.ic_twotone_drafts_on_branded,
            titleRes = R.string.navigation_drafts,
            checked = false
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

