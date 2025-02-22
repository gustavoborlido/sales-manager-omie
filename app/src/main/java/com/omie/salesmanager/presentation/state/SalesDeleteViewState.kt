package com.omie.salesmanager.presentation.state

sealed class SalesDeleteViewState {
    data object Idle : SalesDeleteViewState()
    data object Success : SalesDeleteViewState()
    data class Error(val message: String) : SalesDeleteViewState()
}
