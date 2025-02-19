package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.model.SalesOrderItemDTO
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import kotlinx.coroutines.tasks.await

class SalesOrderRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : SalesOrderRepository {

    override suspend fun addOrder(orderItem: SalesOrderItemDTO): Result<String> {
        return runCatching {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuário não logado")

            val orderId = database.reference.push().key
                ?: throw Exception("Falha ao gerar ID do pedido")

            val userOrdersRef = database.reference.child("users").child(userId).child("orders")

            userOrdersRef.child(orderId).setValue(orderItem).await()

            orderId
        }
    }

    suspend fun getOrders(): Result<List<SalesOrderItemDTO>> {
        return runCatching {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuário não logado")

            val ordersRef = database.reference.child("users").child(userId).child("orders")

            val snapshot = ordersRef.get().await()
            if (snapshot.exists()) {
                val orders =
                    snapshot.children.mapNotNull { it.getValue(SalesOrderItemDTO::class.java) }
                orders
            } else {
                emptyList()
            }
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return runCatching {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuário não logado")

            val orderRef = database.reference
                .child("users")
                .child(userId)
                .child("orders")
                .child(orderId)

            orderRef.child("status").setValue(newStatus).await()
        }
    }

    suspend fun deleteOrder(orderId: String): Result<Unit> {
        return runCatching {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuário não logado")

            val orderRef = database.reference
                .child("users")
                .child(userId)
                .child("orders")
                .child(orderId)

            orderRef.removeValue().await()
        }
    }
}
