package com.angelstudio.angelskitchen.ui.results

import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListItem
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.angelstudio.angelskitchen.domain.repository.RecipeListRepository
import com.angelstudio.angelskitchen.domain.usecase.GetRecipeListUseCase
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
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
class ResultsViewModelTest {
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
    fun `same normalized identity loads once and successful empty content is retained`() =
        runTest(dispatcher) {
            val repository = FakeRepository()
            val model = model(repository)
            model.load(category)
            model.load(category.copy(value = " Beef "))
            model.retry()
            runCurrent()
            assertEquals(listOf(category), repository.queries)
            repository.complete(0, RecipeListResult.Content(emptyList()))
            runCurrent()
            model.load(category)
            model.retry()
            runCurrent()
            assertEquals(1, repository.queries.size)
            assertEquals(RecipeListState.Content(emptyList()), model.uiState.value)
        }

    @Test
    fun `retry runs once only after error and never duplicates active request`() =
        runTest(dispatcher) {
            val repository = FakeRepository()
            val model = model(repository)
            model.retry()
            model.load(category)
            runCurrent()
            repository.complete(0, RecipeListResult.Error(RecipeListError.NETWORK))
            runCurrent()
            model.load(category)
            assertEquals(1, repository.queries.size)
            model.retry()
            model.retry()
            runCurrent()
            assertEquals(2, repository.queries.size)
            repository.complete(1, content("2"))
            runCurrent()
            assertEquals(content("2"), model.uiState.value)
        }

    @Test
    fun `changing either kind or value cancels old requests and ignores uncooperative late result`() =
        runTest(dispatcher) {
            val repository = FakeRepository(ignoreCancellation = true)
            val model = model(repository)
            model.load(category)
            runCurrent()
            val country = RecipeListQuery(RecipeListKind.COUNTRY, "Beef")
            model.load(country)
            runCurrent()
            model.load(country.copy(value = "italian"))
            runCurrent()
            repository.complete(2, content("3"))
            runCurrent()
            repository.complete(1, content("2"))
            repository.complete(0, content("1"))
            runCurrent()
            assertEquals(content("3"), model.uiState.value)
            assertEquals(
                listOf(category, country, country.copy(value = "italian")),
                repository.queries,
            )
        }

    @Test
    fun `models and clearing are isolated and clear cancels owned work`() = runTest(dispatcher) {
        val repository = FakeRepository()
        val first = model(repository)
        val second = model(repository)
        first.load(category)
        second.load(RecipeListQuery(RecipeListKind.COUNTRY, "italian"))
        runCurrent()
        repository.complete(0, content("1"))
        runCurrent()
        assertEquals(content("1"), first.uiState.value)
        assertEquals(RecipeListState.Loading, second.uiState.value)
        store.clear()
        runCurrent()
        assertEquals(listOf(1), repository.cancelled)
    }

    @Test
    fun `restoration reloads persisted identity once but explicit different host route wins`() =
        runTest(dispatcher) {
            val saved = SavedStateHandle()
            val originalRepository = FakeRepository()
            model(originalRepository, saved).load(category.copy(value = " Beef "))
            runCurrent()
            assertEquals("CATEGORY", saved.get<String>(ResultsViewModel.KEY_KIND))
            assertEquals("Beef", saved.get<String>(ResultsViewModel.KEY_VALUE))
            originalRepository.complete(0, content("1"))
            runCurrent()
            val restoredHandle = SavedStateHandle(
                mapOf(
                    ResultsViewModel.KEY_KIND to saved.get<String>(ResultsViewModel.KEY_KIND),
                    ResultsViewModel.KEY_VALUE to saved.get<String>(ResultsViewModel.KEY_VALUE),
                ),
            )
            val repository = FakeRepository()
            val restored = model(repository, restoredHandle)
            restored.load(category)
            runCurrent()
            assertEquals(listOf(category), repository.queries)
            val newRoute = RecipeListQuery(RecipeListKind.COUNTRY, "italian")
            restored.load(newRoute)
            runCurrent()
            repository.complete(1, content("2"))
            runCurrent()
            assertEquals(content("2"), restored.uiState.value)
            assertEquals("COUNTRY", restoredHandle.get<String>(ResultsViewModel.KEY_KIND))
            assertEquals(listOf(0), repository.cancelled)
        }

    @Test
    fun `blank values fail locally and malformed saved identity cannot override explicit route`() =
        runTest(dispatcher) {
            val repository = FakeRepository()
            val model = model(
                repository,
                SavedStateHandle(
                    mapOf(
                        ResultsViewModel.KEY_KIND to "not-a-kind",
                        ResultsViewModel.KEY_VALUE to "stale",
                    ),
                ),
            )
            runCurrent()
            assertTrue(repository.queries.isEmpty())
            model.load(category.copy(value = " "))
            assertEquals(
                RecipeListState.Error(RecipeListError.INVALID_ARGUMENT),
                model.uiState.value,
            )
            model.retry()
            runCurrent()
            assertTrue(repository.queries.isEmpty())
            model.load(category)
            runCurrent()
            assertEquals(listOf(category), repository.queries)
        }

    private val category = RecipeListQuery(RecipeListKind.CATEGORY, "Beef")
    @Test
    fun `legacy country aliases resolve before requesting domain data`() = runTest(dispatcher) {
        val repository = FakeRepository()
        val model = model(repository)
        model.load(RecipeListQuery(RecipeListKind.COUNTRY, "laotian"))
        runCurrent()
        assertEquals(listOf(RecipeListQuery(RecipeListKind.COUNTRY, "laos")), repository.queries)
        repository.complete(0, content("1"))
        runCurrent()
        assertEquals(content("1"), model.uiState.value)
    }

    private fun content(id: String) =
        RecipeListState.Content(listOf(RecipeListItem(id, "Soup", null)))

    private fun model(
        repository: RecipeListRepository,
        saved: SavedStateHandle = SavedStateHandle(),
    ) =
        ResultsViewModel(saved, GetRecipeListUseCase(repository)).also {
            store.put(
                "${nextKey++}",
                it,
            )
        }

    private class FakeRepository(private val ignoreCancellation: Boolean = false) :
        RecipeListRepository {
        val queries = mutableListOf<RecipeListQuery>()
        val cancelled = mutableListOf<Int>()
        private val results = mutableListOf<CompletableDeferred<RecipeListResult>>()
        override suspend fun load(query: RecipeListQuery): RecipeListResult {
            val index = queries.size
            queries += query
            val result = CompletableDeferred<RecipeListResult>().also { results += it }
            return try {
                if (ignoreCancellation) withContext(NonCancellable) { result.await() } else result.await()
            } catch (cancel: kotlinx.coroutines.CancellationException) {
                cancelled += index
                throw cancel
            }
        }

        fun complete(index: Int, result: RecipeListResult) {
            results[index].complete(result)
        }

        fun complete(index: Int, state: RecipeListState.Content) {
            complete(index, RecipeListResult.Content(state.recipes))
        }
    }
}
