package com.omie.salesmanager.domain.repository

import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.domain.model.SalesOrderModel

interface SalesOrderRepository {
    suspend fun addOrder(order: SalesOrderModel): Result<String>
    suspend fun addItem(item: SalesItemModel, orderId: String): Result<String>
    suspend fun getItens(orderId: String): Result<List<SalesItemModel>>
    suspend fun getOrders(): Result<List<SalesOrderModel>>
    suspend fun deleteItem(orderId: String, itemId: String): Result<Unit>
    suspend fun deleteOrder(orderId: String): Result<Unit>
}
