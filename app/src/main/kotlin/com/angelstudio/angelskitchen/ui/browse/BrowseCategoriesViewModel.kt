package com.angelstudio.angelskitchen.ui.browse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.angelstudio.angelskitchen.domain.model.CategoryResult
import com.angelstudio.angelskitchen.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface BrowseCategoriesState {
    data object Loading : BrowseCategoriesState
    data object Error : BrowseCategoriesState
    data class Content(val categories: List<BrowseCategory>) : BrowseCategoriesState
}

@HiltViewModel
class BrowseCategoriesViewModel @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
) : ViewModel() {
    private val mutableState =
        MutableStateFlow<BrowseCategoriesState>(BrowseCategoriesState.Loading)
    val state = mutableState.asStateFlow()

    init {
        load()
    }

    fun retry() {
        if (mutableState.value == BrowseCategoriesState.Error) load()
    }

    private fun load() {
        mutableState.value = BrowseCategoriesState.Loading
        viewModelScope.launch {
            val result = getCategories()
            if (!isActive) return@launch
            mutableState.value = when (result) {
                is CategoryResult.Content -> BrowseCategoriesState.Content(
                    result.categories.map {
                        BrowseCategory(it.apiName, it.thumbnailUrl, it.description)
                    },
                )

                is CategoryResult.Error -> BrowseCategoriesState.Error
            }
        }
    }
}
