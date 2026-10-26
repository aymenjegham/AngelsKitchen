package com.angelstudio.angelskitchen.ui.recipe

import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError

sealed interface RecipeUiState {
    data object Loading : RecipeUiState
    data class Content(val recipe: Recipe) : RecipeUiState
    data class Error(val kind: RecipeError) : RecipeUiState
}
