package com.angelstudio.angelskitchen.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.angelstudio.angelskitchen.ui.navigation.AppNavigationState
import com.angelstudio.angelskitchen.ui.navigation.CategoryResultsRoute
import com.angelstudio.angelskitchen.ui.navigation.RecipeDetailRoute
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.navigation.rememberAppNavigationState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun topLevelStacksRetainNestedRoutesAndRestoreAfterRecreation() {
        val restoration = StateRestorationTester(compose)
        lateinit var state: AppNavigationState
        restoration.setContent {
            state = rememberAppNavigationState()
            Box(Modifier.fillMaxSize())
        }

        compose.runOnIdle {
            state.select(TopLevelDestination.CATEGORIES)
            state.navigate(CategoryResultsRoute("Chicken"))
            state.navigate(RecipeDetailRoute("52772"))
            state.select(TopLevelDestination.WORLDWIDE)
            state.select(TopLevelDestination.CATEGORIES)
            assertEquals(RecipeDetailRoute("52772"), state.activeBackStack.last())
        }

        restoration.emulateSavedInstanceStateRestore()

        compose.runOnIdle {
            assertEquals(TopLevelDestination.CATEGORIES, state.selectedDestination.value)
            assertEquals(RecipeDetailRoute("52772"), state.activeBackStack.last())
            state.back()
            assertEquals(CategoryResultsRoute("Chicken"), state.activeBackStack.last())
        }
    }
}
