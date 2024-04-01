package com.engineerfred.kotlin.next.presentation.screens.notifications

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.screens.notifications.components.NotificationComponent

@Composable
fun NotificationsScreen(
    onSearchClicked: () -> Unit,
    onFollowNotificationClicked: (String) -> Unit,
    onLikeOrCommentNotificationClicked: (String) -> Unit,
    onCommentReplyNotificationClicked: (String) -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 8.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Notifications",
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton( onClick = { onSearchClicked.invoke() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
        }
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), contentAlignment = Alignment.Center){
                    CircularProgressIndicator( modifier = Modifier.size(22.dp) )
                }
            }
            !uiState.loadError.isNullOrEmpty() && !uiState.isLoading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(20.dp), contentAlignment = Alignment.Center
                ){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.loadError,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.onEvent( NotificationsUiEvents.RetryClicked ) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp),
                        ) {
                            Text(
                                text = "Retry",
                                fontSize = 28.sp,
                            )
                        }
                    }
                }
            }
            !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
                if ( uiState.notifications.isEmpty() ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), contentAlignment = Alignment.Center){
                        Text(
                            text = "You have no notifications yet!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val notifications= uiState.notifications
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                          items( count = notifications.size, key = { notifications[it].id } ) {
                              val notification = notifications[it]
                              NotificationComponent(
                                  notification = notification,
                                  onFollowNotificationClicked = {
                                      viewModel.onEvent(
                                          NotificationsUiEvents.NotificationClicked(
                                              notification.id,
                                              notification.read
                                          )
                                      )
                                      onFollowNotificationClicked.invoke(it)
                                  },
                                  onLikeOrCommentNotificationClicked = {
                                      viewModel.onEvent(
                                          NotificationsUiEvents.NotificationClicked(
                                              notification.id,
                                              notification.read
                                          )
                                      )
                                      onLikeOrCommentNotificationClicked.invoke(it)
                                  },
                                  context = context,
                                  isDarkTheme = isDarkTheme,
                                  onUserProfileImageClicked = onUserProfileImageClicked,
                                  onCommentReplyNotificationClicked = {
                                      viewModel.onEvent(
                                          NotificationsUiEvents.NotificationClicked(
                                              notification.id,
                                              notification.read
                                          )
                                      )
                                      Toast.makeText(context,"Fred didn't implement this!", Toast.LENGTH_SHORT).show()
                                  }
                              )
                          }
                    }
                }
            }

        }
    }
}