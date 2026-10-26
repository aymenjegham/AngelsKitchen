package com.angelstudio.angelskitchen.domain.usecase

import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.isValidRecipeId
import com.angelstudio.angelskitchen.domain.repository.RecipeRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import javax.inject.Inject

class GetRecipeUseCase @Inject constructor(private val repository: RecipeRepository) {
    suspend operator fun invoke(recipeId: String?): RecipeResult {
        if (recipeId != null && !isValidRecipeId(recipeId)) {
            return RecipeResult.Failure(RecipeError.INVALID_ARGUMENT)
        }
        return repository.load(recipeId)
    }
}
