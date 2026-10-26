package com.angelstudio.angelskitchen.data.repository

import com.angelstudio.angelskitchen.data.remote.dto.CategoriesDto
import com.angelstudio.angelskitchen.data.remote.dto.MealDto
import com.angelstudio.angelskitchen.data.remote.dto.MealsDto
import com.angelstudio.angelskitchen.data.remote.dto.RecipesDto
import com.angelstudio.angelskitchen.data.remote.RecipeApi
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryTest {
    @Test
    fun `random and detail endpoints use independent calls`() = runTest {
        val ids = mutableListOf<String?>()
        val repository = RetrofitRecipeRepository(
            StubApi { id ->
                ids += id
                StubCall(Response.success(MealsDto(listOf(MealDto(idMeal = "42", strMeal = "Soup")))))
            },
        )
        assertTrue(repository.load(null) is RecipeResult.Success)
        assertTrue(repository.load("42") is RecipeResult.Success)
        assertEquals(listOf(null, "42"), ids)
    }

    @Test
    fun `invalid arguments fail before creating a call`() = runTest {
        var calls = 0
        val repository = RetrofitRecipeRepository(
            StubApi {
                calls++
                StubCall()
            },
        )
        listOf("", " ", " 42", "42 ", "abc", "-42", "4.2").forEach {
            assertEquals(RecipeResult.Failure(RecipeError.INVALID_ARGUMENT), repository.load(it))
        }
        assertEquals(0, calls)
    }

    @Test
    fun `null and empty meals are empty while absent body and broken records are malformed`() =
        runTest {
            listOf("""{"meals":null}""", """{"meals":[]}""", "{}").forEach { json ->
                val result = repository(
                    Response.success(
                        Gson().fromJson(
                            json,
                            MealsDto::class.java,
                        ),
                    ),
                ).load(null)
                assertEquals(RecipeResult.Failure(RecipeError.EMPTY), result)
            }
            listOf(
                Response.success<MealsDto>(null),
                Response.success(meals("null")),
                Response.success(meals("""{"idMeal":"42"}""")),
            ).forEach { response ->
                assertEquals(
                    RecipeResult.Failure(RecipeError.MALFORMED),
                    repository(response).load("42"),
                )
            }
        }

    @Test
    fun `non successful HTTP statuses are server errors`() = runTest {
        listOf(400, 404, 429, 500, 503).forEach { code ->
            val response = Response.error<MealsDto>(
                code,
                "{}".toResponseBody("application/json".toMediaType()),
            )
            assertEquals(RecipeResult.Failure(RecipeError.SERVER), repository(response).load(null))
        }
    }

    @Test
    fun `network and malformed JSON failures have distinct typed outcomes`() = runTest {
        listOf(
            IOException("offline") to RecipeError.NETWORK,
            JsonSyntaxException("bad document") to RecipeError.MALFORMED,
            MalformedJsonException("bad document") to RecipeError.MALFORMED,
            EOFException("truncated document") to RecipeError.MALFORMED,
        ).forEach { (failure, kind) ->
            val repository = RetrofitRecipeRepository(StubApi { StubCall(failure = failure) })
            assertEquals(RecipeResult.Failure(kind), repository.load("42"))
        }
    }

    @Test
    fun `cancellation cancels only the matching HTTP call and permits late callback`() = runTest {
        val first = StubCall()
        val second = StubCall()
        val repository = RetrofitRecipeRepository(StubApi { if (it == "1") first else second })
        val cancelled = async { repository.load("1") }
        val active = async { repository.load("2") }
        runCurrent()
        cancelled.cancelAndJoin()
        assertTrue(first.isCanceled)
        first.fail(IOException("late canceled callback"))
        second.respond(Response.success(meals("""{"idMeal":"2","strMeal":"Soup"}""")))
        assertTrue(cancelled.isCancelled)
        assertEquals("2", (active.await() as RecipeResult.Success).recipe.id)
    }

    @Test
    fun `explicit cancellation exceptions propagate rather than becoming network errors`() =
        runTest {
            val repository = RetrofitRecipeRepository(
                StubApi {
                    StubCall(failure = CancellationException("cancelled"))
                },
            )
            var cancelled = false
            try {
                repository.load("42")
            } catch (_: CancellationException) {
                cancelled = true
            }
            assertTrue(cancelled)
        }

    private fun meals(record: String): MealsDto =
        Gson().fromJson("""{"meals":[$record]}""", MealsDto::class.java)

    private fun repository(response: Response<MealsDto>) =
        RetrofitRecipeRepository(StubApi { StubCall(response) })
}

private class StubApi(private val createCall: (String?) -> Call<MealsDto>) : RecipeApi {
    override fun getRecipe(): Call<MealsDto> = createCall(null)
    override fun getRecipeById(id: String): Call<MealsDto> = createCall(id)
    override fun getRecipes(area: String): Call<RecipesDto> = error("Not a recipe detail request")
    override fun getCategories(): Call<CategoriesDto> = error("Not a recipe detail request")
    override fun getRecipesbyCategory(category: String): Call<RecipesDto> =
        error("Not a recipe detail request")
}

private class StubCall(
    private val response: Response<MealsDto>? = null,
    private val failure: Throwable? = null,
) : Call<MealsDto> {
    private var callback: Callback<MealsDto>? = null
    private var executed = false
    private var cancelled = false

    override fun enqueue(callback: Callback<MealsDto>) {
        check(!executed)
        executed = true
        this.callback = callback
        if (response != null) respond(response)
        else if (failure != null) fail(failure)
    }

    fun respond(response: Response<MealsDto>) = requireNotNull(callback).onResponse(this, response)
    fun fail(failure: Throwable) = requireNotNull(callback).onFailure(this, failure)
    override fun cancel() {
        cancelled = true
    }

    override fun isCanceled(): Boolean = cancelled
    override fun isExecuted(): Boolean = executed
    override fun request(): Request = Request.Builder().url("https://example.com").build()
    override fun timeout(): Timeout = Timeout.NONE
    override fun execute(): Response<MealsDto> = error("Tests must use enqueue")
    public override fun clone(): Call<MealsDto> = StubCall(response, failure)
}
