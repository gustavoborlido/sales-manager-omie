package com.omie.salesmanager.presentation.state

sealed class SalesLoginViewState {
    object Idle : SalesLoginViewState()
    object Loading : SalesLoginViewState()
    object Success : SalesLoginViewState()
    data class Error(val message: String) : SalesLoginViewState()
}
