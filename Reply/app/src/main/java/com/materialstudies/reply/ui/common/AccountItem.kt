package com.materialstudies.reply.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import com.google.accompanist.themeadapter.material.MdcTheme
import com.materialstudies.reply.R
import com.materialstudies.reply.data.Account
import com.materialstudies.reply.data.AccountStore
import com.materialstudies.reply.ui.theme.workSansFontFamily
import com.materialstudies.reply.util.ThemePreviews

@Composable
fun AccountItem(
    account: Account,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(start = dimensionResource(id = R.dimen.grid_2))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = account.avatar), contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.grid_1))
                .size(dimensionResource(id = R.dimen.navigation_drawer_profile_image_size))
                .clip(CircleShape), contentDescription = account.email
        )
        Text(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = dimensionResource(id = R.dimen.grid_2)), maxLines = 1,
            text = account.email, style = MaterialTheme.typography.body1.copy(
                color = if (account.checkedIcon != 0) MaterialTheme.colors.secondary
                else colorResource(id = R.color.color_on_primary_surface_emphasis_medium)
            ), fontFamily = workSansFontFamily, overflow = TextOverflow.Ellipsis
        )
        if (account.checkedIcon != 0) {
            Image(
                painter = painterResource(id = account.checkedIcon),
                modifier = modifier.padding(dimensionResource(id = R.dimen.grid_3)),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
                contentDescription = null
            )
        }
    }
}

@ThemePreviews
@Composable
fun AccountItemPreview() {
    MdcTheme {
        AccountItem(
            account = AccountStore.getAllUserAccounts().first(),
            onClick = {},
        )
    }
}