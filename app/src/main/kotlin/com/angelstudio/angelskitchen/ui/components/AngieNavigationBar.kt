package com.angelstudio.angelskitchen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import com.angelstudio.angelskitchen.R
import com.angelstudio.angelskitchen.ui.navigation.TopLevelDestination
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AngieNavigationBar(
    selectedDestination: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
) {
    val destinations = listOf(
        TopLevelDestination.TODAY to R.string.today,
        TopLevelDestination.CATEGORIES to R.string.categories,
        TopLevelDestination.WORLDWIDE to R.string.worldwide,
    )
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest) {
            destinations.forEachIndexed { index, (destination, label) ->
                NavigationBarItem(
                    selected = selectedDestination == destination,
                    onClick = { onSelect(destination) },
                    icon = { NavigationSymbol(index) },
                    label = { Text(stringResource(label)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

@Composable
private fun NavigationSymbol(index: Int) {
    val color = androidx.compose.material3.LocalContentColor.current
    Canvas(Modifier.size(KitchenSizes.navigationIcon)) {
        val unit = size.width / 26f
        val stroke = Stroke(1.8f * unit, cap = StrokeCap.Round)
        val center = Offset(13f * unit, 13f * unit)
        when (index) {
            0 -> {
                drawCircle(color, 4.5f * unit, center, style = stroke)
                repeat(8) { ray ->
                    val angle = ray * Math.PI / 4
                    drawLine(
                        color,
                        center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * (8f * unit),
                        center + Offset(cos(angle).toFloat(), sin(angle).toFloat()) * (11f * unit),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round,
                    )
                }
            }

            1 -> repeat(4) { tile ->
                drawRoundRect(
                    color,
                    Offset((3f + tile % 2 * 11f) * unit, (3f + tile / 2 * 11f) * unit),
                    Size(8f * unit, 8f * unit),
                    CornerRadius(2f * unit),
                    style = stroke,
                )
            }

            else -> {
                drawCircle(color, 10f * unit, center, style = stroke)
                drawOval(
                    color,
                    Offset(8f * unit, 3f * unit),
                    Size(10f * unit, 20f * unit),
                    style = stroke,
                )
                drawLine(
                    color,
                    Offset(3f * unit, 13f * unit),
                    Offset(23f * unit, 13f * unit),
                    stroke.width,
                )
            }
        }
    }
}
