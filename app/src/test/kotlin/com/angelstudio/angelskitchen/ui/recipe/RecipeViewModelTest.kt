package com.angelstudio.angelskitchen.ui.recipe

import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.repository.RecipeRepository
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import com.angelstudio.angelskitchen.domain.usecase.GetRecipeUseCase

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeViewModelTest {
    @get:Rule
    val dispatcherRule = RecipeMainDispatcherRule()

    @Test
    fun `random loads once and content cannot be refreshed by retry or view recreation`() =
        runTest {
            val calls = mutableListOf<String?>()
            val handle = SavedStateHandle()
            val model = model(handle) { id ->
                calls += id
                RecipeResult.Success(recipe("42"))
            }
            model.load(null)
            model.load(null)
            runCurrent()
            model.load(null)
            model.retry()
            runCurrent()
            assertEquals(listOf<String?>(null), calls)
            assertEquals(RecipeUiState.Content(recipe("42")), model.uiState.value)
            assertEquals(RecipeViewModel.MODE_RANDOM, handle[RecipeViewModel.KEY_MODE])
            assertFalse(handle.contains(RecipeViewModel.KEY_RECIPE_ID))
        }

    @Test
    fun `invalid IDs never request random or detail and remain visible on retry`() = runTest {
        var calls = 0
        val model = model(SavedStateHandle()) {
            calls++
            RecipeResult.Success(recipe("42"))
        }
        listOf("", " ", " 42", "42 ", "-1", "4.2", "abc", "٤٢").forEach {
            model.load(it)
            model.retry()
            runCurrent()
            assertEquals(RecipeUiState.Error(RecipeError.INVALID_ARGUMENT), model.uiState.value)
        }
        assertEquals(0, calls)
    }

    @Test
    fun `error retry is explicit once and preserves the detail identity`() = runTest {
        val calls = mutableListOf<String?>()
        val pending = CompletableDeferred<RecipeResult>()
        val model = model(SavedStateHandle()) { id ->
            calls += id
            if (calls.size == 1) RecipeResult.Failure(RecipeError.NETWORK) else pending.await()
        }
        model.load("42")
        runCurrent()
        assertEquals(RecipeUiState.Error(RecipeError.NETWORK), model.uiState.value)
        model.load("42")
        runCurrent()
        assertEquals(1, calls.size)
        model.retry()
        model.retry()
        runCurrent()
        assertEquals(RecipeUiState.Loading, model.uiState.value)
        assertEquals(listOf("42", "42"), calls)
        pending.complete(RecipeResult.Success(recipe("42")))
        runCurrent()
        assertEquals(RecipeUiState.Content(recipe("42")), model.uiState.value)
    }

    @Test
    fun `saved detail and random modes are restored after process recreation`() = runTest {
        listOf("42", null).forEach { id ->
            val saved = SavedStateHandle()
            val first = model(saved) { RecipeResult.Success(recipe("42")) }
            first.load(id)
            runCurrent()
            val calls = mutableListOf<String?>()
            val recreated = model(
                SavedStateHandle(saved.keys().associateWith { saved.get<Any?>(it) }),
            ) { restored ->
                calls += restored
                RecipeResult.Success(recipe("42"))
            }
            recreated.load(id)
            runCurrent()
            assertEquals(listOf(id), calls)
            assertEquals(RecipeUiState.Content(recipe("42")), recreated.uiState.value)
        }
    }

    @Test
    fun `changing identity cancels old request and ignores an uncooperative late result`() =
        runTest {
            val old = CompletableDeferred<RecipeResult>()
            val current = CompletableDeferred<RecipeResult>()
            val calls = mutableListOf<String?>()
            val model = model(SavedStateHandle()) { id ->
                calls += id
                if (id == "1") withContext(NonCancellable) { old.await() } else current.await()
            }
            model.load("1")
            runCurrent()
            model.load("2")
            runCurrent()
            current.complete(RecipeResult.Success(recipe("2")))
            runCurrent()
            old.complete(RecipeResult.Success(recipe("1")))
            runCurrent()
            assertEquals(listOf("1", "2"), calls)
            assertEquals(RecipeUiState.Content(recipe("2")), model.uiState.value)
        }

    @Test
    fun `separate view models never share results and clearing cancels only its request`() =
        runTest {
            var firstCancelled = false
            val firstPending = CompletableDeferred<RecipeResult>()
            val secondPending = CompletableDeferred<RecipeResult>()
            val first = model(SavedStateHandle()) {
                try {
                    firstPending.await()
                } finally {
                    firstCancelled = true
                }
            }
            val second = model(SavedStateHandle()) { secondPending.await() }
            val store = ViewModelStore().apply { put("first", first) }
            first.load(null)
            second.load("2")
            runCurrent()
            store.clear()
            runCurrent()
            assertTrue(firstCancelled)
            secondPending.complete(RecipeResult.Success(recipe("2")))
            runCurrent()
            assertEquals(RecipeUiState.Content(recipe("2")), second.uiState.value)
            assertEquals(RecipeUiState.Loading, first.uiState.value)
        }

    private fun model(handle: SavedStateHandle, repository: RecipeRepository) =
        RecipeViewModel(handle, GetRecipeUseCase(repository))

    private fun recipe(id: String) = Recipe(
        id, "Soup", null, null, null, emptyList(), emptyList(), null, null,
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeMainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}
