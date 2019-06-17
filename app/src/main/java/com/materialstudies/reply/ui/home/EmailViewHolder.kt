package com.materialstudies.reply.ui.home

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.ui.widget.CircularImageView


class EmailViewHolder(
        private val view: View,
        private val listener: EmailAdapter.EmailAdapterListener
): RecyclerView.ViewHolder(view) {

    private val sender: AppCompatTextView = view.findViewById(R.id.sender_text_view)
    private val subject: AppCompatTextView = view.findViewById(R.id.subject_text_view)
    private val bodyPreview: AppCompatTextView = view.findViewById(R.id.body_preview_text_view)
    private val senderImage: CircularImageView = view.findViewById(R.id.sender_profile_image_view)

    fun bind(email: Email) {
        sender.text = email.sender
        subject.text = email.subject
        bodyPreview.text = email.body
        senderImage.setImageResource(email.senderImg)

        view.setOnClickListener { listener.onEmailClicked(email) }
        view.setOnLongClickListener {
            listener.onEmailLongPressed(email)
        }
    }
}