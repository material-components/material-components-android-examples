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

package com.materialstudies.reply.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.materialstudies.reply.R

@Composable
fun ReplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Colors
    val lightColors = lightColors(
        primary = colorResource(id = R.color.reply_blue_700),
        primaryVariant = colorResource(id = R.color.reply_blue_800),
        secondary = colorResource(id = R.color.reply_orange_500),
        secondaryVariant = colorResource(id = R.color.reply_orange_400),
        background = colorResource(id = R.color.reply_blue_50),
        onPrimary = colorResource(id = R.color.reply_white_50),
        onSecondary = colorResource(id = R.color.reply_black_900),
        surface = colorResource(id = R.color.reply_white_50),
        onSurface = colorResource(id = R.color.reply_black_900),
        error = colorResource(id = R.color.reply_red_400),
        onError = colorResource(id = R.color.reply_black_900),
        onBackground = colorResource(id = R.color.reply_black_900),
    )
    val darkColors = darkColors(
        primary = colorResource(id = R.color.reply_blue_200),
        primaryVariant = colorResource(id = R.color.reply_blue_300),
        secondary = colorResource(id = R.color.reply_orange_300),
        secondaryVariant = colorResource(id = R.color.reply_orange_300),
        onPrimary = colorResource(id = R.color.reply_black_900),
        onSecondary = colorResource(id = R.color.reply_black_900),
        background = colorResource(id = R.color.reply_black_900),
        onBackground = colorResource(id = R.color.reply_white_50),
        surface = colorResource(id = R.color.reply_black_800),
        onSurface = colorResource(id = R.color.reply_white_50),
        error = colorResource(id = R.color.reply_red_200),
        onError = colorResource(id = R.color.reply_black_900),
    )

    // Typography
    val typography = Typography(
        h2 = Typography().h2.copy(
            fontFamily = workSansSemiBoldFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        h3 = Typography().h3.copy(
            fontFamily = workSansBoldFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        h4 = Typography().h4.copy(
            fontFamily = workSansBoldFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        h5 = Typography().h5.copy(
            fontFamily = workSansBoldFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        h6 = Typography().h6.copy(
            fontFamily = workSansMediumFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        body1 = Typography().body1.copy(
            fontFamily = workSansFontFamily,
            fontSize = 16.sp,
            color = colorResource(id = R.color.color_on_surface_emphasis_high),
            lineHeight = 24.sp
        ),
        body2 = Typography().body2.copy(
            fontFamily = workSansFontFamily,
            fontSize = 14.sp,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        subtitle1 = Typography().subtitle1.copy(
            fontFamily = workSansFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        subtitle2 = Typography().subtitle2.copy(
            fontFamily = workSansFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        button = Typography().button.copy(
            fontFamily = workSansMediumFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        caption = Typography().caption.copy(
            fontFamily = workSansFontFamily,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        ),
        overline = Typography().overline.copy(
            fontFamily = workSansSemiBoldFontFamily,
            fontSize = 12.sp,
            color = colorResource(id = R.color.color_on_surface_emphasis_high)
        )
    )

    // Theme
    MaterialTheme(
        colors = if (darkTheme) darkColors else lightColors,
        typography = typography,
        content = content
    )
}