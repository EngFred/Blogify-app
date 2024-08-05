package com.engineerfred.kotlin.next.presentation.screens.feeds.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onSearchClicked: () -> Unit,
    onMenuClicked: () -> Unit,
    onCreatePostClicked: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name), fontWeight = FontWeight.ExtraBold, fontSize = 22.sp) },
        actions = {
            IconButton(onClick = { onCreatePostClicked() }) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null )
            }
            IconButton(onClick = { onSearchClicked() }) {
                Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null )
            }
            IconButton(onClick = { onMenuClicked() }) {
                Icon(painter = painterResource(id = R.drawable.ic_menu), contentDescription = null )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = Color.White,
            titleContentColor = Color.White
        )
    )
}

@Preview( showBackground = true )
@Composable
private fun AppTopBarPreview() {
    NextTheme {
        AppTopBar({},{}, {})
    }
}