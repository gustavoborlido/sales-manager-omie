package com.omie.salesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.omie.salesmanager.presentation.view.SalesAuthView
import com.omie.salesmanager.presentation.view.SalesListView
import com.omie.salesmanager.presentation.view.SalesOrderView
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.SalesManagerTheme
import com.omie.salesmanager.ui.theme.White

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesManagerTheme {

                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(color = DarkBlue)

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "SalesListView") {
                    composable(route = "SalesAuthView") {
                        SalesAuthView(navController)
                    }

                    composable(route = "SalesOrderView") {
                        SalesOrderView(navController)
                    }

                    composable(route = "SalesListView") {
                        SalesListView(navController)
                    }
                }
            }
        }
    }
}
