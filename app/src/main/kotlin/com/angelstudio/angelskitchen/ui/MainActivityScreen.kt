package com.angelstudio.angelskitchen.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.angelstudio.angelskitchen.ui.navigation.MainActivityNavigation
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme

@Composable
fun MainActivityScreen() {
    MainActivityNavigation()
}

@Preview(showBackground = true)
@Composable
private fun MainActivityScreenPreview() {
    AngelsKitchenTheme(useDynamicColors = false) {
        MainActivityScreen()
    }
}
