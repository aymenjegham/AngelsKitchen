package com.angelstudio.angelskitchen.ui.browse

import androidx.lifecycle.ViewModelStore
import com.angelstudio.angelskitchen.domain.model.Category
import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.repository.CategoryRepository
import com.angelstudio.angelskitchen.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BrowseCategoriesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private var nextKey = 0

    @Before
    fun setup() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun loadsOnceAndMapsDomainContent() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = model(repository)
        assertEquals(BrowseCategoriesState.Loading, model.state.value)
        runCurrent()
        repository.complete(0, content("Seafood"))
        runCurrent()
        assertEquals(
            BrowseCategoriesState.Content(
                listOf(
                    BrowseCategory(
                        "Seafood",
                        "https://example.invalid/category.png",
                        "Seafood description",
                    ),
                ),
            ),
            model.state.value,
        )
        model.retry()
        runCurrent()
        assertEquals(1, repository.calls.size)
    }

    @Test
    fun errorsCanRetryWithoutDuplicatingInflightRequests() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = model(repository)
        runCurrent()
        repository.complete(0, CategoryResult.Error(RecipeError.NETWORK))
        runCurrent()
        assertEquals(BrowseCategoriesState.Error, model.state.value)
        model.retry()
        model.retry()
        runCurrent()
        assertEquals(2, repository.calls.size)
        repository.complete(1, content("Vegan"))
        runCurrent()
        assertTrue(model.state.value is BrowseCategoriesState.Content)
    }

    @Test
    fun emptyCatalogErrorsRemainRecoverable() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = model(repository)
        runCurrent()
        repository.complete(0, CategoryResult.Error(RecipeError.EMPTY))
        runCurrent()
        assertEquals(BrowseCategoriesState.Error, model.state.value)
        model.retry()
        runCurrent()
        assertEquals(2, repository.calls.size)
    }

    @Test
    fun clearingCancelsOwnedRequestAndSeparateModelsRemainIsolated() = runTest(dispatcher) {
        val repository = FakeRepository()
        val first = model(repository)
        val second = model(repository)
        runCurrent()
        repository.complete(0, content("Beef"))
        runCurrent()
        assertTrue(first.state.value is BrowseCategoriesState.Content)
        assertEquals(BrowseCategoriesState.Loading, second.state.value)
        store.clear()
        runCurrent()
        assertEquals(listOf(1), repository.cancelled)
        repository.complete(1, content("Vegan"))
        runCurrent()
        assertEquals(BrowseCategoriesState.Loading, second.state.value)
    }

    @Test
    fun clearingIgnoresUncooperativeLateResults() = runTest(dispatcher) {
        val pending = CompletableDeferred<CategoryResult>()
        val model = model(
            CategoryRepository {
                withContext(NonCancellable) { pending.await() }
            },
        )
        runCurrent()
        store.clear()
        pending.complete(content("Beef"))
        runCurrent()
        assertEquals(BrowseCategoriesState.Loading, model.state.value)
    }

    private fun model(repository: CategoryRepository) =
        BrowseCategoriesViewModel(GetCategoriesUseCase(repository)).also {
            store.put(
                "${nextKey++}",
                it,
            )
        }

    private fun content(name: String) = CategoryResult.Content(
        listOf(Category(name, "https://example.invalid/category.png", "$name description")),
    )

    private class FakeRepository : CategoryRepository {
        val calls = mutableListOf<CompletableDeferred<CategoryResult>>()
        val cancelled = mutableListOf<Int>()
        override suspend fun load(): CategoryResult {
            val index = calls.size
            val result = CompletableDeferred<CategoryResult>().also { calls += it }
            return try {
                result.await()
            } catch (cancel: kotlinx.coroutines.CancellationException) {
                cancelled += index
                throw cancel
            }
        }

        fun complete(index: Int, result: CategoryResult) {
            calls[index].complete(result)
        }
    }
}
