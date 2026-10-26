package com.angelstudio.angelskitchen.ui.results

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import coil.compose.SubcomposeAsyncImage
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.domain.model.RecipeListError
import com.angelstudio.angelskitchen.domain.model.RecipeListItem
import com.angelstudio.angelskitchen.domain.model.RecipeListKind
import com.angelstudio.angelskitchen.domain.model.RecipeListQuery
import com.angelstudio.angelskitchen.ui.browse.findBrowseCountry
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import com.angelstudio.angelskitchen.ui.theme.KitchenArtworkColors
import com.angelstudio.angelskitchen.ui.theme.KitchenLayout
import com.angelstudio.angelskitchen.ui.theme.KitchenShapes
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing

fun filterRecipeList(recipes: List<RecipeListItem>, query: String): List<RecipeListItem> {
    val term = query.trim()
    return if (term.isEmpty()) recipes else recipes.filter {
        it.title.contains(
            term,
            ignoreCase = true,
        )
    }
}

@Composable
fun RecipeListScreen(
    query: RecipeListQuery,
    state: RecipeListState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRecipe: (String) -> Unit,
) {
    var searchVisible by rememberSaveable(query.kind, query.value) { mutableStateOf(false) }
    var search by rememberSaveable(query.kind, query.value) { mutableStateOf("") }
    val country = findBrowseCountry(query.value)
    val title = if (query.kind == RecipeListKind.COUNTRY && country != null) {
        stringResource(country.name)
    } else query.value.ifBlank { stringResource(R.string.results_title) }
    val subtitle = when {
        query.kind == RecipeListKind.COUNTRY && country != null -> stringResource(country.description)
        query.kind == RecipeListKind.CATEGORY && query.value.equals("Vegan", ignoreCase = true) ->
            stringResource(R.string.results_vegan_subtitle)

        query.kind == RecipeListKind.CATEGORY ->
            stringResource(R.string.results_category_subtitle, title)

        else -> stringResource(R.string.results_country_subtitle, title)
    }
    val recipes = (state as? RecipeListState.Content)?.recipes
    val visibleRecipes = remember(recipes, search) {
        recipes?.let { filterRecipeList(it, search) }.orEmpty()
    }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = KitchenLayout.resultsMaxWidth)
                    .fillMaxSize()
                    .testTag("recipe-results"),
                contentPadding = PaddingValues(KitchenSpacing.screen),
                verticalArrangement = Arrangement.spacedBy(KitchenSpacing.comfortable),
            ) {
                item(key = "heading") {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = KitchenSpacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.md),
                        verticalAlignment = Alignment.Top,
                    ) {
                        OutlinedIconButton(
                            onClick = onBack,
                            modifier = Modifier.size(KitchenSizes.minimumTouchTarget),
                            shape = CircleShape,
                            border = BorderStroke(
                                KitchenSizes.border,
                                MaterialTheme.colorScheme.outlineVariant,
                            ),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.results_back),
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.xs),
                        ) {
                            Text(
                                title,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.semantics { heading() },
                            )
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        OutlinedIconButton(
                            onClick = { searchVisible = !searchVisible; search = "" },
                            modifier = Modifier.size(KitchenSizes.minimumTouchTarget),
                            shape = CircleShape,
                            border = BorderStroke(
                                KitchenSizes.border,
                                MaterialTheme.colorScheme.outlineVariant,
                            ),
                        ) {
                            Icon(
                                if (searchVisible) Icons.Default.Close else Icons.Default.Search,
                                stringResource(if (searchVisible) R.string.results_close_search else R.string.results_open_search),
                            )
                        }
                    }
                }
                if (searchVisible) {
                    item(key = "search") {
                        OutlinedTextField(
                            value = search,
                            onValueChange = { search = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("results-search"),
                            label = { Text(stringResource(R.string.results_search_label)) },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            trailingIcon = {
                                if (search.isNotEmpty()) {
                                    IconButton(onClick = { search = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            stringResource(R.string.results_clear_search),
                                        )
                                    }
                                }
                            },
                            shape = KitchenShapes.hero,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            ),
                            singleLine = true,
                        )
                    }
                }
                when (state) {
                    RecipeListState.Loading -> item(key = "loading") {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = KitchenSpacing.xxl)
                                .semantics(mergeDescendants = true) {
                                    liveRegion = LiveRegionMode.Polite
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg),
                        ) {
                            CircularProgressIndicator()
                            Text(stringResource(R.string.results_loading))
                        }
                    }

                    is RecipeListState.Error -> item(key = "error") {
                        val message = when (state.kind) {
                            RecipeListError.INVALID_ARGUMENT -> R.string.results_invalid
                            RecipeListError.NETWORK -> R.string.results_network_error
                            RecipeListError.SERVER -> R.string.results_server_error
                            RecipeListError.MALFORMED -> R.string.results_data_error
                        }
                        Column(
                            Modifier
                                .padding(vertical = KitchenSpacing.xl)
                                .semantics { liveRegion = LiveRegionMode.Polite },
                            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.lg),
                        ) {
                            Text(
                                stringResource(message),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            if (state.kind != RecipeListError.INVALID_ARGUMENT) {
                                Button(
                                    onClick = onRetry,
                                    modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
                                ) {
                                    Text(stringResource(R.string.results_retry))
                                }
                            }
                        }
                    }

                    is RecipeListState.Content -> {
                        item(key = "count") {
                            Text(
                                pluralStringResource(
                                    R.plurals.results_count,
                                    visibleRecipes.size,
                                    visibleRecipes.size,
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.semantics {
                                    liveRegion = LiveRegionMode.Polite
                                },
                            )
                        }

                        if (visibleRecipes.isEmpty()) {
                            item(key = "empty") {
                                Text(
                                    stringResource(if (state.recipes.isEmpty()) R.string.results_empty else R.string.results_no_matches),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = KitchenSpacing.xl),
                                )
                            }
                        } else {
                            items(
                                items = visibleRecipes,
                                key = { it.id },
                                contentType = { "recipe" },
                            ) { recipe ->
                                RecipeResultCard(
                                    recipe,
                                    subtitle = title,
                                    onClick = { onRecipe(recipe.id) },
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
private fun RecipeResultCard(recipe: RecipeListItem, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .sizeIn(minHeight = KitchenSizes.resultCardMinHeight)
            .testTag("result-${recipe.id}"),
        shape = KitchenShapes.hero,
        border = BorderStroke(KitchenSizes.border, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        BoxWithConstraints(Modifier.padding(KitchenSpacing.sm)) {
            val stacked = this.maxWidth < KitchenLayout.resultStackBreakpoint ||
                    LocalDensity.current.fontScale >= KitchenLayout.resultStackFontScale
            if (stacked) {
                Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.md)) {
                    RecipeResultImage(
                        recipe,
                        Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = KitchenSizes.resultImageMinHeight,
                                max = KitchenSizes.resultImageMaxHeight,
                            ),
                    )
                    RecipeResultText(recipe.title, subtitle, Modifier.padding(KitchenSpacing.sm))
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.comfortable),
                ) {
                    RecipeResultImage(recipe, Modifier.size(KitchenSizes.resultImage))
                    RecipeResultText(
                        recipe.title,
                        subtitle,
                        Modifier
                            .weight(1f)
                            .padding(
                                vertical = KitchenSpacing.medium,
                                horizontal = KitchenSpacing.xxs,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeResultText(title: String, subtitle: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(KitchenSpacing.small)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun RecipeResultImage(recipe: RecipeListItem, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val placeholderColors = listOf(
        colors.tertiaryContainer,
        colors.primaryContainer,
        colors.secondaryContainer,
        colors.surfaceContainerHigh,
        colors.surfaceContainerHighest,
    )
    val color = placeholderColors[(recipe.id.hashCode() and Int.MAX_VALUE) % placeholderColors.size]
    Surface(
        modifier
            .clip(KitchenShapes.resultImage)
            .clearAndSetSemantics {},
        color = color,
    ) {
        if (recipe.imageUrl == null) {
            RecipeBowlArtwork()
        } else {
            SubcomposeAsyncImage(
                model = recipe.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { RecipeBowlArtwork() },
                error = { RecipeBowlArtwork() },
            )
        }
    }
}

@Composable
private fun RecipeBowlArtwork() {
    Canvas(
        Modifier
            .fillMaxSize()
            .padding(KitchenSpacing.lg),
    ) {
        val w = size.width
        val h = size.height
        drawOval(KitchenArtworkColors.meal, Offset(w * .12f, h * .38f), Size(w * .76f, h * .24f))
        repeat(4) { index ->
            drawCircle(
                KitchenArtworkColors.grain,
                w * .055f,
                Offset(w * (.28f + index * .14f), h * (.46f + index % 2 * .04f)),
            )
        }
        val bowl = Path().apply {
            moveTo(w * .08f, h * .53f)
            cubicTo(w * .13f, h * 1.03f, w * .87f, h * 1.03f, w * .92f, h * .53f)
            close()
        }
        drawPath(bowl, KitchenArtworkColors.white)
        drawPath(
            bowl,
            KitchenArtworkColors.bowlOutline,
            style = Stroke(KitchenSizes.artworkStroke.toPx()),
        )
        drawOval(KitchenArtworkColors.garnish, Offset(w * .48f, h * .27f), Size(w * .19f, h * .12f))
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Preview(showBackground = true, widthDp = 900, heightDp = 700, name = "Tablet")
@Composable
private fun RecipeListPreview() {
    AngelsKitchenTheme {
        RecipeListScreen(
            RecipeListQuery(RecipeListKind.CATEGORY, "Vegan"),
            RecipeListState.Content(
                listOf(
                    RecipeListItem("1", "Chickpea coconut curry", null),
                    RecipeListItem("2", "Lentil bolognese", null),
                    RecipeListItem("3", "Herby quinoa tabbouleh", null),
                ),
            ),
            onBack = {}, onRetry = {}, onRecipe = {},
        )
    }
}
