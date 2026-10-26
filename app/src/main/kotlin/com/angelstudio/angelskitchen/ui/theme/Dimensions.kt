package com.angelstudio.angelskitchen.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Shared design tokens. Use MaterialTheme.colorScheme, typography and shapes for Material roles,
 * KitchenSpacing for gaps/insets, KitchenSizes for component geometry, and KitchenLayout for
 * adaptive constraints. Keep illustration coordinates and preview device sizes local to their art.
 *
 * Values preserve the current UI; change a token here to update all of its consumers.
 */
object KitchenSpacing {
    val xxs = 2.dp
    val compact = 3.dp
    val xs = 4.dp
    val small = 6.dp
    val sm = 8.dp
    val medium = 10.dp
    val md = 12.dp
    val comfortable = 14.dp
    val lg = 16.dp
    val screen = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
}

object KitchenSizes {
    val border = 1.dp
    val artworkStroke = 2.dp
    val minimumTouchTarget = 48.dp
    val icon = 24.dp
    val navigationIcon = 26.dp
    val mascot = 64.dp
    val categoryTile = 52.dp
    val categoryRowMinHeight = 88.dp
    val countryCardMinHeight = 152.dp
    val countryCardMinWidth = 148.dp
    val flagWidth = 56.dp
    val flagHeight = 40.dp
    val resultCardMinHeight = 112.dp
    val resultImage = 92.dp
    val resultImageMinHeight = 104.dp
    val resultImageMaxHeight = 160.dp
    val heroImageWidth = 112.dp
    val heroImageHeight = 144.dp
    val ingredientCardWidth = 156.dp
    val ingredientImageHeight = 132.dp
    val videoAspectRatio = 16f / 9f
}

object KitchenLayout {
    val contentMaxWidth = 1200.dp
    val resultsMaxWidth = 1000.dp
    val readableMaxWidth = 600.dp
    val mediumBreakpoint = 600.dp
    val expandedBreakpoint = 840.dp
    val heroStackBreakpoint = 280.dp
    val resultStackBreakpoint = 240.dp
    val heroStackFontScale = 1.5f
    val resultStackFontScale = 1.8f
}
