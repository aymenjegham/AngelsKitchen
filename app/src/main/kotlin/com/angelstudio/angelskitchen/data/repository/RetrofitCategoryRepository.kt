package com.angelstudio.angelskitchen.data.repository

import com.angelstudio.angelskitchen.data.mapper.CategoryMapper
import com.angelstudio.angelskitchen.data.remote.RecipeApi
import com.angelstudio.angelskitchen.data.remote.awaitResponse
import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.repository.CategoryRepository
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.IOException
import java.util.logging.Logger
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class RetrofitCategoryRepository @Inject constructor(
    private val api: RecipeApi,
) : CategoryRepository {
    override suspend fun load(): CategoryResult = try {
        val response = api.getCategories().awaitResponse()
        if (!response.isSuccessful) {
            response.errorBody()?.close()
            failure(RecipeError.SERVER)
        } else {
            val result = CategoryMapper.map(response.body())
            if (result is CategoryResult.Error) failure(result.kind) else result
        }
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (_: JsonParseException) {
        failure(RecipeError.MALFORMED)
    } catch (_: MalformedJsonException) {
        failure(RecipeError.MALFORMED)
    } catch (_: EOFException) {
        failure(RecipeError.MALFORMED)
    } catch (_: IOException) {
        failure(RecipeError.NETWORK)
    }

    private fun failure(kind: RecipeError): CategoryResult.Error {
        logger.warning("Category request failed: $kind")
        return CategoryResult.Error(kind)
    }

    private companion object {
        val logger: Logger = Logger.getLogger(RetrofitCategoryRepository::class.java.name)
    }
}
