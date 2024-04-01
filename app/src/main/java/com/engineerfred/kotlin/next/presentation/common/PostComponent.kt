package com.engineerfred.kotlin.next.presentation.common

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CommentBank
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.utils.formatPostTimeStamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers

@Composable
fun PostComponent(
    post: Post,
    onComment: (String) -> Unit,
    onUserProfileImageClicked: (String) -> Unit,
    onLikePost: (postId: String, postOwnerId: String) -> Unit,
    onUnLikePost: (postId: String, postOwnerId: String) -> Unit,
    context: Context,
    isDarkTheme: Boolean,
    onImageClicked: (String) -> Unit,
) {

    val btnColor = if (isDarkTheme)  DarkSlateGrey else CrimsonRed

    val textToShow = postHeaderAnnotatedString(
        creatorName = post.ownerName,
        feeling = post.feeling,
        location = post.location,
        taggedFriends = post.friendsTagged
    )
    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
    val likedPost = currentUserId in post.likedBy

    Column(
        Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.Top
        ) {

            val imageRequest = ImageRequest.Builder(context)
                .data(post.ownerProfileImageUrl)
                .crossfade(true)
                .dispatcher(Dispatchers.IO)
                .allowConversionToBitmap(true)
                .memoryCacheKey(post.ownerProfileImageUrl)
                .diskCacheKey(post.ownerProfileImageUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        if (post.ownerId != FirebaseAuth.getInstance().uid) {
                            onUserProfileImageClicked.invoke(post.ownerId)
                        }
                    }
                    .padding(top = 4.dp)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Charcoal else Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = textToShow
                )
                Spacer(modifier = Modifier.size(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(
                                horizontal = 8.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = "Public",
                            fontWeight = FontWeight.Light,
                            fontSize = 13.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.size(7.dp))

                        Icon(
                            painter = painterResource(id = R.drawable.ic_public),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                    }
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    Spacer(modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                        .background(Color.DarkGray))
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Text(text = formatPostTimeStamp(post.postedOn), fontSize = 14.sp)
                }
            }
            Icon(imageVector = Icons.Rounded.MoreHoriz, contentDescription = null)
        }
        Spacer(modifier = Modifier.size(10.dp))
        when {
            post.caption.isNotEmpty() && post.imagesCollection.isEmpty() -> {
                //text post
                Text(text = post.caption, modifier = Modifier.padding(horizontal = 13.dp))
            }
            post.caption.isEmpty() && post.imagesCollection.isNotEmpty() -> {
                PostAsyncImage(images = post.imagesCollection, isDarkTheme = isDarkTheme, onImageClicked)
            }
            post.caption.isNotEmpty() && post.imagesCollection.isNotEmpty() -> {
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Text(text = post.caption, modifier = Modifier.padding(horizontal = 13.dp))
                    Spacer(modifier = Modifier.size(10.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(), contentAlignment = Alignment.Center) {
                        PostAsyncImage(images = post.imagesCollection, isDarkTheme = isDarkTheme, onImageClicked = onImageClicked )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.size(4.dp))
        Row (
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(onClick = {
                 if ( !likedPost ) onLikePost.invoke( post.id, post.ownerId ) else onUnLikePost.invoke( post.id, post.ownerId  )
            }, modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = btnColor,
                    disabledContentColor = Color.White,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = if ( likedPost ) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder, contentDescription = null, tint = Color.White )
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = post.likedBy.size.toString(), color = Color.White)
            }
            Spacer(modifier = Modifier.size(5.dp))
            Button(
                onClick = {
                    onComment.invoke( post.id )
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = btnColor,
                    disabledContentColor = Color.White,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Rounded.CommentBank, contentDescription = null, tint = Color.White )
                Spacer(modifier = Modifier.size(4.dp))
                Text(text = post.commentsCount.toString(), color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(
            Modifier
                .height(2.5.dp)
                .background(Color.DarkGray))
    }
}