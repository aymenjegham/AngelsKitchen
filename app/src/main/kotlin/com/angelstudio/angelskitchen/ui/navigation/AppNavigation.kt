package com.angelstudio.angelskitchen.ui.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.background
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.ui.recipe.RecipeViewModel
import com.angelstudio.angelskitchen.ui.browse.BrowseCategoriesViewModel
import com.angelstudio.angelskitchen.ui.browse.CategoriesScreen
import com.angelstudio.angelskitchen.ui.browse.CountriesScreen
import com.angelstudio.angelskitchen.ui.components.AngieNavigationBar
import com.angelstudio.angelskitchen.ui.media.RecipeVideo
import com.angelstudio.angelskitchen.ui.recipe.RecipeScreen
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.ui.results.RecipeListScreen
import com.angelstudio.angelskitchen.ui.results.ResultsViewModel
import kotlinx.serialization.Serializable

enum class TopLevelDestination { TODAY, CATEGORIES, WORLDWIDE }

@Serializable
data object TodayRoute : NavKey

@Serializable
data object CategoriesRoute : NavKey

@Serializable
data object CountriesRoute : NavKey

@Serializable
data class CategoryResultsRoute(val category: String) : NavKey

@Serializable
data class CountryResultsRoute(val country: String) : NavKey

@Serializable
data class RecipeDetailRoute(val recipeId: String) : NavKey

class AppNavigationState internal constructor(
    val selectedDestination: MutableState<TopLevelDestination>,
    val backStacks: Map<TopLevelDestination, NavBackStack<NavKey>>,
) {
    val activeBackStack: NavBackStack<NavKey>
        get() = backStacks.getValue(selectedDestination.value)

    fun select(destination: TopLevelDestination) {
        selectedDestination.value = destination
    }

    fun navigate(route: NavKey) {
        when (route) {
            TodayRoute -> select(TopLevelDestination.TODAY)
            CategoriesRoute -> select(TopLevelDestination.CATEGORIES)
            CountriesRoute -> select(TopLevelDestination.WORLDWIDE)
            else -> activeBackStack.add(route)
        }
    }

    fun back() {
        if (activeBackStack.size > 1) {
            activeBackStack.removeAt(activeBackStack.lastIndex)
        } else if (selectedDestination.value != TopLevelDestination.TODAY) {
            select(TopLevelDestination.TODAY)
        }
    }
}

@Composable
fun rememberAppNavigationState(): AppNavigationState {
    val selected = rememberSaveable { mutableStateOf(TopLevelDestination.TODAY) }
    val backStacks = mapOf(
        TopLevelDestination.TODAY to rememberNavBackStack(TodayRoute),
        TopLevelDestination.CATEGORIES to rememberNavBackStack(CategoriesRoute),
        TopLevelDestination.WORLDWIDE to rememberNavBackStack(CountriesRoute),
    )
    return remember(selected, backStacks) { AppNavigationState(selected, backStacks) }
}

@Composable
fun MainActivityNavigation(
    navigationState: AppNavigationState = rememberAppNavigationState(),
    modifier: Modifier = Modifier,
) {
    val entryProvider = entryProvider<NavKey> {
        entry<TodayRoute> { TodayEntry(navigationState) }
        entry<CategoriesRoute> { CategoriesEntry(navigationState) }
        entry<CountriesRoute> { CountriesEntry(navigationState) }
        entry<CategoryResultsRoute> { key ->
            ResultsEntry(navigationState, RecipeListQuery(RecipeListKind.CATEGORY, key.category))
        }
        entry<CountryResultsRoute> { key ->
            ResultsEntry(navigationState, RecipeListQuery(RecipeListKind.COUNTRY, key.country))
        }
        entry<RecipeDetailRoute> { key -> RecipeEntry(navigationState, key.recipeId) }
    }
    val decoratedEntries = navigationState.backStacks.mapValues { (_, backStack) ->
        rememberDecoratedNavEntries(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(rememberSaveableStateHolder()),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
    }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BackHandler(
            enabled = navigationState.selectedDestination.value != TopLevelDestination.TODAY &&
                navigationState.activeBackStack.size == 1,
            onBack = navigationState::back,
        )
        NavDisplay(
            entries = decoratedEntries.getValue(navigationState.selectedDestination.value),
            onBack = navigationState::back,
            modifier = Modifier.weight(1f).windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            ),
        )
        AngieNavigationBar(
            selectedDestination = navigationState.selectedDestination.value,
            onSelect = navigationState::select,
        )
    }
}

@Composable
private fun TodayEntry(navigationState: AppNavigationState) {
    val model: RecipeViewModel = hiltViewModel()
    LaunchedEffect(Unit) { model.load(null) }
    RecipeContent(navigationState, model, isToday = true)
}

@Composable
private fun RecipeEntry(navigationState: AppNavigationState, recipeId: String) {
    val model: RecipeViewModel = hiltViewModel()
    LaunchedEffect(recipeId) { model.load(recipeId) }
    RecipeContent(navigationState, model, isToday = false)
}

@Composable
private fun RecipeContent(
    navigationState: AppNavigationState,
    viewModel: RecipeViewModel,
    isToday: Boolean,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    RecipeScreen(
        state = state,
        isToday = isToday,
        onBack = navigationState::back,
        onRetry = viewModel::retry,
        onCategory = { navigationState.navigate(CategoryResultsRoute(it)) },
        onCuisine = { navigationState.navigate(CountryResultsRoute(it)) },
        onSource = { context.openExternalLink(it, R.string.recipe_source_open_error) },
        videoContent = { videoId ->
            RecipeVideo(videoId) { url -> context.openExternalLink(url, R.string.recipe_video_open_error) }
        },
    )
}

@Composable
private fun CategoriesEntry(navigationState: AppNavigationState) {
    val model: BrowseCategoriesViewModel = hiltViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    CategoriesScreen(
        state = state,
        onRetry = model::retry,
        onCategoryClick = { navigationState.navigate(CategoryResultsRoute(it.apiName)) },
    )
}

@Composable
private fun CountriesEntry(navigationState: AppNavigationState) {
    CountriesScreen { country ->
        navigationState.navigate(CountryResultsRoute(country.apiArea))
    }
}

@Composable
private fun ResultsEntry(navigationState: AppNavigationState, query: RecipeListQuery) {
    val model: ResultsViewModel = hiltViewModel()
    LaunchedEffect(query) { model.load(query) }
    val state by model.uiState.collectAsStateWithLifecycle()
    RecipeListScreen(
        query = query,
        state = state,
        onBack = navigationState::back,
        onRetry = model::retry,
        onRecipe = { navigationState.navigate(RecipeDetailRoute(it)) },
    )
}

private fun Context.openExternalLink(url: String, errorMessage: Int) {
    val uri = Uri.parse(url)
    if (uri.scheme !in setOf("http", "https") || uri.host.isNullOrBlank()) {
        Log.w("RecipeSource", "Rejected invalid source link")
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        return
    }
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE))
    } catch (error: ActivityNotFoundException) {
        Log.w("RecipeSource", "No application can open the recipe source", error)
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    } catch (error: SecurityException) {
        Log.w("RecipeSource", "Recipe source launch was denied", error)
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
