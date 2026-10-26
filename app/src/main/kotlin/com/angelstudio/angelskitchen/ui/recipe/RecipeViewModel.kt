package com.angelstudio.angelskitchen.ui.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.isValidRecipeId
import com.angelstudio.angelskitchen.domain.repository.RecipeResult
import com.angelstudio.angelskitchen.domain.usecase.GetRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getRecipe: GetRecipeUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Loading)
    val uiState: StateFlow<RecipeUiState> = mutableUiState.asStateFlow()

    private var hasIdentity = false
    private var recipeId: String? = null
    private var request: Job? = null
    private var generation = 0L

    init {
        when (savedStateHandle.get<String>(KEY_MODE)) {
            MODE_RANDOM -> load(null)
            MODE_DETAIL -> load(savedStateHandle.get<String>(KEY_RECIPE_ID) ?: "")
        }
    }

    fun load(recipeId: String?) {
        if (hasIdentity && this.recipeId == recipeId) return
        hasIdentity = true
        this.recipeId = recipeId
        savedStateHandle[KEY_MODE] = if (recipeId == null) MODE_RANDOM else MODE_DETAIL
        if (recipeId == null) savedStateHandle.remove<String>(KEY_RECIPE_ID)
        else savedStateHandle[KEY_RECIPE_ID] = recipeId
        fetch()
    }

    fun retry() {
        if (hasIdentity && mutableUiState.value is RecipeUiState.Error) fetch()
    }

    private fun fetch() {
        val token = ++generation
        request?.cancel()
        val id = recipeId
        if (id != null && !isValidRecipeId(id)) {
            mutableUiState.value = RecipeUiState.Error(RecipeError.INVALID_ARGUMENT)
            return
        }
        mutableUiState.value = RecipeUiState.Loading
        request = viewModelScope.launch {
            val result = getRecipe(id)
            if (isActive && token == generation) {
                mutableUiState.value = when (result) {
                    is RecipeResult.Success -> RecipeUiState.Content(result.recipe)
                    is RecipeResult.Failure -> RecipeUiState.Error(result.kind)
                }
            }
        }
    }

    companion object {
        const val KEY_RECIPE_ID = "recipeId"
        const val KEY_MODE = "recipeMode"
        const val MODE_RANDOM = "random"
        const val MODE_DETAIL = "detail"
    }
}
