package com.engineerfred.kotlin.next.presentation.screens.chat

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.screens.chat.componets.ChatMessageItem
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.net.URLEncoder
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatScreen(
    receiverId: String,
    isDarkTheme: Boolean,
    onBackClicked: () -> Unit,
    onPlayVideo: (String) -> Unit,
    onPlayChatVideo: (String) -> Unit,
    onViewChatImage: (String) -> Unit,
    onViewUserProfile: (String) -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {

    val fm  = LocalFocusManager.current

    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary)

    val wallPaper = if (isDarkTheme) R.drawable.whatsapp_wallpaper_dark else R.drawable.whatsapp_wallpaper_light

    val galleryPerm2 = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
    val galleryPerm3 = rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    val galleryPerm1 = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var textToShow by rememberSaveable {
        mutableStateOf("")
    }

    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val photoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        try {
            if ( it != null ) {
                viewModel.onEvent( ChatUiEvents.MediaUrlChanged("$it", "photo") )
            }
        } catch( ex: Exception ) {
            ex.printStackTrace()
        }
    }

//    val videoGalleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) {
//        try {
//            if ( it != null ) {
//                viewModel.onEvent( ChatUiEvents.MediaUrlChanged("$it", "video") )
//            }
//        } catch( ex: Exception ) {
//            ex.printStackTrace()
//        }
//    }

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


    when{
        uiState.isGettingReceiver -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = wallPaper),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                CircularProgressIndicator(
                    color = if (isDarkTheme) Color.White else CrimsonRed,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.Center)
                )
            }
        }
        !uiState.isGettingReceiver && !uiState.gettingReceiverError.isNullOrEmpty() -> {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
            ) {
                Image(
                    painter = painterResource(id = wallPaper),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = uiState.gettingReceiverError,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        !uiState.isGettingReceiver && uiState.gettingReceiverError.isNullOrEmpty() -> {
            val receiver = uiState.receiver
            if ( receiver == null ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                ) {
                    Image(
                        painter = painterResource(id = wallPaper),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "User not Found!!",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                val userName = "${receiver.firstName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()) else it.toString() }} ${receiver.lastName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }}"
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onBackClicked.invoke() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        AsyncImage(
                            model = uiState.receiver.profileImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(if (isDarkTheme) Charcoal else Color.LightGray),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        Column(
                            modifier = Modifier
                                .clickable {
                                    onViewUserProfile(receiverId)
                                }
                                .weight(1f)
                                .padding(start = 6.dp)
                        ){
                            Text(
                                text = userName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "last seen",
                                fontSize = 13.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = {
                            Toast.makeText(context,"Feature not implemented!", Toast.LENGTH_LONG).show()
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    when {
                        uiState.gettingChatMessages -> {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                            ){
                                Image(
                                    painter = painterResource(id = wallPaper),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                CircularProgressIndicator(
                                    color = if (isDarkTheme) Color.White else CrimsonRed,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.Center),
                                )
                            }
                        }
                        !uiState.gettingChatMessages && !uiState.gettingChatMessagesError.isNullOrEmpty() -> {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                            ){
                                Image(
                                    painter = painterResource(id = wallPaper),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = uiState.gettingChatMessagesError,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center),
                                )
                            }
                        }
                        !uiState.gettingChatMessages && uiState.gettingChatMessagesError.isNullOrEmpty() -> {
                            val chatMessages = uiState.chatMessages
                            if ( chatMessages.isEmpty() ) {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                ){
                                    Image(
                                        painter = painterResource(id = wallPaper),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = "Start a chat",
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            } else {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                ) {
                                    Image(
                                        painter = painterResource(id = wallPaper),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.TopStart)
                                            .fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.Top,
                                        reverseLayout = true
                                    ) {
                                        items(
                                            count = chatMessages.size,
                                            key = { chatMessages[it].id }
                                        ) {
                                            val chatMessage = chatMessages[it]
                                            ChatMessageItem(
                                                chatMessage = chatMessage,
                                                isDarkTheme = isDarkTheme,
                                                context = context,
                                                onPlayChatVideo = onPlayChatVideo,
                                                onViewChatImage = onViewChatImage
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        Modifier.fillMaxWidth()
                    ) {
                        AnimatedVisibility(visible = !uiState.mediaUrl.isNullOrEmpty()) {
                            if ( uiState.selectedMediaType == "photo") {
                                Box(modifier = Modifier
                                    .wrapContentSize()
                                    .padding(start = 10.dp, top = 10.dp)
                                ) {
                                    AsyncImage(
                                        model = uiState.mediaUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, CrimsonRed, RoundedCornerShape(10.dp))
                                            .background(if (isDarkTheme) Charcoal else Color.LightGray)
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .clickable {
                                                viewModel.onEvent(
                                                    ChatUiEvents.MediaUrlChanged(
                                                        "",
                                                        ""
                                                    )
                                                )
                                            }
                                            .align(Alignment.TopEnd)
                                            .clip(CircleShape)
                                            .background(Color.Black)
                                    )
                                }
                            } else {
                                uiState.mediaUrl?.let {
                                    val postUrl = URLEncoder.encode(uiState.mediaUrl, "utf-8")
                                    val model = ImageRequest.Builder(context)
                                        .data(uiState.mediaUrl)
                                        .videoFrameMillis(10000)
                                        .decoderFactory { result, options, _ ->
                                            VideoFrameDecoder(
                                                result.source,
                                                options
                                            )
                                        }
                                        .build()
                                    Box(modifier = Modifier
                                        .wrapContentSize()
                                        .padding(start = 10.dp, top = 10.dp)
                                    ) {
                                        AsyncImage(
                                            model = model,
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .border(1.dp, CrimsonRed, RoundedCornerShape(10.dp))
                                                .background(if (isDarkTheme) Charcoal else Color.LightGray)
                                                .align(Alignment.TopStart)
                                        )
                                        IconButton(
                                            onClick = { onPlayVideo(postUrl) },
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.PlayCircle,
                                                contentDescription = null,
                                                tint = CrimsonRed,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier
                                                .clickable {
                                                    viewModel.onEvent(
                                                        ChatUiEvents.MediaUrlChanged(
                                                            "",
                                                            ""
                                                        )
                                                    )
                                                }
                                                .align(Alignment.TopEnd)
                                                .clip(CircleShape)
                                                .background(Color.Black)
                                        )
                                }
                                }
                            }
                            Spacer(modifier = Modifier.size(4.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.size(4.dp))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 10.dp)
                        ) {
                            IconButton(onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    if ( galleryPerm3.status.isGranted ) {
                                        viewModel.onEvent( ChatUiEvents.MediaUrlChanged("","") )
                                        photoGalleryLauncher.launch("image/*")
                                    } else {
                                        if (galleryPerm3.status.shouldShowRationale) {
                                            // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                            textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                    " to select and send images. Please grant the permission."
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
                                        viewModel.onEvent( ChatUiEvents.MediaUrlChanged("","") )
                                        photoGalleryLauncher.launch("image/*")
                                    } else {
                                        if (galleryPerm2.shouldShowRationale) {
                                            // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                            textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                    " to select and send images. Please grant the permission."
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
                                        viewModel.onEvent( ChatUiEvents.MediaUrlChanged("","") )
                                        photoGalleryLauncher.launch("image/*")
                                    } else {
                                        if (galleryPerm1.status.shouldShowRationale) {
                                            // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                            textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                    " to select and send images. Please grant the permission."
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
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.PhotoLibrary,
                                    contentDescription = null
                                )
                            }
//                            IconButton(onClick = {
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//                                    if ( galleryPerm3.status.isGranted ) {
//                                        viewModel.onEvent( ChatUiEvents.MediaUrlChanged("","") )
//                                        videoGalleryLauncher.launch("video/*")
//                                    } else {
//                                        if (galleryPerm3.status.shouldShowRationale) {
//                                            // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
//                                            textToShow = "The app needs to access your phone gallery inorder to be able" +
//                                                    " to select and send videos. Please grant the permission."
//                                            isDialogVisible = true
//                                        } else {
//                                            // If it's the first time the user lands on this feature
//                                            galleryPerm3.launchPermissionRequest()
//                                            if ( galleryPerm3.status.isGranted ) {
//                                                Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
//                                            }
//                                        }
//                                    }
//                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                                    if ( galleryPerm2.allPermissionsGranted ) {
//                                        viewModel.onEvent( ChatUiEvents.MediaUrlChanged("","") )
//                                        videoGalleryLauncher.launch("video/*")
//                                    } else {
//                                        if (galleryPerm2.shouldShowRationale) {
//                                            // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
//                                            textToShow = "The app needs to access your phone gallery inorder to be able" +
//                                                    " to select and send videos. Please grant the permission."
//                                            isDialogVisible = true
//                                        } else {
//                                            // If it's the first time the user lands on this feature
//                                            galleryPerm2.launchMultiplePermissionRequest()
//                                            if ( galleryPerm2.allPermissionsGranted ) {
//                                                Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    if ( galleryPerm1.status.isGranted ) {
//                                        viewModel.onEvent( ChatUiEvents.MediaUrlChanged("","") )
//                                        videoGalleryLauncher.launch("video/*")
//                                    } else {
//                                        if (galleryPerm1.status.shouldShowRationale) {
//                                            // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
//                                            textToShow = "The app needs to access your phone gallery inorder to be able" +
//                                                    " to select and send videos. Please grant the permission."
//                                            isDialogVisible = true
//                                        } else {
//                                            // If it's the first time the user lands on this feature
//                                            galleryPerm1.launchPermissionRequest()
//                                            if ( galleryPerm1.status.isGranted ) {
//                                                Toast.makeText(context, "Permission granted!", Toast.LENGTH_SHORT).show()
//                                            }
//                                        }
//                                    }
//                                }
//                            }) {
//                                Icon(
//                                    imageVector = Icons.Rounded.VideoLibrary,
//                                    contentDescription = null
//                                )
//                            }
                            TextField(
                                modifier = Modifier
                                    .weight(1f),
                                value = uiState.messageContent,
                                onValueChange = {
                                    viewModel.onEvent(ChatUiEvents.MessageContentChanged(it))
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = if (isDarkTheme) Color.White else CrimsonRed
                                ),
                                placeholder = {
                                    Text(text = if ( uiState.mediaUrl.isNullOrEmpty() ) "Write a message..." else "Add a caption...", fontSize = 13.sp)
                                },
                                keyboardOptions = KeyboardOptions( imeAction = ImeAction.Done ),
                                keyboardActions = KeyboardActions(
                                    onDone = { fm.clearFocus() }
                                )
                            )
                            IconButton(onClick = {
                                viewModel.onEvent( ChatUiEvents.SendMessage(context) )
                                fm.clearFocus()
                                if ( uiState.mediaUrl != null || uiState.messageContent != null ) {
                                    if (  uiState.selectedMediaType == "video" ) {
                                        Toast.makeText(context, "Sending video...", Toast.LENGTH_SHORT ).show()
                                    } else if( uiState.selectedMediaType == "photo" ) {
                                        Toast.makeText(context, "Sending photo...", Toast.LENGTH_SHORT ).show()
                                    }
                                }
                            }) {
                                Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null )
                            }
                        }
                    }
                }
            }
        }
    }
}