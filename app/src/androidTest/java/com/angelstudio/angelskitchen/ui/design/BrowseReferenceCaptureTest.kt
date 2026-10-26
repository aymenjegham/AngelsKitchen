package com.angelstudio.angelskitchen.ui.design

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import com.angelstudio.angelskitchen.ui.browse.BrowseCategoriesState
import com.angelstudio.angelskitchen.ui.browse.BrowseCategory
import com.angelstudio.angelskitchen.ui.browse.CategoriesScreen
import com.angelstudio.angelskitchen.ui.browse.CountriesScreen
import com.angelstudio.angelskitchen.ui.components.AngieNavigationBar
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.File

class BrowseReferenceCaptureTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun captureCategoriesReference() {
        render(TopLevelDestination.CATEGORIES) {
            CategoriesScreen(
                state = BrowseCategoriesState.Content(
                    listOf("Breakfast", "Beef", "Chicken", "Dessert", "Vegan", "Vegetarian", "Seafood")
                        .map { BrowseCategory(it, "", "") },
                ),
                onRetry = {},
                onCategoryClick = {},
            )
        }
        capture("angie-categories.png")
    }

    @Test
    fun captureWorldwideReference() {
        render(TopLevelDestination.WORLDWIDE) { CountriesScreen(onCountryClick = {}) }
        capture("angie-worldwide.png")
    }

    private fun render(selectedTab: TopLevelDestination, content: @Composable () -> Unit) {
        compose.setContent {
            val configuration = Configuration(LocalConfiguration.current).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or Configuration.UI_MODE_NIGHT_NO
            }
            CompositionLocalProvider(
                LocalConfiguration provides configuration,
                LocalDensity provides Density(LocalDensity.current.density, 1f),
            ) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    Column(Modifier.fillMaxSize()) {
                        Box(Modifier.weight(1f)) { content() }
                        AngieNavigationBar(selectedTab, onSelect = {})
                    }
                }
            }
        }
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val output = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, name)
        output.outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
    }
}
