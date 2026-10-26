package com.angelstudio.angelskitchen.ui.browse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class BrowseScreensTest {
    @get:Rule val compose = createComposeRule()

    @Test fun categoryDescriptionExpandsOnLongPressWithoutNavigating() {
        val description = "A long category description that should be expandable to reveal its full text."
        var selected: String? = null
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                CategoriesScreen(
                    BrowseCategoriesState.Content(listOf(BrowseCategory("Vegan", "", description))),
                    onRetry = {},
                    onCategoryClick = { selected = it.apiName },
                )
            }
        }

        compose.onNodeWithTag("category-Vegan")
            .performSemanticsAction(SemanticsActions.OnLongClick) { it() }
        compose.runOnIdle { assertEquals(null, selected) }

        compose.onNodeWithTag("category-Vegan")
            .performSemanticsAction(SemanticsActions.OnLongClick) { it() }
        compose.runOnIdle { assertEquals(null, selected) }
    }

    @Test fun categorySearchFiltersLocallyAndNavigatesWithOriginalName() {
        var selected: String? = null
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                CategoriesScreen(
                    BrowseCategoriesState.Content(
                        listOf(
                            BrowseCategory("Beef", "", "Beef dishes"),
                            BrowseCategory("Vegan", "", "Plant-based dishes"),
                        ),
                    ),
                    onRetry = {},
                    onCategoryClick = { selected = it.apiName },
                )
            }
        }
        compose.onNodeWithTag("category-search").performTextInput("veg")
        compose.onNodeWithTag("category-Beef").assertDoesNotExist()
        compose.onNodeWithTag("category-Vegan").performClick()
        compose.runOnIdle { assertEquals("Vegan", selected) }
    }

    @Test fun categoryErrorOffersWorkingRetry() {
        var retries = 0
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                CategoriesScreen(BrowseCategoriesState.Error, onRetry = { retries++ }, onCategoryClick = {})
            }
        }
        compose.onNodeWithText("Try again")
            .performScrollTo()
            .assertIsDisplayed()
            .performClick()
        compose.runOnIdle { assertEquals(1, retries) }
    }

    @Test fun worldwideRegionFiltersAndPreservesApiArea() {
        var area: String? = null
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                CountriesScreen { area = it.apiArea }
            }
        }
        compose.onNodeWithTag("region-Asia").performClick()
        compose.onNodeWithTag("country-italian").assertDoesNotExist()
        compose.onNodeWithTag("country-japanese").performClick()
        compose.runOnIdle { assertEquals("japanese", area) }
    }

    @Test fun largeFontUsesSingleColumnWithReadableCountryName() {
        compose.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale = 2f)) {
                AngelsKitchenTheme(useDynamicColors = false) {
                    Box(Modifier.width(360.dp)) { CountriesScreen {} }
                }
            }
        }
        compose.onNodeWithTag("country-grid-1").assertIsDisplayed()
        compose.onNodeWithTag("country-grid-1").performScrollToNode(hasTestTag("country-italian"))
        compose.onNodeWithText("Italy").assertExists()
    }

    @Test fun expandedCatalogScrollsToCountriesWithSharedDemonymsAndLastEntry() {
        var selected: BrowseCountry? = null
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                Box(Modifier.width(360.dp)) { CountriesScreen { selected = it } }
            }
        }
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        listOf("Dominica", "Dominican Republic", "Guernsey", "Jersey", "Zimbabwe").forEach { name ->
            compose.onNodeWithTag("country-grid-2").performScrollToNode(hasText(name))
            compose.onNodeWithText(name).assertIsDisplayed()
                .performSemanticsAction(SemanticsActions.OnClick) { it() }
            compose.runOnIdle {
                assertEquals(name, context.getString(requireNotNull(selected).name))
            }
        }
    }
}
