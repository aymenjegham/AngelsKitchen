package com.angelstudio.angelskitchen.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * App design-system entry point. Palettes live in Colors.kt, text in Typography.kt,
 * corners in Shapes.kt, geometry in Dimensions.kt, and fixed art pigments in ArtworkColors.kt.
 * Consume UI colors through MaterialTheme.colorScheme so light/dark and dynamic colors work.
 */
@Composable
fun AngelsKitchenTheme(
    useDynamicColors: Boolean = false,
    content: @Composable () -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val colors = when {
        useDynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)

        dark -> WarmDarkColors
        else -> WarmLightColors
    }
    MaterialTheme(
        colorScheme = colors,
        typography = KitchenTypography,
        shapes = KitchenMaterialShapes,
        content = content,
    )
}
