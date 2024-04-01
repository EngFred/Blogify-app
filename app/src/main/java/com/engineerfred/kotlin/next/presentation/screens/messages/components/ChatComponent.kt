package com.engineerfred.kotlin.next.presentation.screens.messages.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.engineerfred.kotlin.next.domain.model.User
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import kotlinx.coroutines.Dispatchers
import java.util.Locale

@Composable
fun ChatComponent(
    isDarkTheme: Boolean,
    onChatClicked: (receiverId: String) -> Unit,
    user: User,
    context: Context
) {

    val userName = "${user.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${user.lastName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }}"

    val imageRequest = ImageRequest.Builder(context)
        .data(user.profileImageUrl)
        .crossfade(true)
        .allowConversionToBitmap(true)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(user.profileImageUrl)
        .diskCacheKey(user.profileImageUrl)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Row(
        Modifier
            .clickable { onChatClicked.invoke(user.id) }
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) Charcoal else Color.LightGray)
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Tap to start chatting",
                fontSize = 13.sp,
                color = if ( isDarkTheme ) Color.LightGray else Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}