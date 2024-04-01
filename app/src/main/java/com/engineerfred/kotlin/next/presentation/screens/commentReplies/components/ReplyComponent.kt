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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
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
import com.engineerfred.kotlin.next.domain.model.Reply
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.SteelBlue
import com.engineerfred.kotlin.next.utils.formatCommentTimeStamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers

@Composable
fun ReplyComponent(
    reply: Reply,
    onReplyClicked: (commentOwnerName: String, commentOwnerId: String) -> Unit,
    context: Context,
    commentOwnerId: String,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
) {
    val imageRequest = ImageRequest.Builder(context)
        .data(reply.userProfileImageUrl)
        .crossfade(true)
        .allowConversionToBitmap(true)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(reply.userProfileImageUrl)
        .diskCacheKey(reply.userProfileImageUrl)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    val text = when{
        commentOwnerId == FirebaseAuth.getInstance().uid -> {
            when{
                reply.userId == commentOwnerId -> {
                    //the post owner is
                    if ( reply.userId == reply.replyingToId  ) {
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = SteelBlue)){
                                append("You")
                            }
                        }
                    } else {
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = SteelBlue)){
                                append("You")
                            }
                            append(" to ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                                append(reply.replyingToName)
                            }
                        }
                    }
                }
                else -> {
                    buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                            append(reply.userName)
                        }
                        append(" to ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                            append(reply.replyingToName)
                        }
                    }
                }
            }
        }
        else -> {
            when {
                reply.userId == FirebaseAuth.getInstance().uid -> {
                    if ( reply.userId == reply.replyingToId ) { //the loggedin user was replying to himself
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = SteelBlue)){
                                append("YOU")
                            }
                        }
                    }else { //he wasn't replying to himself
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = SteelBlue)){
                                append("You")
                            }
                            append(" to ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                                append(reply.replyingToName)
                            }
                        }
                    }
                }
                else -> { //for replies where the person who replied is not the one currently logged in
                    if ( reply.userId == reply.replyingToId ) {
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                                append(reply.userName)
                            }
                        }
                    }else {
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                                append(reply.userName)
                            }
                            append(" to ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)){
                                append(reply.replyingToName)
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .clickable {
                onReplyClicked.invoke(reply.userName, reply.userId)
            }
            .padding(start = 40.dp, end = 13.dp, bottom = 13.dp, top = 10.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Box(modifier = Modifier.wrapContentSize().padding(top = 6.dp), contentAlignment = Alignment.Center){
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clickable {
                        if ( reply.userId != FirebaseAuth.getInstance().uid )
                            onUserProfileImageClicked.invoke(reply.userId)
                    }.size(25.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) Charcoal else Color.LightGray)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Text(text = text, fontSize = 13.sp, modifier = Modifier.padding(end = 5.dp))
                Text(text = reply.content)
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Text(text = formatCommentTimeStamp(reply.timestamp), color = Color.Gray)
                    Spacer(modifier = Modifier.size(7.dp))
                    Text(
                        text = "Reply",
                        modifier = Modifier.clickable { onReplyClicked.invoke(reply.userName, reply.userId) },
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
                    }.size(18.dp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(3.dp))
                Text(text = "4", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}
