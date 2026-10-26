package com.angelstudio.angelskitchen.ui.results

import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListItem

sealed interface RecipeListState {
    data object Loading : RecipeListState
    data class Content(val recipes: List<RecipeListItem>) : RecipeListState
    data class Error(val kind: RecipeListError) : RecipeListState
}
