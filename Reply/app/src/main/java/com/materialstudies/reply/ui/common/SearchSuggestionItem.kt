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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.materialstudies.reply.R
import com.materialstudies.reply.data.SearchSuggestion
import com.materialstudies.reply.data.SearchSuggestionStore
import com.materialstudies.reply.ui.theme.ReplyTheme
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun SearchSuggestionItem(
    searchSuggestion: SearchSuggestion,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {}
            .focusable()
            .padding(
                horizontal = dimensionResource(id = R.dimen.grid_3),
                vertical = dimensionResource(id = R.dimen.grid_2),
            )
            .fillMaxWidth()
            .horizontalScroll(state = rememberScrollState())) {
        Image(
            painter = painterResource(id = searchSuggestion.iconResId),
            contentDescription = null
        )
        Column {
            Text(
                text = searchSuggestion.title,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.grid_2),
                ),
                style = MaterialTheme.typography.body1,
            )
            Text(
                text = searchSuggestion.subtitle,
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.grid_2),
                    top = dimensionResource(id = R.dimen.grid_0_5),
                ),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

@ThemePreviews
@Composable
fun SearchSuggestionItemPreview() {
    ReplyTheme {
        SearchSuggestionItem(searchSuggestion = SearchSuggestionStore.YESTERDAY_SUGGESTIONS.first())
    }
}