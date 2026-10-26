package com.angelstudio.angelskitchen.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.angelskitchen.ui.components.AngieNavigationBar
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AngieNavigationBarTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun tabsHaveAccessibleTargetsAndSelection() {
        compose.setContent {
            var selected by remember { mutableStateOf(TopLevelDestination.TODAY) }
            AngelsKitchenTheme(useDynamicColors = false) {
                AngieNavigationBar(selected, onSelect = { selected = it })
            }

        }
        compose.onNodeWithText("Today").assertIsSelected()
        listOf("Today", "Categories", "Worldwide").forEach { label ->
            compose.onNodeWithText(label)
                .assertHeightIsAtLeast(48.dp)
                .assertWidthIsAtLeast(48.dp)
                .performClick()
                .assertIsSelected()
        }
    }

    @Test
    fun narrowNavigationSupportsRtlAndLargeText() {
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(
                LocalDensity provides Density(density, 2f),
                LocalLayoutDirection provides LayoutDirection.Rtl,
            ) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    Box(Modifier.width(320.dp)) {
                        AngieNavigationBar(TopLevelDestination.TODAY, onSelect = {})
                    }
                }
            }
        }
        listOf("Today", "Categories", "Worldwide").forEach { label ->
            compose.onNodeWithText(label).assertIsDisplayed()
            compose.onNodeWithText(label, useUnmergedTree = true)
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { action ->
                    val results = mutableListOf<androidx.compose.ui.text.TextLayoutResult>()
                    assertTrue(action(results))
                    results.forEach { result ->
                        repeat(result.lineCount) { line ->
                            assertTrue(!result.isLineEllipsized(line))
                            assertTrue(result.getLineBottom(line) <= result.size.height)
                        }
                        assertTrue(result.getLineEnd(result.lineCount - 1) == result.layoutInput.text.length)
                    }
                }
        }
    }
}
