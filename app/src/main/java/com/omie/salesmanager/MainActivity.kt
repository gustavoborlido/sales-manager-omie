package com.omie.salesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.omie.salesmanager.presentation.view.SalesAuthView
import com.omie.salesmanager.presentation.view.SalesItemListView
import com.omie.salesmanager.presentation.view.SalesItemAddView
import com.omie.salesmanager.presentation.view.SalesOrderAddView
import com.omie.salesmanager.presentation.view.SalesOrderListView
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.SalesManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesManagerTheme {

                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(color = DarkBlue)

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "SalesOrderListView") {
                    composable(route = "SalesAuthView") {
                        SalesAuthView(navController)
                    }

                    composable(route = "SalesItemAddView/{orderId}") { backStackEntry ->
                        backStackEntry.arguments?.getString("orderId")?.let {
                            SalesItemAddView(navController, it)
                        }
                    }

                    composable(route = "SalesItemListView/{orderId}") { backStackEntry ->
                        backStackEntry.arguments?.getString("orderId")?.let {
                            SalesItemListView(navController, it)
                        }
                    }

                    composable(route = "SalesOrderListView") {
                        SalesOrderListView(navController)
                    }

                    composable(route = "SalesOrderAddView") {
                        SalesOrderAddView(navController)
                    }
                }
            }
        }
    }
}
