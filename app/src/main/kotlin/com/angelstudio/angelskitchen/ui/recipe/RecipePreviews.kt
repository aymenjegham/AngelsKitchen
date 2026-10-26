package com.angelstudio.angelskitchen.ui.recipe

import android.content.res.Configuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme

private val PreviewRecipe = Recipe(
    id = "preview",
    title = "Roasted vegetables with herbs",
    imageUrl = null,
    category = "Vegetarian",
    cuisine = "Mediterranean",
    ingredients = listOf(
        RecipeIngredient("Carrots", "2"),
        RecipeIngredient("Potatoes", "3"),
        RecipeIngredient("Olive oil", "2 tbsp"),
        RecipeIngredient("Fresh herbs", "To taste"),
    ),
    instructions = listOf(
        "Wash and chop the vegetables. Place them in a roasting dish.",
        "Add the oil and herbs, then toss to coat.\n\nRoast until the vegetables are tender.",
    ),
    videoId = "dQw4w9WgXcQ",
    sourceUrl = "https://example.com/recipe",
)

@Preview(name = "Compact", widthDp = 360, heightDp = 800)
@Preview(name = "Tablet", widthDp = 1000, heightDp = 1000)
@Preview(name = "Warm dark", widthDp = 360, heightDp = 800, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large text", widthDp = 360, heightDp = 1000, fontScale = 2f)
@Preview(name = "RTL", widthDp = 360, heightDp = 800, locale = "ar")
@Composable
private fun RecipeContentPreview() {
    RecipePreview(RecipeUiState.Content(PreviewRecipe))
}

@Preview(name = "Today with Angie", widthDp = 393, heightDp = 1000)
@Preview(name = "Today large text", widthDp = 360, heightDp = 1200, fontScale = 2f)
@Composable
private fun TodayRecipePreview() {
    AngelsKitchenTheme {
        RecipeScreen(
            RecipeUiState.Content(PreviewRecipe), true, {}, {}, {}, {}, {}, {},
        )
    }
}

@Preview(name = "Long content", widthDp = 360, heightDp = 1000, fontScale = 2f)
@Composable
private fun LongRecipePreview() {
    RecipePreview(
        RecipeUiState.Content(
            PreviewRecipe.copy(
                title = "A very long recipe title with roasted seasonal vegetables and fresh garden herbs",
                category = "A category with a particularly long name",
                ingredients = PreviewRecipe.ingredients + RecipeIngredient(
                    "A long ingredient name that should wrap without losing its measure",
                    "One generously filled tablespoon",
                ),
            ),
        ),
    )
}

@Preview(name = "Loading", widthDp = 360, heightDp = 640)
@Composable
private fun LoadingRecipePreview() = RecipePreview(RecipeUiState.Loading)

@Preview(name = "Network error", widthDp = 360, heightDp = 640)
@Composable
private fun ErrorRecipePreview() = RecipePreview(RecipeUiState.Error(RecipeError.NETWORK))

@Preview(name = "Empty", widthDp = 360, heightDp = 640)
@Composable
private fun EmptyRecipePreview() = RecipePreview(RecipeUiState.Error(RecipeError.EMPTY))

@Preview(name = "Missing content", widthDp = 360, heightDp = 800)
@Composable
private fun MissingRecipePreview() = RecipePreview(
    RecipeUiState.Content(PreviewRecipe.copy(ingredients = emptyList(), instructions = emptyList())),
)

@Composable
private fun RecipePreview(state: RecipeUiState) {
    AngelsKitchenTheme(useDynamicColors = false) {
        RecipeScreen(
            state = state,
            isToday = false,
            onBack = {},
            onRetry = {},
            onCategory = {},
            onCuisine = {},
            onSource = {},
            videoContent = { Text(it) },
        )
    }
}
