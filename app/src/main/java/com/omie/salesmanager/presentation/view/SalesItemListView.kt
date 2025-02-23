package com.omie.salesmanager.presentation.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.omie.salesmanager.R
import com.omie.salesmanager.components.*
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.enum.SalesScreenEnum
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesItemListViewState
import com.omie.salesmanager.presentation.viewmodel.SalesItemListViewModel
import com.omie.salesmanager.ui.theme.Dimens
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SalesItemListView(
    navController: NavController,
    orderId: String,
    snackbarHostState: SnackbarHostState,
    viewModel: SalesItemListViewModel = koinViewModel()
) {
    val itemState by viewModel.itemState.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    val addItemText = stringResource(R.string.sales_item_add_description_button)

    LaunchedEffect(Unit) {
        viewModel.resetDeleteState()
        viewModel.getItems(orderId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HandleItemListState(
            itemState = itemState,
            paddingValues = PaddingValues(Dimens.Paddings.none),
            orderId = orderId,
            viewModel = viewModel,
        )

        val orderIdKey = stringResource(R.string.sales_order_id_key)

        SalesFloatingActionButton(
            onClick = {
                navController.navigate(
                    SalesScreenEnum.getRoute(
                        SalesScreenEnum.SalesItemAddView,
                        params = mapOf(orderIdKey to orderId)
                    )
                )
            },
            contentDescription = addItemText,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimens.Paddings.medium),
        )
    }

    HandleItemDeleteState(deleteState, snackbarHostState)
}

@Composable
fun ItemList(
    items: List<SalesItemModel>,
    orderId: String,
    viewModel: SalesItemListViewModel,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(items) { item ->
            ItemListItem(item) {
                viewModel.deleteItem(orderId, item.id)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemListItem(item: SalesItemModel, onDelete: (SalesItemModel) -> Unit) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    var showDialog by remember { mutableStateOf(false) }

    val quantityLabel = stringResource(R.string.sales_item_quantity_label)
    val unitPriceLabel = stringResource(R.string.sales_item_unit_price_label)
    val totalPriceLabel = stringResource(R.string.sales_item_total_price_label)

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterStart)
        ) {
            SalesListItemText(
                text = item.productName,
                fontSize = Dimens.Fonts.medium,
                textStyle = MaterialTheme.typography.titleMedium,
                color = White,
                spacerHeight = Dimens.spacerHeightMedium,
                spacerAfter = true
            )
            SalesListItemText(
                text = "$quantityLabel ${item.quantity}",
                color = White,
                spacerHeight = Dimens.spacerHeightSmall,
                spacerAfter = false
            )
            SalesListItemText(
                text = "$unitPriceLabel ${currencyFormatter.format(item.value)}",
                color = White,
                spacerHeight = Dimens.spacerHeightSmall,
                spacerAfter = false
            )
            SalesListItemText(
                text = "$totalPriceLabel ${currencyFormatter.format(item.totalValue)}",
                color = White,
                spacerHeight = Dimens.spacerHeightMedium,
                spacerBefore = false
            )
        }

        SalesDeleteIconButton(Modifier.align(Alignment.CenterEnd)) { showDialog = true }
    }

    HorizontalDivider(
        thickness = Dimens.dividerThickness, color = White.copy(alpha = 0.5f)
    )

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
    itemState: SalesItemListViewState,
    paddingValues: PaddingValues,
    orderId: String,
    viewModel: SalesItemListViewModel,
) {
    when (itemState) {
        is SalesItemListViewState.Loading -> SalesLoadingState()
        is SalesItemListViewState.Success -> SuccessItemState(
            items = itemState.items,
            orderId = orderId,
            viewModel = viewModel,
            paddingValues = paddingValues
        )

        is SalesItemListViewState.Error -> ErrorItemState(errorMessage = itemState.message)
        else -> Unit
    }
}

@Composable
fun SuccessItemState(
    items: List<SalesItemModel>,
    orderId: String,
    viewModel: SalesItemListViewModel,
    paddingValues: PaddingValues
) {
    val emptyItemsMessage = stringResource(R.string.sales_item_empty_message)

    if (items.isEmpty()) {
        EmptyStateMessage(message = emptyItemsMessage)
    } else {
        ItemList(
            items = items,
            orderId = orderId,
            viewModel = viewModel,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun ErrorItemState(errorMessage: String) {
    SalesErrorStateMessage(errorMessage = errorMessage)
}

@Composable
fun HandleItemDeleteState(
    deleteState: SalesDeleteViewState,
    snackbarHostState: SnackbarHostState,
) {
    val itemDeletedMessage = stringResource(R.string.sales_item_deleted_message)
    val itemDeleteErrorMessage = stringResource(R.string.sales_item_delete_error_message)

    LaunchedEffect(deleteState) {
        when (deleteState) {
            is SalesDeleteViewState.Success -> {
                snackbarHostState.showSnackbar(itemDeletedMessage)
            }

            is SalesDeleteViewState.Error -> {
                snackbarHostState.showSnackbar(itemDeleteErrorMessage)
            }

            else -> Unit
        }
    }
}
