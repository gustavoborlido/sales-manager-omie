package com.omie.salesmanager.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesOrderAddView(navController: NavController) {
    val viewModel: SalesOrderAddViewModel = koinViewModel()

    var descriptionOrder by remember { mutableStateOf(TextFieldValue("")) }
    var clientName by remember { mutableStateOf(TextFieldValue("")) }

    val orderState by viewModel.orderState.collectAsState()

    val context = LocalContext.current

    val descriptionOrderFocusRequester = remember { FocusRequester() }
    val clientNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        descriptionOrderFocusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Novo Pedido de Venda",
                        color = White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBlue
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            HandleOrderAddState(orderState, context, navController)
        }
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (descriptionOrder.text.isNotEmpty() && clientName.text.isNotEmpty()) {

                val order = SalesOrderModel(
                    description = descriptionOrder.text,
                    clientName = clientName.text
                )
                viewModel.addOrder(order)
            } else {
                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
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
            Text("Avançar")
        }
    }
}

@Composable
fun HandleOrderAddState(
    orderState: SalesOrderViewState,
    context: android.content.Context,
    navController: NavController
) {
    LaunchedEffect(orderState) {
        when (orderState) {
            is SalesOrderViewState.Success -> {
                navController.navigate("SalesItemListView/{${orderState.orderId}}"){
                    popUpTo("SalesOrderAddView") { inclusive = true }
                }
            }

            is SalesOrderViewState.Error -> {
                Toast.makeText(context, orderState.message, Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }
    }
}
