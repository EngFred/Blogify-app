package com.engineerfred.kotlin.next.presentation.screens.chatWithGemini.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CoralRed
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey

@Composable
fun AiChatItem(
    isFromUser: Boolean,
    prompt: String,
    imageUrl: String?,
    isDarkTheme: Boolean
) {
    val chatsColor = if (isDarkTheme) {
        if (imageUrl==null) {
            if ( isFromUser ) Charcoal else DarkSlateGrey
        } else Color.Transparent
    } else {
        if ( imageUrl == null ){
            if ( isFromUser ) CrimsonRed.copy(alpha = .7f) else CoralRed
        } else Color.Transparent
    }

    val captionBgColor = if (isDarkTheme) {
        if ( isFromUser ) Charcoal else DarkSlateGrey
    } else {
        if ( isFromUser ) CrimsonRed.copy(alpha = .7f) else CoralRed
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(
                    end = if (isFromUser) 0.dp else 40.dp,
                    start = if (isFromUser) 40.dp else 0.dp
                )
                .clip(RoundedCornerShape(8.dp))
                .background(chatsColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if ( isFromUser ) Alignment.End else Alignment.Start
        ) {
            if ( imageUrl != null ) {
                AsyncImage(
                    model = imageUrl,
                    modifier = Modifier
                        .wrapContentWidth()
                        .clip(RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp))
                        .heightIn(max = 260.dp),
                    contentScale = ContentScale.Fit,
                    contentDescription = null
                )
                Text(
                    text = prompt,
                    modifier = Modifier
                        .wrapContentSize()
                        .background(captionBgColor)
                        .padding(12.dp),
                    color = Color.White,
                    lineHeight = 17.sp
                )
            } else {
                Text(text = prompt, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Color.White)
            }
        }
    }
    Spacer(modifier = Modifier.size(8.dp))
}