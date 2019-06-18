package com.materialstudies.reply.data

import androidx.annotation.DrawableRes

data class EmailAttachment(
    @DrawableRes val resId: Int,
    val contentDesc: String
)