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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.materialstudies.reply.R
import com.materialstudies.reply.data.EmailAttachment

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmailAttachmentGrid(
    // TODO Use a stable type, instead of List
    emailAttachments: List<EmailAttachment>,
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        verticalItemSpacing = dimensionResource(id = R.dimen.grid_0_25),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.grid_0_25)),
        columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
        modifier = modifier
            .height(400.dp)
            .fillMaxWidth()
    ) {
        items(items = emailAttachments, key = { it.resId }) {
            Image(
                painter = painterResource(id = it.resId),
                contentScale = ContentScale.FillWidth,
                contentDescription = it.contentDesc
            )
        }
    }
}