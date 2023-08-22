package com.materialstudies.reply.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.theme.ReplyTheme
import com.materialstudies.reply.util.DevicePreviews
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun HomeEmailList(
    emails: List<Email>,
    modifier: Modifier = Modifier,
    onEmailClick: (Long) -> Unit = {},
    onEmailLongClick: () -> Unit = {},
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        state = scrollState,
        modifier = modifier
            .systemBarsPadding()
            .padding(bottom = dimensionResource(id = R.dimen.bottom_app_bar_height))
            .nestedScroll(rememberNestedScrollInteropConnection()),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(items = emails, key = { it.id }) { email ->
            HomeEmailItem(
                email = email,
                onEmailClick = onEmailClick,
                onEmailLongClick = onEmailLongClick
            )
        }
    }
}

@ThemePreviews
@DevicePreviews
@Composable
fun HomeEmailListPreview() {
    ReplyTheme {
        HomeEmailList(emails = EmailStore.getAllEmails())
    }
}