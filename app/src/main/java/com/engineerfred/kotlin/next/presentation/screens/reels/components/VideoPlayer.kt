package com.engineerfred.kotlin.next.presentation.screens.reels.components

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CommentBank
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.engineerfred.kotlin.next.domain.model.Post
import com.engineerfred.kotlin.next.presentation.screens.reels.ReelsViewModel.Companion.cacheDataSourceFactory
import com.engineerfred.kotlin.next.presentation.theme.Charcoal
import com.engineerfred.kotlin.next.utils.formatCommentTimeStamp
import com.google.firebase.auth.FirebaseAuth

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    reel: Post,
    exoPlayer: ExoPlayer,
    isVisible: Boolean,
    isDarkTheme: Boolean,
    onUserProfileImageClicked: (String) -> Unit,
    context: Context
) {

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current
    var isSheetOpen by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = reel) {
        val videoUri = Uri.parse(reel.videoUrl!!)
        val mediaItem = MediaItem.fromUri(videoUri)
        val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(mediaItem)
        exoPlayer.apply {
            setMediaSource(mediaSource, true)
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE
        }
        if (isVisible && !exoPlayer.isPlaying) { // Play only if visible and not already playing
            exoPlayer.playWhenReady = true
            exoPlayer.play()
        } else {
            exoPlayer.pause()
        }
    }

    LaunchedEffect(key1 = isSheetOpen) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = isSheetOpen
    }

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event -> lifecycle = event }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    useController = false
                    hideController()
                    this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            update = {
                when (lifecycle) {
                    Lifecycle.Event.ON_PAUSE -> {
                        it.onPause()
                        it.player?.pause()
                    }

                    Lifecycle.Event.ON_RESUME -> {
                        it.onResume()
                        it.player?.play()
                    }

                    else -> Unit
                }
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.Black)
                .clickable { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() }
                .fillMaxSize()
        )
        Column(
            Modifier.align(Alignment.BottomStart)
        ) {
            Column(
                    Modifier.padding(start = 20.dp, end = 80.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = reel.ownerProfileImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {
                                if (reel.ownerId != FirebaseAuth.getInstance().uid) {
                                    exoPlayer.pause()
                                    onUserProfileImageClicked.invoke(reel.ownerId)
                                }
                            }
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Charcoal),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = reel.ownerName,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )
                }
                if (reel.caption.isNotEmpty()) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = reel.caption,
                        color = Color.White,
                        maxLines = 3,
                        lineHeight = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.size(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = formatCommentTimeStamp(reel.postedOn),
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color.Black.copy(alpha = .5f))
                    .padding(bottom = 10.dp, start = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            Toast
                                .makeText(
                                    context,
                                    "This feature was not implemented!",
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { Toast.makeText(context,"This feature was not implemented!", Toast.LENGTH_LONG).show() }) {
                        Icon(
                            imageVector = Icons.Rounded.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(text = "243", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isSheetOpen = true },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {
                        isSheetOpen = true
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.CommentBank,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(text = "43", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if ( isSheetOpen ) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                isSheetOpen = false
            },

        ) {
            Spacer(modifier = Modifier.size(50.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Image(imageVector = Icons.Rounded.Info, contentDescription = null, modifier = Modifier.size(70.dp) )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "The comment section for reels was not implemented!!",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(300.dp))
        }
    }
}