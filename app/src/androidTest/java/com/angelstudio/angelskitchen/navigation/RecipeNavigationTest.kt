package com.angelstudio.angelskitchen.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.angelskitchen.ui.navigation.CategoryResultsRoute
import com.angelstudio.angelskitchen.ui.navigation.CountryResultsRoute
import com.angelstudio.angelskitchen.ui.navigation.RecipeDetailRoute
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.navigation.rememberAppNavigationState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeNavigationTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun categoryAndCountryRecipeFlowsPreserveArgumentsAndBackStack() {
        lateinit var state: com.angelstudio.angelskitchen.ui.navigation.AppNavigationState
        compose.setContent {
            state = rememberAppNavigationState()
            Box {}
        }
        compose.runOnIdle {
            state.select(TopLevelDestination.CATEGORIES)
            state.navigate(CategoryResultsRoute("Chicken"))
            state.navigate(RecipeDetailRoute("52772"))
            assertEquals(RecipeDetailRoute("52772"), state.activeBackStack.last())
            state.back()
            assertEquals(CategoryResultsRoute("Chicken"), state.activeBackStack.last())

            state.navigate(CountryResultsRoute("Italian"))
            state.navigate(RecipeDetailRoute("52804"))
            assertEquals(RecipeDetailRoute("52804"), state.activeBackStack.last())
            state.back()
            assertEquals(CountryResultsRoute("Italian"), state.activeBackStack.last())
        }
    }

    @Test
    fun selectingAnotherTabRetainsItsOwnRecipeStack() {
        lateinit var state: com.angelstudio.angelskitchen.ui.navigation.AppNavigationState
        compose.setContent {
            state = rememberAppNavigationState()
            Box {}
        }
        compose.runOnIdle {
            state.select(TopLevelDestination.CATEGORIES)
            state.navigate(CategoryResultsRoute("Chicken"))
            state.select(TopLevelDestination.WORLDWIDE)
            state.navigate(CountryResultsRoute("Italian"))
            state.select(TopLevelDestination.CATEGORIES)
            assertEquals(CategoryResultsRoute("Chicken"), state.activeBackStack.last())
            state.select(TopLevelDestination.WORLDWIDE)
            assertEquals(CountryResultsRoute("Italian"), state.activeBackStack.last())
        }
    }
}
