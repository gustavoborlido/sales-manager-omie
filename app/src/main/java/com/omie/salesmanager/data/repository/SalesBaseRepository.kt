package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

abstract class SalesBaseRepository(
    protected val auth: FirebaseAuth,
    protected val database: FirebaseDatabase
) {
    protected fun generateUniqueKey(): String {
        return database.reference.push().key ?: throw Exception("Falha ao gerar chave única")
    }

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
