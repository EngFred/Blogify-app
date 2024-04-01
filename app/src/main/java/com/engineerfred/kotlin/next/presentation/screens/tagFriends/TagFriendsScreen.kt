package com.engineerfred.kotlin.next.presentation.screens.tagFriends

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.common.SearchTextField
import com.engineerfred.kotlin.next.presentation.screens.create.components.Friend
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.utils.dummyListOfFriends

@Composable
fun TagFriendsScreen(
    onBackClicked: () -> Unit,
    onDoneTagFriends: (ArrayList<String>) -> Unit,
    viewModel: TagFriendsViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    isDarkTheme: Boolean
) {

    val uiState = viewModel.uiState.collectAsState().value
    val fm = LocalFocusManager.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    LaunchedEffect(key1 = Unit) {
        Log.v("#","INSIDE TAG FRIENDS SCREEN:: There are ${commonViewModel.friendsList.size} tagged friends inside the common view model!!!")
        Log.v("#","INSIDE TAG FRIENDS SCREEN:: The list --> ${commonViewModel.friendsList} ")
        if ( commonViewModel.friendsList.isNotEmpty() ) {
            uiState.taggedFriends.addAll(commonViewModel.friendsList)
            Log.v("#","INSIDE TAG FRIENDS SCREEN:: Added ${commonViewModel.friendsList.size} tagged friends inside the list in TagFriends Ui State!!!")
        } else {
            Log.v("#","INSIDE TAG FRIENDS SCREEN:: There were no friends added in the list inside the common view model!!!")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
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
                text = "Tag Friends",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        HorizontalDivider()
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            SearchTextField(
                text = { uiState.searchName  },
                onTextChanged = {
                    viewModel.onEvent( TagFriendsUIEvents.NameChanged(it) )
                },
                onDoneClicked = {
                    fm.clearFocus()
                    viewModel.onEvent( TagFriendsUIEvents.SearchClicked )
                }, onCloseClicked = {
                    viewModel.onEvent( TagFriendsUIEvents.NameChanged("") )
                },
                placeHolder = "Search...",
                isDarkTheme = isDarkTheme
            )
            Spacer(modifier = Modifier.size(12.dp))
            if ( uiState.taggedFriends.isEmpty() ) {
                Text(
                    text = "Suggestions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(9.dp))
                dummyListOfFriends.forEach {
                    Friend(
                        name = it,
                        onSelected = {
                            viewModel.onEvent( TagFriendsUIEvents.FriendAdded(it) )
                        }, isDarkTheme = isDarkTheme
                    )
                }
            } else {
                var isSelected by rememberSaveable {
                    mutableStateOf(true)
                }
                Text(
                    text = "Tagged Friends",
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(9.dp))
                uiState.taggedFriends.forEach {friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                isSelected = !isSelected
                                if (friend !in uiState.taggedFriends) uiState.taggedFriends.add(
                                    friend
                                ) else uiState.taggedFriends.remove(friend)
                            }
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        AsyncImage(
                            model = "",
                            contentDescription = null,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(
                            text = friend,
                            fontSize = 22.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .weight(1f)
                        )
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                isSelected = !isSelected
                                if (friend !in uiState.taggedFriends) uiState.taggedFriends.add(
                                    friend
                                ) else uiState.taggedFriends.remove(friend)
                            }, colors = CheckboxDefaults.colors(
                                checkmarkColor = Color.White,
                                checkedColor =  if ( isDarkTheme ) DarkSlateGrey else CrimsonRed,
                            )
                        )
                    }
                }
                HorizontalDivider()
                Text(
                    text = "Suggestions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.size(9.dp))
                dummyListOfFriends.forEach {
                    if ( it !in uiState.taggedFriends ) {
                        Friend(
                            name = it,
                            onSelected = {
                                viewModel.onEvent( TagFriendsUIEvents.FriendAdded(it) )
                            }, isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Use the search box to search for your friends who aren't listed here")
            Spacer(modifier = Modifier.size(4.dp))
        }
        HorizontalDivider()
        Button(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 10.dp)
                .fillMaxWidth(),
            onClick = {
                 onDoneTagFriends.invoke(uiState.taggedFriends)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDarkTheme) DarkSlateGrey else CrimsonRed,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(3.dp)
        ) {
            Text(text = "Done")
        }
    }
}