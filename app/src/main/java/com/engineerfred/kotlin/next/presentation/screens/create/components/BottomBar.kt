package com.engineerfred.kotlin.next.presentation.screens.create.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.engineerfred.kotlin.next.domain.model.PostType
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.NextTheme

@Composable
fun BottomBar(
    modifier: Modifier = Modifier,
    selected: String,
    onPostTypeSelected: (String) -> Unit,
    isDarkTheme: Boolean
) {

    val bgColor = if (isDarkTheme) Charcoal else CrimsonRed

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.clickable {
                if ( selected != PostType.Post.name ) onPostTypeSelected.invoke(PostType.Post.name)
            },
            text = "Post",
            fontWeight = FontWeight.Bold,
            color = if ( selected == PostType.Post.name ) {
                if ( isDarkTheme ) Color.White else CrimsonRed
            } else Color.Gray
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            modifier = Modifier.clickable {
                 if ( selected != PostType.Reel.name ) onPostTypeSelected.invoke(PostType.Reel.name)
            },
            text = "Reel",
            fontWeight = FontWeight.Bold,
            color = if ( selected == PostType.Reel.name ) {
                if ( isDarkTheme ) Color.White else CrimsonRed
            } else Color.Gray
        )
    }
}

@Preview( showBackground = true )
@Composable
private fun BottomBarPreview() {
    NextTheme {
        BottomBar(selected = "", onPostTypeSelected = {}, isDarkTheme = false)
    }
}