package com.engineerfred.kotlin.next.presentation.screens.chat.componets

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.engineerfred.kotlin.next.domain.model.ChatMessage
import com.engineerfred.kotlin.next.domain.model.MessageType
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CoralRed
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder

@Composable
fun ChatMessageItem(
    chatMessage: ChatMessage,
    isDarkTheme: Boolean,
    onPlayChatVideo: (String) -> Unit,
    onViewChatImage: (String) -> Unit,
    context: Context
) {
    val isFromCurrentUser = chatMessage.senderId == FirebaseAuth.getInstance().uid

    val chatsColor = if (isDarkTheme) {
        if ( chatMessage.type == MessageType.TextOnly.name ) {
            if ( isFromCurrentUser ) Charcoal else DarkSlateGrey
        } else Color.Transparent
    } else {
        if ( chatMessage.type == MessageType.TextOnly.name ) {
            if ( isFromCurrentUser ) CrimsonRed.copy(alpha = .7f) else CoralRed
        } else Color.Transparent
    }

    val captionBgColor = if (isDarkTheme) {
        if ( isFromCurrentUser ) Charcoal else DarkSlateGrey
    } else {
        if ( isFromCurrentUser ) CrimsonRed.copy(alpha = .7f) else CoralRed
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isFromCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(
                    end = if (isFromCurrentUser) 0.dp else 40.dp,
                    start = if (isFromCurrentUser) 40.dp else 0.dp
                )
                .clip(RoundedCornerShape(8.dp))
                .background(chatsColor),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = if ( isFromCurrentUser) Alignment.End else Alignment.Start
        ) {
            when (chatMessage.type) {
                MessageType.TextOnly.name -> {
                    Text(text = chatMessage.content!!, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = Color.White)
                }
                MessageType.ImageOnly.name -> {
                    val imageUrl = URLEncoder.encode(chatMessage.mediaUrl, "utf-8")
                    AsyncImage(
                        model = chatMessage.mediaUrl,
                        modifier = Modifier
                            .clickable {
                                onViewChatImage.invoke(imageUrl)
                            }
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .heightIn(max = 260.dp),
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
                }
                MessageType.ImageWithCaption.name -> {
                    val imageUrl = URLEncoder.encode(chatMessage.mediaUrl, "utf-8")
                    AsyncImage(
                        model = chatMessage.mediaUrl,
                        modifier = Modifier
                            .clickable {
                                onViewChatImage.invoke(imageUrl)
                            }
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .heightIn(max = 260.dp),
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = chatMessage.content!!,
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(captionBgColor)
                            .padding(12.dp),
                        color = Color.White,
                        lineHeight = 17.sp
                    )
                }
                MessageType.VideoOnly.name -> {
                    val model = ImageRequest.Builder(context)
                        .data(chatMessage.mediaUrl)
                        .videoFrameMillis(10000)
                        .decoderFactory { result, options, _ ->
                            VideoFrameDecoder(
                                result.source,
                                options
                            )
                        }.build()

                    val videoUrl = URLEncoder.encode(chatMessage.mediaUrl, "utf-8")

                    Box(
                        modifier = Modifier
                            .height(260.dp)
                    ){
                        AsyncImage(
                            model = model,
                            modifier = Modifier
                                .wrapContentSize()
                                .heightIn(max = 260.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                        IconButton(
                            onClick = {
                                onPlayChatVideo.invoke(videoUrl)
                            },
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = .5f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(70.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
                MessageType.VideoWithCaption.name -> {
                    val model = ImageRequest.Builder(context)
                        .data(chatMessage.mediaUrl)
                        .videoFrameMillis(10000)
                        .decoderFactory { result, options, _ ->
                            VideoFrameDecoder(
                                result.source,
                                options
                            )
                        }.build()

                    val videoUrl = URLEncoder.encode(chatMessage.mediaUrl, "utf-8")

                    Box(modifier = Modifier
                        .heightIn(max = 260.dp)
                    ) {
                        AsyncImage(
                            model = model,
                            modifier = Modifier
                                .wrapContentSize()
                                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                .heightIn(max = 260.dp),
                            contentScale = ContentScale.Fit,
                            contentDescription = null
                        )
                        IconButton(
                            onClick = {
                                onPlayChatVideo.invoke(videoUrl)
                            },
                            modifier = Modifier
                                .size(70.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = .5f))
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = chatMessage.content!!,
                        modifier = Modifier
                            .wrapContentSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(captionBgColor)
                            .padding(12.dp),
                        color = Color.White,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.size(8.dp))
}