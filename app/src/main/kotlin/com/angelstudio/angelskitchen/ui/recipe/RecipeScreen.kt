package com.angelstudio.angelskitchen.ui.recipe

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.core.os.ConfigurationCompat
import coil.compose.SubcomposeAsyncImage
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.domain.model.Recipe
import com.angelstudio.angelskitchen.domain.model.RecipeError
import com.angelstudio.angelskitchen.domain.model.RecipeIngredient
import com.angelstudio.angelskitchen.ui.theme.KitchenLayout
import com.angelstudio.angelskitchen.ui.theme.KitchenShapes
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing
import java.text.SimpleDateFormat
import java.util.Date
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    state: RecipeUiState,
    isToday: Boolean,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onCategory: (String) -> Unit,
    onCuisine: (String) -> Unit,
    onSource: (String) -> Unit,
    videoContent: @Composable (videoId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            if (isToday) {
                TodayHeading()
            } else TopAppBar(
                title = { Text(stringResource(if (isToday) R.string.recipe_today else R.string.recipe_detail)) },
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    if (!isToday) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.sizeIn(
                                minWidth = KitchenSizes.minimumTouchTarget,
                                minHeight = KitchenSizes.minimumTouchTarget,
                            ),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.recipe_back),
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (state) {
                RecipeUiState.Loading -> RecipeLoading()
                is RecipeUiState.Error -> RecipeFailure(state.kind, onRetry)
                is RecipeUiState.Content -> RecipeContent(
                    state.recipe, isToday, onCategory, onCuisine, onSource, videoContent,
                )
            }
        }
    }
}

@Composable
private fun TodayHeading() {
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
        ?: java.util.Locale.getDefault()
    val pattern = android.text.format.DateFormat.getBestDateTimePattern(locale, "EEEE d MMMM")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KitchenSpacing.xl, vertical = KitchenSpacing.lg),
        verticalArrangement = Arrangement.spacedBy(KitchenSpacing.xs),
    ) {
        Text(
            SimpleDateFormat(pattern, locale).format(Date()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            stringResource(R.string.recipe_today_with_angie),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { heading() },
        )
    }
}

@Composable
private fun RecipeLoading() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(KitchenSpacing.xl)
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg, Alignment.CenterVertically),
    ) {
        CircularProgressIndicator()
        Text(stringResource(R.string.recipe_loading), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun RecipeFailure(error: RecipeError, onRetry: () -> Unit) {
    val message = when (error) {
        RecipeError.NETWORK -> R.string.recipe_error_network
        RecipeError.SERVER -> R.string.recipe_error_server
        RecipeError.EMPTY -> R.string.recipe_error_empty
        RecipeError.INVALID_ARGUMENT -> R.string.recipe_error_argument
        RecipeError.MALFORMED -> R.string.recipe_error_malformed
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg, Alignment.CenterVertically),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(KitchenSpacing.xl),
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = KitchenLayout.readableMaxWidth)
                    .semantics { liveRegion = LiveRegionMode.Polite },
                verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg),
            ) {
                SectionHeading(stringResource(R.string.recipe_error_title))
                Text(stringResource(message), style = MaterialTheme.typography.bodyLarge)
                if (error != RecipeError.INVALID_ARGUMENT) {
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
                    ) {
                        Text(stringResource(R.string.recipe_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeContent(
    recipe: Recipe,
    isToday: Boolean,
    onCategory: (String) -> Unit,
    onCuisine: (String) -> Unit,
    onSource: (String) -> Unit,
    videoContent: @Composable (String) -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val expanded = this.maxWidth >= KitchenLayout.expandedBreakpoint
        val gutter =
            if (this.maxWidth < KitchenLayout.mediumBreakpoint) KitchenSpacing.lg else KitchenSpacing.xl
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        LazyColumn(
            state = listState,
            modifier = Modifier
                .widthIn(max = KitchenLayout.contentMaxWidth)
                .fillMaxSize()
                .testTag("recipe-content"),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(gutter),
            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.xl),
        ) {
            item(key = "header") {
                RecipeHeader(recipe, isToday, onCategory, onCuisine) {
                    scope.launch { listState.animateScrollToItem(if (expanded) 1 else 2) }
                }
            }
            if (expanded) {
                item(key = "body") {
                    Row(horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.xxl)) {
                        Ingredients(recipe.ingredients, Modifier.weight(0.4f))
                        Column(
                            modifier = Modifier.weight(0.6f),
                            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg),
                        ) {
                            SectionHeading(stringResource(R.string.recipe_preparation))
                            Preparation(recipe.instructions)
                        }
                    }
                }
            } else {
                item(key = "ingredients") { Ingredients(recipe.ingredients) }
                item(key = "preparation") { SectionHeading(stringResource(R.string.recipe_preparation)) }
                if (recipe.instructions.isEmpty()) {
                    item { Text(stringResource(R.string.recipe_preparation_unavailable)) }
                } else {
                    itemsIndexed(
                        items = recipe.instructions,
                        key = { index, _ -> "instruction-$index" },
                        contentType = { _, _ -> "instruction" },
                    ) { _, paragraph ->
                        Text(paragraph, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            item(key = "supporting") { SupportingContent(recipe, onSource, videoContent) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipeHeader(
    recipe: Recipe,
    isToday: Boolean,
    onCategory: (String) -> Unit,
    onCuisine: (String) -> Unit,
    onStartCooking: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg)) {
        Surface(
            shape = KitchenShapes.hero,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth(),
        ) {
            BoxWithConstraints(Modifier.padding(KitchenSpacing.screen)) {
                val stack = this.maxWidth < KitchenLayout.heroStackBreakpoint ||
                        LocalDensity.current.fontScale >= KitchenLayout.heroStackFontScale
                if (stack) {
                    Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg)) {
                        RecipePhoto(recipe.imageUrl)
                        HeroText(recipe.title, isToday, onStartCooking)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.lg),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        RecipePhoto(
                            recipe.imageUrl,
                            Modifier
                                .width(KitchenSizes.heroImageWidth)
                                .height(KitchenSizes.heroImageHeight)
                                .align(Alignment.CenterVertically),
                        )
                        HeroText(recipe.title, isToday, onStartCooking, Modifier.weight(1f))
                    }
                }
            }
        }
        if (!recipe.category.isNullOrBlank() || !recipe.cuisine.isNullOrBlank()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(KitchenSpacing.sm),
            ) {
                recipe.category?.takeIf { it.isNotBlank() }?.let { category ->
                    val description = stringResource(R.string.recipe_category_action, category)
                    MetadataAction(
                        onClick = { onCategory(category) },
                        label = category,
                        description = description,
                    )
                }
                recipe.cuisine?.takeIf { it.isNotBlank() }?.let { cuisine ->
                    val description = stringResource(R.string.recipe_cuisine_action, cuisine)
                    MetadataAction(
                        onClick = { onCuisine(cuisine) },
                        label = cuisine,
                        description = description,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroText(
    title: String,
    isToday: Boolean,
    onStartCooking: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg)) {
        if (isToday) {
            Text(
                stringResource(R.string.recipe_pick_tonight),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
        Text(
            title.ifBlank { stringResource(R.string.recipe_untitled) },
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Button(
            onClick = onStartCooking,
            shape = KitchenShapes.pill,
            modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Text(
                stringResource(R.string.recipe_start_cooking),
                Modifier.padding(start = KitchenSpacing.sm),
            )
        }
    }
}

@Composable
private fun RecipePhoto(
    imageUrl: String?,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(KitchenSizes.videoAspectRatio),
) {
    Card(modifier = modifier) {
        if (imageUrl.isNullOrBlank()) {
            ImageStatus(R.string.recipe_photo_unavailable)
        } else {
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = stringResource(R.string.recipe_photo),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = { ImageStatus(R.string.recipe_photo_loading) },
                error = { ImageStatus(R.string.recipe_photo_unavailable) },
            )
        }
    }
}

@Composable
private fun MetadataAction(label: String, description: String, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        modifier = Modifier
            .sizeIn(
                minWidth = KitchenSizes.minimumTouchTarget,
                minHeight = KitchenSizes.minimumTouchTarget,
            )
            .semantics { contentDescription = description },
        shape = KitchenShapes.pill,
        label = {
            Text(
                label,
                modifier = Modifier.padding(vertical = KitchenSpacing.sm),
                style = MaterialTheme.typography.labelLarge,
            )
        },
    )
}

@Composable
private fun ImageStatus(message: Int) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(KitchenSpacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(message), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SectionHeading(text: String) {
    Text(
        text,
        modifier = Modifier.semantics { heading() },
        style = MaterialTheme.typography.titleLarge,
    )
}

@Composable
private fun Ingredients(ingredients: List<RecipeIngredient>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.recipe_ingredients),
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                pluralStringResource(
                    R.plurals.recipe_ingredient_count,
                    ingredients.size,
                    ingredients.size,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (ingredients.isEmpty()) {
            Text(
                stringResource(R.string.recipe_ingredients_unavailable),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.md)) {
                itemsIndexed(
                    items = ingredients,
                    key = { _, ingredient -> "${ingredient.name}:${ingredient.measure}" },
                    contentType = { _, _ -> "ingredient" },
                ) { index, ingredient ->
                    val colors = MaterialTheme.colorScheme
                    val artColor = when (index % 3) {
                        0 -> colors.primaryContainer
                        1 -> colors.secondaryContainer
                        else -> colors.tertiaryContainer
                    }
                    Card(
                        modifier = Modifier
                            .width(KitchenSizes.ingredientCardWidth)
                            .semantics(mergeDescendants = true) {},
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    ) {
                        Surface(color = artColor) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(KitchenSizes.ingredientImageHeight),
                                contentAlignment = Alignment.Center,
                            ) {
                                SubcomposeAsyncImage(
                                    model = "https://www.themealdb.com/images/ingredients/${
                                        Uri.encode(
                                            ingredient.name,
                                        )
                                    }-Small.png",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(KitchenSpacing.lg),
                                    contentScale = ContentScale.Fit,
                                    loading = { IngredientArtwork(ingredient.name) },
                                    error = { IngredientArtwork(ingredient.name) },
                                )
                            }
                        }
                        Column(
                            Modifier.padding(KitchenSpacing.lg),
                            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.xs),
                        ) {
                            Text(
                                ingredient.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                            if (ingredient.measure.isNotBlank()) {
                                Text(
                                    ingredient.measure,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Preparation(instructions: List<String>) {
    if (instructions.isEmpty()) {
        Text(
            stringResource(R.string.recipe_preparation_unavailable),
            style = MaterialTheme.typography.bodyLarge,
        )
    } else {
        instructions.forEach { Text(it, style = MaterialTheme.typography.bodyLarge) }
    }
}

@Composable
private fun SupportingContent(
    recipe: Recipe,
    onSource: (String) -> Unit,
    videoContent: @Composable (String) -> Unit,
) {
    var videoRequested by rememberSaveable(recipe.id) { mutableStateOf(false) }
    val videoId = recipe.videoId?.takeIf { recipeVideoIdPattern.matches(it) }
    val source = recipe.sourceUrl?.let { Uri.parse(it) }
        ?.takeIf { it.scheme in listOf("https", "http") && !it.host.isNullOrBlank() }
    Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg)) {
        if (videoId != null) {
            SectionHeading(stringResource(R.string.recipe_video))
            if (videoRequested) {
                videoContent(videoId)
            } else {
                Button(
                    onClick = { videoRequested = true },
                    modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
                ) {
                    Text(stringResource(R.string.recipe_play_video))
                }
            }
        }
        if (source != null) {
            val description = stringResource(R.string.recipe_source_action, source.host.orEmpty())
            Button(
                onClick = { onSource(source.toString()) },
                modifier = Modifier
                    .heightIn(min = KitchenSizes.minimumTouchTarget)
                    .semantics { contentDescription = description },
            ) {
                Text(stringResource(R.string.recipe_source))
            }
        }
    }
}

private val recipeVideoIdPattern = Regex("[A-Za-z0-9_-]{11}")
