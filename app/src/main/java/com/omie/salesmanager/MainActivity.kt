package com.omie.salesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.omie.salesmanager.presentation.view.SalesAuthView
import com.omie.salesmanager.presentation.view.SalesOrderView
import com.omie.salesmanager.ui.theme.SalesManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesManagerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "SalesAuthView") {
                    composable(route = "SalesAuthView") {
                        SalesAuthView(navController)
                    }

                    composable(route = "SalesOrderView") {
                        SalesOrderView(navController)
                    }
                }
            }
        }
    }
}
