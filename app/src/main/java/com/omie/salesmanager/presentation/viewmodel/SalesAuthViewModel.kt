package com.omie.salesmanager.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import com.omie.salesmanager.presentation.state.SalesAuthViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException

class SalesAuthViewModel(private val authRepository: SalesAuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<SalesAuthViewState>(SalesAuthViewState.Idle)
    val loginState: StateFlow<SalesAuthViewState> = _loginState.asStateFlow()

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun login() {
        _loginState.value = SalesAuthViewState.Loading

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess {
                _loginState.value = SalesAuthViewState.Success
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is FirebaseAuthInvalidUserException -> "Usuário não encontrado ou desativado."
                    is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha incorretos."
                    is FirebaseTooManyRequestsException -> "Muitas tentativas falhas. Tente novamente mais tarde."
                    is FirebaseNetworkException -> "Falha na conexão. Verifique sua internet."
                    is FirebaseAuthException -> "Erro de autenticação: ${exception.message}"
                    is IllegalArgumentException -> "Entrada inválida. Verifique os dados informados."
                    else -> "Erro desconhecido."
                }
                _loginState.value = SalesAuthViewState.Error(errorMessage)
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _loginState.value = SalesAuthViewState.Loading

        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            result.onSuccess {
                _loginState.value = SalesAuthViewState.Success
            }.onFailure { exception ->
                val errorMessage = when (exception) {
                    is FirebaseAuthException -> "Erro de autenticação: ${exception.message}"
                    else -> "Erro desconhecido."
                }
                _loginState.value = SalesAuthViewState.Error(errorMessage)
            }
        }
    }
}
