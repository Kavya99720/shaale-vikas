package com.shaalevikas.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.shaalevikas.app.navigation.NavGraph
import com.shaalevikas.app.ui.theme.ShaaleVikasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaaleVikasTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
