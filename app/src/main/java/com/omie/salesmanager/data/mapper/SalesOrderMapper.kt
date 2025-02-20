package com.omie.salesmanager.data.mapper

import com.omie.salesmanager.data.model.SalesOrderItemDTO
import com.omie.salesmanager.domain.model.SalesOrderModel

fun SalesOrderItemDTO.toDomain(): SalesOrderModel {
    return SalesOrderModel(
        productName = this.productName,
        quantity = this.quantity,
        value = this.value
    )
}

fun List<SalesOrderItemDTO>.toDomainList(): List<SalesOrderModel> {
    return this.map { it.toDomain() }
}

fun SalesOrderModel.toDTO(): SalesOrderItemDTO {
    return SalesOrderItemDTO(
        productName = this.productName,
        quantity = this.quantity,
        value = this.value
    )
}
