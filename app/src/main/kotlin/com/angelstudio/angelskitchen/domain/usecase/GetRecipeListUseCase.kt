package com.angelstudio.angelskitchen.domain.usecase

import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.angelstudio.angelskitchen.domain.repository.RecipeListRepository
import javax.inject.Inject

class GetRecipeListUseCase @Inject constructor(private val repository: RecipeListRepository) {
    suspend operator fun invoke(query: RecipeListQuery): RecipeListResult {
        val normalized = query.copy(value = query.value.trim())
        if (normalized.value.isEmpty()) return RecipeListResult.Error(RecipeListError.INVALID_ARGUMENT)
        return repository.load(normalized)
    }
}
