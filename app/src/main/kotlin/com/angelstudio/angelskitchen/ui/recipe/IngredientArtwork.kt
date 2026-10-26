package com.angelstudio.angelskitchen.ui.recipe

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.angelstudio.angelskitchen.ui.theme.KitchenArtworkColors
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing
import java.util.Locale

@Composable
internal fun IngredientArtwork(name: String) {
    val ingredient = name.trim().lowercase(Locale.ROOT)
    Canvas(Modifier
        .fillMaxSize()
        .padding(KitchenSpacing.md)
        .clearAndSetSemantics {}) {
        val unit = minOf(size.width, size.height) / 100f
        scale(unit, unit, pivot = Offset.Zero) {
            val left = (size.width / unit - 100f) / 2f
            val top = (size.height / unit - 100f) / 2f
            fun point(x: Float, y: Float) = Offset(left + x, top + y)
            val green = KitchenArtworkColors.leaf
            when (ingredient) {
                "tomato", "tomatoes" -> {
                    drawOval(KitchenArtworkColors.tomato, point(20f, 30f), Size(60f, 62f))
                    drawOval(KitchenArtworkColors.tomatoHighlight, point(31f, 40f), Size(17f, 11f))
                    drawLine(green, point(50f, 33f), point(50f, 20f), 4f)
                    drawPath(
                        Path().apply {
                            moveTo(left + 50f, top + 35f)
                            lineTo(left + 30f, top + 27f)
                            lineTo(left + 43f, top + 38f)
                            lineTo(left + 56f, top + 38f)
                            lineTo(left + 70f, top + 27f)
                            close()
                        },
                        green,
                    )
                }

                "pepper", "peppers", "red pepper", "red peppers", "red bell pepper", "red bell peppers" -> {
                    drawPath(
                        Path().apply {
                            moveTo(left + 50f, top + 30f)
                            cubicTo(
                                left + 20f,
                                top + 17f,
                                left + 22f,
                                top + 61f,
                                left + 32f,
                                top + 79f,
                            )
                            cubicTo(
                                left + 41f,
                                top + 97f,
                                left + 61f,
                                top + 97f,
                                left + 70f,
                                top + 78f,
                            )
                            cubicTo(
                                left + 81f,
                                top + 58f,
                                left + 82f,
                                top + 20f,
                                left + 50f,
                                top + 30f,
                            )
                            close()
                        },
                        KitchenArtworkColors.pepper,
                    )
                    drawPath(
                        Path().apply {
                            moveTo(left + 43f, top + 35f)
                            cubicTo(
                                left + 34f,
                                top + 53f,
                                left + 36f,
                                top + 77f,
                                left + 46f,
                                top + 88f,
                            )
                        },
                        KitchenArtworkColors.pepperHighlight,
                        style = Stroke(3f),
                    )
                    drawLine(green, point(50f, 29f), point(50f, 13f), 5f)
                    drawOval(green, point(36f, 26f), Size(29f, 8f))
                }

                "egg", "eggs" -> {
                    drawOval(KitchenArtworkColors.eggshell, point(17f, 39f), Size(38f, 49f))
                    drawOval(
                        KitchenArtworkColors.eggshellOutline,
                        point(17f, 39f),
                        Size(38f, 49f),
                        style = Stroke(2f),
                    )
                    drawOval(KitchenArtworkColors.eggshellLight, point(47f, 29f), Size(39f, 54f))
                    drawOval(
                        KitchenArtworkColors.eggshellOutline,
                        point(47f, 29f),
                        Size(39f, 54f),
                        style = Stroke(2f),
                    )
                    drawOval(KitchenArtworkColors.white, point(54f, 36f), Size(9f, 15f))
                }

                else -> {
                    drawPath(
                        Path().apply {
                            moveTo(left + 20f, top + 53f)
                            cubicTo(
                                left + 23f,
                                top + 90f,
                                left + 77f,
                                top + 90f,
                                left + 80f,
                                top + 53f,
                            )
                            close()
                        },
                        green,
                    )
                    drawOval(KitchenArtworkColors.bowlRim, point(20f, 47f), Size(60f, 13f))
                    drawPath(
                        Path().apply {
                            moveTo(left + 48f, top + 48f)
                            cubicTo(
                                left + 28f,
                                top + 47f,
                                left + 28f,
                                top + 28f,
                                left + 32f,
                                top + 22f,
                            )
                            cubicTo(
                                left + 47f,
                                top + 25f,
                                left + 51f,
                                top + 34f,
                                left + 48f,
                                top + 48f,
                            )
                        },
                        KitchenArtworkColors.leafLight,
                    )
                    drawPath(
                        Path().apply {
                            moveTo(left + 50f, top + 46f)
                            cubicTo(
                                left + 51f,
                                top + 28f,
                                left + 65f,
                                top + 23f,
                                left + 75f,
                                top + 24f,
                            )
                            cubicTo(
                                left + 74f,
                                top + 40f,
                                left + 66f,
                                top + 49f,
                                left + 50f,
                                top + 46f,
                            )
                        },
                        green,
                    )
                }
            }
        }
    }
}
