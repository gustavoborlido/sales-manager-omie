package com.omie.salesmanager.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.omie.salesmanager.ui.theme.Gray
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.presentation.viewmodel.SalesItemAddViewModel
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Green
import com.omie.salesmanager.ui.theme.LightBlue
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesItemAddView(
    navController: NavController,
    orderId: String,
    snackbarHostState: SnackbarHostState
) {
    val viewModel: SalesItemAddViewModel = koinViewModel()

    var productName by remember { mutableStateOf(TextFieldValue("")) }
    var quantity by remember { mutableStateOf(TextFieldValue("")) }
    var value by remember { mutableStateOf(TextFieldValue("")) }

    val orderState by viewModel.orderState.collectAsState()

    val context = LocalContext.current

    val productNameFocusRequester = remember { FocusRequester() }
    val quantityFocusRequester = remember { FocusRequester() }
    val valueFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        productNameFocusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = DarkBlue)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    PriceInput(value, valueFocusRequester) { value = it }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(DarkBlue)
                .align(Alignment.BottomCenter)
        ) {
            AddProductButton(
                orderId = orderId,
                productName = productName,
                quantity = quantity,
                value = value,
                viewModel = viewModel,
                orderState = orderState,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        HandleItemAddState(orderState, navController, snackbarHostState)
    }
}

@Composable
fun ProductNameInput(
    productName: TextFieldValue,
    productNameFocusRequester: FocusRequester,
    quantityFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = productName,
        singleLine = true,
        onValueChange = {
            if (it.text.length <= 30) {
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
        label = { Text("Nome do Produto") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(productNameFocusRequester),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LightBlue,
            focusedLabelColor = White,
            unfocusedLabelColor = Gray,
            unfocusedBorderColor = Gray,
            cursorColor = LightBlue
        ),
        textStyle = TextStyle(color = White)
    )
}

@Composable
fun QuantityInput(
    quantity: TextFieldValue,
    quantityFocusRequester: FocusRequester,
    valueFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = quantity,
        onValueChange = {
            if (it.text.isEmpty() || (it.text.toIntOrNull() != null && it.text.length <= 4)) {
                onValueChange(it)
            }
        },
        label = { Text("Quantidade") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                valueFocusRequester.requestFocus()
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(quantityFocusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    valueFocusRequester.requestFocus()
                    true
                } else {
                    false
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LightBlue,
            focusedLabelColor = White,
            unfocusedLabelColor = Gray,
            unfocusedBorderColor = Gray,
            cursorColor = LightBlue
        ),
        textStyle = TextStyle(color = White)
    )
}

@Composable
fun PriceInput(
    value: TextFieldValue,
    valueFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value,
        onValueChange = {
            val regex = "^\\d{0,6}(\\.\\d{0,2})?$".toRegex()
            if (it.text.isEmpty() || it.text.matches(regex)) {
                onValueChange(it)
            }
        },
        label = { Text("Valor") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(valueFocusRequester)
            .onKeyEvent {
                if (it.key == Key.Enter) {
                    keyboardController?.hide()
                    true
                } else {
                    false
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LightBlue,
            focusedLabelColor = White,
            unfocusedLabelColor = Gray,
            unfocusedBorderColor = Gray,
            cursorColor = LightBlue
        ),
        textStyle = TextStyle(color = White)
    )
}

@Composable
fun AddProductButton(
    orderId: String,
    productName: TextFieldValue,
    quantity: TextFieldValue,
    value: TextFieldValue,
    viewModel: SalesItemAddViewModel,
    orderState: SalesOrderViewState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Button(
        onClick = {
            if (productName.text.isNotEmpty() && quantity.text.isNotEmpty() && value.text.isNotEmpty()) {
                val item = SalesItemModel(
                    id = "",
                    productName = productName.text,
                    quantity = quantity.text.toInt(),
                    value = value.text.toDouble()
                )

                viewModel.addItem(item, orderId)
            } else {
                errorMessage = "Preencha todos os campos"
            }
        },
        modifier = modifier,
        enabled = orderState !is SalesOrderViewState.Loading,
        colors = ButtonDefaults.buttonColors(
            contentColor = DarkBlue,
            containerColor = Green
        )
    ) {
        if (orderState is SalesOrderViewState.Loading) {
            CircularProgressIndicator(color = Green, modifier = Modifier.size(24.dp))
        } else {
            Text("CADASTRAR")
        }

        errorMessage?.let {
            LaunchedEffect(it) {
                snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
                errorMessage = null
            }
        }
    }
}

@Composable
fun HandleItemAddState(
    orderState: SalesOrderViewState,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(orderState) {
        when (orderState) {
            is SalesOrderViewState.Success -> {
                navController.popBackStack()
            }

            is SalesOrderViewState.Error -> {
                snackbarHostState.showSnackbar(
                    message = orderState.message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }
    }
}
