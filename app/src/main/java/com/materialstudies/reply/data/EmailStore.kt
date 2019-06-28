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
class EmailStore {

    private val allEmails = mutableListOf(
        Email(
            0,
            "Google Express - 15m ago",
            "me",
            "Package shipped!",
            """
                Cucumber Mask Facial has shipped.

                Keep an eye out for a package to arrive between this Thursday and next Tuesday. If for any reason you don't receive your package before the end of next week, please reach out to us for details on your shipment.

                As always, thank you for shopping with us and we hope you love our specially formulated Cucumber Mask!
            """.trimIndent(),
            R.drawable.avatar_express,
            isStarred = true
        ),
        Email(
            1,
            "Ali Connors - 25m ago",
            "me",
            "Brunch this weekend?",
            """
                I'll be in your neighborhood doing errands and was hoping to catch you for a coffee this Saturday. If you don't have anything scheduled, it would be great to see you! It feels like its been forever.

                If we do get a chance to get together, remind me to tell you about Kim. She stopped over at the house to say hey to the kids and told me all about her trip to Mexico.

                Talk to you soon,

                Ali
            """.trimIndent(),
            R.drawable.avatar_1
        ),
        Email(
            2,
            "Sandra Adams - 6 hrs ago",
            "me",
            "Bonjour from Paris",
            "Here are some great shots from my trip...",
            R.drawable.avatar_2,
            listOf(
                EmailAttachment(R.drawable.paris_1, "Bridge in Paris"),
                EmailAttachment(R.drawable.paris_2, "Bridge in Paris at night"),
                EmailAttachment(R.drawable.paris_3, "City street in Paris"),
                EmailAttachment(R.drawable.paris_4, "Street with bike in Paris")
            ),
            true
        ),
        Email(
            3,
            "Trevor Hansen - 12 hrs ago",
            "me",
            "High school reunion?",
            """
                Hi friends,

                I was at the grocery store on Sunday night.. when I ran into Genie Williams! I almost didn't recognize her afer 20 years!

                Anyway, it turns out she is on the organizing committee for the high school reunion this fall. I don't know if you were planning on going or not, but she could definitely use our help in trying to track down lots of missing alums. If you can make it, we're doing a little phone-tree party at her place next Saturday, hoping that if we can find one person, thee more will...
            """.trimIndent(),
            R.drawable.avatar_3
        ),
        Email(
            4,
            "Britta Holt - 18 hrs ago",
            "me",
            "Brazil trip",
            """
                Thought we might be able to go over some details about our upcoming vacation.

                I've been doing a bit of research and have come across a few paces in Northern Brazil that I think we should check out. One, the north has some of the most predictable wind on the planet. I'd love to get out on the ocean and kitesurf for a couple of days if we're going to be anywhere near or around Taiba. I hear it's beautiful there and if you're up for it, I'd love to go. Other than that, I haven't spent too much time looking into places along our road trip route. I'm assuming we can find places to stay and things to do as we drive and find places we think look interesting. But... I know you're more of a planner, so if you have ideas or places in mind, lets jot some ideas down!

                Maybe we can jump on the phone later today if you have a second.

                I've been doing a bit of research and have come across a few paces in Northern Brazil that I think we should check out. One, the north has some of the most predictable wind on the planet. I'd love to get out on the ocean and kitesurf for a couple of days if we're going to be anywhere near or around Taiba. I hear it's beautiful there and if you're up for it, I'd love to go. Other than that, I haven't spent too much time looking into places along our road trip route. I'm assuming we can find places to stay and things to do as we drive and find places we think look interesting. But... I know you're more of a planner, so if you have ideas or places in mind, lets jot some ideas down!
            """.trimIndent(),
            R.drawable.avatar_4,
            isStarred = true
        ),
        Email(
            5,
            "Frank Hawkins - 20 hrs ago",
            "me",
            "Update to Your Itinerary",
            "",
            R.drawable.avatar_5
        ),
        Email(
            6,
            "Britta Holt - 21 hrs ago",
            "me",
            "Recipe to try",
            "Raspberry Pie: We should make this pie recipe tonight! The filling is " +
                "very quick to put together.",
            R.drawable.avatar_6
        ),
        Email(
            7,
            "Google Express - 22 hrs ago",
            "me",
            "Delivered",
            "Your shoes should be waiting for you at home!",
            R.drawable.avatar_7
        )
    )

    private val _emails: MutableLiveData<List<Email>> = MutableLiveData()
    val emails: LiveData<List<Email>>
        get() = _emails

    init {
        _emails.value = allEmails
    }

    fun get(id: Int): Email? {
        return allEmails.firstOrNull { it.id == id }
    }

    fun delete(id: Int) {
        allEmails.removeAll { it.id == id }
        _emails.value = allEmails
    }

    fun update(id: Int, with: Email.() -> Unit) {
        allEmails.find { it.id == id }?.let {
            it.with()
            _emails.value = allEmails
        }
    }

    fun getAllFolders() = listOf(
        "Receipts",
        "Pine Elementary",
        "Taxes",
        "Vacation",
        "Mortgage",
        "Grocery coupons"
    )
}

