package com.angelstudio.angelskitchen.ui.results

import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListItem
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import com.angelstudio.angelskitchen.ui.components.AngieNavigationBar
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipeListScreenTest {
    @get:Rule val compose = createComposeRule()
    private val selection = RecipeListQuery(RecipeListKind.CATEGORY, "Vegan")
    private val recipes = listOf(
        RecipeListItem("10001", "Chickpea coconut curry", null),
        RecipeListItem("10002", "Lentil bolognese", null),
        RecipeListItem("10003", "Herby quinoa tabbouleh", null),
        RecipeListItem("10004", "Crispy tofu stir-fry", null),
        RecipeListItem("10005", "Roasted beetroot soup", null),
    )

    @Test
    fun categoryResultsOpenRecipesAndNameSearchFiltersWithoutRefetching() {
        var opened = ""
        compose.setContent {
            AngelsKitchenTheme {
                RecipeListScreen(selection, RecipeListState.Content(recipes), {}, {}, { opened = it })
            }
        }
        compose.onNodeWithText("Plant-based, full flavour").assertExists()
        compose.onNodeWithTag("result-10001").assertHeightIsAtLeast(48.dp).performClick()
        compose.runOnIdle { assertEquals("10001", opened) }
        compose.onNodeWithContentDescription("Search this recipe list")
            .assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        compose.onNodeWithTag("results-search").performTextInput("LENTIL")
        compose.onNodeWithText("Lentil bolognese").assertIsDisplayed()
        compose.onNodeWithText("Chickpea coconut curry").assertDoesNotExist()
        compose.onNodeWithContentDescription("Clear recipe search").performClick()
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasText("Chickpea coconut curry"))
        compose.onNodeWithText("Chickpea coconut curry").assertIsDisplayed()
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasText("Search recipes by name"))
        compose.onNodeWithTag("results-search").performTextInput("no-such-recipe")
        compose.onNodeWithText("No matching recipes. Try another name.").assertIsDisplayed()
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasContentDescription("Close recipe search"))
        compose.onNodeWithContentDescription("Close recipe search").performClick()
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasText("Chickpea coconut curry"))
        compose.onNodeWithText("Chickpea coconut curry").assertIsDisplayed()
    }

    @Test
    fun countryUsesReadableNameAndProvidesBackAndRetry() {
        var backs = 0
        var retries = 0
        compose.setContent {
            AngelsKitchenTheme {
                RecipeListScreen(
                    RecipeListQuery(RecipeListKind.COUNTRY, "italian"),
                    RecipeListState.Error(RecipeListError.NETWORK),
                    { backs++ }, { retries++ }, {},
                )
            }
        }
        compose.onNodeWithText("Italy").assertIsDisplayed()
        compose.onNodeWithText("Pasta and risotto").assertIsDisplayed()
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasText("Try again"))
        compose.onNodeWithText("Try again").performClick()
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasContentDescription("Back"))
        compose.onNodeWithContentDescription("Back").performClick()
        compose.runOnIdle { assertEquals(1, retries); assertEquals(1, backs) }
    }

    @Test
    fun missingArgumentsAreVisibleWithoutRetry() {
        compose.setContent {
            AngelsKitchenTheme {
                RecipeListScreen(
                    selection.copy(value = ""),
                    RecipeListState.Error(RecipeListError.INVALID_ARGUMENT), {}, {}, {},
                )
            }
        }
        compose.onNodeWithText("This selection is missing or invalid. Go back and choose a category or country.")
            .assertIsDisplayed()
        compose.onNodeWithText("Try again").assertDoesNotExist()
    }

    @Test
    fun emptySelectionHasZeroCountAndDistinctEmptyMessage() {
        compose.setContent {
            AngelsKitchenTheme {
                RecipeListScreen(selection, RecipeListState.Content(emptyList()), {}, {}, {})
            }
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val resources = context.resources
        val countResourceId = resources.getIdentifier("results_count", "plurals", context.packageName)
        assertTrue("Missing app plural resource: results_count", countResourceId != 0)
        val count = resources.getQuantityString(countResourceId, 0, 0)
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasText(count))
        compose.onNodeWithText(count).assertIsDisplayed()
        compose.onNodeWithTag("recipe-results")
            .performScrollToNode(hasText("No recipes are available for this selection yet."))
        compose.onNodeWithText("No recipes are available for this selection yet.").assertIsDisplayed()
        compose.onNodeWithText("No matching recipes. Try another name.").assertDoesNotExist()
    }

    @Test
    fun searchAndFilteredCountSurviveSavedStateRestoration() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            AngelsKitchenTheme {
                RecipeListScreen(selection, RecipeListState.Content(recipes), {}, {}, {})
            }
        }
        compose.onNodeWithContentDescription("Search this recipe list").performClick()
        compose.onNodeWithTag("results-search").performTextInput("lentil")
        restoration.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("results-search").assertIsDisplayed()
        compose.onNodeWithText("1 recipe").assertIsDisplayed()
        compose.onNodeWithText("Lentil bolognese").assertIsDisplayed()
        compose.onNodeWithText("Chickpea coconut curry").assertDoesNotExist()
    }

    @Test
    fun narrowRtlAtDoubleFontScaleKeepsRecipeTitleAndActionsComplete() {
        val title = "Chickpea coconut curry with roasted vegetables and fragrant herbs"
        compose.setContent {
            CompositionLocalProvider(
                LocalDensity provides Density(LocalDensity.current.density, 2f),
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                AngelsKitchenTheme {
                    Box(Modifier.width(320.dp)) {
                        RecipeListScreen(
                            selection, RecipeListState.Content(listOf(RecipeListItem("10001", title, null))), {}, {}, {},
                        )
                    }
                }
            }
        }
        compose.onNodeWithTag("recipe-results").performScrollToNode(hasText(title))
        compose.onNodeWithText(title, useUnmergedTree = true)
            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                val layouts = mutableListOf<TextLayoutResult>()
                assertTrue(action(layouts))
                layouts.forEach { layout ->
                    repeat(layout.lineCount) { line ->
                        assertTrue(!layout.isLineEllipsized(line))
                        assertTrue(layout.getLineLeft(line) >= 0)
                        assertTrue(layout.getLineRight(line) <= layout.size.width)
                        assertTrue(layout.getLineBottom(line) <= layout.size.height)
                    }
                    assertEquals(layout.layoutInput.text.length, layout.getLineEnd(layout.lineCount - 1))
                }
            }
        compose.onNodeWithTag("result-10001").assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp)
    }

    @Test
    fun captureFourthScreenReference() {
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
                        Box(Modifier.weight(1f)) {
                            RecipeListScreen(selection, RecipeListState.Content(recipes), {}, {}, {})
                        }
                        AngieNavigationBar(TopLevelDestination.CATEGORIES, onSelect = {})
                    }
                }
            }
        }
        val output = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir, "angie-results.png")
        output.outputStream().use {
            assertTrue(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
        }
    }
}
