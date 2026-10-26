package com.angelstudio.angelskitchen.domain.model

enum class RecipeListKind { CATEGORY, COUNTRY }

data class RecipeListQuery(val kind: RecipeListKind, val value: String)

data class RecipeListItem(val id: String, val title: String, val imageUrl: String?)

enum class RecipeListError { NETWORK, SERVER, MALFORMED, INVALID_ARGUMENT }

sealed interface RecipeListResult {
    data class Content(val recipes: List<RecipeListItem>) : RecipeListResult
    data class Error(val kind: RecipeListError) : RecipeListResult
}
