package com.angelstudio.angelskitchen.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertWidthIsAtLeast
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.ui.recipe.RecipeUiState
import com.angelstudio.angelskitchen.ui.recipe.RecipeScreen
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import androidx.core.os.ConfigurationCompat
import androidx.test.platform.app.InstrumentationRegistry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val recipe = Recipe(
        id = "12345",
        title = "Test recipe",
        imageUrl = null,
        category = "Vegetarian",
        cuisine = "Italian",
        ingredients = listOf(RecipeIngredient("Tomato", "2 cups")),
        instructions = listOf("First paragraph.\n\nSecond paragraph."),
        videoId = "abcdefghijk",
        sourceUrl = "https://example.com/recipe",
    )

    @Test
    fun sectionsHaveHeadingsAndIngredientsHaveMergedSemantics() {
        show()
        compose.onNodeWithText("Ingredients").performScrollTo()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("Tomato", substring = true)
            .assertTextContains("2 cups")
        compose.onNodeWithText("Preparation").performScrollTo()
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText("First paragraph.\n\nSecond paragraph.").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun metadataSourceAndBackActionsHaveMinimumTargetsAndCallbacks() {
        var category: String? = null
        var cuisine: String? = null
        var source: String? = null
        var backs = 0
        show(onCategory = { category = it }, onCuisine = { cuisine = it }, onSource = { source = it }, onBack = { backs++ })
        compose.onNodeWithContentDescription("Back")
            .assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        compose.onNodeWithContentDescription("Browse category: Vegetarian")
            .performScrollTo().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        compose.onNodeWithContentDescription("Browse cuisine: Italian")
            .performScrollTo().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        compose.onNodeWithTag("recipe-content")
            .performScrollToNode(hasContentDescription("Read original recipe on example.com"))
        compose.onNodeWithContentDescription("Read original recipe on example.com")
            .performScrollTo().assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        compose.runOnIdle {
            assertEquals(1, backs)
            assertEquals(recipe.category, category)
            assertEquals(recipe.cuisine, cuisine)
            assertEquals(recipe.sourceUrl, source)
        }
    }

    @Test
    fun videoIsCreatedOnlyAfterPlayAndResetsForAnotherRecipe() {
        val state = mutableStateOf<RecipeUiState>(RecipeUiState.Content(recipe))
        show(state)
        compose.onNodeWithText("Fake video abcdefghijk").assertDoesNotExist()
        scrollToText("Play video")
        compose.onNodeWithText("Play video").performScrollTo()
            .assertHeightIsAtLeast(48.dp).assertWidthIsAtLeast(48.dp).performClick()
        compose.onNodeWithText("Fake video abcdefghijk").assertIsDisplayed()
        compose.runOnIdle { state.value = RecipeUiState.Content(recipe.copy(id = "67890")) }
        compose.onNodeWithText("Fake video abcdefghijk").assertDoesNotExist()
        scrollToText("Play video")
        compose.onNodeWithText("Play video").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun retryIsActionableButInvalidArgumentsCannotRetry() {
        val state = mutableStateOf<RecipeUiState>(RecipeUiState.Error(RecipeError.NETWORK))
        var retries = 0
        show(state, onRetry = { retries++ })
        compose.onNodeWithText("Try again").assertHeightIsAtLeast(48.dp)
            .assertWidthIsAtLeast(48.dp).performClick()
        compose.runOnIdle {
            assertEquals(1, retries)
            state.value = RecipeUiState.Error(RecipeError.INVALID_ARGUMENT)
        }
        compose.onNodeWithText("Try again").assertDoesNotExist()
        compose.onNodeWithText("This recipe link is invalid.", substring = true).assertIsDisplayed()
    }

    @Test
    fun invalidOptionalMediaIsOmittedAndMissingCoreContentIsVisible() {
        show(mutableStateOf(RecipeUiState.Content(recipe.copy(
            ingredients = emptyList(), instructions = emptyList(), videoId = "invalid", sourceUrl = "javascript:alert(1)",
        ))))
        compose.onNodeWithText("Ingredients are not available for this recipe.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Preparation instructions are not available for this recipe.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Play video").assertDoesNotExist()
        compose.onNodeWithText("Read original recipe").assertDoesNotExist()
    }

    @Test
    fun loadingIsAnnouncedAndTodayHasNoBackAction() {
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                RecipeScreen(
                    RecipeUiState.Loading, true, {}, {}, {}, {}, {}, {},
                )
            }
        }
        compose.onNodeWithText("Today with Angie").assertIsDisplayed()
        compose.onNodeWithText("Loading recipe").assertIsDisplayed()
        compose.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun explicitVideoChoiceSurvivesSavedStateRestoration() {
        val restoration = StateRestorationTester(compose)
        restoration.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                RecipeScreen(
                    RecipeUiState.Content(recipe), false, {}, {}, {}, {}, {},
                    { Text("Restored video $it") },
                )
            }
        }
        scrollToText("Play video")
        compose.onNodeWithText("Play video").performScrollTo().performClick()
        restoration.emulateSavedInstanceStateRestore()
        scrollToText("Restored video abcdefghijk")
        compose.onNodeWithText("Restored video abcdefghijk").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Play video").assertDoesNotExist()
    }

    @Test
    fun todayShowsLocalizedDateRealIngredientCountAndStartCookingScrollsToPreparation() {
        compose.setContent {
            AngelsKitchenTheme {
                RecipeScreen(RecipeUiState.Content(recipe), true, {}, {}, {}, {}, {}, {})
            }
        }
        val configuration = InstrumentationRegistry.getInstrumentation().targetContext.resources.configuration
        val locale = ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
        val pattern = android.text.format.DateFormat.getBestDateTimePattern(locale, "EEEE d MMMM")
        compose.onNodeWithText(SimpleDateFormat(pattern, locale).format(Date())).assertIsDisplayed()
        compose.onNodeWithText("Today with Angie").assertIsDisplayed()
        compose.onNodeWithText("ANGIE'S PICK TONIGHT").assertIsDisplayed()
        compose.onNodeWithText("Start cooking").assertHeightIsAtLeast(48.dp)
            .assertWidthIsAtLeast(48.dp).performClick()
        compose.onNodeWithText("Preparation").assertIsDisplayed()
        scrollToText("1 item")
        compose.onNodeWithText("1 item").assertIsDisplayed()
    }

    @Test
    fun detailsDoNotUseTodayOnlyEditorialLabels() {
        show()
        compose.onNodeWithText("Today with Angie").assertDoesNotExist()
        compose.onNodeWithText("ANGIE'S PICK TONIGHT").assertDoesNotExist()
        compose.onNodeWithText("Start cooking").assertIsDisplayed()
    }

    private fun scrollToText(text: String) {
        compose.onNodeWithTag("recipe-content").performScrollToNode(hasText(text))
    }

    private fun show(
        state: MutableState<RecipeUiState> = mutableStateOf(RecipeUiState.Content(recipe)),
        onBack: () -> Unit = {},
        onRetry: () -> Unit = {},
        onCategory: (String) -> Unit = {},
        onCuisine: (String) -> Unit = {},
        onSource: (String) -> Unit = {},
    ) {
        compose.setContent {
            AngelsKitchenTheme(useDynamicColors = false) {
                RecipeScreen(
                    state = state.value,
                    isToday = false,
                    onBack = onBack,
                    onRetry = onRetry,
                    onCategory = onCategory,
                    onCuisine = onCuisine,
                    onSource = onSource,
                    videoContent = { Text("Fake video $it") },
                )
            }
        }
    }
}
