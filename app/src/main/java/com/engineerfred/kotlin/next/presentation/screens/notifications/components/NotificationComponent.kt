package com.engineerfred.kotlin.next.presentation.screens.notifications.components

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.engineerfred.kotlin.next.domain.model.Notification
import com.engineerfred.kotlin.next.domain.model.NotificationType
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.utils.formatTimestampToDateAndTimeString
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers

@Composable
fun NotificationComponent(
    notification: Notification,
    onFollowNotificationClicked: (idOfTheUserWhoFollowed:String) -> Unit,
    onLikeOrCommentNotificationClicked: (postId:String) -> Unit,
    onCommentReplyNotificationClicked: (commentId:String) -> Unit,
    context: Context,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit
) {

    val content  = when (notification.type) {
        NotificationType.Follow.name -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    append(notification.fromUserName)
                }
                append(" started following you.")
            }
        }
        NotificationType.Like.name -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    append(notification.fromUserName)
                }
                append(" liked your post.")
            }
        }
        NotificationType.Comment.name -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    append(notification.fromUserName)
                }
                append(" commented on your post.")
            }
        }
        NotificationType.ReplyToComment.name -> {
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    append(notification.fromUserName)
                }
                append(" replied to your comment on ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    if ( notification.postOwnerId != FirebaseAuth.getInstance().uid )
                        append("${notification.postOwnerName}'s post.")
                    else append("your post.")
                }
            }
        }
        NotificationType.FollowBack.name -> {
            buildAnnotatedString {
                append("You are now friends with ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                    append(notification.fromUserName)
                }
            }
        }
        else -> {
            buildAnnotatedString { append("") }
        }
    }

    val bgColor = if (  !isDarkTheme ) {
        if ( notification.read  ) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = .4f)
    } else {
        if ( notification.read  ) Color.Transparent else Charcoal
    }
    
    val imageRequest = ImageRequest.Builder(context)
        .data(notification.fromUserProfileImageUrl)
        .crossfade(true)
        .allowConversionToBitmap(true)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(notification.fromUserProfileImageUrl)
        .diskCacheKey(notification.fromUserProfileImageUrl)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()


    Row(
        Modifier
            .fillMaxWidth()
            .clickable {
                when (notification.type) {
                    NotificationType.Follow.name, NotificationType.FollowBack.name -> onFollowNotificationClicked.invoke(
                        notification.fromUserId ?: "none"
                    )
                    NotificationType.Like.name, NotificationType.Comment.name -> {
                        onLikeOrCommentNotificationClicked.invoke(notification.postId ?: "none")
                    }
                    NotificationType.ReplyToComment.name -> { //the post id for this case is the comment id
                        onCommentReplyNotificationClicked.invoke( notification.postId ?: "none" )
                    }
                }
            }
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {

        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clickable {
                    notification.fromUserId?.let {
                        if ( it != FirebaseAuth.getInstance().uid ) {
                            onUserProfileImageClicked.invoke(it)
                        }
                    }
                }
                .size(80.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) DarkSlateGrey else Color.LightGray)
        )
        Spacer(modifier = Modifier.size(11.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = content,
                fontSize = 18.sp,
                //color = Color.White
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = formatTimestampToDateAndTimeString(notification.postedAt),
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
        Icon(
            imageVector = Icons.Rounded.MoreVert,
            contentDescription = null,
            //tint = Color.White
        )
    }
    HorizontalDivider()
}
//
//@Preview( showBackground = true)
//@Composable
//private fun NotificationComponentPreview() {
//    NextTheme {
//        NotificationComponent(
//            notification = Notification(
//
//            ),
//            onFollowNotificationClicked = {}, {}, LocalContext.current, false
//        )
//    }
//}