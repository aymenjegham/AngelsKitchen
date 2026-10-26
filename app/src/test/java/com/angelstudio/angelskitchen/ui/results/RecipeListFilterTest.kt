package com.angelstudio.angelskitchen.ui.results

import com.angelstudio.angelskitchen.domain.model.RecipeListItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeListFilterTest {
    private val recipes = listOf(
        RecipeListItem("1", "Chickpea curry", null),
        RecipeListItem("2", "Lentil curry", null),
        RecipeListItem("3", "Roasted vegetables", null),
    )

    @Test fun emptySearchPreservesAllRecipes() {
        assertEquals(recipes, filterRecipeList(recipes, "  "))
    }

    @Test fun searchTrimsWhitespaceIgnoresCaseAndPreservesOrder() {
        assertEquals(recipes.take(2), filterRecipeList(recipes, " CURRY "))
    }

    @Test fun searchOnlyMatchesNamesAndCanReturnEmpty() {
        assertEquals(emptyList<RecipeListItem>(), filterRecipeList(recipes, "1"))
        assertEquals(emptyList<RecipeListItem>(), filterRecipeList(emptyList(), "curry"))
    }
}
