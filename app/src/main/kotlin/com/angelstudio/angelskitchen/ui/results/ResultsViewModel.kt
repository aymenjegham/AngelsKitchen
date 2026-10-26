package com.angelstudio.angelskitchen.ui.results

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.domain.model.RecipeListResult
import com.angelstudio.angelskitchen.domain.usecase.GetRecipeListUseCase
import com.angelstudio.angelskitchen.ui.browse.findBrowseCountry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecipeList: GetRecipeListUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<RecipeListState>(RecipeListState.Loading)
    val uiState: StateFlow<RecipeListState> = mutableUiState.asStateFlow()
    private var query: RecipeListQuery? = null
    private var request: Job? = null
    private var generation = 0L

    init {
        val kind = savedStateHandle.get<String>(KEY_KIND)
            ?.let { stored -> RecipeListKind.entries.firstOrNull { it.name == stored } }
        val value = savedStateHandle.get<String>(KEY_VALUE)
        if (kind != null && value != null) load(RecipeListQuery(kind, value))
    }

    /** A host's explicit route always wins over a different restored route. */
    fun load(query: RecipeListQuery) {
        val normalized = query.copy(value = query.value.trim())
        if (this.query == normalized) return
        this.query = normalized
        savedStateHandle[KEY_KIND] = normalized.kind.name
        savedStateHandle[KEY_VALUE] = normalized.value
        fetch()
    }

    fun retry() {
        if (query != null && mutableUiState.value is RecipeListState.Error) fetch()
    }

    private fun fetch() {
        val current = query ?: return
        val token = ++generation
        request?.cancel()
        if (current.value.isEmpty()) {
            mutableUiState.value = RecipeListState.Error(RecipeListError.INVALID_ARGUMENT)
            return
        }
        mutableUiState.value = RecipeListState.Loading
        request = viewModelScope.launch {
            val resolved = if (current.kind == RecipeListKind.COUNTRY) {
                current.copy(value = findBrowseCountry(current.value)?.apiArea ?: current.value)
            } else current
            val result = getRecipeList(resolved)
            if (isActive && token == generation) {
                mutableUiState.value = when (result) {
                    is RecipeListResult.Content -> RecipeListState.Content(result.recipes)
                    is RecipeListResult.Error -> RecipeListState.Error(result.kind)
                }
            }
        }
    }

    companion object {
        const val KEY_KIND = "recipeListKind"
        const val KEY_VALUE = "recipeListValue"
    }
}
