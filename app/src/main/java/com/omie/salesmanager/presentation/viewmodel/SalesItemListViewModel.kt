package com.omie.salesmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesItemListViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesItemListViewModel(private val orderRepository: SalesOrderRepository) : ViewModel() {

    private val _itemState = MutableStateFlow<SalesItemListViewState>(SalesItemListViewState.Idle)
    val itemState: StateFlow<SalesItemListViewState> = _itemState.asStateFlow()

    private val _deleteState =
        MutableStateFlow<SalesDeleteViewState>(SalesDeleteViewState.Idle)
    val deleteState: StateFlow<SalesDeleteViewState> = _deleteState.asStateFlow()

    fun getItems(orderId: String) {
        _itemState.value = SalesItemListViewState.Loading

        viewModelScope.launch {
            val result = orderRepository.getItens(orderId)
            result.onSuccess { orders ->
                _itemState.value = SalesItemListViewState.Success(orders)
            }.onFailure { exception ->
                _itemState.value =
                    SalesItemListViewState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }

    fun deleteItem(orderId: String, itemId: String) {
        _deleteState.value = SalesDeleteViewState.Idle

        viewModelScope.launch {
            val result = orderRepository.deleteItem(orderId, itemId)
            result.onSuccess {
                _deleteState.value =
                    SalesDeleteViewState.Success
                getItems(orderId)
            }.onFailure { exception ->
                _deleteState.value =
                    SalesDeleteViewState.Error(exception.message ?: "Erro ao excluir o item")
            }
        }
    }

    fun resetDeleteState() {
        _deleteState.value = SalesDeleteViewState.Idle
    }
}
