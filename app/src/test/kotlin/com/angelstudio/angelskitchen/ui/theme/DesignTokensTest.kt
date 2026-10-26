package com.angelstudio.angelskitchen.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.angelstudio.angelskitchen.ui.browse.browseColumnCount
import kotlin.math.floor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DesignTokensTest {
    @Test
    fun touchTargetsMeetAccessibilityMinimum() {
        assertTrue(KitchenSizes.minimumTouchTarget >= 48.dp)
    }

    @Test
    fun typographyPreservesExistingTextMetrics() {
        assertEquals(32.sp, KitchenTypography.headlineLarge.fontSize)
        assertEquals(40.sp, KitchenTypography.headlineLarge.lineHeight)
        assertEquals(22.sp, KitchenTypography.titleLarge.fontSize)
        assertEquals(30.sp, KitchenTypography.titleLarge.lineHeight)
        assertEquals(18.sp, KitchenTypography.bodyLarge.fontSize)
        assertEquals(28.sp, KitchenTypography.bodyLarge.lineHeight)
        assertEquals(16.sp, KitchenTypography.bodyMedium.fontSize)
        assertEquals(24.sp, KitchenTypography.bodyMedium.lineHeight)
        assertEquals(34.sp, KitchenTextStyles.browseHeading.fontSize)
        assertEquals(40.sp, KitchenTextStyles.browseHeading.lineHeight)
        assertEquals(FontWeight.ExtraBold, KitchenTextStyles.browseHeading.fontWeight)
    }

    @Test
    fun materialShapesPreserveExistingCorners() {
        assertEquals(CornerSize(8.dp), KitchenMaterialShapes.small.topStart)
        assertEquals(CornerSize(16.dp), KitchenMaterialShapes.medium.topStart)
        assertEquals(CornerSize(24.dp), KitchenMaterialShapes.large.topStart)
        assertEquals(CornerSize(28.dp), KitchenShapes.hero.topStart)
        assertEquals(CornerSize(22.dp), KitchenShapes.resultImage.topStart)
        assertEquals(CornerSize(6.dp), KitchenShapes.speechBubble.bottomStart)
    }

    @Test
    fun countryGridPreservesColumnCountsAcrossWidthsAndFontScales() {
        for (width in 0..2400) {
            for (fontScale in listOf(0.85f, 1f, 1.3f, 1.5f, 1.8f, 2f, 2.5f)) {
                val available = (width.toFloat().coerceAtMost(1200f) - 40f).coerceAtLeast(0f)
                val cardWidth = 148f * fontScale.coerceAtLeast(1f)
                val expected = floor((available + 12f) / (cardWidth + 12f))
                    .toInt().coerceIn(1, if (width < 600) 2 else 4)
                assertEquals(
                    "width=$width fontScale=$fontScale",
                    expected,
                    browseColumnCount(width.toFloat(), fontScale),
                )
            }
        }
    }
}
