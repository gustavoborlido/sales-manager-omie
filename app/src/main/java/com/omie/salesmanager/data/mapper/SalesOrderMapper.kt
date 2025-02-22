package com.omie.salesmanager.data.mapper

import com.omie.salesmanager.data.dto.SalesItemDTO
import com.omie.salesmanager.data.dto.SalesOrderDTO
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.domain.model.SalesOrderModel

fun SalesItemDTO.toDomain(): SalesItemModel {
    return SalesItemModel(
        id = "",
        productName = this.productName,
        quantity = this.quantity,
        value = this.value
    )
}

fun SalesItemModel.toDTO(): SalesItemDTO {
    return SalesItemDTO(
        productName = this.productName,
        quantity = this.quantity,
        value = this.value
    )
}

fun Map<String, SalesItemDTO>.toDomainMap(): Map<String, SalesItemModel> {
    return this.mapValues { it.value.toDomain() }
}

fun Map<String, SalesItemModel>.toDTOMap(): Map<String, SalesItemDTO> {
    return this.mapValues { it.value.toDTO() }
}

fun SalesOrderDTO.toDomain(): SalesOrderModel {
    return SalesOrderModel(
        id = "",
        clientName = this.clientName,
        description = this.description,
        items = this.items.toDomainMap()
    )
}

fun SalesOrderModel.toDTO(): SalesOrderDTO {
    return SalesOrderDTO(
        description = this.description,
        clientName = this.clientName,
        items = this.items.toDTOMap()
    )
}
