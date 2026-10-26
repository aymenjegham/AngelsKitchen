package com.angelstudio.angelskitchen.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.ui.recipe.RecipeUiState
import com.angelstudio.angelskitchen.ui.recipe.RecipeScreen
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecipeAdaptiveAccessibilityTest {
    @get:Rule
    val compose = createComposeRule()

    private val recipe = Recipe(
        id = "adaptive",
        title = "Roasted seasonal vegetables with fresh garden herbs",
        imageUrl = null,
        category = "Vegetarian",
        cuisine = "Mediterranean",
        ingredients = listOf(RecipeIngredient("Fresh seasonal vegetables", "Two generous cups")),
        instructions = listOf("Wash the vegetables.\n\nRoast until tender."),
        videoId = "abcdefghijk",
        sourceUrl = "https://example.com/recipe",
    )

    @Test
    fun narrowRtlAtDoubleFontScaleKeepsCoreActionsAndTextUnclipped() {
        var actions = 0
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 2f),
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    RecipeScreen(
                        RecipeUiState.Content(recipe), false,
                        { actions++ }, {}, { actions++ }, { actions++ }, { actions++ },
                        { Text("Fake video") },
                        modifier = Modifier.requiredWidth(320.dp),
                    )
                }
            }
        }
        compose.onNodeWithText(recipe.title).performScrollTo()
        assertTextFits(recipe.title)
        listOf(
            "Browse category: Vegetarian" to "Vegetarian",
            "Browse cuisine: Mediterranean" to "Mediterranean",
            "Read original recipe on example.com" to "Read original recipe",
        ).forEach { (description, label) ->
            compose.onNodeWithTag("recipe-content")
                .performScrollToNode(hasContentDescription(description))
            compose.onNodeWithContentDescription(description).performScrollTo()
                .assertIsDisplayed().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp)
            assertTextFits(label)
            compose.onNodeWithContentDescription(description).performClick()
        }
        compose.onNodeWithTag("recipe-content").performScrollToNode(hasText("Play video"))
        compose.onNodeWithText("Play video").performScrollTo()
            .assertIsDisplayed().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp)
        assertTextFits("Play video")
        compose.onNodeWithText("Play video").performClick()
        compose.onNodeWithText("Fake video").assertIsDisplayed()
        compose.onNodeWithContentDescription("Back").assertIsDisplayed().performClick()
        compose.runOnIdle { assertEquals(4, actions) }
    }

    @Test
    fun expandedActualContentWidthPlacesIngredientsBesidePreparation() {
        compose.setContent {
            val configuration = LocalConfiguration.current
            val deviceDensity = LocalDensity.current
            // Fit a real 900dp layout into the device viewport without changing its window.
            val density = deviceDensity.density * configuration.screenWidthDp / 900f
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale = 1f)) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    RecipeScreen(
                        RecipeUiState.Content(recipe), false, {}, {}, {}, {}, {}, {},
                        modifier = Modifier.requiredWidth(900.dp),
                    )
                }
            }
        }
        compose.onNodeWithText("Ingredients").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Preparation").assertIsDisplayed()
        val ingredients = compose.onNodeWithText("Ingredients").fetchSemanticsNode().boundsInRoot
        val preparation = compose.onNodeWithText("Preparation").fetchSemanticsNode().boundsInRoot
        assertEquals("Section headings share one row", ingredients.top, preparation.top, 1f)
        assertTrue("Preparation is alongside ingredients", preparation.left > ingredients.right)
    }

    @Test
    fun warmLightNormalTextRolesMeetWcagContrast() = assertWarmContrast(Configuration.UI_MODE_NIGHT_NO)

    @Test
    fun warmDarkNormalTextRolesMeetWcagContrast() = assertWarmContrast(Configuration.UI_MODE_NIGHT_YES)

    private fun assertTextFits(text: String) {
        val results = mutableListOf<TextLayoutResult>()
        val node = compose.onNodeWithText(text, useUnmergedTree = true)
        node.assertIsDisplayed().performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(results)
        }
        assertTrue("Text layout is available: $text", results.isNotEmpty())
        results.forEach {
            val details = "text=$text size=${it.size} overflowHeight=${it.didOverflowHeight} " +
                "overflowWidth=${it.didOverflowWidth} lines=${it.lineCount} " +
                "firstBaseline=${it.firstBaseline} lastBaseline=${it.lastBaseline} " +
                "maxLines=${it.layoutInput.maxLines} constraints=${it.layoutInput.constraints} " +
                "paragraphWidth=${it.multiParagraph.width} " +
                "maxIntrinsicWidth=${it.multiParagraph.intrinsics.maxIntrinsicWidth} " +
                "lineBounds=${(0 until it.lineCount).map { line -> it.getLineLeft(line) to it.getLineRight(line) }}"
            // Paragraph width can retain a larger measurement constraint; inspect rendered lines.
            assertTrue("Text has rendered lines: $details", it.lineCount > 0)
            for (line in 0 until it.lineCount) {
                assertFalse("Line $line is not ellipsized: $details", it.isLineEllipsized(line))
                assertTrue("Line $line starts within text bounds: $details", it.getLineLeft(line) >= 0f)
                assertTrue("Line $line ends within text bounds: $details", it.getLineRight(line) <= it.size.width)
                assertTrue("Line $line fits vertically: $details", it.getLineBottom(line) <= it.size.height)
            }
            assertEquals(
                "All source text is rendered: $details",
                it.layoutInput.text.length,
                it.getLineEnd(it.lineCount - 1, visibleEnd = false),
            )
        }
        val bounds = node.fetchSemanticsNode().boundsInRoot
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        assertTrue("Text stays within the viewport: $text", bounds.left >= root.left && bounds.right <= root.right)
    }

    private fun assertWarmContrast(nightMode: Int) {
        var colors: ColorScheme? = null
        compose.setContent {
            val configuration = Configuration(LocalConfiguration.current).apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or nightMode
            }
            CompositionLocalProvider(LocalConfiguration provides configuration) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    val scheme = MaterialTheme.colorScheme
                    SideEffect { colors = scheme }
                    Text("Contrast fixture")
                }
            }
        }
        compose.runOnIdle {
            val scheme = requireNotNull(colors)
            val roles = listOf(
                "body" to (scheme.onSurface to scheme.surface),
                "ingredient measure" to (scheme.onSurfaceVariant to scheme.surface),
                "button" to (scheme.onPrimary to scheme.primary),
                "chip label" to (scheme.onSurfaceVariant to scheme.surface),
                "image placeholder" to (scheme.onSurface to scheme.surfaceContainerHighest),
                "app bar" to (scheme.onSurface to scheme.surfaceContainer),
                "hero title" to (scheme.onSurface to scheme.primaryContainer),
                "hero eyebrow" to (scheme.onPrimaryContainer to scheme.primaryContainer),
                "ingredient card text" to (scheme.onSurface to scheme.surfaceContainerLowest),
                "ingredient card measure" to (scheme.onSurfaceVariant to scheme.surfaceContainerLowest),
            )
            roles.forEach { (name, pair) ->
                val ratio = contrast(pair.first, pair.second)
                assertTrue("$name contrast is $ratio; expected at least 4.5", ratio >= 4.5f)
            }
        }
    }

    private fun contrast(foreground: Color, background: Color): Float {
        val first = foreground.luminance()
        val second = background.luminance()
        return (maxOf(first, second) + 0.05f) / (minOf(first, second) + 0.05f)
    }
}
