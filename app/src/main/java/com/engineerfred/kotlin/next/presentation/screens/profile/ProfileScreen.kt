package com.engineerfred.kotlin.next.presentation.screens.profile

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.AlertDialog
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
import com.engineerfred.kotlin.next.utils.formatTimestampToDateString
import com.engineerfred.kotlin.next.utils.pluralizeWord
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    onBackClicked: () -> Unit,
    commonViewModel: CommonViewModel,
    onEditProfile: () -> Unit,
    onComment: (String) -> Unit,
    onCreatePost: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
) {

    val currentUser = commonViewModel.currentUser
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState().value

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )


    if ( !uiState.profileImageUpdateError.isNullOrEmpty() ) {
        Toast.makeText(context, uiState.profileImageUpdateError, Toast.LENGTH_SHORT).show()
    }

    if ( !uiState.coverImageUpdateError.isNullOrEmpty() ) {
        Toast.makeText(context, uiState.coverImageUpdateError, Toast.LENGTH_SHORT).show()
    }

    val profilePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        try {
            if ( it != null ) {
                viewModel.onEvent( ProfileUiEvents.ProfileImageUrlChanged("$it", context) )
            }
        } catch( ex: Exception ) {
            ex.printStackTrace()
        }
    }

    val coverPictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        try {
            if ( it != null ) {
                viewModel.onEvent( ProfileUiEvents.CoverImageUrlChanged("$it", context) )
            }
        } catch( ex: Exception ) {
            ex.printStackTrace()
        }
    }


    val galleryPerm2 = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
    val galleryPerm3 = rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    val galleryPerm1 = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var textToShow by rememberSaveable {
        mutableStateOf("")
    }

    val name = if ( currentUser != null ) "${currentUser.firstName.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.ROOT
        ) else it.toString()
    }} ${currentUser.lastName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}" else ""


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
                text = name,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (  currentUser != null ) {
            viewModel.setCurrentUser(currentUser)
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                                model = uiState.coverImageUrl.ifEmpty { currentUser.coverImageUrl },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            )
                            IconButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        if ( galleryPerm3.status.isGranted ) {
                                            coverPictureLauncher.launch("image/*")
                                        } else {
                                            if (galleryPerm3.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select images. Please grant the permission."
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
                                            coverPictureLauncher.launch("image/*")
                                        } else {
                                            if (galleryPerm2.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select images. Please grant the permission."
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
                                            coverPictureLauncher.launch("image/*")
                                        } else {
                                            if (galleryPerm1.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select images. Please grant the permission."
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
                                }, enabled = !uiState.updatingCoverPhoto,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 10.dp, bottom = 12.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(27.dp)
                                )
                            }
                            if ( uiState.updatingCoverPhoto ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .align(Alignment.Center)
                                        .background(Color.Black.copy(alpha = .4f)),
                                    contentAlignment = Alignment.Center
                                ){
                                    CircularProgressIndicator( modifier = Modifier.size(60.dp) )
                                }
                            }
                        }
                        Box(modifier = Modifier.width(160.dp)) {
                            AsyncImage(
                                model = uiState.profileImageUrl.ifEmpty { currentUser.profileImageUrl },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(top = 80.dp, start = 14.dp)
                                    .clip(CircleShape)
                                    .size(120.dp)
                                    .background(Color.DarkGray)
                                    .border(1.dp, if (isDarkTheme) Color.White else CrimsonRed, CircleShape),
                            )
                            if ( uiState.updatingProfilePhoto ) {
                                Box(
                                    modifier = Modifier
                                        .width(136.dp)
                                        .height(200.dp)
                                        .align(Alignment.Center)
                                        .padding(top = 80.dp, end = 14.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = .4f)),
                                    contentAlignment = Alignment.Center
                                ){
                                    CircularProgressIndicator( modifier = Modifier.size(60.dp) )
                                }
                            }
                            IconButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        if ( galleryPerm3.status.isGranted ) {
                                            profilePictureLauncher.launch("image/*")
                                        } else {
                                            if (galleryPerm3.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select images. Please grant the permission."
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
                                            profilePictureLauncher.launch("image/*")
                                        } else {
                                            if (galleryPerm2.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select images. Please grant the permission."
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
                                            profilePictureLauncher.launch("image/*")
                                        } else {
                                            if (galleryPerm1.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select images. Please grant the permission."
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
                                enabled = !uiState.updatingProfilePhoto,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 25.dp, bottom = 7.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { onEditProfile.invoke() },
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .align(Alignment.BottomEnd),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = "Edit Profile", color = if ( isDarkTheme ) Color.White else CrimsonRed )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    Text(
                        text = currentUser.email,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )
                    if ( !currentUser.about.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = currentUser.about,
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
                            text =  formatTimestampToDateString(currentUser.joinDate),
                            color = if ( isDarkTheme ) Color.White else Color.DarkGray,
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
                                text = currentUser.following.size.toString(),
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
                                text = currentUser.followers.size.toString(),
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = pluralizeWord("Follower", currentUser.followersCount)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(Modifier.height(4.dp))
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
                                CircularProgressIndicator( modifier = Modifier.size(23.dp) )
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
                                    onClick = { viewModel.onEvent(ProfileUiEvents.RetryClicked) },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Retry",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    !uiState.isGettingUserPosts && uiState.gettingUserPostError.isNullOrEmpty() -> {
                        if ( uiState.userPosts.isEmpty() ) {
                            item {
                                Spacer(modifier = Modifier.height(20.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Text(
                                        text = "You got no posts!",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Button(
                                        onClick = {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                                if ( galleryPerm3.status.isGranted ) {
                                                    onCreatePost.invoke()
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
                                                    onCreatePost.invoke()
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
                                                    onCreatePost.invoke()
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
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 80.dp)
                                    ) {
                                        Text(
                                            text = "Create post",
                                            fontSize = 21.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
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
                                    onComment = { onComment.invoke(it) },
                                    onLikePost = { postId, _ ->
                                        viewModel.onEvent(ProfileUiEvents.PostLiked(postId))
                                    },
                                    onUnLikePost = { postId, _ ->
                                        viewModel.onEvent(ProfileUiEvents.PostUnLiked(postId))
                                    },
                                    context = context,
                                    isDarkTheme = isDarkTheme, onUserProfileImageClicked = onUserProfileImageClicked
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center){
                Text(
                    text = "Hmm :(",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    maxLines = 1
                )
            }
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
    }
}