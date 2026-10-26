package com.angelstudio.angelskitchen.di

import com.angelstudio.angelskitchen.data.repository.RetrofitCategoryRepository
import com.angelstudio.angelskitchen.data.repository.RetrofitRecipeListRepository
import com.angelstudio.angelskitchen.data.repository.RetrofitRecipeRepository
import com.angelstudio.angelskitchen.domain.repository.CategoryRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeListRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRecipeRepository(repository: RetrofitRecipeRepository): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindRecipeListRepository(repository: RetrofitRecipeListRepository): RecipeListRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(repository: RetrofitCategoryRepository): CategoryRepository
}
