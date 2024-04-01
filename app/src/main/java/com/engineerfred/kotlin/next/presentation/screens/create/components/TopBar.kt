package com.engineerfred.kotlin.next.presentation.screens.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DodgerBlue
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onCloseClicked: () -> Unit,
    onPostClicked: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 8.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onCloseClicked() }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null,
                modifier = Modifier.size(35.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = "Create post",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            modifier = Modifier.clickable { onPostClicked() },
            text = "POST",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if ( isSystemInDarkTheme() ) CrimsonRed else DodgerBlue
        )
    }
}


@Preview( showBackground = true )
@Composable
private fun TopBarPreview() {
    NextTheme {
        TopBar(onCloseClicked = {}, onPostClicked = {})
    }
}