package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.mapper.toDTO
import com.omie.salesmanager.data.mapper.toDomainList
import com.omie.salesmanager.data.model.SalesItemDTO
import com.omie.salesmanager.domain.model.SalesItemModel
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import kotlinx.coroutines.tasks.await

class SalesOrderRepositoryImpl(
    auth: FirebaseAuth,
    database: FirebaseDatabase
) : SalesOrderRepository, SalesBaseRepository(auth, database) {

    override suspend fun addOrder(order: SalesOrderModel): Result<String> {
        return runCatching {
            val orderId = database.reference.push().key
                ?: throw Exception("Falha ao gerar ID do pedido")

            getUserOrdersRef().child(orderId).setValue(order.toDTO()).await()
            orderId
        }
    }

    override suspend fun addItem(item: SalesItemModel, orderId: String): Result<String> {
        return runCatching {
            getUserOrderItemsRef(orderId).setValue(item.toDTO()).await()
            orderId
        }
    }

    override suspend fun getItens(orderId: String): Result<List<SalesItemModel>> {
        return runCatching {
            val snapshot = getUserOrderItemsRef("-OJ_4yDciNE1H6aFxKLd").get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { it.getValue(SalesItemDTO::class.java) }
                    .toDomainList()
            } else {
                emptyList()
            }
        }
    }
}

