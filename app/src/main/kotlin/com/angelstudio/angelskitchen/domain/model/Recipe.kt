package com.angelstudio.angelskitchen.domain.model

data class Recipe(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val category: String?,
    val cuisine: String?,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val videoId: String?,
    val sourceUrl: String?,
)

data class RecipeIngredient(val name: String, val measure: String)
