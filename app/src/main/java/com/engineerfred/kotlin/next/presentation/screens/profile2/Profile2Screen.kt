package com.engineerfred.kotlin.next.presentation.screens.profile2

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.common.PostComponent
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.utils.formatTimestampToDateString
import com.engineerfred.kotlin.next.utils.pluralizeWord
import java.net.URLEncoder
import java.util.Locale

@Composable
fun Profile2Screen(
    onBackClicked: () -> Unit,
    onComment: () -> Unit,
    viewModel: Profile2ViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    userId: String,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
    onImageClicked: (String) -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState().value

    val currentUser = commonViewModel.currentUser
    val context = LocalContext.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    if ( !uiState.error.isNullOrEmpty() ) {
        Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                CircularProgressIndicator( modifier = Modifier.size(22.dp), color = if (isDarkTheme) Color.White else CrimsonRed )
            }
        }
        !uiState.isLoading && !uiState.loadError.isNullOrEmpty() -> {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), contentAlignment = Alignment.Center){
                Text(text = uiState.loadError, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
        !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
            if ( uiState.otherUser == null ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp), contentAlignment = Alignment.Center){
                    Text(text = "User not found!", modifier = Modifier.fillMaxWidth(), fontSize = 24.sp, textAlign = TextAlign.Center)
                }
            } else {

                val user = uiState.otherUser
                val userName = "${
                    user.firstName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }} ${user.lastName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"

                if ( currentUser != null ) {
                    LaunchedEffect(key1 = true) {
                        viewModel.initializeState(currentUser)  //HERE we are sure that we have both the currentUser and the otherUser
                        Log.i("PInit", "Following user: ${uiState.followedUser}\nFollow back user: ${uiState.followBackUser}\nFriends: ${uiState.friends}\nNot Friends: ${uiState.notFriends}")
                    }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(vertical = 8.dp, horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            IconButton(onClick = { onBackClicked.invoke() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back),
                                    contentDescription = null,
                                    modifier = Modifier.size(35.dp),
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = userName,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        HorizontalDivider()
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            item {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                ) {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                    ) {
                                        AsyncImage(
                                            model = user.coverImageUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .clickable {
                                                    if ( !user.coverImageUrl.isNullOrEmpty() ) {
                                                        val coverImageUrl = URLEncoder.encode(user.coverImageUrl, "utf-8")
                                                        onImageClicked.invoke(coverImageUrl)
                                                    } else {
                                                        Toast.makeText(context, "No cover image!", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                                .align(Alignment.TopStart)
                                                .fillMaxWidth()
                                                .height(150.dp)
                                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                                        )
                                    }
                                    Box(modifier = Modifier.width(160.dp)) {
                                        AsyncImage(
                                            model = user.profileImageUrl,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(top = 80.dp, start = 14.dp)
                                                .clip(CircleShape)
                                                .size(120.dp)
                                                .background(if (isDarkTheme) DarkSlateGrey else Color.DarkGray)
                                                .border(
                                                    1.dp,
                                                    if (isDarkTheme) Color.White else CrimsonRed,
                                                    CircleShape
                                                ),
                                        )
                                    }
                                    when {
                                        uiState.followedUser -> {
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.onEvent( Profile2UiEvents.FollowButtonClicked )
                                                },
                                                enabled = !uiState.followingUnfollowingUserInProgress,
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .height(45.dp)
                                                    .padding(end = 10.dp)
                                                    .align(Alignment.BottomEnd),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    disabledContentColor =  if ( isDarkTheme ) Color.White else CrimsonRed,
                                                    containerColor = Color.Transparent
                                                )
                                            ) {
                                                if ( !uiState.followingUnfollowingUserInProgress ){
                                                    Text(text = "Following", fontSize = 16.sp, color =  if (isDarkTheme) Color.White else CrimsonRed )
                                                }else {
                                                    CircularProgressIndicator( modifier = Modifier.size(22.dp), color = Color.White )
                                                }
                                            }
                                        }
                                        uiState.friends -> {
                                            OutlinedButton(
                                                onClick = {
                                                    viewModel.onEvent( Profile2UiEvents.FollowButtonClicked )
                                                },
                                                enabled = !uiState.followingUnfollowingUserInProgress,
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .height(45.dp)
                                                    .padding(end = 10.dp)
                                                    .align(Alignment.BottomEnd),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    disabledContentColor =  if ( isDarkTheme ) Color.White else CrimsonRed,
                                                    containerColor = Color.Transparent
                                                )
                                            ) {
                                                if ( !uiState.followingUnfollowingUserInProgress ){
                                                    Text(text = "Friends", fontSize = 16.sp, color =  if (isDarkTheme) Color.White else CrimsonRed )
                                                }else {
                                                    CircularProgressIndicator( modifier = Modifier.size(22.dp), color = Color.White )
                                                }
                                            }
                                        }
                                        uiState.followBackUser -> {
                                            Button(
                                                onClick = {
                                                    viewModel.onEvent( Profile2UiEvents.FollowButtonClicked )
                                                },
                                                enabled = !uiState.followingUnfollowingUserInProgress,
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .height(45.dp)
                                                    .padding(end = 10.dp)
                                                    .align(Alignment.BottomEnd),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    disabledContainerColor = if ( isDarkTheme ) DarkSlateGrey else CrimsonRed,
                                                    containerColor = if ( isDarkTheme ) DarkSlateGrey else CrimsonRed,
                                                    disabledContentColor = Color.White
                                                )
                                            ) {
                                                if ( !uiState.followingUnfollowingUserInProgress ){
                                                    Text(text = "Follow back", fontSize = 16.sp, color = Color.White )
                                                }else {
                                                    CircularProgressIndicator( modifier = Modifier.size(22.dp), color = Color.White )
                                                }
                                            }
                                        }
                                        uiState.notFriends -> {
                                            Button(
                                                onClick = {
                                                    viewModel.onEvent( Profile2UiEvents.FollowButtonClicked )
                                                },
                                                enabled = !uiState.followingUnfollowingUserInProgress,
                                                modifier = Modifier
                                                    .width(200.dp)
                                                    .height(45.dp)
                                                    .padding(end = 10.dp)
                                                    .align(Alignment.BottomEnd),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    disabledContainerColor = if ( isDarkTheme ) DarkSlateGrey else CrimsonRed,
                                                    containerColor = if ( isDarkTheme ) DarkSlateGrey else CrimsonRed,
                                                    disabledContentColor = Color.White
                                                )
                                            ) {
                                                if ( !uiState.followingUnfollowingUserInProgress ){
                                                    Text(text = "Follow", fontSize = 16.sp, color = Color.White )
                                                }else {
                                                    CircularProgressIndicator( modifier = Modifier.size(22.dp), color = Color.White )
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = userName,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                                Text(
                                    text = user.email,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(horizontal = 14.dp)
                                )
                                if ( !user.about.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = user.about,
                                        modifier = Modifier.padding(horizontal = 14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Rounded.DateRange, contentDescription = null )
                                    Text(
                                        text = formatTimestampToDateString(user.joinDate),
                                        color = if ( isDarkTheme  ) Color.White else Color.DarkGray,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(7.dp))
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Text(
                                            text = user.following.size.toString(),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        Text(
                                            text = "Following"
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Text(
                                            text = user.followers.size.toString(),
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))
                                        Text(
                                            text = pluralizeWord("Follower", user.followers.size.toLong())
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider( Modifier.height(4.dp) )
                            }
                            when{
                                uiState.isGettingUserPosts -> {
                                    item {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            contentAlignment = Alignment.Center
                                        ){
                                            CircularProgressIndicator( modifier = Modifier.size(23.dp), color = if (isDarkTheme) Color.White else CrimsonRed )
                                        }
                                    }
                                }
                                !uiState.isGettingUserPosts && !uiState.gettingUserPostError.isNullOrEmpty() -> {
                                    item {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 15.dp)
                                                .weight(1f),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = uiState.gettingUserPostError,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                color = Color.Red
                                            )
                                            Spacer(modifier = Modifier.size(6.dp))
                                            OutlinedButton(
                                                onClick = { viewModel.onEvent(Profile2UiEvents.RetryClicked) },
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text(
                                                    text = "Retry",
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isDarkTheme) Color.White else CrimsonRed
                                                )
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    if ( uiState.userPosts.isEmpty() ) {
                                        item {
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                                contentAlignment = Alignment.Center
                                            ){
                                                Text(
                                                    text = "User has no posts!",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    } else {
                                        val posts = uiState.userPosts
                                        items(
                                            count = posts.size,
                                            key = { posts[it].id }
                                        ){
                                            val post = posts[it]
                                            PostComponent(
                                                post = post,
                                                onComment = {
                                                    Toast.makeText(context,"This feature was not implemented here! But still you can comment and view post comments from the feeds.", Toast.LENGTH_LONG).show()
                                                },
                                                onLikePost = { postId, postOwnerId ->
                                                    viewModel.onEvent(Profile2UiEvents.PostLiked(postId, postOwnerId))
                                                },
                                                onUnLikePost = { postId, _ ->
                                                      viewModel.onEvent(Profile2UiEvents.PostUnLiked(postId))
                                                },
                                                context = context,
                                                isDarkTheme = isDarkTheme,
                                                onUserProfileImageClicked = onUserProfileImageClicked,
                                                onImageClicked = onImageClicked
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp), contentAlignment = Alignment.Center){
                        Text(text = "Current User is null!", modifier = Modifier.fillMaxWidth(), fontSize = 24.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}