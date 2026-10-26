package com.angelstudio.angelskitchen.domain.repository

import com.angelstudio.angelskitchen.domain.model.CategoryResult

fun interface CategoryRepository {
    suspend fun load(): CategoryResult
}
