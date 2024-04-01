package com.engineerfred.kotlin.next.presentation.screens.commentReplies.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.engineerfred.kotlin.next.domain.model.Comment
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.utils.formatCommentTimeStamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers

@Composable
fun MainCommentComponent(
    comment: Comment,
    onReplyClicked: (commentOwnerName: String, commentOwnerId: String) -> Unit,
    context: Context,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
) {
    val imageRequest = ImageRequest.Builder(context)
        .data(comment.userProfileImageUrl)
        .crossfade(true)
        .allowConversionToBitmap(true)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(comment.userProfileImageUrl)
        .diskCacheKey(comment.userProfileImageUrl)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    Row(
        Modifier
            .fillMaxWidth()
            .padding(13.dp),
        verticalAlignment = Alignment.Top
    ) {

        Box(modifier = Modifier.wrapContentSize().padding(top = 6.dp), contentAlignment = Alignment.Center){
            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clickable {
                        if ( comment.userId != FirebaseAuth.getInstance().uid )
                            onUserProfileImageClicked.invoke(comment.userId)
                    }
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Charcoal else Color.LightGray)
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 6.dp)
        ) {
            Text(text = comment.userName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = comment.content)
            Spacer(modifier = Modifier.size(5.dp))
            Row {
                Text(text = formatCommentTimeStamp(comment.timestamp), color = Color.Gray)
                Spacer(modifier = Modifier.size(7.dp))
                Text(
                    text = "Reply",
                    modifier = Modifier.clickable { onReplyClicked.invoke( comment.userName, comment.userId ) },
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.FavoriteBorder,
                modifier = Modifier.clickable {
                    Toast.makeText(context,"Feature was not implemented!", Toast.LENGTH_SHORT).show()
                }.size(24.dp),
                contentDescription = null
            )
            Spacer(modifier = Modifier.size(3.dp))
            Text(text = "24", color = Color.Gray, fontSize = 12.sp)
        }
    }
    HorizontalDivider()

}