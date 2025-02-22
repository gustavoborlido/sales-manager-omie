package com.omie.salesmanager.domain.model

data class SalesItemModel(
    var id: String,
    val productName: String,
    val quantity: Int,
    val value: Double
) {
    val totalValue: Double
        get() = quantity * value
}
