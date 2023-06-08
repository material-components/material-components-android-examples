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

package com.materialstudies.reply.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.layout.DisplayFeature
import com.google.accompanist.adaptive.HorizontalTwoPaneStrategy
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.themeadapter.material.MdcTheme
import com.materialstudies.reply.data.Email
import com.materialstudies.reply.data.EmailStore
import com.materialstudies.reply.ui.email.EmailScreen
import com.materialstudies.reply.util.DevicePreviews
import com.materialstudies.reply.util.ReplyContentType
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun HomeScreen(
    email: Email?,
    emails: List<Email>,
    contentType: ReplyContentType,
    modifier: Modifier = Modifier,
    displayFeatures: List<DisplayFeature> = emptyList(),
    onNavigateToEmail: (Long) -> Unit = {},
    onOpenEmail: (Long) -> Unit = {},
    onEmailLongClick: () -> Unit = {},
) {
    if (email == null || contentType == ReplyContentType.SINGLE_PANE)
        HomeEmailList(
            modifier = modifier,
            emails = emails,
            onEmailClick = { emailId -> onNavigateToEmail(emailId) },
            onEmailLongClick = { onEmailLongClick() }
        )
    else
        TwoPane(
            modifier = modifier,
            first = {
                HomeEmailList(
                    emails = emails,
                    onEmailClick = { emailId -> onOpenEmail(emailId) },
                    onEmailLongClick = { onEmailLongClick() }
                )
            },
            second = { EmailScreen(email = email, shouldShowBackIcon = false) },
            strategy = HorizontalTwoPaneStrategy(
                splitFraction = 0.5f, gapWidth = 16.dp
            ),
            displayFeatures = displayFeatures
        )
}

@ThemePreviews
@DevicePreviews
@Composable
fun HomeScreenSinglePanePreview() {
    MdcTheme {
        HomeScreen(
            email = EmailStore.get(0L),
            emails = EmailStore.getAllEmails(),
            contentType = ReplyContentType.SINGLE_PANE
        )
    }
}

@ThemePreviews
@DevicePreviews
@Composable
fun HomeScreenTwoPanePreview() {
    MdcTheme {
        HomeScreen(
            email = EmailStore.get(0L),
            emails = EmailStore.getAllEmails(),
            contentType = ReplyContentType.TWO_PANE
        )
    }
}