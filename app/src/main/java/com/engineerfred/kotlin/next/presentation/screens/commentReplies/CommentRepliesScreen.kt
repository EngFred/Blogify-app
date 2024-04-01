package com.engineerfred.kotlin.next.presentation.screens.commentReplies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.ui.focus.FocusDirection
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
import com.engineerfred.kotlin.next.presentation.screens.commentReplies.components.MainCommentComponent
import com.engineerfred.kotlin.next.presentation.screens.commentReplies.components.ReplyComponent
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed

@Composable
fun CommentRepliesScreen(
    commentId: String,
    postId: String,
    postOwnerName: String,
    commentOwnerName: String,
    commentOwnerId: String,
    onBackClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    commonViewModel: CommonViewModel,
    viewModel: CommentRepliesViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsState().value
    val focusManager = LocalFocusManager.current
    val currentUser = commonViewModel.currentUser
    val context = LocalContext.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
        ){
            IconButton(onClick = { onBackClicked.invoke() }, modifier = Modifier.align(Alignment.TopStart)) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(
                text = "Replies",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(onClick = { onSearchClicked.invoke() }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = null, tint = Color.White)
            }
        }
        HorizontalDivider()
        if ( currentUser ==  null ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), contentAlignment = Alignment.Center
            ){
                Text(text = "Hmm :(, Current user is null!", fontWeight = FontWeight.Bold)
            }
        } else {
            viewModel.setCurrentUser(currentUser)
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator( color = if (isDarkTheme) Color.White else CrimsonRed, modifier = Modifier.size(21.dp))
                    }
                }
                !uiState.isLoading  && !uiState.loadError.isNullOrEmpty() -> {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.loadError,
                            color = Color.Red,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.onEvent(CommentRepliesUiEvents.RetryClicked) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Retry", color = if (isDarkTheme) Color.White else CrimsonRed)
                        }
                    }
                }
                !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
                    val comment = uiState.comment
                    if ( comment != null ) {
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            item {
                                MainCommentComponent(comment = comment, onReplyClicked = { commentOwnerName, commentOwnerId ->
                                    focusManager.moveFocus(FocusDirection.Up)
                                    viewModel.onEvent( CommentRepliesUiEvents.ReplyTo( commentOwnerName, commentOwnerId ) )
                                }, context = context, isDarkTheme = isDarkTheme, onUserProfileImageClicked = onUserProfileImageClicked)
                                Spacer(modifier = Modifier.size(8.dp))
                            }
                            when {
                                uiState.loadingCommentReplies -> {
                                    item {
                                        Spacer(modifier = Modifier.size(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            contentAlignment = Alignment.Center
                                        ){
                                            CircularProgressIndicator( color = if (isDarkTheme) Color.White else CrimsonRed, modifier = Modifier.size(21.dp))
                                        }
                                    }
                                }
                                !uiState.loadingCommentReplies  && !uiState.commentRepliesLoadError.isNullOrEmpty() -> {
                                    item {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(modifier = Modifier.size(10.dp))
                                            Text(
                                                text = uiState.commentRepliesLoadError,
                                                modifier = Modifier.fillMaxWidth(),
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.size(10.dp))
                                            OutlinedButton(
                                                onClick = { viewModel.onEvent(CommentRepliesUiEvents.RetryClicked) },
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(text = "Retry", color = if (isDarkTheme) Color.White else CrimsonRed)
                                            }
                                        }
                                    }
                                }
                                !uiState.loadingCommentReplies  && uiState.commentRepliesLoadError.isNullOrEmpty() -> {
                                    val commentReplies = uiState.commentReplies
                                    if ( commentReplies.isEmpty() ) {
                                       item {
                                           Spacer(modifier = Modifier.size(10.dp))
                                           Box(modifier = Modifier
                                               .fillMaxWidth()
                                               .weight(1f), contentAlignment = Alignment.Center
                                           ){
                                               Text(text = "No replies yet!", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                           }
                                       }
                                    } else {
                                        items(count = commentReplies.size, key = { commentReplies[it].id }) {
                                            val reply = commentReplies[it]
                                            ReplyComponent(
                                                reply = reply,
                                                onReplyClicked = { commentOwnerName, commentOwnerId ->
                                                    focusManager.moveFocus(FocusDirection.Up)
                                                    viewModel.onEvent( CommentRepliesUiEvents.ReplyTo( commentOwnerName, commentOwnerId ) )
                                                } , commentOwnerId = commentOwnerId,
                                                context = context, isDarkTheme = isDarkTheme, onUserProfileImageClicked = onUserProfileImageClicked
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
                                .padding(start = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                modifier = Modifier.weight(1f).heightIn(max=155.dp, min=55.dp),
                                value = uiState.replyTextValue,
                                onValueChange = {
                                    viewModel.onEvent(CommentRepliesUiEvents.ReplyTextChanged(it))
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = if (isDarkTheme) Color.White else CrimsonRed
                                ),
                                maxLines = 5,
                                placeholder = {
                                    Text(
                                        text = if (uiState.replyingTo.isEmpty()) "Write a reply..." else "Reply to ${uiState.replyingTo}",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 13.sp
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = 13.sp
                                ),
                                shape = RoundedCornerShape(33.dp),
                                keyboardOptions = KeyboardOptions( imeAction = ImeAction.Done ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                )
                            )
                            IconButton(onClick = {
                                viewModel.onEvent( CommentRepliesUiEvents.ReplyButtonClicked )
                                focusManager.clearFocus()
                            }, enabled = uiState.replyTextValue.isNotEmpty()) {
                                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null )
                            }
                        }
                        Spacer(modifier = Modifier.size(6.dp))
                    } else {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), contentAlignment = Alignment.Center
                        ){
                            Text(text = "Comment not found!!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

}