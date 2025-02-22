package com.omie.salesmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import com.omie.salesmanager.ui.theme.White

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesManagerTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(color = DarkBlue)

                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                var topBarTitle by remember { mutableStateOf("") }
                var showTopBar by remember { mutableStateOf(true) }
                var showBackButton by remember { mutableStateOf(true) }

                Scaffold(
                    snackbarHost = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 80.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            SnackbarHost(hostState = snackbarHostState)
                        }
                    },
                    topBar = {
                        if (showTopBar) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        topBarTitle,
                                        color = White
                                    )
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = DarkBlue
                                ),
                                navigationIcon = {
                                    if (showBackButton) {
                                        IconButton(onClick = { navController.popBackStack() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Voltar",
                                                tint = White
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavHost(
                            navController = navController,
                            startDestination = "SalesAuthView"
                        ) {
                            composable(route = "SalesAuthView") {
                                showTopBar = false
                                showBackButton = false
                                SalesAuthView(navController, snackbarHostState)
                            }

                            composable(route = "SalesOrderListView") {
                                showTopBar = true
                                showBackButton = false
                                topBarTitle = "Pedidos"
                                SalesOrderListView(navController, snackbarHostState)
                            }

                            composable(route = "SalesOrderAddView") {
                                showTopBar = true
                                showBackButton = true
                                topBarTitle = "Adicionar Pedido"
                                SalesOrderAddView(navController, snackbarHostState)
                            }

                            composable(route = "SalesItemListView/{orderId}") { backStackEntry ->
                                backStackEntry.arguments?.getString("orderId")?.let {
                                    showTopBar = true
                                    showBackButton = true
                                    topBarTitle = "Itens do Pedido"
                                    SalesItemListView(navController, it, snackbarHostState)
                                }
                            }

                            composable(route = "SalesItemAddView/{orderId}") { backStackEntry ->
                                backStackEntry.arguments?.getString("orderId")?.let {
                                    showTopBar = true
                                    showBackButton = true
                                    topBarTitle = "Adicionar Item"
                                    SalesItemAddView(navController, it, snackbarHostState)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}