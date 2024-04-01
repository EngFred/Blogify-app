package com.engineerfred.kotlin.next.presentation.screens.create.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun Friend(
    modifier: Modifier = Modifier,
    name: String = "Fred Omongole",
    onSelected: () -> Unit,
    isDarkTheme: Boolean
) {

    var isSelected by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isSelected = !isSelected
                onSelected.invoke()
            }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = "",
            contentDescription = null,
            modifier = Modifier
                .padding(top = 4.dp)
                .size(50.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) Charcoal else Color.LightGray)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = name,
            fontSize = 22.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(end = 10.dp)
                .weight(1f)
        )
        Checkbox(checked = isSelected, onCheckedChange = {
            isSelected = !isSelected
            onSelected.invoke()
        } )
    }
}

@Preview( showBackground = true )
@Composable
private fun FriendPreview() {
    NextTheme {
        Friend( onSelected = {}, isDarkTheme = false )
    }
}