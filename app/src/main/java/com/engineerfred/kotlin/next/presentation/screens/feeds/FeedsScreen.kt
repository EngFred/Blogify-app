package com.engineerfred.kotlin.next.presentation.screens.feeds

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.common.PostComponent
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FeedsScreen(
    onCreateClicked: () -> Unit,
    onComment: (String) -> Unit,
    viewModel: FeedsViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
    onPostImageClicked: (String) -> Unit,
) {

    val uiState = viewModel.uiState
    val context = LocalContext.current

    val bgColor = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    val currentUser = commonViewModel.currentUser
    
    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )


    val galleryPerm2 = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
    val galleryPerm3 = rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    val galleryPerm1 = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var textToShow by rememberSaveable {
        mutableStateOf("")
    }

    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    if ( isDialogVisible ) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                Button(onClick = {
                    isDialogVisible = false
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        galleryPerm3.launchPermissionRequest()
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        galleryPerm2.launchMultiplePermissionRequest()
                    } else {
                        galleryPerm1.launchPermissionRequest()
                    }
                }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )) {
                    Text(text = "Grant Permission")
                }
            },
            title = {
                Text(text = "Permission Required!")
            },
            text = {
                Text(text = textToShow)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    if ( currentUser != null ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor), contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator( color = if (  isDarkTheme ) Color.White else CrimsonRed )
                }
            }
            !uiState.loadError.isNullOrEmpty() -> {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(bgColor)
                    .padding(14.dp), contentAlignment = Alignment.Center ){
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Rounded.Error, contentDescription = null, Modifier.size(50.dp) )
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = uiState.loadError, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.onEvent( FeedsUiEvents.RetryClicked ) },
                            shape = RoundedCornerShape( 8.dp )
                        ) {
                            Text(text = "Retry")
                        }
                    }
                }
            }
            !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
                viewModel.setCurrentUser(currentUser)
                if ( uiState.feeds.isEmpty() ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center ){
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Rounded.Info, contentDescription = null, Modifier.size(50.dp) )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(text = "There are no posts yet!", fontWeight = FontWeight.Bold, color = textColor)
                            Spacer(modifier = Modifier.size(10.dp))
                            OutlinedButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        if ( galleryPerm3.status.isGranted ) {
                                            onCreateClicked.invoke()
                                        } else {
                                            if (galleryPerm3.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to add posts. Please grant the permission."
                                                isDialogVisible = true
                                            } else {
                                                // If it's the first time the user lands on this feature
                                                galleryPerm3.launchPermissionRequest()
                                                if ( galleryPerm3.status.isGranted ) {
                                                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        if ( galleryPerm2.allPermissionsGranted ) {
                                            onCreateClicked.invoke()
                                        } else {
                                            if (galleryPerm2.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to add posts. Please grant the permission."
                                                isDialogVisible = true
                                            } else {
                                                // If it's the first time the user lands on this feature
                                                galleryPerm2.launchMultiplePermissionRequest()
                                                if ( galleryPerm2.allPermissionsGranted ) {
                                                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    } else {
                                        if ( galleryPerm1.status.isGranted ) {
                                            onCreateClicked.invoke()
                                        } else {
                                            if (galleryPerm1.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to add posts. Please grant the permission."
                                                isDialogVisible = true
                                            } else {
                                                // If it's the first time the user lands on this feature
                                                galleryPerm1.launchPermissionRequest()
                                                if ( galleryPerm1.status.isGranted ) {
                                                    Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape( 8.dp )
                            ) {
                                Text(text = "Create", color = textColor)
                            }
                        }
                    }
                } else {
                    val feeds = uiState.feeds
                    LazyColumn(
                        Modifier.fillMaxWidth()
                    ) {
                        items(
                            count = feeds.size,
                            key = { feeds[it].id },
                            contentType = { feeds[it].type }
                        ) {
                            val post = feeds[it]
                            PostComponent(
                                post = post,
                                onComment = onComment,
                                context = context,
                                onLikePost = { postId, postOwnerId ->
                                    viewModel.onEvent(FeedsUiEvents.PostLiked(postId, postOwnerId))
                                }, onUnLikePost = { postId, _  ->
                                    viewModel.onEvent(FeedsUiEvents.PostUnLiked(postId))
                                }, isDarkTheme  =isDarkTheme,
                                onUserProfileImageClicked = onUserProfileImageClicked,
                                onImageClicked = onPostImageClicked
                            )
                        }
                    }
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