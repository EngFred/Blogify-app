package com.engineerfred.kotlin.next.presentation.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.screens.messages.components.ChatComponent
import com.engineerfred.kotlin.next.presentation.screens.messages.components.ChatWithGeminiComponent
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed

@Composable
fun MessageScreen(
    isDarkTheme: Boolean,
    onChatWithGemini: () -> Unit,
    onChatClicked: (receiverId: String) -> Unit,
    viewModel: MessagesViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )
    val currentUser = commonViewModel.currentUser

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

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
                text = "Messages",
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = { }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
        }
        if ( currentUser == null ) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f), contentAlignment = Alignment.Center
            ){
                Text(text = "Hmm :(!, Current user is null.", color = Color.Red, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    ChatWithGeminiComponent(isDarkTheme = isDarkTheme, onCardClicked = onChatWithGemini)
                    HorizontalDivider()
                    Text(
                        text = "Friends:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 14.dp, top = 10.dp)
                    )
                }
                when {
                    uiState.isLoading -> {
                        item {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f), contentAlignment = Alignment.Center
                            ){
                                CircularProgressIndicator( color = if (isDarkTheme) Color.White else CrimsonRed, modifier = Modifier.size(50.dp) )
                            }
                        }
                    }
                    !uiState.isLoading && !uiState.loadError.isNullOrEmpty() -> {
                        item {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                                    .weight(1f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = uiState.loadError, color = Color.Red, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.size(8.dp))
                                OutlinedButton(
                                    onClick = { viewModel.onEvent(MessagesUiEvents.RetryClicked) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text( text = "Retry", fontWeight = FontWeight.SemiBold, color = if (isDarkTheme) Color.White else CrimsonRed )
                                }
                            }
                        }
                    }

                    !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
                        val users = uiState.friends
                        if ( users.isEmpty() ) {
                            item {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f), contentAlignment = Alignment.Center
                                ){
                                    Text(text = "There are no other users yet!!", color = Color.Red, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            items(
                                count = users.size,
                                key = { users[it].id }
                            ) {
                                val user = users[it]
                                val friends = currentUser.id in user.followers && user.id in currentUser.followers
                                if ( friends ) {
                                    ChatComponent(user = user, isDarkTheme = isDarkTheme, onChatClicked = onChatClicked, context = context)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}