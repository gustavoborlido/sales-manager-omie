package com.omie.salesmanager.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.presentation.state.SalesListViewState
import com.omie.salesmanager.presentation.viewmodel.SalesListViewModel
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Green
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesOrderListView(navController: NavController) {
    val viewModel: SalesListViewModel = koinViewModel()
    val orderState by viewModel.listState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getOrders()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Lista de Pedidos",
                        color = White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBlue
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("SalesOrderAddView") },
                containerColor = Green
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Criar Pedido",
                    tint = DarkBlue
                )
            }
        }
    ) { paddingValues ->
        HandleListState(orderState, paddingValues)
    }
}

@Composable
fun OrderItemList(order: SalesItemModel) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = order.productName,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                color = White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Quantidade: ${order.quantity}", color = White)
            Text(text = "Valor Unitário: ${currencyFormatter.format(order.value)}", color = White)
            Text(text = "Valor Total: ${currencyFormatter.format(order.totalValue)}", color = White)
        }
    }
}

@Composable
fun HandleOrderListState(orderState: SalesListViewState, paddingValues: PaddingValues) {
    when (orderState) {
        is SalesListViewState.Loading -> {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(LightBlue),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(color = Green)
//            }
        }

        is SalesListViewState.Success -> {
            val orders = orderState.orders
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(LightBlue)
//                    .padding(paddingValues)
//            ) {
//                items(orders) { order ->
//                    OrderItem(order)
//                }
//            }
        }

        is SalesListViewState.Error -> {
            val errorMessage = orderState.message
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(LightBlue),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(text = "Erro: $errorMessage", color = MaterialTheme.colorScheme.error)
//            }
        }

        else -> {}
    }
}


