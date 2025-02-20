package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class SalesBaseRepository(
    protected val auth: FirebaseAuth,
    protected val database: FirebaseDatabase  // Agora o FirebaseDatabase está disponível para as classes derivadas
) {
    protected fun getUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("Usuário não logado")
    }

    protected fun getUserOrdersRef(): DatabaseReference {
        return database.reference.child("users").child(getUserId()).child("orders")
    }

    protected fun getUserOrderItemsRef(orderId: String): DatabaseReference {
        return database.reference
            .child("users")
            .child(getUserId())
            .child("orders")
            .child(orderId)
            .child("items")
    }
}
