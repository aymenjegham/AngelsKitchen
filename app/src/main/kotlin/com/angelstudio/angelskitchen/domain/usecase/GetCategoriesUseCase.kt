package com.angelstudio.angelskitchen.domain.usecase

import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke(): CategoryResult = repository.load()
}
