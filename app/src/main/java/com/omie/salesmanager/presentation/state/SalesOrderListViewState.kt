package com.omie.salesmanager.presentation.state

import com.omie.salesmanager.domain.model.SalesOrderModel

sealed class SalesOrderListViewState {
    data object Idle : SalesOrderListViewState()
    data object Loading : SalesOrderListViewState()
    data class Success(val orders: List<SalesOrderModel>) : SalesOrderListViewState()
    data class Error(val message: String) : SalesOrderListViewState()
    data object Deleted : SalesOrderListViewState()
}
