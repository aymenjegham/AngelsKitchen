package com.angelstudio.angelskitchen.domain.model

internal fun isValidRecipeId(id: String): Boolean = id.matches(Regex("[0-9]+"))
