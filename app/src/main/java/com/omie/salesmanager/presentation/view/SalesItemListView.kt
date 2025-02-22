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
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesItemListViewState
import com.omie.salesmanager.presentation.viewmodel.SalesItemListViewModel
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Green
import com.omie.salesmanager.ui.theme.LightBlue
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalesItemListView(
    navController: NavController,
    orderId: String,
    snackbarHostState: SnackbarHostState
) {
    val viewModel: SalesItemListViewModel = koinViewModel()
    val itemState by viewModel.itemState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.resetDeleteState()
        viewModel.getItems(orderId)
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
            HandleItemListState(
                orderState = itemState,
                paddingValues = PaddingValues(0.dp),
                orderId = orderId,
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
            )
        }

        FloatingActionButton(
            onClick = { navController.navigate("SalesItemAddView/$orderId") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Green
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Adicionar Item",
                tint = DarkBlue
            )
        }
    }
    HandleItemDeleteState(deleteState, snackbarHostState)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemList(item: SalesItemModel, onDelete: (SalesItemModel) -> Unit) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            .combinedClickable(
                onClick = { /*Do nothing */ },
                onLongClick = { showDialog = true }
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.productName,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                color = White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Quantidade: ${item.quantity}", color = White)
            Text(text = "Valor Unitário: ${currencyFormatter.format(item.value)}", color = White)
            Text(text = "Valor Total: ${currencyFormatter.format(item.totalValue)}", color = White)
        }
    }

    if (showDialog) {
        SalesDeleteDialog(
            onConfirm = {
                onDelete(item)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun HandleItemListState(
    orderState: SalesItemListViewState,
    paddingValues: PaddingValues,
    orderId: String,
    viewModel: SalesItemListViewModel,
    snackbarHostState: SnackbarHostState
) {
    when (orderState) {
        is SalesItemListViewState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBlue),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Green)
            }
        }

        is SalesItemListViewState.Success -> {
            val items = orderState.items
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ainda não há itens neste pedido. Toque no botão para adicionar um!",
                        fontSize = 18.sp,
                        color = DarkBlue
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightBlue)
                        .padding(paddingValues)
                ) {
                    items(items) { item ->
                        ItemList(item) {
                            viewModel.deleteItem(orderId, item.id)
                        }
                    }
                }
            }
        }

        is SalesItemListViewState.Error -> {
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

        else -> Unit
    }
}

@Composable
fun HandleItemDeleteState(
    deleteState: SalesDeleteViewState,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is SalesDeleteViewState.Success -> {
                snackbarHostState.showSnackbar("Item excluído com sucesso")

            }

            is SalesDeleteViewState.Error -> {
                snackbarHostState.showSnackbar("Erro ao excluir item")
            }

            else -> Unit
        }
    }
}
