package com.example.movieapp.detail.presentation

import android.media.browse.MediaBrowser
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
//import androidx.media3.exoplayer.ExoPlayer
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem


import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    val context = LocalContext.current
    var player by remember { mutableStateOf<ExoPlayer?>(null) }
    if (videoUrl.isNullOrBlank()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No trailer available", color = Color.Red)
        }
        return
    }
    DisposableEffect(videoUrl) {
        // Create Player
        player = ExoPlayer.Builder(context).build().apply {
            try {
                val media = MediaItem.fromUri(Uri.parse(videoUrl))
                setMediaItem(media)
                prepare()
                playWhenReady = autoPlay
            }catch (e: Exception){
                Log.e("VideoPlayer", "Error loading video: ${e.message}")
            }
        }


        onDispose {
            player?.release()
            player = null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(it).apply {
                this.player = player
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                useController = true
            }
        }
    )
}
