package com.angelstudio.angelskitchen.data.mapper

import com.angelstudio.angelskitchen.data.remote.dto.MealDto
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.domain.model.isValidRecipeId
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import java.net.URI
import java.net.URISyntaxException
import java.util.Collections

internal object RecipeMapper {
    private val videoIdPattern = Regex("[A-Za-z0-9_-]{11}")
    private val paragraphs = Regex("\\n[\\t ]*\\n+")

    fun map(meal: MealDto): RecipeResult {
        val id = meal.idMeal.normalized() ?: return RecipeResult.Failure(RecipeError.MALFORMED)
        val title = meal.strMeal.normalized() ?: return RecipeResult.Failure(RecipeError.MALFORMED)
        if (!isValidRecipeId(id)) return RecipeResult.Failure(RecipeError.MALFORMED)

        val ingredients = ingredientSlots(meal).mapNotNull { (ingredient, measure) ->
            val name = ingredient.normalized() ?: return@mapNotNull null
            RecipeIngredient(name, measure.normalized().orEmpty())
        }
        val instructions = meal.strInstructions.normalized()
            ?.replace("\r\n", "\n")?.replace('\r', '\n')
            ?.split(paragraphs)?.map(String::trim)?.filter(String::isNotEmpty).orEmpty()
        return RecipeResult.Success(
            Recipe(
                id = id,
                title = title,
                imageUrl = httpUrl(meal.strMealThumb),
                category = meal.strCategory.normalized(),
                cuisine = meal.strArea.normalized(),
                ingredients = Collections.unmodifiableList(ingredients),
                instructions = Collections.unmodifiableList(instructions),
                videoId = youtubeId(meal.strYoutube),
                sourceUrl = httpUrl(meal.strSource),
            ),
        )
    }

    private fun ingredientSlots(meal: MealDto): List<Pair<String?, String?>> = with(meal) {
        listOf(
            strIngredient1 to strMeasure1,
            strIngredient2 to strMeasure2,
            strIngredient3 to strMeasure3,
            strIngredient4 to strMeasure4,
            strIngredient5 to strMeasure5,
            strIngredient6 to strMeasure6,
            strIngredient7 to strMeasure7,
            strIngredient8 to strMeasure8,
            strIngredient9 to strMeasure9,
            strIngredient10 to strMeasure10,
            strIngredient11 to strMeasure11,
            strIngredient12 to strMeasure12,
            strIngredient13 to strMeasure13,
            strIngredient14 to strMeasure14,
            strIngredient15 to strMeasure15,
            strIngredient16 to strMeasure16,
            strIngredient17 to strMeasure17,
            strIngredient18 to strMeasure18,
            strIngredient19 to strMeasure19,
            strIngredient20 to strMeasure20,
        )
    }

    fun httpUrl(value: String?): String? {
        val normalized = value.normalized() ?: return null
        val uri = parseUri(normalized) ?: return null
        return normalized.takeIf {
            (uri.scheme.equals("https", true) || uri.scheme.equals("http", true)) &&
                    !uri.host.isNullOrBlank() && uri.rawUserInfo == null &&
                    (uri.port == -1 || uri.port in 1..65535)
        }
    }

    fun youtubeId(value: String?): String? {
        val url = httpUrl(value) ?: return null
        val uri = parseUri(url) ?: return null
        val path = uri.path.orEmpty().trim('/')
        val id = when (uri.host.lowercase()) {
            "youtu.be", "www.youtu.be" -> path.takeIf { '/' !in it }
            "youtube.com", "www.youtube.com", "m.youtube.com", "music.youtube.com" ->
                when {
                    path == "watch" -> uri.rawQuery.orEmpty().split('&')
                        .firstOrNull { it.startsWith("v=") }?.removePrefix("v=")

                    path.startsWith("embed/") || path.startsWith("shorts/") ||
                            path.startsWith("live/") ->
                        path.substringAfter('/').takeIf { '/' !in it }

                    else -> null
                }

            else -> null
        }
        return id?.takeIf(videoIdPattern::matches)
    }

    private fun parseUri(value: String): URI? = try {
        URI(value)
    } catch (_: URISyntaxException) {
        null
    }

    private fun String?.normalized(): String? = this?.trim()?.takeIf {
        it.isNotEmpty() && !it.equals("null", ignoreCase = true)
    }
}
