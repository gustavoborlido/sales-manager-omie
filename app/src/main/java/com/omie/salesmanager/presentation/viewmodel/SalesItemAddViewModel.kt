package com.omie.salesmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.state.SalesOrderViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesItemAddViewModel(private val orderRepository: SalesOrderRepository) : ViewModel() {

    private val _orderState = MutableStateFlow<SalesOrderViewState>(SalesOrderViewState.Idle)
    val orderState: StateFlow<SalesOrderViewState> = _orderState.asStateFlow()

    fun addItem(item: SalesItemModel, orderId: String) {
        _orderState.value = SalesOrderViewState.Loading

        viewModelScope.launch {
            val result = orderRepository.addItem(item, orderId)
            result.onSuccess { itemId ->
                _orderState.value = SalesOrderViewState.Success(itemId)
            }.onFailure { exception ->
                _orderState.value =
                    SalesOrderViewState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }
}
