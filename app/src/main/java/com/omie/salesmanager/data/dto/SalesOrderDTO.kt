package com.omie.salesmanager.data.dto

data class SalesOrderDTO(
    val description: String = "",
    val clientName: String = "",
    val items: Map<String, SalesItemDTO> = mapOf()
)
