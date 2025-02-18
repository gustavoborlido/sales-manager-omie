package com.omie.salesmanager.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.omie.salesmanager.domain.repository.SalesAuthRepository

class SalesViewModel(private val authRepository: SalesAuthRepository) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var loginResult by mutableStateOf<String?>(null)

    fun login() {
        isLoading = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            isLoading = false
            loginResult = result.getOrElse { "Erro: ${it.message}" }
        }
    }
}
