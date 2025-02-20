package com.omie.salesmanager.data.mapper

import com.omie.salesmanager.data.model.SalesItemDTO
import com.omie.salesmanager.data.model.SalesOrderDTO
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.domain.model.SalesOrderModel

fun SalesItemDTO.toDomain(): SalesItemModel {
    return SalesItemModel(
        productName = this.productName,
        quantity = this.quantity,
        value = this.value
    )
}

fun List<SalesItemDTO>.toDomainList(): List<SalesItemModel> {
    return this.map { it.toDomain() }
}

fun SalesItemModel.toDTO(): SalesItemDTO {
    return SalesItemDTO(
        productName = this.productName,
        quantity = this.quantity,
        value = this.value
    )
}

fun List<SalesItemModel>.toDTOList(): List<SalesItemDTO> {
    return this.map { it.toDTO() }
}

fun SalesOrderDTO.toDomain(): SalesOrderModel {
    return SalesOrderModel(
        clientName = this.clientName,
        description = this.description,
        items = this.items.toDomainList()
    )
}

fun SalesOrderModel.toDTO(): SalesOrderDTO {
    return SalesOrderDTO(
        description = this.description,
        clientName = this.clientName,
        items = this.items.map { it.toDTO() }
    )
}
