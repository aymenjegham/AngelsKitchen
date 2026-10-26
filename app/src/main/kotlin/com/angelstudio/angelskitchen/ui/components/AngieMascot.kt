package com.angelstudio.angelskitchen.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.angelstudio.angelskitchen.R

@Composable
fun AngieMascot(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.angie_mascot),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
