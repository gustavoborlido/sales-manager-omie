package com.omie.salesmanager.presentation.state

sealed class SalesOrderViewState {
    object Success : SalesOrderViewState()
    object Idle : SalesOrderViewState()
    object Loading : SalesOrderViewState()
    data class Error(val message: String) : SalesOrderViewState()
}
