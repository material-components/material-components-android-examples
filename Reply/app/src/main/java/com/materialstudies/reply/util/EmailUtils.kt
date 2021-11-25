package com.materialstudies.reply.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object EmailUtils {

    private const val DEFAULT_EMAIL_ID = 0L

    private val _selectedEmailState = MutableStateFlow(DEFAULT_EMAIL_ID)
    val selectedEmailState: StateFlow<Long> = _selectedEmailState.asStateFlow()

    fun updateEmailId(emailId: Long) {
        _selectedEmailState.value = emailId
    }
}