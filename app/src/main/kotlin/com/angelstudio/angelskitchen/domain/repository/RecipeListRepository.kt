package com.angelstudio.angelskitchen.domain.repository

import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult

fun interface RecipeListRepository {
    suspend fun load(query: RecipeListQuery): RecipeListResult
}
