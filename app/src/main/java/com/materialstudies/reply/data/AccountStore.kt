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

import com.materialstudies.reply.R

/**
 * An static data store of [Account]s. This includes both [Account]s owned by the current user and
 * all [Account]s of the current user's contacts.
 */
object AccountStore {

    private val userAccounts = listOf(
        Account(
            0L,
            "Jeff",
            "Hansen",
            "hikingfan@gmail.com",
            R.drawable.avatar_10
        ),
        Account(
            0L,
            "Jeff",
            "H",
            "jeffhansen@gmail.com",
            R.drawable.avatar_10
        )
    )

    private val userContactAccounts = listOf(
        Account(
            1L,
            "Tracy",
            "Alvarez",
            "tracealvie@gmail.com",
            R.drawable.avatar_4
        ),
        Account(
            2L,
            "Allison",
            "Trabucco",
            "atrabucco222@gmail.com",
            R.drawable.avatar_7
        ),
        Account(
            3L,
            "Ali",
            "Connors",
            "aliconnors@gmail.com",
            R.drawable.avatar_5
        ),
        Account(
            4L,
            "Alberto",
            "Williams",
            "albertowilliams124@gmail.com",
            R.drawable.avatar_8
        ),
        Account(
            5L,
            "Kim",
            "Alen",
            "alen13@gmail.com",
            R.drawable.avatar_9
        ),
        Account(
            6L,
            "Google",
            "Express",
            "express@google.com",
            R.drawable.avatar_express
        ),
        Account(
            7L,
            "Sandra",
            "Adams",
            "sandraadams@gmail.com",
            R.drawable.avatar_2
        ),
        Account(
            8L,
            "Trevor",
            "Hansen",
            "trevorhandsen@gmail.com",
            R.drawable.avatar_3
        ),
        Account(
            9L,
            "Britta",
            "Holt",
            "bholt@gmail.com",
            R.drawable.avatar_4
        ),
        Account(
            10L,
            "Frank",
            "Hawkins",
            "fhawkank@gmail.com",
            R.drawable.avatar_6
        )
    )

    /**
     * Get the current user's default account.
     */
    fun getDefaultUserAccount() = userAccounts.first()

    /**
     * Get all [Account]s owned by the current user.
     */
    fun getUserAccounts() = userAccounts

    /**
     * Whether or not the given [accountId] is an account owned by the current user.
     */
    fun isUserAccount(accountId: Long): Boolean = userAccounts.any { it.id == accountId }

    /**
     * Get the contact of the current user with the given [accountId].
     */
    fun getContactAccountById(accountId: Long): Account {
        return userContactAccounts.firstOrNull { it.id == accountId } ?: userContactAccounts.first()
    }
}