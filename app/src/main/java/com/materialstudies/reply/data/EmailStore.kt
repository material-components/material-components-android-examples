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
            "Package shipped!",
            "Cucumber Mask Facial has shipped",
            R.drawable.avatar_express,
            isStarred = true
        ),
        Email(
            1,
            "Ali Connors - 25m ago",
            "Brunch this weekend?",
            "I'll be in your neighborhood doing errands...",
            R.drawable.avatar_1
        ),
        Email(
            2,
            "Sandra Adams - 6 hrs ago",
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
            "High school reunion?",
            "",
            R.drawable.avatar_3
        ),
        Email(
            4,
            "Britta Holt - 18 hrs ago",
            "Brazil trip",
            "Thought we might be able to go over some details about our upcoming vacation...",
            R.drawable.avatar_4,
            isStarred = true
        ),
        Email(
            5,
            "Frank Hawkins - 20 hrs ago",
            "Update to Your Itinerary",
            "",
            R.drawable.avatar_5
        ),
        Email(
            6,
            "Britta Holt - 21 hrs ago",
            "Recipe to try",
            "Raspberry Pie: We should make this pie recipe tonight! The filling is " +
                "very quick to put together.",
            R.drawable.avatar_6
        ),
        Email(
            7,
            "Google Express - 22 hrs ago",
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

}

