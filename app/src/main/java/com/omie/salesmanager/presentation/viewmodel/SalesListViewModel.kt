package com.omie.salesmanager.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omie.salesmanager.domain.repository.SalesListRepository
import com.omie.salesmanager.presentation.state.SalesListViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesListViewModel(private val orderRepository: SalesListRepository) : ViewModel() {

    private val _listState = MutableStateFlow<SalesListViewState>(SalesListViewState.Idle)
    val listState: StateFlow<SalesListViewState> = _listState.asStateFlow()

    fun getOrders() {
        _listState.value = SalesListViewState.Loading

        viewModelScope.launch {
            val result = orderRepository.getOrders()
            result.onSuccess { orders ->
                _listState.value = SalesListViewState.Success(orders)
            }.onFailure { exception ->
                _listState.value =
                    SalesListViewState.Error(exception.message ?: "Erro desconhecido")
            }
        }
    }
}
