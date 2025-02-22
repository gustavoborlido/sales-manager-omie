package com.omie.salesmanager.presentation.view

import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.omie.salesmanager.presentation.state.SalesAuthViewState
import com.omie.salesmanager.presentation.viewmodel.SalesAuthViewModel
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Gray
import com.omie.salesmanager.ui.theme.Green
import com.omie.salesmanager.ui.theme.LightBlue
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun SalesAuthView(
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val viewModel: SalesAuthViewModel = koinViewModel()
    val loginState by viewModel.loginState.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBlue),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = DarkBlue)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoginHeader()
                Spacer(modifier = Modifier.height(20.dp))
                EmailField(viewModel)
                Spacer(modifier = Modifier.height(10.dp))
                PasswordField(viewModel)
                Spacer(modifier = Modifier.height(20.dp))
                LoginButton(loginState, viewModel)
            }
        }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            errorMessage = null
        }
    }

    HandleLoginState(loginState, navController, onError = { message ->
        errorMessage = message
    })
}

@Composable
fun LoginHeader() {
    Text(
        text = "Bem-vindo",
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White
    )
}

@Composable
fun EmailField(viewModel: SalesAuthViewModel) {
    OutlinedTextField(
        value = viewModel.email,
        onValueChange = { viewModel.email = it },
        label = { Text("E-mail", color = Color.White) },
        modifier = Modifier.fillMaxWidth(),
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
fun PasswordField(viewModel: SalesAuthViewModel) {
    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { viewModel.password = it },
        label = { Text("Senha", color = Color.White) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
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
fun LoginButton(loginState: SalesAuthViewState, viewModel: SalesAuthViewModel) {
    Button(
        onClick = { viewModel.login() },
        modifier = Modifier.fillMaxWidth(),
        enabled = loginState !is SalesAuthViewState.Loading,
        colors = ButtonDefaults.buttonColors(
            contentColor = DarkBlue,
            containerColor = Green
        )
    ) {
        if (loginState is SalesAuthViewState.Loading) {
            CircularProgressIndicator(color = Green, modifier = Modifier.size(24.dp))
        } else {
            Text("Entrar")
        }
    }
}

@Composable
fun HandleLoginState(
    loginState: SalesAuthViewState,
    navController: NavController,
    onError: (String) -> Unit
) {
    LaunchedEffect(loginState) {
        when (loginState) {
            is SalesAuthViewState.Success -> {
                navController.navigate("SalesOrderListView") {
                    popUpTo("SalesAuthView") { inclusive = true }
                }
            }

            is SalesAuthViewState.Error -> {
                onError(loginState.message)
            }

            else -> Unit
        }
    }
}