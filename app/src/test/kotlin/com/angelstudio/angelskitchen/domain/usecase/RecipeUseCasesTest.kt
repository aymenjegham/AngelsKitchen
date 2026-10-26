package com.angelstudio.angelskitchen.domain.usecase

import com.angelstudio.angelskitchen.domain.model.Category
import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.angelstudio.angelskitchen.domain.repository.CategoryRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeListRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class RecipeUseCasesTest {
    @Test
    fun invalidDetailIdsNeverReachRepository() = runTest {
        val useCase =
            GetRecipeUseCase(RecipeRepository { error("Invalid IDs must not reach data") })
        listOf("", " ", " 42", "42 ", "-1", "abc", "٤٢").forEach {
            assertEquals(RecipeResult.Failure(RecipeError.INVALID_ARGUMENT), useCase(it))
        }
    }

    @Test
    fun detailAndRandomIdentitiesAndResultsArePreserved() = runTest {
        val ids = mutableListOf<String?>()
        val expected = RecipeResult.Success(
            Recipe("42", "Soup", null, null, null, emptyList(), emptyList(), null, null),
        )
        val useCase = GetRecipeUseCase(
            RecipeRepository {
                ids += it
                expected
            },
        )
        assertSame(expected, useCase(null))
        assertSame(expected, useCase("42"))
        assertEquals(listOf(null, "42"), ids)
    }

    @Test
    fun listSelectionIsTrimmedAndBlankSelectionFailsBeforeData() = runTest {
        val queries = mutableListOf<RecipeListQuery>()
        val expected = RecipeListResult.Content(emptyList())
        val useCase = GetRecipeListUseCase(
            RecipeListRepository {
                queries += it
                expected
            },
        )
        assertEquals(
            RecipeListResult.Error(RecipeListError.INVALID_ARGUMENT),
            useCase(RecipeListQuery(RecipeListKind.CATEGORY, " ")),
        )
        assertSame(expected, useCase(RecipeListQuery(RecipeListKind.COUNTRY, " italian ")))
        assertEquals(listOf(RecipeListQuery(RecipeListKind.COUNTRY, "italian")), queries)
    }

    @Test
    fun repositoryFailuresAreNotReplacedWithEmptySuccesses() = runTest {
        val detailFailure = RecipeResult.Failure(RecipeError.NETWORK)
        val listFailure = RecipeListResult.Error(RecipeListError.MALFORMED)
        val categoriesFailure = CategoryResult.Error(RecipeError.SERVER)
        assertSame(detailFailure, GetRecipeUseCase(RecipeRepository { detailFailure })("42"))
        assertSame(
            listFailure,
            GetRecipeListUseCase(RecipeListRepository { listFailure })(
                RecipeListQuery(
                    RecipeListKind.CATEGORY,
                    "Beef",
                ),
            ),
        )
        assertSame(
            categoriesFailure,
            GetCategoriesUseCase(CategoryRepository { categoriesFailure })(),
        )
    }

    @Test
    fun categoriesAreProvidedAsDomainModels() = runTest {
        val expected = CategoryResult.Content(listOf(Category("Beef", "", "Beef recipes")))
        assertSame(expected, GetCategoriesUseCase(CategoryRepository { expected })())
    }

    @Test
    fun cancellationPropagatesThroughEveryUseCase() = runTest {
        val cancelled = CancellationException("Screen removed")
        val actions: List<suspend () -> Any> = listOf(
            { GetRecipeUseCase(RecipeRepository { throw cancelled })("42") },
            {
                GetRecipeListUseCase(RecipeListRepository { throw cancelled })(
                    RecipeListQuery(RecipeListKind.CATEGORY, "Beef"),
                )
            },
            { GetCategoriesUseCase(CategoryRepository { throw cancelled })() },
        )
        actions.forEach { action ->
            try {
                action()
                fail("Cancellation must propagate")
            } catch (actual: CancellationException) {
                assertSame(cancelled, actual)
            }
        }
    }
}
