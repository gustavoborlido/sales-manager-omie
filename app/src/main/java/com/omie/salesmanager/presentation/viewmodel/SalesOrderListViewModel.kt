package com.omie.salesmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesDeleteViewState
import com.omie.salesmanager.presentation.state.SalesOrderListViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesOrderListViewModel(private val orderRepository: SalesOrderRepository) : ViewModel() {

    private val _orderState =
        MutableStateFlow<SalesOrderListViewState>(SalesOrderListViewState.Idle)
    val orderState: StateFlow<SalesOrderListViewState> = _orderState.asStateFlow()

    private val _deleteState =
        MutableStateFlow<SalesDeleteViewState>(SalesDeleteViewState.Idle)
    val deleteState: StateFlow<SalesDeleteViewState> = _deleteState.asStateFlow()

    fun getOrders() {
        _orderState.value = SalesOrderListViewState.Loading

        viewModelScope.launch {
            val result = orderRepository.getOrders()
            result.onSuccess { orders ->
                _orderState.value = SalesOrderListViewState.Success(orders)
            }.onFailure { exception ->
                _orderState.value =
                    SalesOrderListViewState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }

    fun deleteOrder(orderId: String) {
        _deleteState.value = SalesDeleteViewState.Idle

        viewModelScope.launch {
            val result = orderRepository.deleteOrder(orderId)
            result.onSuccess {
                _deleteState.value = SalesDeleteViewState.Success
                getOrders()
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
