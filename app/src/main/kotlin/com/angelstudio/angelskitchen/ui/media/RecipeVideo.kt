package com.angelstudio.angelskitchen.ui.media

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay

@Composable
fun RecipeVideo(videoId: String, onOpenYouTube: (String) -> Unit) {
    var attempt by remember(videoId) { mutableIntStateOf(0) }
    key(videoId, attempt) {
        VideoAttempt(videoId, onRetry = { attempt++ }, onOpenYouTube = onOpenYouTube)
    }
}

@Composable
private fun VideoAttempt(videoId: String, onRetry: () -> Unit, onOpenYouTube: (String) -> Unit) {
    var failure by remember { mutableStateOf<Int?>(null) }
    val message = failure
    if (message != null) {
        VideoFailure(
            message = message,
            onRetry = onRetry,
            onOpenYouTube = { onOpenYouTube("https://www.youtube.com/watch?v=$videoId") },
        )
    } else {
        EmbeddedPlayer(videoId, onFailure = { failure = it })
    }
}

@Composable
internal fun VideoFailure(@StringRes message: Int, onRetry: () -> Unit, onOpenYouTube: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.md)) {
        Text(
            stringResource(message),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
        )
        if (message == R.string.recipe_video_embedding_disabled) {
            Button(
                onClick = onOpenYouTube,
                modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
            ) {
                Text(stringResource(R.string.recipe_video_open_youtube))
            }
        } else {
            Button(
                onClick = onRetry,
                modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
            ) {
                Text(stringResource(R.string.recipe_video_retry))
            }
        }
    }
}

@Composable
private fun EmbeddedPlayer(videoId: String, onFailure: (Int) -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var ready by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<YouTubePlayer?>(null) }
    val playerView = remember(context) {
        YouTubePlayerView(context).apply { enableAutomaticInitialization = false }
    }

    DisposableEffect(playerView, lifecycle, videoId) {
        var disposed = false
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (disposed) return
                player = youTubePlayer
                ready = true
                youTubePlayer.cueVideo(videoId, 0f)
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                if (disposed) return
                Log.w("RecipeVideo", "Embedded video failed: $error")
                onFailure(videoErrorMessage(error))
            }
        }
        val pauseObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                player?.pause()
            }
        }
        lifecycle.addObserver(playerView)
        lifecycle.addObserver(pauseObserver)
        playerView.initialize(listener)
        onDispose {
            disposed = true
            player?.pause()
            lifecycle.removeObserver(pauseObserver)
            lifecycle.removeObserver(playerView)
            playerView.removeYouTubePlayerListener(listener)
            playerView.release()
        }
    }

    LaunchedEffect(ready) {
        if (!ready) {
            delay(20_000)
            Log.w("RecipeVideo", "Embedded video initialization timed out")
            onFailure(R.string.recipe_video_timeout)
        }

    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KitchenSpacing.md),
    ) {
        if (!ready) {
            CircularProgressIndicator()
            Text(stringResource(R.string.recipe_video_loading))
        }
        AndroidView(
            factory = { playerView },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(KitchenSizes.videoAspectRatio),
        )
    }
}

@StringRes
internal fun videoErrorMessage(error: PlayerConstants.PlayerError): Int = when (error) {
    PlayerConstants.PlayerError.REQUEST_MISSING_HTTP_REFERER -> R.string.recipe_video_configuration_error
    PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> R.string.recipe_video_embedding_disabled
    PlayerConstants.PlayerError.VIDEO_NOT_FOUND -> R.string.recipe_video_not_found
    PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST -> R.string.recipe_video_invalid
    PlayerConstants.PlayerError.HTML_5_PLAYER -> R.string.recipe_video_playback_error
    PlayerConstants.PlayerError.UNKNOWN -> R.string.recipe_video_load_error
}
