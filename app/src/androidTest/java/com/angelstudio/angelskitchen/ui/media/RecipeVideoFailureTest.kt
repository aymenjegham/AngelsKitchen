package com.angelstudio.angelskitchen.ui.media

import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipeVideoFailureTest {
    @get:Rule val compose = createComposeRule()

    @Test fun restrictedVideoOffersYouTubeInsteadOfRetry() {
        var opens = 0
        var retries = 0
        val message = appStringId("recipe_video_embedding_disabled")
        compose.setContent {
            AngelsKitchenTheme {
                VideoFailure(message, { retries++ }, { opens++ })
            }
        }
        compose.onNodeWithText("Retry video").assertDoesNotExist()
        compose.onNodeWithText("Open in YouTube").assertHeightIsAtLeast(48.dp).performClick()
        compose.runOnIdle {
            assertEquals(1, opens)
            assertEquals(0, retries)
        }
    }

    @Test fun transientFailureStillOffersRetry() {
        var retries = 0
        val message = appStringId("recipe_video_timeout")
        compose.setContent {
            AngelsKitchenTheme {
                VideoFailure(message, { retries++ }, {})
            }
        }
        compose.onNodeWithText("Open in YouTube").assertDoesNotExist()
        compose.onNodeWithText("Retry video").assertHeightIsAtLeast(48.dp).performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    private fun appStringId(name: String): Int {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return context.resources.getIdentifier(name, "string", context.packageName).also {
            assertTrue("Missing app string resource: $name", it != 0)
        }
    }
}
