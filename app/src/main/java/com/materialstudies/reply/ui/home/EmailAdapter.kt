package com.materialstudies.reply.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailDiffCallback
import com.materialstudies.reply.databinding.EmailItemLayoutBinding

/**
 * Simple adapter to display Email's in MainActivity.
 */
class EmailAdapter(
        private val listener: EmailAdapterListener
) : ListAdapter<Email, EmailViewHolder>(EmailDiffCallback) {

    interface EmailAdapterListener {
        fun onEmailClicked(email: Email)
        fun onEmailLongPressed(email: Email): Boolean
        fun onEmailStarChanged(email: Email, newValue: Boolean)
        fun onEmailArchived(email: Email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        return EmailViewHolder(
            EmailItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}