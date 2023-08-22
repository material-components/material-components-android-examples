package com.materialstudies.reply.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.theme.ReplyTheme
import com.materialstudies.reply.util.DevicePreviews
import com.materialstudies.reply.util.ThemePreviews

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeEmailItem(
    email: Email,
    modifier: Modifier = Modifier,
    onEmailClick: (Long) -> Unit = {},
    onEmailLongClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = dimensionResource(id = R.dimen.grid_0_25),
                horizontal = dimensionResource(id = R.dimen.grid_0_5)
            )
            .combinedClickable(
                onClick = { onEmailClick(email.id) },
                onLongClick = { onEmailLongClick() },
            )
            .focusable(true)
    )
    {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    vertical = dimensionResource(id = R.dimen.grid_2),
                    horizontal = dimensionResource(id = R.dimen.grid_2)
                )
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.grid_1))
                ) {
                    Text(
                        text = email.senderPreview,
                        style = MaterialTheme.typography.body2,
                        maxLines = 1
                    )
                    Text(
                        modifier = Modifier.padding(
                            top = dimensionResource(id = R.dimen.grid_1),

                            ), text = email.subject,
//                        fontFamily = workSansBoldFontFamily,
                        style = if (email.isImportant) MaterialTheme.typography.h4
                        else MaterialTheme.typography.h5,
                        maxLines = 1
                    )
                }
                Image(
                    painterResource(id = email.sender.avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.email_sender_profile_image_size))
                        .clip(CircleShape)
                )
            }
            if (email.hasBody)
                Text(
                    text = email.body, maxLines = 2,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.grid_1)),
                    style = MaterialTheme.typography.body1,
                )
            if (email.attachments.isNotEmpty())
                EmailAttachmentRow(emailAttachments = email.attachments)
        }
    }
}

@ThemePreviews
@DevicePreviews
@Composable
fun HomeEmailItemPreview() {
    ReplyTheme {
        HomeEmailItem(email = EmailStore.get(4L)!!)
    }
}