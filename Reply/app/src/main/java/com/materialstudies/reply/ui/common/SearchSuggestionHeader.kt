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

import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.themeadapter.material.MdcTheme
import com.materialstudies.reply.R
import com.materialstudies.reply.ui.theme.workSansFontFamily
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun SearchSuggestionHeader(
    @StringRes titleResId: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = titleResId),
        modifier = modifier.padding(
            start = dimensionResource(id = R.dimen.grid_3),
            end = dimensionResource(id = R.dimen.grid_3),
            top = dimensionResource(id = R.dimen.grid_2),
            bottom = dimensionResource(id = R.dimen.grid_0_5)
        ),
        style = MaterialTheme.typography.subtitle2,
        fontFamily = workSansFontFamily
    )
}

fun ViewGroup.addSearchSuggestionHeaderComposeView(
    @StringRes titleResId: Int,
    modifier: Modifier = Modifier,
) {
    this.addView(ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MdcTheme {
                SearchSuggestionHeader(
                    titleResId = titleResId,
                    modifier = modifier
                )
            }
        }
    })
}

@ThemePreviews
@Composable
fun SearchSuggestionHeaderPreview() {
    MdcTheme {
        SearchSuggestionHeader(titleResId = R.string.search_suggestion_title_yesterday)
    }
}