package com.materialstudies.reply.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

enum class ReplyContentType {
    SINGLE_PANE, TWO_PANE
}

fun getContentType(windowSize: WindowSizeClass) =
    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> ReplyContentType.TWO_PANE
        else -> ReplyContentType.SINGLE_PANE
    }