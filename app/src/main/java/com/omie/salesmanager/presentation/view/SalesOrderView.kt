package com.omie.salesmanager.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.omie.salesmanager.data.model.SalesOrderItemDTO
import com.omie.salesmanager.presentation.viewmodel.SalesOrderViewModel
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import org.koin.androidx.compose.koinViewModel

@Composable
fun SalesOrderView(navController: NavController) {

    val viewModel: SalesOrderViewModel = koinViewModel()

    var productName by remember { mutableStateOf(TextFieldValue("")) }
    var quantity by remember { mutableStateOf(TextFieldValue("")) }
    var value by remember { mutableStateOf(TextFieldValue("")) }

    val orderState by viewModel.orderState.collectAsState()

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header()
        ProductNameInput(productName) { productName = it }
        QuantityInput(quantity) { quantity = it }
        PriceInput(value) { value = it }
        AddProductButton(
            productName = productName,
            quantity = quantity,
            value = value,
            viewModel = viewModel
        )

        HandleOrderState(orderState, context)
    }
}

@Composable
fun Header() {
    Text(
        text = "Cadastro de Pedidos",
        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun ProductNameInput(
    productName: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = productName,
        onValueChange = onValueChange,
        label = { Text("Nome do Produto") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun QuantityInput(
    quantity: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = quantity,
        onValueChange = onValueChange,
        label = { Text("Quantidade") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PriceInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Valor") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun AddProductButton(
    productName: TextFieldValue,
    quantity: TextFieldValue,
    value: TextFieldValue,
    viewModel: SalesOrderViewModel
) {
    val context = LocalContext.current

    Button(
        onClick = {
            if (productName.text.isNotEmpty() && quantity.text.isNotEmpty() && value.text.isNotEmpty()) {
                val orderItem = SalesOrderItemDTO(
                    productName = productName.text,
                    quantity = quantity.text.toInt(),
                    value = value.text.toDouble()
                )

                viewModel.addOrder(orderItem)
            } else {
                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Incluir Produto")
    }
}

@Composable
fun HandleOrderState(
    orderState: SalesOrderViewState,
    context: android.content.Context,
) {
    when (orderState) {
        is SalesOrderViewState.Loading -> {
            Text("Carregando...")
        }

        is SalesOrderViewState.Success -> {
            Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_SHORT)
                .show()
        }

        is SalesOrderViewState.Error -> {
            Toast.makeText(context, orderState.message, Toast.LENGTH_SHORT).show()
        }

        else -> {
            // No active state
        }
    }
}
