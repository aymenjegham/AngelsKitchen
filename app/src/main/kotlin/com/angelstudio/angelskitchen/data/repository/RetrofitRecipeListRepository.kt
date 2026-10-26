package com.angelstudio.angelskitchen.data.repository

import android.util.Log
import com.angelstudio.angelskitchen.data.mapper.RecipeListMapper
import com.angelstudio.angelskitchen.data.remote.RecipeApi
import com.angelstudio.angelskitchen.data.remote.awaitResponse
import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.angelstudio.angelskitchen.domain.repository.RecipeListRepository
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.util.logging.Logger
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class RetrofitRecipeListRepository @Inject constructor(
    private val api: RecipeApi,
) : RecipeListRepository {
    override suspend fun load(query: RecipeListQuery): RecipeListResult {
        val value = query.value.trim()
        if (value.isEmpty()) return failure(RecipeListError.INVALID_ARGUMENT)
        return try {
            val call = when (query.kind) {
                RecipeListKind.CATEGORY -> api.getRecipesbyCategory(value)
                RecipeListKind.COUNTRY -> api.getRecipes(value)
            }
            val response = call.awaitResponse()
            if (!response.isSuccessful) {
                response.errorBody()?.close()
                failure(RecipeListError.SERVER)
            } else {
                val result = RecipeListMapper.map(response.body())
                if (result is RecipeListResult.Error) failure(result.kind) else result
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: JsonParseException) {
            failure(RecipeListError.MALFORMED)
        } catch (_: MalformedJsonException) {
            failure(RecipeListError.MALFORMED)
        } catch (_: EOFException) {
            failure(RecipeListError.MALFORMED)
        } catch (_: IOException) {
            failure(RecipeListError.NETWORK)
        }
    }

    private fun failure(kind: RecipeListError): RecipeListResult.Error {
        logger.warning("Recipe list request failed: $kind")
        return RecipeListResult.Error(kind)
    }

    private companion object {
        val logger: Logger = Logger.getLogger(RetrofitRecipeListRepository::class.java.name)
    }
}
