/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.materialstudies.reply.ui.email

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.themeadapter.material.MdcTheme
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.theme.workSansBoldFontFamily
import com.materialstudies.reply.ui.theme.workSansFontFamily
import com.materialstudies.reply.util.DevicePreviews
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun EmailScreen(
    email: Email,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    shouldShowBackIcon: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = dimensionResource(id = R.dimen.grid_0_5),
                end = dimensionResource(id = R.dimen.grid_0_5),
            ),
    ) {
        Column(
            modifier = modifier
                .padding(
                    start = dimensionResource(id = R.dimen.grid_2),
                    end = dimensionResource(id = R.dimen.grid_2),
                    bottom = dimensionResource(id = R.dimen.bottom_app_bar_height)
                )
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.grid_3))
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = email.subject,
                    style = MaterialTheme.typography.h3,
                    fontFamily = workSansBoldFontFamily
                )
                if (shouldShowBackIcon)
                    Image(
                        modifier = Modifier
                            .padding(dimensionResource(id = R.dimen.grid_2))
                            .clickable { onBackClick() },
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = null
                    )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.grid_1)),
                ) {
                    Text(
                        text = email.senderPreview,
                        style = MaterialTheme.typography.body2,
                        fontFamily = workSansFontFamily
                    )
                    Text(
                        modifier = Modifier.padding(
                            top = dimensionResource(id = R.dimen.grid_0_25)
                        ),
                        text = stringResource(
                            id = R.string.email_recipient_to, email.recipientsPreview
                        ),
                        style = MaterialTheme.typography.caption.copy(
                            color = MaterialTheme.colors.onSurface.copy(
                                alpha = ContentAlpha.medium
                            )
                        ),
                        fontFamily = workSansFontFamily
                    )
                }
                Image(
                    painter = painterResource(id = email.sender.avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.email_sender_profile_image_size))
                        .clip(CircleShape)
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.grid_3)),
                text = email.body,
                fontFamily = workSansFontFamily
            )
            if (email.attachments.isNotEmpty())
                EmailAttachmentGrid(
                    modifier = Modifier.fillMaxWidth(), emailAttachments = email.attachments
                )
        }
    }
}

@ThemePreviews
@DevicePreviews
@Composable
fun EmailScreenPreview() {
    MdcTheme {
        EmailScreen(email = EmailStore.get(4L)!!)
    }
}