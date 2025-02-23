package com.omie.salesmanager.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.omie.salesmanager.R
import com.omie.salesmanager.components.SalesClearIconButton
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.enum.SalesScreenEnum
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import com.omie.salesmanager.presentation.viewmodel.SalesOrderAddViewModel
import com.omie.salesmanager.ui.theme.White
import com.omie.salesmanager.ui.theme.Black
import com.omie.salesmanager.ui.theme.Dimens
import org.koin.androidx.compose.koinViewModel

private const val MAX_TEXT_LENGTH = 30

@Composable
fun SalesOrderAddView(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: SalesOrderAddViewModel = koinViewModel()
) {
    var descriptionOrder by remember { mutableStateOf(TextFieldValue("")) }
    var clientName by remember { mutableStateOf(TextFieldValue("")) }

    val orderState by viewModel.orderState.collectAsState()

    val descriptionOrderFocusRequester = remember { FocusRequester() }
    val clientNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        descriptionOrderFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.Paddings.medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.Paddings.medium)
        ) {
            OrderDescriptionInput(
                descriptionOrder,
                descriptionOrderFocusRequester,
                clientNameFocusRequester
            ) { descriptionOrder = it }

            OrderClientNameInput(clientName, clientNameFocusRequester) {
                clientName = it
            }
        }

        val keyboardInsets = WindowInsets.ime.asPaddingValues()

        OrderAddButton(
            descriptionOrder = descriptionOrder,
            clientName = clientName,
            viewModel = viewModel,
            orderState = orderState,
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(keyboardInsets)
        )

        HandleOrderAddState(orderState, navController, snackbarHostState)
    }
}

@Composable
fun HandleOrderAddState(
    orderState: SalesOrderViewState,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val orderIdKey = stringResource(R.string.sales_order_id_key)

    when (orderState) {
        is SalesOrderViewState.Success -> {
            SuccessOrderAddState(
                orderId = orderState.orderId,
                navController = navController,
                orderIdKey = orderIdKey
            )
        }

        is SalesOrderViewState.Error -> {
            ErrorOrderAddState(
                errorMessage = orderState.message,
                snackbarHostState = snackbarHostState
            )
        }

        else -> Unit
    }
}

@Composable
fun SuccessOrderAddState(orderId: String, navController: NavController, orderIdKey: String) {
    LaunchedEffect(orderId) {
        navController.navigate(
            SalesScreenEnum.getRoute(
                SalesScreenEnum.SalesItemListView,
                params = mapOf(orderIdKey to orderId)
            )
        ) {
            popUpTo(SalesScreenEnum.SalesOrderAddView.route) { inclusive = true }
        }
    }
}

@Composable
fun ErrorOrderAddState(errorMessage: String, snackbarHostState: SnackbarHostState) {
    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Short)
    }
}

@Composable
fun OrderDescriptionInput(
    description: TextFieldValue,
    descriptionFocusRequester: FocusRequester,
    clientNameFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    TextField(
        value = description,
        singleLine = true,
        onValueChange = {
            if (it.text.length <= MAX_TEXT_LENGTH) {
                onValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                clientNameFocusRequester.requestFocus()
            }
        ),
        label = { Text(stringResource(R.string.sales_order_description_label), color = White) },
        colors = OutlinedTextFieldDefaults.colors(),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(descriptionFocusRequester),
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (description.text.isNotEmpty()) {
                SalesClearIconButton(onClick = { onValueChange(TextFieldValue("")) })
            }
        }
    )
}

@Composable
fun OrderClientNameInput(
    clientName: TextFieldValue,
    clientNameFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = clientName,
        singleLine = true,
        onValueChange = {
            if (it.text.length <= MAX_TEXT_LENGTH) {
                onValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        label = { Text(stringResource(R.string.sales_order_client_add_label), color = White) },
        colors = OutlinedTextFieldDefaults.colors(),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(clientNameFocusRequester),
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (clientName.text.isNotEmpty()) {
                SalesClearIconButton(onClick = { onValueChange(TextFieldValue("")) })
            }
        }
    )
}

@Composable
fun OrderAddButton(
    descriptionOrder: TextFieldValue,
    clientName: TextFieldValue,
    viewModel: SalesOrderAddViewModel,
    orderState: SalesOrderViewState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fillAllFieldsMessage = stringResource(R.string.sales_order_fill_all_fields_message)

    Button(
        onClick = {
            keyboardController?.hide()
            if (descriptionOrder.text.isNotEmpty() && clientName.text.isNotEmpty()) {
                val order = SalesOrderModel(
                    id = "",
                    description = descriptionOrder.text,
                    clientName = clientName.text
                )
                viewModel.addOrder(order)
            } else {
                errorMessage = fillAllFieldsMessage
            }
        },
        modifier = modifier
            .padding(Dimens.Paddings.medium),
        enabled = orderState !is SalesOrderViewState.Loading,
        colors = ButtonDefaults.buttonColors(
            contentColor = Black,
        ),
        shape = RectangleShape
    ) {
        if (orderState is SalesOrderViewState.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(Dimens.Size.large))
        } else {
            Text(stringResource(R.string.sales_order_next_button))
        }
    }

    errorMessage?.let {
        LaunchedEffect(it) {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            errorMessage = null
        }
    }
}

