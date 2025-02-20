package com.omie.salesmanager.data.model

data class SalesOrderDTO(
    val description: String = "",
    val clientName: String = "",
    val items: List<SalesItemDTO> = listOf()
)
