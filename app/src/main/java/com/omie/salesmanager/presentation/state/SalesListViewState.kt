package com.omie.salesmanager.presentation.state

import com.omie.salesmanager.domain.model.SalesOrderModel

sealed class SalesListViewState {
    object Idle : SalesListViewState()
    object Loading : SalesListViewState()
    data class Success(val orders: List<SalesOrderModel>) : SalesListViewState()
    data class Error(val message: String) : SalesListViewState()
}
