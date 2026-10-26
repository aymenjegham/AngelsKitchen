package com.angelstudio.angelskitchen.data.mapper

import com.angelstudio.angelskitchen.data.remote.dto.MealDto
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeMapperTest {
    private val gson = Gson()

    @Test
    fun `normalization is pure and leaves immutable transport fields unchanged`() {
        val meal = MealDto(
            idMeal = "42",
            strMeal = " Soup ",
            strIngredient1 = " Tomato ",
            strMeasure1 = " 2 ",
            strIngredient2 = " ",
            strMeasure2 = "ignored",
            strIngredient3 = "Salt",
            strIngredient20 = "Basil",
            strMeasure20 = "one leaf",
        )
        val snapshot = meal.copy()
        val first = success(meal)
        val second = success(meal)
        assertEquals(snapshot, meal)
        assertEquals(first, second)
        assertEquals(
            listOf(
                RecipeIngredient("Tomato", "2"),
                RecipeIngredient("Salt", ""),
                RecipeIngredient("Basil", "one leaf"),
            ),
            first.ingredients,
        )
    }

    @Test
    fun `optional null and blank fields are omitted without invented content`() {
        val recipe = success(meal())
        assertEquals("42", recipe.id)
        assertEquals("Soup", recipe.title)
        assertNull(recipe.imageUrl)
        assertNull(recipe.category)
        assertNull(recipe.cuisine)
        assertNull(recipe.videoId)
        assertNull(recipe.sourceUrl)
        assertTrue(recipe.ingredients.isEmpty())
        assertTrue(recipe.instructions.isEmpty())
    }

    @Test
    fun `all twenty ingredient slots retain their matching measurements and order`() {
        val fields = (1..20).joinToString(",") {
            """"strIngredient$it":" Ingredient $it ","strMeasure$it":" Measure $it """"
        }
        assertEquals(
            (1..20).map { RecipeIngredient("Ingredient $it", "Measure $it") },
            success(meal(fields)).ingredients,
        )
    }

    @Test
    fun `instructions preserve numbering and single line breaks and split only paragraphs`() {
        val recipe = success(
            meal(
                """"strInstructions":" 1. Chop.\r\nKeep the skin.\r\n \r\n2. Simmer.\n\nServe. ","strCategory":" Soup ","strArea":" French " """,
            ),
        )
        assertEquals(
            listOf("1. Chop.\nKeep the skin.", "2. Simmer.", "Serve."),
            recipe.instructions,
        )
        assertEquals("Soup", recipe.category)
        assertEquals("French", recipe.cuisine)
    }

    @Test
    fun `missing core identity and title are malformed`() {
        listOf(
            """{"strMeal":"Soup"}""",
            """{"idMeal":"42","strMeal":" "}""",
            """{"idMeal":"bad","strMeal":"Soup"}""",
            """{"idMeal":"42","strMeal":null}""",
        ).forEach { json ->
            assertEquals(
                RecipeResult.Failure(RecipeError.MALFORMED),
                RecipeMapper.map(gson.fromJson(json, MealDto::class.java)),
            )
        }
    }

    @Test
    fun `http links reject invalid schemes hosts credentials whitespace and ports`() {
        listOf(
            null, "", "null", "javascript:alert(1)", "file:///recipe", "ftp://example.com",
            "https:///recipe", "https://example.com/a b", "//example.com/recipe",
            "https://user:password@example.com", "https://example.com:99999",
        ).forEach { assertNull("Should reject $it", RecipeMapper.httpUrl(it)) }
        assertEquals(
            "https://example.com/recipe?q=1",
            RecipeMapper.httpUrl(" https://example.com/recipe?q=1 "),
        )
        assertEquals("http://example.com", RecipeMapper.httpUrl("http://example.com"))
    }

    @Test
    fun `youtube urls accept official watch short embed shorts and live forms`() {
        listOf(
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ&feature=share",
            "https://m.youtube.com/watch?feature=share&v=dQw4w9WgXcQ",
            "https://youtu.be/dQw4w9WgXcQ?t=2",
            "https://youtube.com/embed/dQw4w9WgXcQ",
            "https://www.youtube.com/shorts/dQw4w9WgXcQ",
            "https://youtube.com/live/dQw4w9WgXcQ",
        ).forEach { assertEquals(it, "dQw4w9WgXcQ", RecipeMapper.youtubeId(it)) }
    }

    @Test
    fun `youtube urls reject lookalike hosts invalid IDs and ambiguous paths`() {
        listOf(
            null, "", "dQw4w9WgXcQ", "https://evil.youtube.com/watch?v=dQw4w9WgXcQ",
            "https://youtube.com.evil.com/watch?v=dQw4w9WgXcQ",
            "https://youtube.com/watch?v=short",
            "https://www.youtube.com/watch?v=dQw4w9WgXc&feature=share",
            "https://youtube.com/watch?v=dQw4w9WgXcQ!",
            "https://youtu.be/dQw4w9WgXcQ/another",
            "https://youtube.com/embed/dQw4w9WgXcQ/another",
            "https://youtube.com/watch?video=dQw4w9WgXcQ",
            "javascript:youtube.com/watch?v=dQw4w9WgXcQ",
        ).forEach { assertNull("Should reject $it", RecipeMapper.youtubeId(it)) }
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `mapped ingredient lists cannot mutate the snapshot`() {
        val recipe = success(meal(""""strIngredient1":"Tomato" """))
        (recipe.ingredients as MutableList).clear()
    }

    private fun meal(fields: String = ""): MealDto = gson.fromJson(
        """{"idMeal":"42","strMeal":" Soup "${if (fields.isEmpty()) "" else ",$fields"}}""",
        MealDto::class.java,
    )

    private fun success(meal: MealDto): Recipe =
        (RecipeMapper.map(meal) as RecipeResult.Success).recipe
}
