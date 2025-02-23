package com.omie.salesmanager.presentation.view

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.omie.salesmanager.R
import com.omie.salesmanager.components.SalesClearIconButton
import com.omie.salesmanager.enum.SalesScreenEnum
import com.omie.salesmanager.presentation.state.SalesAuthViewState
import com.omie.salesmanager.presentation.viewmodel.SalesAuthViewModel
import com.omie.salesmanager.ui.theme.Black
import com.omie.salesmanager.ui.theme.DarkBlue
import com.omie.salesmanager.ui.theme.Dimens
import com.omie.salesmanager.ui.theme.Gray
import com.omie.salesmanager.ui.theme.White
import org.koin.androidx.compose.koinViewModel

@Composable
fun SalesAuthView(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: SalesAuthViewModel = koinViewModel()
) {
    val loginState by viewModel.loginState.collectAsState()

    val passwordFocusRequester = remember { FocusRequester() }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val data: Intent? = result.data
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account.idToken?.let { token ->
                viewModel.signInWithGoogle(token)
            }
        } catch (e: ApiException) {
            errorMessage = context.getString(R.string.sales_auth_google_error_message, e.message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.Paddings.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = DarkBlue)
        ) {
            Column(
                modifier = Modifier.padding(Dimens.Paddings.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoginHeader()
                Spacer(modifier = Modifier.height(Dimens.spacerHeightLarge))
                EmailInput(viewModel, passwordFocusRequester = passwordFocusRequester)
                Spacer(modifier = Modifier.height(Dimens.spacerHeightMedium))
                PasswordInput(viewModel, passwordFocusRequester)
                Spacer(modifier = Modifier.height(Dimens.spacerHeightLarge))
                LoginButton(loginState, viewModel)
                Spacer(modifier = Modifier.height(Dimens.spacerHeightMedium))
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Dimens.Paddings.small),
                    thickness = 1.dp,
                    color = Gray
                )
                GoogleSignInButton {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail().build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }
            }
        }
    }

    errorMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message, duration = SnackbarDuration.Short
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
        text = stringResource(R.string.sales_auth_welcome),
        style = MaterialTheme.typography.headlineSmall,
        color = White,
    )
}

@Composable
fun EmailInput(viewModel: SalesAuthViewModel, passwordFocusRequester: FocusRequester) {
    TextField(value = viewModel.email,
        onValueChange = { viewModel.email = it },
        label = { Text(stringResource(R.string.sales_auth_email_label), color = White) },
        keyboardActions = KeyboardActions(
            onDone = {
                passwordFocusRequester.requestFocus()
            }
        ),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(),
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (viewModel.email.isNotEmpty()) {
                SalesClearIconButton(onClick = { viewModel.email = "" })
            }
        })
}

@Composable
fun PasswordInput(viewModel: SalesAuthViewModel, passwordFocusRequester: FocusRequester) {

    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(value = viewModel.password,
        onValueChange = { viewModel.password = it },
        label = { Text(stringResource(R.string.sales_auth_password_label), color = White) },
        visualTransformation = PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(passwordFocusRequester),
        colors = OutlinedTextFieldDefaults.colors(),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(color = White),
        trailingIcon = {
            if (viewModel.password.isNotEmpty()) {
                SalesClearIconButton(onClick = { viewModel.password = "" })
            }
        })
}

@Composable
fun LoginButton(loginState: SalesAuthViewState, viewModel: SalesAuthViewModel) {

    val keyboardController = LocalSoftwareKeyboardController.current

    Button(
        onClick = {
            keyboardController?.hide()
            viewModel.login()
        },
        modifier = Modifier.fillMaxWidth(),
        enabled = loginState !is SalesAuthViewState.Loading,
        colors = ButtonDefaults.buttonColors(
            contentColor = Black,
        ),
        shape = RectangleShape
    ) {
        if (loginState is SalesAuthViewState.Loading) {
            CircularProgressIndicator(modifier = Modifier.size(Dimens.Size.large))
        } else {
            Text(stringResource(R.string.sales_auth_login_button))
        }
    }
}

@Composable
fun HandleLoginState(
    loginState: SalesAuthViewState, navController: NavController, onError: (String) -> Unit
) {
    LaunchedEffect(loginState) {
        when (loginState) {
            is SalesAuthViewState.Success -> {
                navController.navigate(SalesScreenEnum.SalesOrderListView.route) {
                    popUpTo(SalesScreenEnum.SalesAuthView.route) { inclusive = true }
                }
            }

            is SalesAuthViewState.Error -> {
                onError(loginState.message)
            }

            else -> Unit
        }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
            contentColor = Black,
        ), shape = RectangleShape
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_logo),
                contentDescription = stringResource(R.string.sales_auth_google_description_icon),
                modifier = Modifier.size(Dimens.Size.large)
            )
            Spacer(modifier = Modifier.width(Dimens.spacerHeightMedium))
            Text(stringResource(R.string.sales_auth_google_button))
        }
    }
}
