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

package com.materialstudies.reply.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.materialstudies.reply.R
import com.materialstudies.reply.data.SearchSuggestionStore
import com.materialstudies.reply.ui.common.SearchSuggestionHeader
import com.materialstudies.reply.ui.common.SearchSuggestionItem
import com.materialstudies.reply.ui.theme.ReplyTheme
import com.materialstudies.reply.util.DevicePreviews
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding(),
        topBar = {
            SearchBar(onBackClicked = { onBackClicked() })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            SearchSuggestionHeader(
                titleResId = R.string.search_suggestion_title_yesterday
            )
            SearchSuggestionStore.YESTERDAY_SUGGESTIONS.forEach {
                SearchSuggestionItem(searchSuggestion = it)
            }
            SearchSuggestionHeader(
                titleResId = R.string.search_suggestion_title_this_week
            )
            SearchSuggestionStore.THIS_WEEK_SUGGESTIONS.forEach {
                SearchSuggestionItem(searchSuggestion = it)
            }
        }
    }
}

@ThemePreviews
@DevicePreviews
@Composable
fun SearchScreenPreview() {
    ReplyTheme {
        SearchScreen()
    }
}