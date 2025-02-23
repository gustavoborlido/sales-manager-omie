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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.omie.salesmanager.enum.SalesScreenEnum
import com.omie.salesmanager.presentation.view.SalesAuthView
import com.omie.salesmanager.presentation.view.SalesItemListView
import com.omie.salesmanager.presentation.view.SalesItemAddView
import com.omie.salesmanager.presentation.view.SalesOrderAddView
import com.omie.salesmanager.presentation.view.SalesOrderListView
import com.omie.salesmanager.ui.theme.SalesManagerTheme
import com.omie.salesmanager.ui.theme.White
import com.omie.salesmanager.ui.theme.Black

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalesManagerTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(color = White)

                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                var topBarTitle by remember { mutableStateOf("") }
                var showTopBar by remember { mutableStateOf(true) }
                var showBackButton by remember { mutableStateOf(true) }

                Scaffold(
                    snackbarHost = { CustomSnackbarHost(snackbarHostState) },
                    containerColor = Black,
                    topBar = {
                        CustomTopBar(
                            showTopBar = showTopBar,
                            topBarTitle = topBarTitle,
                            showBackButton = showBackButton,
                            onBackButtonClick = { navController.popBackStack() }
                        )
                    },
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        NavHost(
                            navController = navController,
                            startDestination = SalesScreenEnum.SalesAuthView.route
                        ) {
                            composable(SalesScreenEnum.SalesAuthView.route) {
                                SalesAuthView(navController, snackbarHostState)
                                showTopBar = false
                                showBackButton = false
                            }
                            composable(SalesScreenEnum.SalesOrderListView.route) {
                                SalesOrderListView(navController, snackbarHostState)
                                showTopBar = true
                                showBackButton = false
                                topBarTitle = stringResource(R.string.sales_order_list_title)
                            }
                            composable(SalesScreenEnum.SalesOrderAddView.route) {
                                SalesOrderAddView(navController, snackbarHostState)
                                showTopBar = true
                                showBackButton = true
                                topBarTitle = stringResource(R.string.sales_order_add_title)
                            }
                            composable(SalesScreenEnum.SalesItemListView.route) { backStackEntry ->
                                backStackEntry.arguments?.getString(stringResource(R.string.sales_order_id_key))
                                    ?.let {
                                        SalesItemListView(navController, it, snackbarHostState)
                                        showTopBar = true
                                        showBackButton = true
                                        topBarTitle = stringResource(R.string.sales_item_list_title)
                                    }
                            }
                            composable(SalesScreenEnum.SalesItemAddView.route) { backStackEntry ->
                                backStackEntry.arguments?.getString(stringResource(R.string.sales_order_id_key))
                                    ?.let {
                                        SalesItemAddView(navController, it, snackbarHostState)
                                        showTopBar = true
                                        showBackButton = true
                                        topBarTitle = stringResource(R.string.sales_item_add_title)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSnackbarHost(snackbarHostState: SnackbarHostState) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(hostState = snackbarHostState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    showTopBar: Boolean,
    topBarTitle: String,
    showBackButton: Boolean,
    onBackButtonClick: () -> Unit
) {
    if (showTopBar) {
        CenterAlignedTopAppBar(
            title = { Text(topBarTitle, color = White) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Black
            ),
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.sales_order_description_back_button),
                            tint = White
                        )
                    }
                }
            }
        )
    }
}
