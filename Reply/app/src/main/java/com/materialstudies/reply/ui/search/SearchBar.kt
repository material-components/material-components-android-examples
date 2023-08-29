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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.materialstudies.reply.R
import com.materialstudies.reply.ui.theme.ReplyTheme
import com.materialstudies.reply.util.DevicePreviews
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    val borderColor = MaterialTheme.colors.onSurface
    Row(modifier = modifier
        .fillMaxWidth()
        .drawBehind {
            drawLine(
                color = borderColor,
                start = Offset(x = 0F, y = size.height),
                end = Offset(x = size.width, y = size.height),
                strokeWidth = 0.5F
            )
        }
        .padding(horizontal = dimensionResource(id = R.dimen.grid_2)),
        verticalAlignment = Alignment.CenterVertically) {
        Image(
            modifier = Modifier.clickable { onBackClicked() },
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null
        )
        TextField(
            modifier = Modifier.weight(1F),
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = {
                Text(text = stringResource(id = R.string.search_suggestion_hint))
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
        Image(
            painter = painterResource(id = R.drawable.ic_mic),
            contentDescription = null
        )
    }
}

@ThemePreviews
@DevicePreviews
@Composable
fun SearchBarPreview() {
    ReplyTheme {
        SearchBar()
    }
}