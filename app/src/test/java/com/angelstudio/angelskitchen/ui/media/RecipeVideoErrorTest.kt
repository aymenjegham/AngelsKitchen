package com.angelstudio.angelskitchen.ui.media

import com.angelstudio.angelskitchen.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants.PlayerError
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeVideoErrorTest {
    @Test fun playerErrorsHaveDistinctActionableMessages() {
        val expected = mapOf(
            PlayerError.UNKNOWN to R.string.recipe_video_load_error,
            PlayerError.INVALID_PARAMETER_IN_REQUEST to R.string.recipe_video_invalid,
            PlayerError.HTML_5_PLAYER to R.string.recipe_video_playback_error,
            PlayerError.VIDEO_NOT_FOUND to R.string.recipe_video_not_found,
            PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER to R.string.recipe_video_embedding_disabled,
            PlayerError.REQUEST_MISSING_HTTP_REFERER to R.string.recipe_video_configuration_error,
        )
        assertEquals(PlayerError.entries.toSet(), expected.keys)
        expected.forEach { (error, message) -> assertEquals(message, videoErrorMessage(error)) }
    }
}
