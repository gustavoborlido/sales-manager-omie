package com.omie.salesmanager.presentation.view

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.omie.salesmanager.presentation.state.SalesLoginViewState
import com.omie.salesmanager.presentation.viewmodel.SalesLoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SalesAuthView(navController: NavController) {
    val viewModel: SalesLoginViewModel = koinViewModel()
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
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

    HandleLoginState(loginState, context, navController)
}

@Composable
fun LoginHeader() {
    Text(
        text = "Login",
        style = MaterialTheme.typography.headlineLarge
    )
}

@Composable
fun EmailField(viewModel: SalesLoginViewModel) {
    OutlinedTextField(
        value = viewModel.email,
        onValueChange = { viewModel.email = it },
        label = { Text("E-mail") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordField(viewModel: SalesLoginViewModel) {
    OutlinedTextField(
        value = viewModel.password,
        onValueChange = { viewModel.password = it },
        label = { Text("Senha") },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LoginButton(loginState: SalesLoginViewState, viewModel: SalesLoginViewModel) {
    Button(
        onClick = { viewModel.login() },
        modifier = Modifier.fillMaxWidth(),
        enabled = loginState !is SalesLoginViewState.Loading
    ) {
        if (loginState is SalesLoginViewState.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text("Entrar")
        }
    }
}

@Composable
fun HandleLoginState(
    loginState: SalesLoginViewState,
    context: android.content.Context,
    navController: NavController
) {
    LaunchedEffect(loginState) {
        when (loginState) {
            is SalesLoginViewState.Success -> {
                Toast.makeText(context, "Login com sucesso", Toast.LENGTH_SHORT).show()
                navController.navigate("SalesOrderView"){
                    popUpTo("SalesAuthView") { inclusive = true }
                }
            }
            is SalesLoginViewState.Error -> {
                Toast.makeText(context, loginState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
}
