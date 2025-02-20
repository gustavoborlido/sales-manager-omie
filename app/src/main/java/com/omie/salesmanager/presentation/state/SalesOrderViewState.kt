package com.omie.salesmanager.presentation.state

sealed class SalesOrderViewState {
    object Idle : SalesOrderViewState()
    object Loading : SalesOrderViewState()
    object Success : SalesOrderViewState()
    data class Error(val message: String) : SalesOrderViewState()
}
