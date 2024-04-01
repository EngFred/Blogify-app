package com.engineerfred.kotlin.next.presentation.screens.people.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import kotlinx.coroutines.Dispatchers
import java.util.Locale

@Composable
fun PersonComponent(
    user: User,
    onUserClicked: (String) -> Unit,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onFollowBack: (String) -> Unit,
    context: Context,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
    currentUser:User
) {

    val btnColor = if ( isDarkTheme ) Charcoal else CrimsonRed
    val userName = "${user.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${user.lastName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }}"

    val followedUser = currentUser.id in user.followers && user.id !in currentUser.followers

    val followedBackUser = user.id in currentUser.followers && currentUser.id !in user.followers

    val followUser = currentUser.id !in user.followers && user.id !in currentUser.followers

    var isFollowing by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = followUser, key2 = followedUser, key3 = followedBackUser) {
        isFollowing = false
    }

    val btnText = when {
        followUser-> "Follow"
        followedUser -> "Following"
        followedBackUser -> "Follow back"
        else -> ""
    }

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
            .fillMaxWidth()
            .clickable { onUserClicked.invoke(user.id) }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {

        AsyncImage(
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clickable {
                    onUserProfileImageClicked.invoke(user.id)
                }
                .size(75.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) Charcoal else Color.LightGray)
        )
        Spacer(modifier = Modifier.size(11.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            Text(
                text = userName,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if ( followUser || followedBackUser ) {
                Button(
                    onClick = {
                        isFollowing = !isFollowing
                        when {
                            followUser -> onFollow.invoke(user.id)
                            followedBackUser -> onFollowBack.invoke(user.id)
                        }
                    }, enabled = !isFollowing,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        disabledContainerColor = btnColor,
                        containerColor = btnColor,
                        disabledContentColor = Color.White,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .height(38.dp)
                        .defaultMinSize(1.dp, 1.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp)
                ) {
                    if ( isFollowing ) {
                        CircularProgressIndicator( modifier = Modifier.size(23.dp), color = Color.White )
                    } else {
                        Text(text = btnText, fontSize = 15.sp, color = Color.White)
                    }
                }
            }
            if ( followedUser ) {
                OutlinedButton(
                    onClick = {
                        onUnfollow.invoke(user.id)
                    }, enabled = !isFollowing,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(200.dp)
                        .height(38.dp)
                        .defaultMinSize(1.dp, 1.dp),
                    contentPadding = PaddingValues(horizontal = 40.dp, vertical = 5.dp)
                ) {
                    if ( isFollowing ) {
                        CircularProgressIndicator( modifier = Modifier.size(23.dp), color = if (isDarkTheme) Color.White else CrimsonRed )
                    } else {
                        Text(text = btnText, fontSize = 15.sp, color = if (isDarkTheme) Color.White else CrimsonRed)
                    }
                }
            }
        }
        Icon(imageVector = Icons.Rounded.MoreVert, contentDescription = null)

    }
}

//@Preview( showBackground = true )
//@Composable
//private fun PersonComponentPreview() {
//    NextTheme {
//        PersonComponent(
//            user = User(
//            id = "", email = "",
//            lastName = "Wasswa",
//            firstName = "Douglaus",
//            profileImageUrl = "",
//            joinDate = 0L
//        ), {}, onFollow = {}, LocalContext.current, isDarkTheme = false, {}, {},{} User())
//    }
//}