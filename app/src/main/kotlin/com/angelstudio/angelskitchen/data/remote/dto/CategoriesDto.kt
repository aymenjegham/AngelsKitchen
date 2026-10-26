package com.angelstudio.angelskitchen.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CategoriesDto(
    @SerializedName("categories") val categories: List<CategoryDto?>? = null,
)
