package com.engineerfred.kotlin.next.presentation.screens.create

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.VideoCameraBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.domain.model.PostType
import com.engineerfred.kotlin.next.presentation.common.CommonViewModel
import com.engineerfred.kotlin.next.presentation.common.postHeaderAnnotatedString
import com.engineerfred.kotlin.next.presentation.screens.create.components.BottomBar
import com.engineerfred.kotlin.next.presentation.screens.create.components.CaptionTextField
import com.engineerfred.kotlin.next.presentation.screens.create.components.OptionRow
import com.engineerfred.kotlin.next.presentation.screens.create.components.TopBar
import com.engineerfred.kotlin.next.presentation.screens.create.components.User
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkOrange
import com.engineerfred.kotlin.next.presentation.theme.SeaGreen
import com.engineerfred.kotlin.next.presentation.theme.SteelBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.net.URLEncoder

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CreateScreen(
    onNavigateUp: () -> Unit,
    onPlayVideoClicked: (String) -> Unit,
    onTagFriendsClicked: () -> Unit,
    onAddFeelingClicked: () -> Unit,
    onAddLocationClicked: () -> Unit,
    onAddingPost: () -> Unit,
    viewModel: CreateViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    feeling: String?,
    location: String?,
    taggedFriends: ArrayList<String>?,
    isDarkTheme: Boolean
) {

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    val currentUser = commonViewModel.currentUser

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    LaunchedEffect(key1 = taggedFriends) {
        if ( taggedFriends != null ) {
            commonViewModel.addList(taggedFriends)
            Log.i("#","Added ${taggedFriends.size} tagged friends inside the list in the common viewModel!!!")
        }
    }

    LaunchedEffect(key1 = uiState.addingPostInProgress) {
        if ( uiState.addingPostInProgress ) {
            Toast.makeText(context, "Uploading your post...", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = feeling) {
        if ( feeling != null ) {
            viewModel.onEvent( CreateUiEvents.FeelingSelected(feeling) )
        }
        Log.i("#","Feeling: $feeling")
    }

    LaunchedEffect(key1 = location) {
        if ( location != null ) {
            viewModel.onEvent( CreateUiEvents.LocationSelected(location) )
        }
        Log.i("#","Location: $location")
    }

    LaunchedEffect(key1 = taggedFriends) {
        if ( taggedFriends != null ) {
            viewModel.onEvent( CreateUiEvents.FriendsTagged(taggedFriends) )
        }
        Log.i("#","TaggedFriends: $taggedFriends")
    }

    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var rationaleText by rememberSaveable {
        mutableStateOf("")
    }

    if ( !uiState.invalidText.isNullOrEmpty() ) {
        Toast.makeText(context, uiState.invalidText, Toast.LENGTH_LONG ).show()
    }

    if ( uiState.addingPostInProgress ) {
        onAddingPost()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        try {
            if ( it != null ) {
                if ( uiState.postType == PostType.Post.name ) {
                    viewModel.onEvent( CreateUiEvents.ImageUrlChanged("$it") )
                } else {
                    viewModel.onEvent( CreateUiEvents.VideoUrlChanged("$it") )
                }
            }
        } catch( ex: Exception ) {
            ex.printStackTrace()
        }
    }

    val galleryPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState( Manifest.permission.READ_MEDIA_IMAGES )
    } else {
        rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val galleryPermissionStateVideo = rememberPermissionState(permission = Manifest.permission.READ_MEDIA_VIDEO )

    if ( currentUser != null ) {
        viewModel.setCurrentUser(currentUser)
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val textToShow = postHeaderAnnotatedString(
                creatorName = "${currentUser.firstName} ${currentUser.lastName}",
                feeling = uiState.feeling,
                location = uiState.location,
                taggedFriends = uiState.taggedFriends
            )

            val captionPaceHolder = if ( uiState.postType == PostType.Post.name  ) {
                if ( uiState.imagesCollection.isEmpty() ) "What's on your mind" else "Add a caption..."
            } else {
                if ( uiState.videoUrl.isNullOrEmpty() ) "What's on your mind?" else "Add a caption to your video"
            }

            val captionTextFieldHeight = if ( uiState.postType == PostType.Post.name ) {
                if ( uiState.imagesCollection.isEmpty() ) 300.dp else 140.dp
            } else {
                if ( uiState.videoUrl.isNullOrEmpty() ) 300.dp else 140.dp
            }

            TopBar(
                onCloseClicked = onNavigateUp,
                onPostClicked = {
                    viewModel.onEvent( CreateUiEvents.PostButtonClicked(context) )
                }
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                User(
                    text = textToShow,
                    onAudienceButtonClicked = {
                        Toast.makeText(context, "This feature is not yet implemented!", Toast.LENGTH_SHORT ).show()
                    }, userProfileImage = currentUser.profileImageUrl, isDarkTheme = isDarkTheme)
                Spacer(modifier = Modifier.size(13.dp))
                CaptionTextField(
                    text = { uiState.captionTextValue },
                    onTextChanged = {
                        if ( uiState.captionTextValue.length < 200 ) {
                            viewModel.onEvent( CreateUiEvents.PostTextValueChanged(it) )
                        }
                    }, placeHolder = captionPaceHolder,
                    height = captionTextFieldHeight,
                    isDarkTheme = isDarkTheme
                )
                Spacer(modifier = Modifier.size(13.dp))
                if ( uiState.postType == PostType.Post.name ) {
                    AnimatedVisibility(visible = uiState.imagesMutableList.isNotEmpty()) {
                        LazyRow {
                            items(
                                count = uiState.imagesMutableList.size
                            ) {
                                val imageUrl = uiState.imagesMutableList[it]
                                Box(modifier = Modifier
                                    .width(200.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(Color.LightGray)
                                ) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    IconButton(onClick = {
                                        uiState.imagesCollection.remove(imageUrl)
                                    }, modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(end = 12.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = .5f))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Delete,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.size(10.dp))
                            }
                        }
                    }
                } else {
                    AnimatedVisibility(visible = !uiState.videoUrl.isNullOrEmpty()) {
                        uiState.videoUrl?.let {
                            val postUrl = URLEncoder.encode(uiState.videoUrl, "utf-8")
                            val model = ImageRequest.Builder(context)
                                .data(it)
                                .videoFrameMillis(10000)
                                .decoderFactory { result, options, _ ->
                                    VideoFrameDecoder(
                                        result.source,
                                        options
                                    )
                                }
                                .build()

                            Box(modifier = Modifier
                                .width(300.dp)
                                .height(300.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = model,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { onPlayVideoClicked(postUrl) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PlayCircle,
                                        contentDescription = null,
                                        tint = CrimsonRed,
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
                OptionRow(
                    onRowClicked = {
                        if ( uiState.postType == PostType.Post.name ) {
                            if ( uiState.imagesCollection.size < 3 ) {
                                galleryLauncher.launch("image/*")
                            } else {
                                Toast.makeText(context, "You can only post upto 3 images!", Toast.LENGTH_SHORT).show()
                            }
                        } else galleryLauncher.launch("video/*")
                    },
                    imageVectorIcon = if ( uiState.postType == PostType.Post.name ) Icons.Rounded.Image else Icons.Rounded.VideoCameraBack,
                    title = "Pick Photo/Video",
                    iconTint = CrimsonRed
                )
                Spacer(modifier = Modifier.size(10.dp))
                OptionRow(
                    onRowClicked = {
                        onTagFriendsClicked.invoke()
                    },
                    painterResourceIcon = R.drawable.ic_add_person,
                    title = "Tag friends",
                    iconTint = SteelBlue
                )
                Spacer(modifier = Modifier.size(10.dp))
                OptionRow(
                    onRowClicked = {  onAddFeelingClicked.invoke()  },
                    painterResourceIcon = R.drawable.ic_emoji,
                    title = "Add Feeling",
                    iconTint = DarkOrange
                )
                Spacer(modifier = Modifier.size(10.dp))
                OptionRow(
                    onRowClicked = { onAddLocationClicked.invoke() },
                    painterResourceIcon = R.drawable.ic_add_location,
                    title = "Add location",
                    iconTint = SeaGreen
                )
                Spacer(modifier = Modifier.size(10.dp))
            }
            HorizontalDivider()
            BottomBar(
                selected = uiState.postType,
                onPostTypeSelected = {
                    if ( it == PostType.Post.name ) {
                        viewModel.onEvent( CreateUiEvents.PostTypeChanged(it) )
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ) {
                            viewModel.onEvent( CreateUiEvents.PostTypeChanged(it) )
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (galleryPermissionStateVideo.status.isGranted){
                                viewModel.onEvent( CreateUiEvents.PostTypeChanged(it) )
                            } else {
                                if (galleryPermissionStateVideo.status.shouldShowRationale) {
                                    // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                    rationaleText = "The app needs to access your videos in gallery inorder to be able" +
                                            " to select and send videos. Please grant the permission."
                                    isDialogVisible = true
                                } else {
                                    // If it's the first time the user lands on this feature
                                    galleryPermissionStateVideo.launchPermissionRequest()
                                    if ( galleryPermissionStateVideo.status.isGranted ) {
                                        Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            viewModel.onEvent( CreateUiEvents.PostTypeChanged(it) )
                        }
                    }
                }, isDarkTheme = isDarkTheme
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text(text = "Hmm :(, Current user is null")
        }
    }

    if ( isDialogVisible ) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            confirmButton = {
                Button(onClick = {
                    isDialogVisible = false
                    galleryPermissionState.launchPermissionRequest()
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
                Text(text = rationaleText)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}