package com.omie.salesmanager.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import com.omie.salesmanager.presentation.state.SalesLoginViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SalesLoginViewModel(private val authRepository: SalesAuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<SalesLoginViewState>(SalesLoginViewState.Idle)
    val loginState: StateFlow<SalesLoginViewState> = _loginState.asStateFlow()

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun login() {
        _loginState.value = SalesLoginViewState.Loading

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess {
                _loginState.value = SalesLoginViewState.Success
            }.onFailure { exception ->
                _loginState.value = SalesLoginViewState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }
}
