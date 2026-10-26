package com.angelstudio.angelskitchen.domain.model

sealed interface CategoryResult {
    data class Content(val categories: List<Category>) : CategoryResult
    data class Error(val kind: RecipeError) : CategoryResult
}
