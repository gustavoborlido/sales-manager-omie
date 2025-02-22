package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.dto.SalesItemDTO
import com.omie.salesmanager.data.dto.SalesOrderDTO
import com.omie.salesmanager.data.mapper.toDTO
import com.omie.salesmanager.data.mapper.toDomain
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
            val orderId = generateUniqueKey()
            getUserOrdersRef().child(orderId).setValue(order.toDTO()).await()
            orderId
        }
    }

    override suspend fun addItem(item: SalesItemModel, orderId: String): Result<String> {
        return runCatching {
            val itemKey = generateUniqueKey()
            val itemRef = getUserOrderItemsRef(orderId).child(itemKey)
            itemRef.setValue(item.toDTO()).await()
            itemKey
        }
    }

    override suspend fun getItens(orderId: String): Result<List<SalesItemModel>> {
        return runCatching {
            val snapshot = getUserOrderItemsRef(orderId).get().await()
            if (snapshot.exists()) {
                val items = snapshot.children.mapNotNull {
                    val itemDTO = it.getValue(SalesItemDTO::class.java)
                    itemDTO?.toDomain()?.apply {
                        this.id = it.key ?: ""
                    }
                }
                items
            } else {
                emptyList()
            }
        }
    }

    override suspend fun getOrders(): Result<List<SalesOrderModel>> {
        return runCatching {
            val snapshot = getUserOrdersRef().get().await()
            if (snapshot.exists()) {
                val items = snapshot.children.mapNotNull {
                    val itemDTO = it.getValue(SalesOrderDTO::class.java)
                    itemDTO?.toDomain()?.apply {
                        this.id = it.key ?: ""
                    }
                }
                items
            } else {
                emptyList()
            }
        }
    }

    override suspend fun deleteItem(orderId: String, itemId: String): Result<Unit> {
        return runCatching {
            val itemRef = getUserOrderItemsRef(orderId).child(itemId)
            itemRef.removeValue().await()
        }
    }

    override suspend fun deleteOrder(orderId: String): Result<Unit> {
        return runCatching {
            val orderRef = getUserOrdersRef().child(orderId)
            orderRef.removeValue().await()
        }
    }
}
