package com.omie.salesmanager.presentation.state

sealed class SalesOrderViewState {
    object Idle : SalesOrderViewState()
    object Loading : SalesOrderViewState()
    data class Success(val orderId: String) : SalesOrderViewState()
    data class Error(val message: String) : SalesOrderViewState()
}
