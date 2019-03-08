package com.materialstudies.reply.data

/**
 * A simple data class to represent an Email.
 */
data class Email(
        val id: Int,
        val sender: String,
        val subject: String,
        val body: String,
        val senderImg: Int
)

