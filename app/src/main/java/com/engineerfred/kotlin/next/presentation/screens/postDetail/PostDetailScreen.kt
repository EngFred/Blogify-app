package com.engineerfred.kotlin.next.presentation.screens.postDetail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.common.PostComponent
import com.engineerfred.kotlin.next.presentation.screens.postDetail.components.CommentComponent
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed

@Composable
fun PostDetailScreen(
    postId: String,
    onBackClicked: () -> Unit,
    onReplyClicked: (commentId: String, postId: String, postOwnerName: String,  commentOwnerName: String, commentOwnerId: String ) -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
    onPostImageClicked: (String) -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    val currentUser = commonViewModel.currentUser

    Column(
        Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp, horizontal = 14.dp)
        ) {
            IconButton(
                onClick = { onBackClicked() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
            Text(
                text = "Post Detail",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if ( currentUser != null ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
                        CircularProgressIndicator( color = if (isDarkTheme) Color.White else CrimsonRed, modifier = Modifier.size(24.dp) )
                    }
                }
                !uiState.loadError.isNullOrEmpty() && !uiState.isLoading-> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Rounded.Error, contentDescription = null, Modifier.size(50.dp) )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(text = uiState.loadError, textAlign = TextAlign.Center)
                            Spacer(modifier = Modifier.size(10.dp))
                            OutlinedButton(
                                onClick = { viewModel.onEvent( PostDetailEvents.RetryClicked ) },
                                shape = RoundedCornerShape( 8.dp )
                            ) {
                                Text(text = "Retry", color = if (isDarkTheme) Color.White else CrimsonRed)
                            }
                        }
                    }
                }

                !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
                    viewModel.setCurrentUser(currentUser)
                    if ( uiState.post == null ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
                            Text(text = "Post not found!!", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxWidth().weight(1f)
                        ) {
                            item {
                                PostComponent(
                                    post = uiState.post,
                                    {},
                                    onLikePost = {_, postOwnerId ->
                                        viewModel.onEvent( PostDetailEvents.PostLiked(postOwnerId) )
                                    },
                                    onUnLikePost = { _, postOwnerId ->
                                        viewModel.onEvent( PostDetailEvents.PostUnLiked(postOwnerId) )
                                    },
                                    onUserProfileImageClicked = onUserProfileImageClicked,
                                    context = context,
                                    isDarkTheme = isDarkTheme,
                                    onImageClicked = onPostImageClicked
                                )
                                Text(
                                    text = "Comments:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 10.dp)
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                            }
                            when{
                                uiState.loadingPostComments -> {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                                            CircularProgressIndicator( color = if (isDarkTheme) Color.White else CrimsonRed, modifier = Modifier.size(21.dp))
                                            Spacer(modifier = Modifier.size(10.dp))
                                        }
                                    }
                                }
                                !uiState.loadingPostComments && !uiState.postCommentsLoadError.isNullOrEmpty() -> {
                                    item {
                                        Column(
                                            Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = uiState.postCommentsLoadError,
                                                color = Color.Red,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.size(10.dp))
                                            OutlinedButton(
                                                onClick = { viewModel.onEvent(PostDetailEvents.RetryLoadingPostComments) },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(text = "Retry")
                                            }
                                        }
                                        Spacer(modifier = Modifier.size(10.dp))
                                    }
                                }

                                !uiState.loadingPostComments && uiState.postCommentsLoadError.isNullOrEmpty() -> {
                                    if ( uiState.postComments.isEmpty() ) {
                                        item {
                                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                                                Text(
                                                    text = "No comments yet!",
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    } else {
                                        val comments = uiState.postComments
                                        items(count = comments.size, key = { comments[it].id }) {
                                            val comment = comments[it]
                                            CommentComponent(
                                                comment = comment,
                                                onReplyClicked = { commentId, commentOwnerName, commentOwnerId ->
                                                      onReplyClicked.invoke(commentId, postId, uiState.post.ownerName, commentOwnerName, commentOwnerId )
                                                },
                                                context = context, isDarkTheme = isDarkTheme, onUserProfileImageClicked
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        HorizontalDivider()
                        Spacer(modifier = Modifier.size(6.dp))
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp)
                                ,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = uiState.inputTextValue,
                                onValueChange = {
                                    viewModel.onEvent(PostDetailEvents.CommentTextChanged(it))
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = if (isDarkTheme) Color.White else CrimsonRed
                                ),
                                placeholder = {
                                    Text(text = if ( !uiState.isReplying ) "Write a comment..." else "Write a reply...", fontSize = 13.sp)
                                },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions( imeAction = ImeAction.Done ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )
                            IconButton(onClick = {
                                viewModel.onEvent( PostDetailEvents.Commented)
                                focusManager.clearFocus()
                            }, enabled = uiState.inputTextValue.isNotEmpty() && uiState.postCommentsLoadError.isNullOrEmpty()) {
                                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null )
                            }
                        }
                        Spacer(modifier = Modifier.size(6.dp))
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
                Text(
                    text = "Hmm :(, Current user is null!",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}