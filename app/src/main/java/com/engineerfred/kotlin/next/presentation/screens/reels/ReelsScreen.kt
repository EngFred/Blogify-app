package com.engineerfred.kotlin.next.presentation.screens.reels

import android.Manifest
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.engineerfred.kotlin.next.R
import com.engineerfred.kotlin.next.core.SetSystemBarColor
import com.engineerfred.kotlin.next.presentation.screens.reels.components.VideoPlayer
import com.engineerfred.kotlin.next.presentation.theme.DarkSlateGrey
import com.engineerfred.kotlin.next.presentation.theme.EerieBlack
import com.engineerfred.kotlin.next.presentation.theme.SteelBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ReelsScreen(
    onCreateReel: () -> Unit,
    onSearch: () -> Unit,
    onBack: () -> Unit,
    viewModel: ReelsViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
) {

    val uiState = viewModel.uiState.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    val context = LocalContext.current

    SetSystemBarColor( color = Color.Black )

    val galleryPerm2 = rememberMultiplePermissionsState(permissions = listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO))
    val galleryPerm3 = rememberPermissionState(  Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED )
    val galleryPerm1 = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    var textToShow by rememberSaveable {
        mutableStateOf("")
    }

    var isDialogVisible by rememberSaveable {
        mutableStateOf(false)
    }

    var showScrollDialog by rememberSaveable {
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
            containerColor = DarkSlateGrey,
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }

    LaunchedEffect(key1 = Unit) {
        val window = (view.context as Activity).window
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            viewModel.player.stop()
        }
    }

    BackHandler {
        viewModel.player.stop()
        onBack.invoke()
    }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
    ) {
        when{
            uiState.isLoading -> {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(23.dp))
                }
            }
            !uiState.isLoading && !uiState.loadError.isNullOrEmpty() -> {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
                    .padding(15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.loadError,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.onEvent(ReelsUiEvents.RetryClicked) },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Retry", fontWeight = FontWeight.Bold)
                    }
                }
            }
            !uiState.isLoading && uiState.loadError.isNullOrEmpty() -> {
                when{
                    uiState.reels.isEmpty() -> {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black)
                            .weight(1f)
                            .padding(15.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No reels yet!",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            OutlinedButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        if ( galleryPerm3.status.isGranted ) {
                                            onCreateReel.invoke()
                                        } else {
                                            if (galleryPerm3.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select and post videos. Please grant the permission."
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
                                            onCreateReel.invoke()
                                        } else {
                                            if (galleryPerm2.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select and post videos. Please grant the permission."
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
                                            onCreateReel.invoke()
                                        } else {
                                            if (galleryPerm1.status.shouldShowRationale) {
                                                // If the user has denied the permission but the rationale can be shown, then gently explain why the app requires this permission
                                                textToShow = "The app needs to access your phone gallery inorder to be able" +
                                                        " to select and post videos. Please grant the permission."
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
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(text = "Create reel", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    else -> {
                        LaunchedEffect(key1 = true) {
                            showScrollDialog = true
                            delay(7000)
                            showScrollDialog = false
                        }
                        val reels = uiState.reels
                        val pagerState = rememberPagerState { reels.size }

                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                        ) {
                            VerticalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxSize()
                                    .background(Color.Black),
                                userScrollEnabled = false
                            ) { index ->
                                var isVisible by remember {
                                    mutableStateOf(true)
                                }

                                LaunchedEffect(key1 = index) {
                                    Log.d("Babe", "Index is $index")
                                    Log.d("Babe", "Current page is ${pagerState.currentPage}")
                                    isVisible = index == pagerState.currentPage
                                }

                                VideoPlayer(
                                    reel = reels[index],
                                    exoPlayer = viewModel.player,
                                    isVisible = isVisible,
                                    isDarkTheme = isDarkTheme,
                                    onUserProfileImageClicked = onUserProfileImageClicked,
                                    context = context
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxWidth()
                                    .padding(top = 20.dp, end = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    viewModel.player.stop()
                                    onBack.invoke()
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_back),
                                        contentDescription = null,
                                        modifier = Modifier.size(35.dp),
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(
                                    text = "Reels",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = {
                                    viewModel.player.stop()
                                    onSearch.invoke()
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_search),
                                        contentDescription = null,
                                        modifier = Modifier.size(35.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                            if ( showScrollDialog ) {
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .fillMaxWidth()
                                        .padding(top = 20.dp, end = 16.dp, start = 16.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SteelBlue),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "The scroll feature was not implemented! Use the buttons on your bottom right to move to the next or previous video.",
                                        fontSize = 14.sp,
                                        lineHeight = 17.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                            if ( reels.size > 1 ) {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(20.dp)
                                ) {

                                    when (pagerState.currentPage) {
                                        0 -> {
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        pagerState.scrollToPage( pagerState.currentPage+1 )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(EerieBlack)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.ArrowDownward,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                        reels.size-1 -> {
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        pagerState.scrollToPage( pagerState.currentPage-1 )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(EerieBlack)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.ArrowUpward,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                        else -> {
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        pagerState.scrollToPage( pagerState.currentPage-1 )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(EerieBlack)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.ArrowUpward,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }
                                            Spacer(modifier = Modifier.size(10.dp))
                                            IconButton(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        pagerState.scrollToPage( pagerState.currentPage+1 )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(EerieBlack)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.ArrowDownward,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}