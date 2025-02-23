package com.omie.salesmanager.presentation.view

import CurrencyUtils.createCurrencyVisualTransformation
import CurrencyUtils.formatCurrency
import CurrencyUtils.parseCurrencyToDouble
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.omie.salesmanager.R
import com.omie.salesmanager.components.SalesClearIconButton
import com.omie.salesmanager.components.SalesLoadingState
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import com.omie.salesmanager.presentation.viewmodel.SalesItemAddViewModel
import com.omie.salesmanager.ui.theme.Black
import com.omie.salesmanager.ui.theme.White
import com.omie.salesmanager.ui.theme.Dimens
import org.koin.androidx.compose.koinViewModel

private const val MAX_TEXT_LENGTH = 30
private const val MAX_QUANTITY_LENGTH = 4

@Composable
fun SalesItemAddView(
    navController: NavController,
    orderId: String,
    snackbarHostState: SnackbarHostState,
    viewModel: SalesItemAddViewModel = koinViewModel()
) {
    var productName by remember { mutableStateOf(TextFieldValue("")) }
    var quantity by remember { mutableStateOf(TextFieldValue("")) }
    var value by remember { mutableStateOf(TextFieldValue("")) }
    var price by remember { mutableDoubleStateOf(0.0) }

    val orderState by viewModel.orderState.collectAsState()

    val productNameFocusRequester = remember { FocusRequester() }
    val quantityFocusRequester = remember { FocusRequester() }
    val valueFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        productNameFocusRequester.requestFocus()
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
            ProductNameInput(
                productName,
                productNameFocusRequester,
                quantityFocusRequester
            ) { productName = it }

            QuantityInput(
                quantity,
                quantityFocusRequester,
                valueFocusRequester
            ) { quantity = it }

            PriceInput(value, valueFocusRequester) { newValue, newPrice ->
                value = newValue
                price = newPrice
            }
        }

        val keyboardInsets = WindowInsets.ime.asPaddingValues()

        AddProductButton(
            orderId = orderId,
            productName = productName,
            quantity = quantity,
            value = value,
            price = price,
            viewModel = viewModel,
            orderState = orderState,
            snackbarHostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(keyboardInsets)
        )

        HandleItemAddState(orderState, navController, snackbarHostState)
    }
}

@Composable
fun HandleItemAddState(
    orderState: SalesOrderViewState,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    when (orderState) {
        is SalesOrderViewState.Loading -> SalesLoadingState()
        is SalesOrderViewState.Success -> {
            SuccessItemAddState(navController)
        }

        is SalesOrderViewState.Error -> {
            ErrorItemAddState(
                errorMessage = orderState.message,
                snackbarHostState = snackbarHostState
            )
        }

        else -> Unit
    }
}

@Composable
fun SuccessItemAddState(navController: NavController) {
    LaunchedEffect(Unit) {
        navController.popBackStack()
    }
}

@Composable
fun ErrorItemAddState(errorMessage: String, snackbarHostState: SnackbarHostState) {
    LaunchedEffect(errorMessage) {
        snackbarHostState.showSnackbar(errorMessage, duration = SnackbarDuration.Short)
    }
}

@Composable
fun ProductNameInput(
    productName: TextFieldValue,
    productNameFocusRequester: FocusRequester,
    quantityFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    TextField(
        value = productName,
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
                quantityFocusRequester.requestFocus()
            }
        ),
        label = { Text(stringResource(R.string.sales_item_product_name_label), color = White) },
        colors = OutlinedTextFieldDefaults.colors(),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(productNameFocusRequester),
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (productName.text.isNotEmpty()) {
                SalesClearIconButton(onClick = { onValueChange(TextFieldValue("")) })
            }
        }
    )
}

@Composable
fun QuantityInput(
    quantity: TextFieldValue,
    quantityFocusRequester: FocusRequester,
    valueFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    TextField(
        value = quantity,
        singleLine = true,
        onValueChange = {
            if (it.text.isEmpty() || (it.text.toIntOrNull() != null && it.text.length <= MAX_QUANTITY_LENGTH)) {
                onValueChange(it)
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                valueFocusRequester.requestFocus()
            }
        ),
        label = { Text(stringResource(R.string.sales_item_quantity_add_label), color = White) },
        colors = OutlinedTextFieldDefaults.colors(),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(quantityFocusRequester),
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (quantity.text.isNotEmpty()) {
                SalesClearIconButton(onClick = { onValueChange(TextFieldValue("")) })
            }
        }
    )
}

@Composable
fun PriceInput(
    value: TextFieldValue,
    valueFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue, Double) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = value,
        singleLine = true,
        onValueChange = { newValue ->
            val numericValue = parseCurrencyToDouble(newValue.text)
            val formattedText = formatCurrency(numericValue)

            val updatedValue = TextFieldValue(
                text = formattedText,
                selection = TextRange(formattedText.length)
            )

            onValueChange(updatedValue, numericValue)
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        ),
        label = { Text(stringResource(R.string.sales_item_price_label), color = White) },
        colors = OutlinedTextFieldDefaults.colors(),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(valueFocusRequester),
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                SalesClearIconButton(onClick = { onValueChange(TextFieldValue(""), 0.0) })
            }
        },
        visualTransformation = createCurrencyVisualTransformation()
    )
}

@Composable
fun AddProductButton(
    orderId: String,
    productName: TextFieldValue,
    quantity: TextFieldValue,
    value: TextFieldValue,
    price: Double,
    viewModel: SalesItemAddViewModel,
    orderState: SalesOrderViewState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fillAllFieldsMessage = stringResource(R.string.sales_item_fill_all_fields_message)

    Button(
        onClick = {
            keyboardController?.hide()
            if (productName.text.isNotEmpty() && quantity.text.isNotEmpty() && value.text.isNotEmpty()) {
                val item = SalesItemModel(
                    id = "",
                    productName = productName.text,
                    quantity = quantity.text.toInt(),
                    value = price
                )
                viewModel.addItem(item, orderId)
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
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text(stringResource(R.string.sales_item_add_button))
        }
    }

    errorMessage?.let {
        LaunchedEffect(it) {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            errorMessage = null
        }
    }
}
