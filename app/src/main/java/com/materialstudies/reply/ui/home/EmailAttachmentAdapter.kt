package com.materialstudies.reply.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.materialstudies.reply.data.EmailAttachment
import com.materialstudies.reply.databinding.EmailAttachmentItemLayoutBinding

class EmailAttachmentAdapter : RecyclerView.Adapter<EmailAttachmentViewHolder>() {

    private var list: List<EmailAttachment> = emptyList()

    override fun getItemCount(): Int = list.size

    fun submitList(attachments: List<EmailAttachment>) {
        list = attachments
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailAttachmentViewHolder {
        return EmailAttachmentViewHolder(
            EmailAttachmentItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EmailAttachmentViewHolder, position: Int) {
        holder.bind(list[position])
    }
}