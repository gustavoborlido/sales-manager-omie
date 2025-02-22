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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import com.omie.salesmanager.presentation.viewmodel.SalesOrderAddViewModel
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Green
import com.omie.salesmanager.ui.theme.LightBlue
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun SalesOrderAddView(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val viewModel: SalesOrderAddViewModel = koinViewModel()

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
                    OrderDescriptionInput(
                        descriptionOrder,
                        descriptionOrderFocusRequester,
                        clientNameFocusRequester
                    ) { descriptionOrder = it }

                    OrderClientNameInput(clientName, clientNameFocusRequester) {
                        clientName = it
                    }
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
            OrderAddButton(
                descriptionOrder = descriptionOrder,
                clientName = clientName,
                viewModel = viewModel,
                orderState = orderState,
                snackbarHostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        HandleOrderAddState(orderState, navController, snackbarHostState)
    }
}

@Composable
fun OrderDescriptionInput(
    description: TextFieldValue,
    descriptionFocusRequester: FocusRequester,
    clientNameFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = description,
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
                clientNameFocusRequester.requestFocus()
            }
        ),
        label = { Text("Descrição") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(descriptionFocusRequester),
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
fun OrderClientNameInput(
    clientName: TextFieldValue,
    clientNameFocusRequester: FocusRequester,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = clientName,
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
                clientNameFocusRequester.requestFocus()
            }
        ),
        label = { Text("Nome do cliente") },
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(clientNameFocusRequester),
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
fun OrderAddButton(
    descriptionOrder: TextFieldValue,
    clientName: TextFieldValue,
    viewModel: SalesOrderAddViewModel,
    orderState: SalesOrderViewState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Button(
        onClick = {
            if (descriptionOrder.text.isNotEmpty() && clientName.text.isNotEmpty()) {
                val order = SalesOrderModel(
                    id = "",
                    description = descriptionOrder.text,
                    clientName = clientName.text
                )
                viewModel.addOrder(order)
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
            Text("AVANÇAR")
        }
    }

    errorMessage?.let {
        LaunchedEffect(it) {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            errorMessage = null
        }
    }
}


@Composable
fun HandleOrderAddState(
    orderState: SalesOrderViewState,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(orderState) {
        when (orderState) {
            is SalesOrderViewState.Success -> {
                navController.navigate("SalesItemListView/${orderState.orderId}") {
                    popUpTo("SalesOrderAddView") { inclusive = true }
                }
            }

            is SalesOrderViewState.Error -> {
                snackbarHostState.showSnackbar(
                    orderState.message,
                    duration = SnackbarDuration.Short
                )
            }

            else -> Unit
        }
    }
}
