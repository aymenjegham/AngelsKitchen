package com.angelstudio.angelskitchen.data.repository

import com.angelstudio.angelskitchen.data.mapper.RecipeMapper
import com.angelstudio.angelskitchen.data.remote.RecipeApi
import com.angelstudio.angelskitchen.data.remote.awaitResponse
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.isValidRecipeId
import com.angelstudio.angelskitchen.domain.repository.RecipeRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.util.logging.Logger
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class RetrofitRecipeRepository @Inject constructor(
    private val api: RecipeApi,
) : RecipeRepository {
    override suspend fun load(recipeId: String?): RecipeResult {
        if (recipeId != null && !isValidRecipeId(recipeId)) {
            return RecipeResult.Failure(RecipeError.INVALID_ARGUMENT)
        }
        return try {
            val call = if (recipeId == null) api.getRecipe() else api.getRecipeById(recipeId)
            val response = call.awaitResponse()
            if (!response.isSuccessful) {
                logger.warning("Recipe request failed: HTTP ${response.code()}")
                response.errorBody()?.close()
                RecipeResult.Failure(RecipeError.SERVER)
            } else {
                val body = response.body()
                val result = when {
                    body == null -> RecipeResult.Failure(RecipeError.MALFORMED)
                    body.meals.isNullOrEmpty() -> RecipeResult.Failure(RecipeError.EMPTY)
                    else -> {
                        val meal = body.meals.firstOrNull()
                        if (meal == null) RecipeResult.Failure(RecipeError.MALFORMED)
                        else RecipeMapper.map(meal)
                    }
                }
                if (result == RecipeResult.Failure(RecipeError.MALFORMED)) {
                    logger.warning("Recipe response failed validation: MALFORMED")
                }
                result
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (malformed: JsonParseException) {
            failure(RecipeError.MALFORMED, malformed)
        } catch (malformed: MalformedJsonException) {
            failure(RecipeError.MALFORMED, malformed)
        } catch (malformed: EOFException) {
            failure(RecipeError.MALFORMED, malformed)
        } catch (network: IOException) {
            failure(RecipeError.NETWORK, network)
        }
    }

    private fun failure(kind: RecipeError, cause: Exception): RecipeResult.Failure {
        logger.warning("Recipe request failed: $kind (${cause.javaClass.simpleName})")
        return RecipeResult.Failure(kind)
    }

    private companion object {
        val logger: Logger = Logger.getLogger(RetrofitRecipeRepository::class.java.name)
    }
}
