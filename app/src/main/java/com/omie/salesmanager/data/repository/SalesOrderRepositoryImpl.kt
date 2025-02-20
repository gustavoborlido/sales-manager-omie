package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.mapper.toDTO
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import kotlinx.coroutines.tasks.await

class SalesOrderRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : SalesOrderRepository {

    override suspend fun addOrder(orderItem: SalesOrderModel): Result<String> {
        return runCatching {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuário não logado")

            val orderId = database.reference.push().key
                ?: throw Exception("Falha ao gerar ID do pedido")

            val userOrdersRef = database.reference.child("users").child(userId).child("orders")

            userOrdersRef.child(orderId).setValue(orderItem.toDTO()).await()

            orderId
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
