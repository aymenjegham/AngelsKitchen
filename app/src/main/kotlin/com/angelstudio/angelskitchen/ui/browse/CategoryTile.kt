package com.angelstudio.angelskitchen.ui.browse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.angelstudio.angelskitchen.ui.theme.KitchenSizes
import com.angelstudio.angelskitchen.ui.theme.KitchenSpacing
import java.util.Locale

@Composable
internal fun CategoryTile(category: BrowseCategory, background: Color) {
    var imageLoaded by remember(category.imageUrl) { mutableStateOf(false) }
    val ink = MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        Modifier
            .size(KitchenSizes.categoryTile)
            .clip(MaterialTheme.shapes.medium)
            .background(background)
            .padding(KitchenSpacing.sm),
    ) {
        if (!imageLoaded) {
            Canvas(Modifier.fillMaxSize()) {
                val stroke = Stroke(width = size.minDimension * 0.075f, cap = StrokeCap.Round)
                val w = size.width
                val h = size.height
                when (category.apiName.lowercase(Locale.ROOT)) {
                    "vegan", "vegetarian" -> {
                        drawPath(
                            Path().apply {
                                moveTo(w * .2f, h * .8f)
                                cubicTo(w * .15f, h * .35f, w * .55f, h * .2f, w * .85f, h * .15f)
                                cubicTo(w * .8f, h * .65f, w * .55f, h * .85f, w * .2f, h * .8f)
                                close()
                            },
                            ink, style = stroke,
                        )
                        drawLine(
                            ink,
                            Offset(w * .22f, h * .78f),
                            Offset(w * .65f, h * .4f),
                            stroke.width,
                            StrokeCap.Round,
                        )
                    }

                    "dessert" -> {
                        drawArc(
                            ink,
                            180f,
                            180f,
                            false,
                            Offset(w * .16f, h * .28f),
                            Size(w * .68f, h * .46f),
                            style = stroke,
                        )
                        drawPath(
                            Path().apply {
                                moveTo(w * .22f, h * .55f)
                                lineTo(w * .3f, h * .86f)
                                lineTo(w * .7f, h * .86f)
                                lineTo(w * .78f, h * .55f)
                                close()
                            },
                            ink, style = stroke,
                        )
                        drawCircle(ink, w * .055f, Offset(w * .5f, h * .18f))
                    }

                    "seafood" -> {
                        drawOval(
                            ink,
                            Offset(w * .12f, h * .3f),
                            Size(w * .58f, h * .4f),
                            style = stroke,
                        )
                        drawPath(
                            Path().apply {
                                moveTo(w * .68f, h * .5f)
                                lineTo(w * .9f, h * .3f)
                                lineTo(w * .9f, h * .7f)
                                close()
                            },
                            ink, style = stroke,
                        )
                        drawCircle(ink, w * .035f, Offset(w * .28f, h * .46f))
                    }

                    else -> {
                        drawCircle(ink, w * .28f, Offset(w * .55f, h * .53f), style = stroke)
                        drawCircle(ink, w * .19f, Offset(w * .55f, h * .53f), style = stroke)
                        drawLine(
                            ink,
                            Offset(w * .1f, h * .18f),
                            Offset(w * .1f, h * .84f),
                            stroke.width,
                            StrokeCap.Round,
                        )
                        drawLine(
                            ink,
                            Offset(w * .88f, h * .18f),
                            Offset(w * .88f, h * .84f),
                            stroke.width,
                            StrokeCap.Round,
                        )
                    }
                }
            }
        }
        AsyncImage(
            model = category.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            onSuccess = { imageLoaded = true },
            onError = { imageLoaded = false },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
