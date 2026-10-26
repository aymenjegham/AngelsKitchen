package com.angelstudio.angelskitchen.ui.browse

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.animateContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.ui.components.AngieMascot
import com.angelstudio.angelskitchen.ui.theme.KitchenLayout
import com.angelstudio.angelskitchen.ui.theme.KitchenShapes
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing
import com.angelstudio.angelskitchen.ui.theme.KitchenTextStyles

@Composable
fun CategoriesScreen(
    state: BrowseCategoriesState,
    onRetry: () -> Unit,
    onCategoryClick: (BrowseCategory) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    BrowseBody {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(KitchenSpacing.screen),
            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.screen),
        ) {
            item {
                BrowseHeading(
                    stringResource(R.string.browse_categories_title),
                    stringResource(R.string.browse_categories_subtitle),
                )
            }
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category-search"),
                    label = { Text(stringResource(R.string.browse_search_categories)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = KitchenShapes.pill,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    ),
                )
            }
            item { AngiePrompt(stringResource(R.string.browse_categories_prompt)) }
            when (state) {
                BrowseCategoriesState.Loading -> item {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(KitchenSpacing.xl),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(KitchenSpacing.md),
                    ) {
                        CircularProgressIndicator()
                        Text(stringResource(R.string.browse_loading_categories))
                    }
                }

                BrowseCategoriesState.Error -> item {
                    Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.md)) {
                        Text(stringResource(R.string.browse_categories_error))
                        Button(
                            onClick = onRetry,
                            modifier = Modifier.heightIn(min = KitchenSizes.minimumTouchTarget),
                        ) {
                            Text(stringResource(R.string.browse_retry))
                        }
                    }
                }

                is BrowseCategoriesState.Content -> {
                    val categories = filterCategories(state.categories, query)
                    item {
                        if (categories.isEmpty()) {
                            Text(stringResource(R.string.browse_no_categories))
                        } else {
                            Surface(
                                shape = MaterialTheme.shapes.large,
                                border = BorderStroke(
                                    KitchenSizes.border,
                                    MaterialTheme.colorScheme.outlineVariant,
                                ),
                                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                            ) {
                                Column {
                                    categories.forEachIndexed { index, category ->
                                        CategoryRow(category, index, onCategoryClick)
                                        if (index < categories.lastIndex) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(
                                                    alpha = 0.6f,
                                                ),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CountriesScreen(onCountryClick: (BrowseCountry) -> Unit) {
    var selectedRegion by rememberSaveable { mutableStateOf(BrowseRegion.All) }
    BrowseBody {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val columns = browseColumnCount(this.maxWidth.value, LocalDensity.current.fontScale)
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("country-grid-$columns"),
                contentPadding = PaddingValues(KitchenSpacing.screen),
                horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.md),
                verticalArrangement = Arrangement.spacedBy(KitchenSpacing.comfortable),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BrowseHeading(
                        stringResource(R.string.browse_worldwide_title),
                        stringResource(R.string.browse_worldwide_subtitle),
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    AngiePrompt(
                        stringResource(R.string.browse_worldwide_prompt),
                        Modifier.padding(top = KitchenSpacing.sm, bottom = KitchenSpacing.xs),
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.sm),
                    ) {
                        BrowseRegion.entries.forEach { region ->
                            FilterChip(
                                selected = selectedRegion == region,
                                onClick = { selectedRegion = region },
                                label = {
                                    Text(
                                        stringResource(region.label),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(
                                            horizontal = KitchenSpacing.sm,
                                            vertical = KitchenSpacing.sm,
                                        ),
                                    )
                                },
                                modifier = Modifier
                                    .heightIn(min = KitchenSizes.minimumTouchTarget)
                                    .testTag("region-${region.name}"),
                                shape = KitchenShapes.pill,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                border = BorderStroke(
                                    KitchenSizes.border,
                                    if (selectedRegion == region) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outlineVariant,
                                ),
                            )
                        }
                    }
                }
                items(
                    items = filterCountries(selectedRegion),
                    key = { it.countryCode },
                    contentType = { "country" },
                ) { country ->
                    CountryCard(country, onCountryClick)
                }
            }
        }
    }
}

@Composable
private fun BrowseBody(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            Box(
                Modifier
                    .widthIn(max = KitchenLayout.contentMaxWidth)
                    .fillMaxWidth(),
            ) { content() }
        }
    }
}

@Composable
private fun BrowseHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(KitchenSpacing.xs)) {
        Text(
            title,
            style = KitchenTextStyles.browseHeading,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AngiePrompt(message: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        AngieMascot(
            Modifier
                .size(KitchenSizes.mascot)
                .clearAndSetSemantics {},
        )
        Spacer(Modifier.width(KitchenSpacing.md))
        Surface(
            modifier = Modifier.weight(1f),
            shape = KitchenShapes.speechBubble,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) {
            Text(
                message,
                modifier = Modifier.padding(KitchenSpacing.lg),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun CategoryRow(category: BrowseCategory, index: Int, onClick: (BrowseCategory) -> Unit) {
    var descriptionExpanded by rememberSaveable(category.apiName) { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val categoryTileColors = listOf(
        colors.tertiaryContainer,
        colors.secondaryContainer,
        colors.primaryContainer,
        colors.surfaceContainerHigh,
        colors.surfaceContainerHighest,
        colors.surfaceContainerLow,
    )
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = KitchenSizes.categoryRowMinHeight),
    ) {
        Row(
            Modifier
                .animateContentSize()
                .combinedClickable(
                    onClick = { onClick(category) },
                    onLongClickLabel = stringResource(
                        if (descriptionExpanded) R.string.browse_collapse_description
                        else R.string.browse_expand_description,
                    ),
                    onLongClick = {
                        if (category.description.isNotBlank()) {
                            descriptionExpanded = !descriptionExpanded
                        }
                    },
                )
                .testTag("category-${category.apiName}")
                .padding(horizontal = KitchenSpacing.lg, vertical = KitchenSpacing.comfortable),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(KitchenSpacing.comfortable),
        ) {
            CategoryTile(category, categoryTileColors[index % categoryTileColors.size])
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(KitchenSpacing.compact),
            ) {
                Text(
                    category.apiName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                if (category.description.isNotBlank()) {
                    Text(
                        category.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (descriptionExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(KitchenSizes.icon),
            )
        }
    }
}

@Composable
private fun CountryCard(country: BrowseCountry, onClick: (BrowseCountry) -> Unit) {
    Surface(
        onClick = { onClick(country) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = KitchenSizes.countryCardMinHeight)
            .testTag("country-${country.apiArea}"),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(KitchenSizes.border, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            Modifier.padding(KitchenSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(KitchenSpacing.xs),
        ) {
            Image(
                painter = painterResource(country.flagResource),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(KitchenSizes.flagWidth, KitchenSizes.flagHeight)
                    .clip(MaterialTheme.shapes.small)
                    .border(
                        KitchenSizes.border,
                        MaterialTheme.colorScheme.outlineVariant,
                        MaterialTheme.shapes.small,
                    ),
            )
            Spacer(Modifier.height(KitchenSpacing.xxs))
            Text(
                stringResource(country.name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                stringResource(country.description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
