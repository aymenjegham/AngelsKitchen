package com.angelstudio.angelskitchen.data.remote

import com.angelstudio.angelskitchen.data.remote.dto.CategoriesDto
import com.angelstudio.angelskitchen.data.remote.dto.MealsDto
import com.angelstudio.angelskitchen.data.remote.dto.RecipesDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApi {
    @GET("random.php")
    fun getRecipe(): Call<MealsDto>

    @GET("lookup.php")
    fun getRecipeById(@Query("i") id: String): Call<MealsDto>

    @GET("filter.php")
    fun getRecipes(@Query("a") area: String): Call<RecipesDto>

    @GET("filter.php")
    fun getRecipesbyCategory(@Query("c") category: String): Call<RecipesDto>

    @GET("categories.php")
    fun getCategories(): Call<CategoriesDto>
}
