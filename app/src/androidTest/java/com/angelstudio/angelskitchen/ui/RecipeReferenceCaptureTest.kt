package com.angelstudio.angelskitchen.ui

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import coil.ImageLoader
import coil.compose.LocalImageLoader
import coil.intercept.Interceptor
import coil.request.ErrorResult
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.ui.recipe.RecipeUiState
import com.angelstudio.angelskitchen.ui.components.AngieNavigationBar
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.recipe.RecipeScreen
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipeReferenceCaptureTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun captureTodayReference() {
        val fixture = Recipe(
            id = "reference",
            title = "Classic shakshuka",
            imageUrl = null,
            category = "Vegetarian",
            cuisine = "Tunisian",
            ingredients = listOf(
                RecipeIngredient("Tomatoes", "4 ripe"),
                RecipeIngredient("Red pepper", "1 large"),
                RecipeIngredient("Eggs", "4"),
            ),
            instructions = listOf("Cook the tomatoes and pepper, then add the eggs."),
            videoId = null,
            sourceUrl = null,
        )
        compose.setContent {
            val configuration = Configuration(LocalConfiguration.current).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
            }
            val density = LocalDensity.current
            val context = LocalContext.current
            val images = remember(context) {
                ImageLoader.Builder(context).components {
                    add(Interceptor { chain ->
                        ErrorResult(null, chain.request, IllegalStateException("Deterministic reference image fallback"))
                    })
                }.build()
            }
            CompositionLocalProvider(
                LocalConfiguration provides configuration,
                LocalDensity provides Density(density.density, fontScale = 1f),
                LocalImageLoader provides images,
            ) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    Column(Modifier.fillMaxSize()) {
                        RecipeScreen(
                            state = RecipeUiState.Content(fixture),
                            isToday = true,
                            onBack = {},
                            onRetry = {},
                            onCategory = {},
                            onCuisine = {},
                            onSource = {},
                            videoContent = { Text(it) },
                            modifier = Modifier.weight(1f),
                        )
                        AngieNavigationBar(selectedDestination = TopLevelDestination.TODAY, onSelect = {})
                    }
                }
            }
        }
        compose.waitForIdle()
        val image = compose.onRoot().captureToImage().asAndroidBitmap()
        val output = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "angie-today.png")
        output.outputStream().use { assertTrue(image.compress(Bitmap.CompressFormat.PNG, 100, it)) }
        assertTrue(output.length() > 0)
    }
}
