package com.omie.salesmanager.domain.model

data class SalesOrderModel(
    val description: String,
    val clientName: String,
    val items: List<SalesItemModel> = listOf()
) {
    val totalOrderValue: Double
        get() = items.sumOf { it.totalValue }
}
