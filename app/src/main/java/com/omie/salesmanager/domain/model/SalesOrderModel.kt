package com.omie.salesmanager.domain.model

data class SalesOrderModel(
    var id: String,
    val description: String,
    val clientName: String,
    val items: Map<String, SalesItemModel> = mapOf()
) {
    val totalOrderValue: Double
        get() = items.values.sumOf { it.totalValue }
}
