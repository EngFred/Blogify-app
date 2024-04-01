package com.engineerfred.kotlin.next.presentation.screens.player

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.engineerfred.kotlin.next.core.SetSystemBarColor

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    videoUrl: String,
    onBackClicked: () -> Unit
) {

    val context = LocalContext.current

    SetSystemBarColor( color = Color.Black )

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

    val exoPlayer =  remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem( MediaItem.fromUri( android.net.Uri.parse(videoUrl) ) )
            prepare()
            play()
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    BackHandler {
        exoPlayer.release()
        onBackClicked.invoke()
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
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
            .fillMaxSize()
            .background(Color.Black)
    )

}