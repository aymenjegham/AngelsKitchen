package com.angelstudio.angelskitchen.data.mapper

import com.angelstudio.angelskitchen.data.remote.dto.RecipesDto
import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListItem
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.angelstudio.angelskitchen.domain.model.isValidRecipeId

internal object RecipeListMapper {
    fun map(body: RecipesDto?): RecipeListResult {
        if (body == null) return RecipeListResult.Error(RecipeListError.MALFORMED)
        val recipes = linkedMapOf<String, RecipeListItem>()
        for (record in body.recipes.orEmpty()) {
            val row = record ?: return RecipeListResult.Error(RecipeListError.MALFORMED)
            val id = row.idMeal?.trim().orEmpty()
            val title = row.strMeal?.trim().orEmpty()
            if (!isValidRecipeId(id) || title.isEmpty()) {
                return RecipeListResult.Error(RecipeListError.MALFORMED)
            }
            if (id !in recipes) {
                recipes[id] = RecipeListItem(id, title, RecipeMapper.httpUrl(row.strMealThumb))
            }
        }
        return RecipeListResult.Content(recipes.values.toList())
    }
}
