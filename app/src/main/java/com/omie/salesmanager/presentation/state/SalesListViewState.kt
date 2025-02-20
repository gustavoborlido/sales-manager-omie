package com.omie.salesmanager.presentation.state

import com.omie.salesmanager.domain.model.SalesItemModel

sealed class SalesListViewState {
    object Idle : SalesListViewState()
    object Loading : SalesListViewState()
    data class Success(val orders: List<SalesItemModel>) : SalesListViewState()
    data class Error(val message: String) : SalesListViewState()
}
