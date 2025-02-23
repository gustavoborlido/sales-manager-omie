package com.omie.salesmanager.presentation.view

import CurrencyUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.omie.salesmanager.R
import com.omie.salesmanager.components.EmptyStateMessage
import com.omie.salesmanager.components.SalesLoadingState
import com.omie.salesmanager.components.SalesDeleteDialog
import com.omie.salesmanager.components.SalesErrorStateMessage
import com.omie.salesmanager.components.SalesFloatingActionButton
import com.omie.salesmanager.components.SalesListItemText
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.enum.SalesScreenEnum
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesOrderListViewState
import com.omie.salesmanager.presentation.viewmodel.SalesOrderListViewModel
import com.omie.salesmanager.ui.theme.Dimens
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun SalesOrderListView(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: SalesOrderListViewModel = koinViewModel()
) {
    val orderListViewState by viewModel.orderState.collectAsState()
    val deleteViewState by viewModel.deleteState.collectAsState()
    val createOrderText = stringResource(R.string.sales_order_create_button)

    LaunchedEffect(Unit) {
        viewModel.resetDeleteState()
        viewModel.getOrders()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HandleOrderListState(
            orderState = orderListViewState,
            paddingValues = PaddingValues(Dimens.Paddings.none),
            navController = navController,
            viewModel = viewModel,
        )

        SalesFloatingActionButton(
            onClick = { navController.navigate(SalesScreenEnum.SalesOrderAddView.route) },
            contentDescription = createOrderText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.Paddings.medium),
        )
    }

    HandleOrderDeleteState(deleteViewState, snackbarHostState)
}

@Composable
fun OrderList(
    orders: List<SalesOrderModel>,
    navController: NavController,
    viewModel: SalesOrderListViewModel,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(orders) { order ->
            OrderItemList(order, navController) {
                viewModel.deleteOrder(orderId = order.id)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderItemList(
    order: SalesOrderModel,
    navController: NavController,
    onDelete: (SalesOrderModel) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val clientLabel = stringResource(R.string.sales_order_client_label)
    val totalValueLabel = stringResource(R.string.sales_order_total_value_label)

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { navController.navigate("SalesItemListView/${order.id}") },
                    onLongClick = { showDialog = true }
                )
                .align(Alignment.CenterStart)
        ) {
            SalesListItemText(
                text = order.description,
                fontSize = Dimens.Fonts.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                color = White,
                spacerHeight = Dimens.spacerHeightMedium,
                spacerAfter = true
            )
            SalesListItemText(
                text = "$clientLabel ${order.clientName}",
                color = White,
                spacerHeight = Dimens.spacerHeightSmall,
                spacerAfter = false
            )
            SalesListItemText(
                text = "$totalValueLabel ${CurrencyUtils.formatCurrency(order.totalOrderValue)}",
                color = White,
                spacerHeight = Dimens.spacerHeightMedium,
                spacerBefore = false
            )
        }
    }

    HorizontalDivider(
        thickness = Dimens.dividerThickness,
        color = White.copy(alpha = 0.5f)
    )

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
    viewModel: SalesOrderListViewModel
) {
    when (orderState) {
        is SalesOrderListViewState.Loading -> SalesLoadingState()
        is SalesOrderListViewState.Success -> SuccessState(
            orders = orderState.orders,
            navController = navController,
            viewModel = viewModel,
            paddingValues = paddingValues
        )

        is SalesOrderListViewState.Error -> ErrorState(errorMessage = orderState.message)
        else -> Unit
    }
}

@Composable
fun SuccessState(
    orders: List<SalesOrderModel>,
    navController: NavController,
    viewModel: SalesOrderListViewModel,
    paddingValues: PaddingValues
) {
    val emptyOrdersMessage = stringResource(R.string.sales_order_empty_message)

    if (orders.isEmpty()) {
        EmptyStateMessage(message = emptyOrdersMessage)
    } else {
        OrderList(
            orders = orders,
            navController = navController,
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun ErrorState(errorMessage: String) {
    SalesErrorStateMessage(errorMessage = errorMessage)
}

@Composable
fun HandleOrderDeleteState(
    deleteState: SalesDeleteViewState,
    snackbarHostState: SnackbarHostState,
) {
    val orderDeletedMessage = stringResource(R.string.sales_order_deleted_message)
    val orderDeleteErrorMessage = stringResource(R.string.sales_order_delete_error_message)

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is SalesDeleteViewState.Success -> {
                snackbarHostState.showSnackbar(orderDeletedMessage)
            }

            is SalesDeleteViewState.Error -> {
                snackbarHostState.showSnackbar(orderDeleteErrorMessage)
            }

            else -> Unit
        }
    }
}

