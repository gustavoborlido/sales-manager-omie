package com.omie.salesmanager.presentation.state

sealed class SalesAuthViewState {
    object Idle : SalesAuthViewState()
    object Loading : SalesAuthViewState()
    object Success : SalesAuthViewState()
    data class Error(val message: String) : SalesAuthViewState()
}
