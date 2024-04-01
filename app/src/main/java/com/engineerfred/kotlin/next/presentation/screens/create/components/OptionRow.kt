package com.engineerfred.kotlin.next.presentation.screens.create.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun OptionRow(
    modifier: Modifier = Modifier,
    onRowClicked: () -> Unit,
    @DrawableRes painterResourceIcon: Int? = null,
    imageVectorIcon: ImageVector? = null,
    title: String,
    iconTint: Color
) {
    Row(
       modifier = modifier
           .fillMaxWidth()
           .clickable { onRowClicked() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if ( painterResourceIcon != null && imageVectorIcon == null ) {
            Icon(
                painter = painterResource(id = painterResourceIcon),
                contentDescription = null,
                tint = iconTint
            )
        } else if ( painterResourceIcon == null && imageVectorIcon != null ) {
            Icon(
                imageVector = imageVectorIcon,
                contentDescription = null,
                tint = iconTint
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = title)
    }
}

@Preview( showBackground = true )
@Composable
private fun OptionRowPreview() {
    NextTheme {
        OptionRow(
            onRowClicked = { /*TODO*/ },
            painterResourceIcon = R.drawable.ic_photo,
            title = "Photos",
            iconTint = CrimsonRed
        )
    }
}