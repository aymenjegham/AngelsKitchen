package com.angelstudio.angelskitchen.data.repository

import com.angelstudio.angelskitchen.data.remote.dto.CategoriesDto
import com.angelstudio.angelskitchen.data.remote.dto.CategoryDto
import com.angelstudio.angelskitchen.data.remote.dto.MealsDto
import com.angelstudio.angelskitchen.data.remote.dto.RecipeDto
import com.angelstudio.angelskitchen.data.remote.dto.RecipesDto
import com.angelstudio.angelskitchen.data.remote.RecipeApi
import com.angelstudio.angelskitchen.domain.model.Category
import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListItem
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.google.gson.Gson
import com.google.gson.JsonParseException
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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.BufferedSource
import okio.Buffer
import okio.Timeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class ResultsDataTest {
    @Test fun `both endpoints trim values and preserve validated unique API order`() = runTest {
        val queries = mutableListOf<RecipeListQuery>()
        val repository = RetrofitRecipeListRepository(ListApi {
            queries += it
            ListCall(Response.success(recipes(
                """{"idMeal":" 2 ","strMeal":" Soup ","strMealThumb":"https://example.com/2.jpg"},
                   {"idMeal":"1","strMeal":"Salad","strMealThumb":"file:///unsafe"},
                   {"idMeal":"2","strMeal":"Duplicate"}""",
            )))
        })
        val expected = RecipeListResult.Content(listOf(
            RecipeListItem("2", "Soup", "https://example.com/2.jpg"),
            RecipeListItem("1", "Salad", null),
        ))
        assertEquals(expected, repository.load(RecipeListQuery(RecipeListKind.CATEGORY, " Beef ")))
        assertEquals(expected, repository.load(RecipeListQuery(RecipeListKind.COUNTRY, " italian ")))
        assertEquals(listOf(category("Beef"), country("italian")), queries)
    }

    @Test fun `blank arguments create no HTTP request`() = runTest {
        val repository = RetrofitRecipeListRepository(ListApi { error("Must not request") })
        assertEquals(error(RecipeListError.INVALID_ARGUMENT), repository.load(category(" \n ")))
    }

    @Test fun `country values reach data unchanged after trimming without presentation aliases`() = runTest {
        val areas = mutableListOf<String>()
        val repository = RetrofitRecipeListRepository(ListApi {
            areas += it.value
            ListCall(Response.success(RecipesDto(listOf(RecipeDto(idMeal = "42", strMeal = "Soup")))))
        })
        assertEquals(
            RecipeListResult.Content(listOf(RecipeListItem("42", "Soup", null))),
            repository.load(country(" Italy ")),
        )
        assertEquals(listOf("Italy"), areas)
    }

    @Test fun `null and empty meal lists are successful empty content but absent body is malformed`() =
        runTest {
            listOf("""{"meals":null}""", """{"meals":[]}""", "{}").forEach { json ->
                val body = Gson().fromJson(json, RecipesDto::class.java)
                assertEquals(RecipeListResult.Content(emptyList()), repository(Response.success(body)).load(category()))
            }
            assertEquals(
                error(RecipeListError.MALFORMED),
                repository(Response.success(null)).load(category()),
            )
        }

    @Test fun `any invalid record including duplicate or null records invalidates entire response`() =
        runTest {
            listOf(
                "null", """{"idMeal":"abc","strMeal":"Soup"}""",
                """{"idMeal":"2","strMeal":" "}""", """{"strMeal":"Soup"}""",
                """{"idMeal":"1","strMeal":null}""",
            ).forEach { row ->
                val body = recipes("""{"idMeal":"1","strMeal":"Valid"},$row""")
                assertEquals(
                    error(RecipeListError.MALFORMED),
                    repository(Response.success(body)).load(category()),
                )
            }
        }

    @Test fun `HTTP errors close bodies and all expected failures remain typed`() = runTest {
        listOf(400, 404, 429, 500).forEach { code ->
            val body = TrackingBody()
            assertEquals(
                error(RecipeListError.SERVER),
                repository(Response.error(code, body)).load(category()),
            )
            assertTrue(body.closed)
        }
        listOf(
            IOException("private network details") to RecipeListError.NETWORK,
            JsonParseException("private payload") to RecipeListError.MALFORMED,
            MalformedJsonException("private payload") to RecipeListError.MALFORMED,
            EOFException("private payload") to RecipeListError.MALFORMED,
        ).forEach { (failure, kind) ->
            val repository = RetrofitRecipeListRepository(ListApi { ListCall(failure = failure) })
            assertEquals(error(kind), repository.load(category()))
        }
    }

    @Test fun `real Retrofit parses successful and malformed responses without network sockets`() =
        runTest {
            var json = """{"meals":[{"idMeal":"42","strMeal":"Soup"}]}"""
            val paths = mutableListOf<String>()
            val client = OkHttpClient.Builder().addInterceptor { chain ->
                paths += chain.request().url.encodedPath + "?" +
                    chain.request().url.encodedQuery.orEmpty()
                okhttp3.Response.Builder().request(chain.request())
                    .protocol(okhttp3.Protocol.HTTP_1_1).code(200).message("OK")
                    .body(json.toResponseBody("application/json".toMediaType())).build()
            }.build()
            val api = Retrofit.Builder().baseUrl("https://example.invalid/api/json/v1/1/")
                .client(client).addConverterFactory(GsonConverterFactory.create())
                .build().create(RecipeApi::class.java)
            val repository = RetrofitRecipeListRepository(api)
            assertEquals(
                RecipeListResult.Content(listOf(RecipeListItem("42", "Soup", null))),
                repository.load(country("italian")),
            )
            json = """{"meals":[{"idMeal":"""
            assertEquals(error(RecipeListError.MALFORMED), repository.load(category("Beef")))
            assertEquals(
                listOf("/api/json/v1/1/filter.php?a=italian", "/api/json/v1/1/filter.php?c=Beef"),
                paths,
            )
            client.dispatcher.executorService.shutdown()
            client.connectionPool.evictAll()
        }

    @Test fun `cancellation is propagated and closes late error bodies without cancelling other requests`() =
        runTest {
            val first = ListCall<RecipesDto>()
            val second = ListCall<RecipesDto>()
            val repository = RetrofitRecipeListRepository(ListApi {
                if (it.kind == RecipeListKind.CATEGORY) first else second
            })
            val cancelled = async { repository.load(category()) }
            val active = async { repository.load(country()) }
            runCurrent()
            cancelled.cancelAndJoin()
            assertTrue(first.isCanceled)
            val body = TrackingBody()
            first.respond(Response.error(500, body))
            assertTrue(body.closed)
            second.respond(Response.success(recipes("")))
            assertEquals(RecipeListResult.Content(emptyList()), active.await())
            assertTrue(cancelled.isCancelled)
        }

    @Test fun `explicit cancellation exceptions are not converted to errors`() = runTest {
        val repository = RetrofitRecipeListRepository(ListApi {
            ListCall(failure = CancellationException("Cancelled"))
        })
        var cancelled = false
        try {
            repository.load(category())
        } catch (_: CancellationException) {
            cancelled = true
        }
        assertTrue(cancelled)
    }

    @Test fun `categories trim names skip blanks deduplicate and default optional values`() = runTest {
        val body = CategoriesDto(listOf(
            CategoryDto(" Beef ", " https://example.com/beef.png ", " Beef description "),
            CategoryDto(" "),
            CategoryDto("Beef", "ignored duplicate", "ignored duplicate"),
            CategoryDto("Seafood"),
        ))
        assertEquals(
            CategoryResult.Content(listOf(
                Category("Beef", " https://example.com/beef.png ", " Beef description "),
                Category("Seafood", "", ""),
            )),
            categoryRepository(Response.success(body)).load(),
        )
        val parsed = Gson().fromJson(
            """{"categories":[{"strCategory":"Beef","strCategoryThumb":"image","strCategoryDescription":"description"}]}""",
            CategoriesDto::class.java,
        )
        assertEquals(
            CategoryResult.Content(listOf(Category("Beef", "image", "description"))),
            categoryRepository(Response.success(parsed)).load(),
        )
    }

    @Test fun `null empty and unusable categories are empty while absent body is malformed`() = runTest {
        listOf(
            CategoriesDto(),
            CategoriesDto(emptyList()),
            CategoriesDto(listOf(null, CategoryDto(), CategoryDto(" "))),
        ).forEach { body ->
            assertEquals(
                CategoryResult.Error(RecipeError.EMPTY),
                categoryRepository(Response.success(body)).load(),
            )
        }
        assertEquals(
            CategoryResult.Error(RecipeError.MALFORMED),
            categoryRepository(Response.success(null)).load(),
        )
    }

    @Test fun `category HTTP errors close bodies and expected failures have typed results`() = runTest {
        listOf(400, 404, 429, 500).forEach { code ->
            val body = TrackingBody()
            assertEquals(
                CategoryResult.Error(RecipeError.SERVER),
                categoryRepository(Response.error(code, body)).load(),
            )
            assertTrue(body.closed)
        }
        listOf(
            IOException("private network details") to RecipeError.NETWORK,
            JsonParseException("private payload") to RecipeError.MALFORMED,
            MalformedJsonException("private payload") to RecipeError.MALFORMED,
            EOFException("private payload") to RecipeError.MALFORMED,
        ).forEach { (failure, kind) ->
            val repository = RetrofitCategoryRepository(CategoryApi { ListCall(failure = failure) })
            assertEquals(CategoryResult.Error(kind), repository.load())
        }
    }

    @Test fun `category cancellation cancels its HTTP call and closes discarded error bodies`() = runTest {
        val first = ListCall<CategoriesDto>()
        val second = ListCall<CategoriesDto>()
        var requests = 0
        val repository = RetrofitCategoryRepository(CategoryApi {
            if (requests++ == 0) first else second
        })
        val cancelled = async { repository.load() }
        val active = async { repository.load() }
        runCurrent()
        cancelled.cancelAndJoin()
        assertTrue(first.isCanceled)
        val body = TrackingBody()
        first.respond(Response.error(500, body))
        assertTrue(body.closed)
        second.respond(Response.success(CategoriesDto(listOf(CategoryDto("Beef")))))
        assertEquals(
            CategoryResult.Content(listOf(Category("Beef", "", ""))),
            active.await(),
        )
        assertTrue(cancelled.isCancelled)
    }

    @Test fun `explicit category cancellation exceptions are not converted to errors`() = runTest {
        val repository = RetrofitCategoryRepository(CategoryApi {
            ListCall(failure = CancellationException("Cancelled"))
        })
        var cancelled = false
        try {
            repository.load()
        } catch (_: CancellationException) {
            cancelled = true
        }
        assertTrue(cancelled)
    }

    private fun category(value: String = "Beef") = RecipeListQuery(RecipeListKind.CATEGORY, value)
    private fun country(value: String = "italian") = RecipeListQuery(RecipeListKind.COUNTRY, value)
    private fun error(kind: RecipeListError) = RecipeListResult.Error(kind)
    private fun recipes(rows: String): RecipesDto =
        Gson().fromJson("""{"meals":[$rows]}""", RecipesDto::class.java)
    private fun repository(response: Response<RecipesDto>) =
        RetrofitRecipeListRepository(ListApi { ListCall(response) })
    private fun categoryRepository(response: Response<CategoriesDto>) =
        RetrofitCategoryRepository(CategoryApi { ListCall(response) })
}

private class ListApi(private val create: (RecipeListQuery) -> Call<RecipesDto>) : RecipeApi {
    override fun getRecipes(area: String) = create(RecipeListQuery(RecipeListKind.COUNTRY, area))
    override fun getRecipesbyCategory(category: String) =
        create(RecipeListQuery(RecipeListKind.CATEGORY, category))
    override fun getRecipe(): Call<MealsDto> = error("Not used")
    override fun getRecipeById(id: String): Call<MealsDto> = error("Not used")
    override fun getCategories(): Call<CategoriesDto> = error("Not used")
}

private class CategoryApi(private val create: () -> Call<CategoriesDto>) : RecipeApi {
    override fun getCategories(): Call<CategoriesDto> = create()
    override fun getRecipe(): Call<MealsDto> = error("Not used")
    override fun getRecipeById(id: String): Call<MealsDto> = error("Not used")
    override fun getRecipes(area: String): Call<RecipesDto> = error("Not used")
    override fun getRecipesbyCategory(category: String): Call<RecipesDto> = error("Not used")
}

private class ListCall<T>(
    private val response: Response<T>? = null,
    private val failure: Throwable? = null,
) : Call<T> {
    private lateinit var callback: Callback<T>
    private var cancelled = false
    private var executed = false
    override fun enqueue(callback: Callback<T>) {
        check(!executed)
        executed = true
        this.callback = callback
        if (response != null) respond(response)
        else if (failure != null) callback.onFailure(this, failure)
    }
    fun respond(response: Response<T>) = callback.onResponse(this, response)
    override fun cancel() { cancelled = true }
    override fun isCanceled() = cancelled
    override fun isExecuted() = executed
    override fun request(): Request = Request.Builder().url("https://example.invalid/").build()
    override fun timeout(): Timeout = Timeout.NONE
    override fun execute(): Response<T> = error("Use enqueue")
    override fun clone(): Call<T> = ListCall(response, failure)
}

private class TrackingBody : ResponseBody() {
    var closed = false
    private val buffer = Buffer().writeUtf8("{}")
    override fun contentType() = "application/json".toMediaType()
    override fun contentLength() = 2L
    override fun source(): BufferedSource = buffer
    override fun close() {
        closed = true
        super.close()
    }
}
