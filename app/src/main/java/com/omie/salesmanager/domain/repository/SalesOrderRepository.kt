package com.omie.salesmanager.domain.repository

import com.omie.salesmanager.data.model.SalesOrderItemDTO

interface SalesOrderRepository {
    suspend fun addOrder(orderItem: SalesOrderItemDTO): Result<String>
}
