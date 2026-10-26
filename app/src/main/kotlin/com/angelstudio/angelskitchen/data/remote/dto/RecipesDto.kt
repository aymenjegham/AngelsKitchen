package com.angelstudio.angelskitchen.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RecipesDto(
    @SerializedName("meals") val recipes: List<RecipeDto?>? = null,
)
