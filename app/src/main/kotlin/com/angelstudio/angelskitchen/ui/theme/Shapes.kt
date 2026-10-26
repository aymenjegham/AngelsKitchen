package com.angelstudio.angelskitchen.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

internal val KitchenMaterialShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
)

object KitchenShapes {
    val pill = RoundedCornerShape(50)
    val hero = RoundedCornerShape(28.dp)
    val resultImage = RoundedCornerShape(22.dp)
    val speechBubble = RoundedCornerShape(
        topStart = 28.dp,
        topEnd = 28.dp,
        bottomEnd = 28.dp,
        bottomStart = 6.dp,
    )
}
