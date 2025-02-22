package com.omie.salesmanager.presentation.state

import com.omie.salesmanager.domain.model.SalesItemModel

sealed class SalesItemListViewState {
    data object Idle : SalesItemListViewState()
    data object Loading : SalesItemListViewState()
    data class Success(val items: List<SalesItemModel>) : SalesItemListViewState()
    data class Error(val message: String) : SalesItemListViewState()
    data object Deleted : SalesItemListViewState()
}
