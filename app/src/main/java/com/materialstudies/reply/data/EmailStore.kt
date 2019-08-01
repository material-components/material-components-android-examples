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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.materialstudies.reply.R

/**
 * A static data store of [Email]s.
 */
object EmailStore {

    private val allEmails = mutableListOf(
        Email(
            0L,
            AccountStore.getContactAccountById(6L),
            listOf(AccountStore.getDefaultUserAccount()),
            "Package shipped!",
            """
                Cucumber Mask Facial has shipped.

                Keep an eye out for a package to arrive between this Thursday and next Tuesday. If for any reason you don't receive your package before the end of next week, please reach out to us for details on your shipment.

                As always, thank you for shopping with us and we hope you love our specially formulated Cucumber Mask!
            """.trimIndent(),
            isStarred = true
        ),
        Email(
            1L,
            AccountStore.getContactAccountById(3L),
            listOf(AccountStore.getDefaultUserAccount()),
            "Brunch this weekend?",
            """
                I'll be in your neighborhood doing errands and was hoping to catch you for a coffee this Saturday. If you don't have anything scheduled, it would be great to see you! It feels like its been forever.

                If we do get a chance to get together, remind me to tell you about Kim. She stopped over at the house to say hey to the kids and told me all about her trip to Mexico.

                Talk to you soon,

                Ali
            """.trimIndent()
        ),
        Email(
            2L,
            AccountStore.getContactAccountById(7L),
            listOf(AccountStore.getDefaultUserAccount()),
            "Bonjour from Paris",
            "Here are some great shots from my trip...",
            listOf(
                EmailAttachment(R.drawable.paris_1, "Bridge in Paris"),
                EmailAttachment(R.drawable.paris_2, "Bridge in Paris at night"),
                EmailAttachment(R.drawable.paris_3, "City street in Paris"),
                EmailAttachment(R.drawable.paris_4, "Street with bike in Paris")
            ),
            true
        ),
        Email(
            3L,
            AccountStore.getContactAccountById(8L),
            listOf(AccountStore.getDefaultUserAccount()),
            "High school reunion?",
            """
                Hi friends,

                I was at the grocery store on Sunday night.. when I ran into Genie Williams! I almost didn't recognize her afer 20 years!

                Anyway, it turns out she is on the organizing committee for the high school reunion this fall. I don't know if you were planning on going or not, but she could definitely use our help in trying to track down lots of missing alums. If you can make it, we're doing a little phone-tree party at her place next Saturday, hoping that if we can find one person, thee more will...
            """.trimIndent()
        ),
        Email(
            4L,
            AccountStore.getContactAccountById(9L),
            listOf(
                AccountStore.getDefaultUserAccount(),
                AccountStore.getContactAccountById(8L),
                AccountStore.getContactAccountById(3L)
            ),
            "Brazil trip",
            """
                Thought we might be able to go over some details about our upcoming vacation.

                I've been doing a bit of research and have come across a few paces in Northern Brazil that I think we should check out. One, the north has some of the most predictable wind on the planet. I'd love to get out on the ocean and kitesurf for a couple of days if we're going to be anywhere near or around Taiba. I hear it's beautiful there and if you're up for it, I'd love to go. Other than that, I haven't spent too much time looking into places along our road trip route. I'm assuming we can find places to stay and things to do as we drive and find places we think look interesting. But... I know you're more of a planner, so if you have ideas or places in mind, lets jot some ideas down!

                Maybe we can jump on the phone later today if you have a second.

                I've been doing a bit of research and have come across a few paces in Northern Brazil that I think we should check out. One, the north has some of the most predictable wind on the planet. I'd love to get out on the ocean and kitesurf for a couple of days if we're going to be anywhere near or around Taiba. I hear it's beautiful there and if you're up for it, I'd love to go. Other than that, I haven't spent too much time looking into places along our road trip route. I'm assuming we can find places to stay and things to do as we drive and find places we think look interesting. But... I know you're more of a planner, so if you have ideas or places in mind, lets jot some ideas down!
            """.trimIndent(),
            isStarred = true
        ),
        Email(
            5L,
            AccountStore.getContactAccountById(10L),
            listOf(AccountStore.getDefaultUserAccount()),
            "Update to Your Itinerary",
            ""
        ),
        Email(
            6L,
            AccountStore.getContactAccountById(9L),
            listOf(AccountStore.getDefaultUserAccount()),
            "Recipe to try",
            "Raspberry Pie: We should make this pie recipe tonight! The filling is " +
                "very quick to put together."
        ),
        Email(
            7L,
            AccountStore.getContactAccountById(6L),
            listOf(AccountStore.getDefaultUserAccount()),
            "Delivered",
            "Your shoes should be waiting for you at home!"
        )
    )

    private val _emails: MutableLiveData<List<Email>> = MutableLiveData()
    val emails: LiveData<List<Email>>
        get() = _emails

    init {
        _emails.value = allEmails
    }

    /**
     * Get an [Email] with the given [id].
     */
    fun get(id: Long): Email? {
        return allEmails.firstOrNull { it.id == id }
    }

    /**
     * Create a new, blank [Email].
     */
    fun create(): Email {
        return Email(
            System.nanoTime(), // Unique ID generation.
            AccountStore.getDefaultUserAccount()
        )
    }

    /**
     * Create a new [Email] that is a reply to the email with the given [replyToId].
     */
    fun createReplyTo(replyToId: Long): Email {
        val replyTo = get(replyToId) ?: return create()
        return Email(
            id = System.nanoTime(),
            sender = replyTo.recipients.firstOrNull() ?: AccountStore.getDefaultUserAccount(),
            recipients = listOf(replyTo.sender) + replyTo.recipients,
            subject = replyTo.subject,
            isStarred = replyTo.isStarred,
            isImportant = replyTo.isImportant
        )
    }

    /**
     * Delete the [Email] with the given [id].
     */
    fun delete(id: Long) {
        allEmails.removeAll { it.id == id }
        _emails.value = allEmails
    }

    /**
     * Update the [Email] with the given [id] by applying all mutations from [with].
     */
    fun update(id: Long, with: Email.() -> Unit) {
        allEmails.find { it.id == id }?.let {
            it.with()
            _emails.value = allEmails
        }
    }

    /**
     * Get a list of [EmailFolder]s by which [Email]s can be categorized.
     */
    fun getAllFolders() = listOf(
        "Receipts",
        "Pine Elementary",
        "Taxes",
        "Vacation",
        "Mortgage",
        "Grocery coupons"
    )

}

