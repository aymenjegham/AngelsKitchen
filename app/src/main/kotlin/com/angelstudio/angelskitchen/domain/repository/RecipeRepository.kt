package com.angelstudio.angelskitchen.domain.repository

import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError

fun interface RecipeRepository {
    suspend fun load(recipeId: String?): RecipeResult
}

sealed interface RecipeResult {
    data class Success(val recipe: Recipe) : RecipeResult
    data class Failure(val kind: RecipeError) : RecipeResult
}
