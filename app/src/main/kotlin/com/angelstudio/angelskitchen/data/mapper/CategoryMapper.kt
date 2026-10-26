package com.angelstudio.angelskitchen.data.mapper

import com.angelstudio.angelskitchen.data.remote.dto.CategoriesDto
import com.angelstudio.angelskitchen.domain.model.Category
import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.model.RecipeError

internal object CategoryMapper {
    fun map(body: CategoriesDto?): CategoryResult {
        if (body == null) return CategoryResult.Error(RecipeError.MALFORMED)
        val categories = body.categories.orEmpty().mapNotNull { category ->
            val name = category?.strCategory?.trim().orEmpty()
            if (name.isEmpty()) null else Category(
                apiName = name,
                thumbnailUrl = category?.strCategoryThumb.orEmpty(),
                description = category?.strCategoryDescription.orEmpty(),
            )
        }.distinctBy { it.apiName }
        return if (categories.isEmpty()) CategoryResult.Error(RecipeError.EMPTY)
        else CategoryResult.Content(categories)
    }
}
