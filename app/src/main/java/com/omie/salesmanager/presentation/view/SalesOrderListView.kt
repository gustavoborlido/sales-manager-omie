package com.omie.salesmanager.presentation.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.omie.salesmanager.components.SalesDeleteDialog
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesOrderListViewState
import com.omie.salesmanager.presentation.viewmodel.SalesOrderListViewModel
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Green
import com.omie.salesmanager.ui.theme.LightBlue
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalesOrderListView(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val viewModel: SalesOrderListViewModel = koinViewModel()
    val orderState by viewModel.orderState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetDeleteState()
        viewModel.getOrders()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            HandleOrderListState(
                orderState = orderState,
                paddingValues = PaddingValues(0.dp),
                navController = navController,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }

        FloatingActionButton(
            onClick = { navController.navigate("SalesOrderAddView") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Green
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Criar Pedido",
                tint = DarkBlue
            )
        }
    }

    HandleOrderDeleteState(deleteState, snackbarHostState)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderItemList(
    order: SalesOrderModel,
    navController: NavController,
    onDelete: (SalesOrderModel) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .combinedClickable(
                onClick = { navController.navigate("SalesItemListView/${order.id}") },
                onLongClick = { showDialog = true }
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = order.description,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                color = White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Cliente: ${order.clientName}", color = White)
            Text(
                text = "Valor Total: ${currencyFormatter.format(order.totalOrderValue)}",
                color = White
            )
        }
    }

    if (showDialog) {
        SalesDeleteDialog(
            onConfirm = {
                onDelete(order)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun HandleOrderListState(
    orderState: SalesOrderListViewState,
    paddingValues: PaddingValues,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: SalesOrderListViewModel
) {
    when (orderState) {
        is SalesOrderListViewState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green)
            }
        }

        is SalesOrderListViewState.Success -> {
            val orders = orderState.orders
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum pedido registrado",
                        color = DarkBlue,
                        fontSize = 18.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBlue)
                        .padding(paddingValues)
                ) {
                    items(orders) { order ->
                        OrderItemList(order, navController) {
                            viewModel.deleteOrder(orderId = order.id)
                        }
                    }
                }
            }
        }

        is SalesOrderListViewState.Error -> {
            val errorMessage = orderState.message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Erro: $errorMessage", color = MaterialTheme.colorScheme.error)
            }
        }

        is SalesOrderListViewState.Deleted -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("Pedido deletado com sucesso.")
                viewModel.getOrders()
            }
        }

        else -> Unit
    }
}

@Composable
fun HandleOrderDeleteState(
    deleteState: SalesDeleteViewState,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is SalesDeleteViewState.Success -> {
                snackbarHostState.showSnackbar("Pedido excluído com sucesso")

            }

            is SalesDeleteViewState.Error -> {
                snackbarHostState.showSnackbar("Erro ao excluir item")
            }

            else -> Unit
        }
    }
}
