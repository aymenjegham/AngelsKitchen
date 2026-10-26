package com.angelstudio.angelskitchen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.angelstudio.angelskitchen.ui.MainActivityScreen
import com.angelstudio.angelskitchen.ui.theme.AngelsKitchenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AngelsKitchenTheme {
                MainActivityScreen()
            }
        }
    }
}
