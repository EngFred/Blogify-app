package com.engineerfred.kotlin.next.presentation.screens.people

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
import com.engineerfred.kotlin.next.presentation.screens.people.components.PersonComponent
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PeopleScreen(
    viewModel: PeopleViewModel = hiltViewModel(),
    onUserClicked: (String) -> Unit,
    onSearchClicked: () -> Unit,
    commonViewModel: CommonViewModel,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    val currentUser = commonViewModel.currentUser

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    if ( !uiState.followError.isNullOrEmpty() ) {
        Toast.makeText(context, uiState.followError, Toast.LENGTH_SHORT).show()
    }

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
                text = "People",
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

        when{
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
                            onClick = { viewModel.onEvent( PeopleUiEvents.RetryClicked ) },
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
                if ( uiState.users.isEmpty() ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), contentAlignment = Alignment.Center){
                        Text(
                            text = "There are no users yet!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    val users = uiState.users
                    val loggedInUser = FirebaseAuth.getInstance().currentUser

                    if ( currentUser == null ) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), contentAlignment = Alignment.Center){
                            Text(
                                text = "Hmm Current user is null!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LaunchedEffect(key1 = Unit) {
                            viewModel.setCurrentUser(currentUser)
                        }
                        LazyColumn(
                            Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            items(count = users.size, key = {users[it].id}) {
                                val user = users[it]
                                val notFriends = currentUser.id in user.followers && user.id in currentUser.followers
                                if ( !notFriends && user.id != currentUser.id ) {
                                    PersonComponent(
                                        user = user,
                                        onUserClicked = onUserClicked,
                                        onFollow = {
                                            viewModel.onEvent(
                                                PeopleUiEvents.FollowedUser(it)
                                            )
                                        },
                                        onFollowBack = {
                                            viewModel.onEvent(
                                                PeopleUiEvents.FollowedBackUser(it)
                                            )
                                        },
                                        onUnfollow = {
                                            viewModel.onEvent(
                                                PeopleUiEvents.UnFollowedUser(it)
                                            )
                                        },
                                        context = context,
                                        isDarkTheme = isDarkTheme,
                                        onUserProfileImageClicked = onUserProfileImageClicked,
                                        currentUser = currentUser
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}