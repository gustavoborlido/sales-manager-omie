package com.omie.salesmanager.domain.repository

import com.omie.salesmanager.domain.model.SalesOrderModel

interface SalesListRepository {
    suspend fun getOrders(): Result<List<SalesOrderModel>>
}
