package com.engineerfred.kotlin.next.presentation.screens.chatWithGemini

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.screens.chatWithGemini.components.AiChatItem
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.presentation.theme.CrimsonRed
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun ChatWithGeminiScreen(
    isDarkTheme: Boolean,
    onBackClicked: () -> Unit,
    viewModel: ChatWithGeminiViewModel = hiltViewModel()
) {

    val fm = LocalFocusManager.current
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    var textToShow by rememberSaveable {
        mutableStateOf("")
    }

    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    val wallPaper = if (isDarkTheme) R.drawable.whatsapp_wallpaper_dark else R.drawable.whatsapp_wallpaper_light

    if ( !uiState.responseError.isNullOrEmpty() ) {
        LaunchedEffect(key1 = uiState.responseError) {
            Toast.makeText(context, uiState.responseError, Toast.LENGTH_LONG).show()
        }
    }

    if ( !uiState.clearingChatError.isNullOrEmpty() ) {
        LaunchedEffect(key1 = uiState.clearingChatError) {
            Toast.makeText( context, uiState.clearingChatError, Toast.LENGTH_LONG ).show()
        }
    }

    if ( uiState.chatList.isNotEmpty() && uiState.chatList.size > 1 ) {
        LaunchedEffect(key1 = Unit) {
            val lastItemIndex = uiState.chatList.lastIndex-1
            scrollState.scrollToItem(lastItemIndex)
        }
    }

    LaunchedEffect(key1 = !uiState.messageAdded) {
        if ( uiState.chatList.size > 2 ) {
            val lastItemIndex = uiState.chatList.lastIndex-1
            scrollState.scrollToItem(lastItemIndex)
        }
    }

    SetSystemBarColor( color = MaterialTheme.colorScheme.primary )

    val galleryPerm2 = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
    val galleryPerm3 = rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    val galleryPerm1 = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    val photoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        try {
            if ( it != null ) {
                viewModel.onEvent( ChatWithGeminiUiEvents.ImageUrlChanged("$it") )
            }
        } catch( ex: Exception ) {
            ex.printStackTrace()
        }
    }

    Column {
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
            Image(
                painter = painterResource(id = R.drawable.ai),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Charcoal else Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Gemini",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 2,
                color = Color.White,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Column {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Clear chat", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) },
                        onClick = {
                            menuExpanded = false
                            viewModel.onEvent(ChatWithGeminiUiEvents.ClearChat)
                        }, enabled = !uiState.clearingChat && !uiState.isGettingResponse
                    )
                }
            }
        }
        if (uiState.chatList.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
                Image(
                    painter = painterResource(id = wallPaper),
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.TopStart).fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Chat with Gemini",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ){
                if ( uiState.clearingChat ) {
                    Image(
                        painter = painterResource(id = wallPaper),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.TopStart).fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = .7f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator( color = Color.White, modifier = Modifier.size(50.dp) )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Clearing chat...",
                            fontSize = 15.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Image(
                        painter = painterResource(id = wallPaper),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.TopStart).fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 10.dp),
                        state = scrollState
                    ){
                        items(
                            count = uiState.chatList.size,
                            key = { uiState.chatList[it].id }
                        ) {
                            val chat = uiState.chatList[it]
                            AiChatItem(
                                isFromUser = chat.isFromUser,
                                prompt = chat.prompt,
                                imageUrl = chat.imageUrl,
                                isDarkTheme
                            )
                        }
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            AnimatedVisibility(visible = !uiState.imageUrl.isNullOrEmpty()) {
                Box(modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 10.dp, top = 10.dp)) {
                    AsyncImage(
                        model = uiState.imageUrl,
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
                                viewModel.onEvent(ChatWithGeminiUiEvents.ImageUrlChanged(""))
                            }
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )

                }
                Spacer(modifier = Modifier.size(4.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.size(4.dp))
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 6.dp, top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        if ( galleryPerm3.status.isGranted ) {
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
                TextField(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(max = 150.dp),
                    value = uiState.prompt,
                    onValueChange = {
                        viewModel.onEvent(ChatWithGeminiUiEvents.MessageContentChanged(it))
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = if (isDarkTheme) Color.White else CrimsonRed
                    ),
                    placeholder = {
                        Text(text = if ( uiState.imageUrl == null ) "Write a message..." else "Add a caption...", fontSize = 14.sp)
                    },
                    textStyle = TextStyle(
                        fontSize = 14.sp
                    ),
                    shape = RoundedCornerShape(33.dp),
                    keyboardOptions = KeyboardOptions( imeAction = ImeAction.Done ),
                    keyboardActions = KeyboardActions(
                        onDone = { fm.clearFocus() }
                    )
                )
                IconButton(
                    onClick = {
                    viewModel.onEvent(ChatWithGeminiUiEvents.SendMessage(context))
                    fm.clearFocus()
                    if ( uiState.chatList.isNotEmpty() && uiState.chatList.size > 1 ) {
                        coroutineScope.launch {
                            val lastItemIndex = uiState.chatList.lastIndex-1
                            scrollState.scrollToItem(lastItemIndex)
                        }
                    }
                }, enabled = !uiState.isGettingResponse && !uiState.clearingChat) {
                    if ( uiState.isGettingResponse ) {
                        CircularProgressIndicator( modifier = Modifier.size(20.dp), color = if (isDarkTheme) DarkSlateGrey else CrimsonRed )
                    } else {
                        Icon(imageVector = Icons.AutoMirrored.Rounded.Send, contentDescription = null )
                    }
                }
            }
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