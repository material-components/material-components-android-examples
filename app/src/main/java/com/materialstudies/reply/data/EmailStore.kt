package com.materialstudies.reply.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.materialstudies.reply.R

/**
 * A static data store of [Email]'s.
 */
class EmailStore {

    private val allEmails = mutableListOf(
            Email(
                    0,
                    "Google Express - 15m ago",
                    "Package shipped!",
                    "Cucumber Mask Facial has shipped",
                    R.drawable.avatar_express
            ),
            Email(
                    1,
                    "Ali Connors - 25m ago",
                    "Brunch this weekend?",
                    "I'll be in your neighborhood doing errands...",
                    R.drawable.avatar1
            ),
            Email(
                    2,
                    "Sandra Adams - 6 hrs ago",
                    "Bonjour from Paris",
                    "Here are some great shots from my trip...",
                    R.drawable.avatar2
            ),
            Email(
                    3,
                    "Trevor Hansen - 12 hrs ago",
                    "High school reunion?",
                    "",
                    R.drawable.avatar3
            ),
            Email(
                    4,
                    "Britta Holt - 18 hrs ago",
                    "Brazil trip",
                    "Thought we might be able to go over some details about our upcoming vacation...",
                    R.drawable.avatar4
            ),
            Email(
                    5,
                    "Frank Hawkins - 20 hrs ago",
                    "Update to Your Itinerary",
                    "",
                    R.drawable.avatar5
            ),
            Email(
                    6,
                    "Britta Holt - 21 hrs ago",
                    "Recipe to try",
                    "Raspberry Pie: We should make this pie recipe tonight! The filling is very quick to put together.",
                    R.drawable.avatar6
            ),
            Email(
                    7,
                    "Google Express - 22 hrs ago",
                    "Delivered",
                    "Your shoes should be waiting for you at home!",
                    R.drawable.avatar7
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

