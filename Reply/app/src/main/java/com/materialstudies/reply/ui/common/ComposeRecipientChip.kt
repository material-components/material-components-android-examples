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

package com.materialstudies.reply.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.google.accompanist.themeadapter.material.MdcTheme
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Account
import com.materialstudies.reply.data.AccountStore
import com.materialstudies.reply.ui.theme.workSansFontFamily
import com.materialstudies.reply.util.ThemePreviews

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ComposeRecipientChip(
    onClick: () -> Unit,
    account: Account,
    modifier: Modifier = Modifier
) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Image(
                painter = painterResource(id = account.avatar),
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(dimensionResource(id = R.dimen.chip_icon_diameter))
                    .clip(CircleShape),
                contentDescription = null
            )
        }
    ) {
        Text(
            text = account.fullName,
            fontFamily = workSansFontFamily
        )
    }
}

@ThemePreviews
@Composable
fun ComposeRecipientChipPreview() {
    MdcTheme {
        ComposeRecipientChip(
            account = AccountStore.getAllUserAccounts().first(),
            onClick = {}
        )
    }
}