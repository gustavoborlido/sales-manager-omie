package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.mapper.toDomainList
import com.omie.salesmanager.data.model.SalesOrderItemDTO
import com.omie.salesmanager.domain.model.SalesOrderModel
import com.omie.salesmanager.domain.repository.SalesListRepository
import kotlinx.coroutines.tasks.await

class SalesListRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : SalesListRepository {

    override suspend fun getOrders(): Result<List<SalesOrderModel>> {
        return runCatching {
            val userId = auth.currentUser?.uid
                ?: throw Exception("Usuário não logado")

            val ordersRef = database.reference.child("users").child(userId).child("orders")

            val snapshot = ordersRef.get().await()
            if (snapshot.exists()) {
                snapshot.children.mapNotNull { it.getValue(SalesOrderItemDTO::class.java) }
                    .toDomainList()
            } else {
                emptyList()
            }
        }
    }
}
