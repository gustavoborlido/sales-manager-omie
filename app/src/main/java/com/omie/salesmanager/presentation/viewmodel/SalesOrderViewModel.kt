package com.omie.salesmanager.presentation.viewmodel

import com.omie.salesmanager.data.model.SalesOrderItemDTO

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesOrderViewModel(private val orderRepository: SalesOrderRepository) : ViewModel() {

    private val _orderState = MutableStateFlow<SalesOrderViewState>(SalesOrderViewState.Idle)
    val orderState: StateFlow<SalesOrderViewState> = _orderState.asStateFlow()

    fun addOrder(orderItem: SalesOrderModel) {
        _orderState.value = SalesOrderViewState.Loading

        viewModelScope.launch {
            val result = orderRepository.addOrder(orderItem)
            result.onSuccess {
                _orderState.value = SalesOrderViewState.Success
            }.onFailure { exception ->
                _orderState.value =
                    SalesOrderViewState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }
}
