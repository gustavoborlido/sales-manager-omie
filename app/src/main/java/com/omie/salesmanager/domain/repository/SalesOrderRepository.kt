package com.omie.salesmanager.domain.repository

import com.omie.salesmanager.domain.model.SalesOrderModel

interface SalesOrderRepository {
    suspend fun addOrder(orderItem: SalesOrderModel): Result<String>
}
