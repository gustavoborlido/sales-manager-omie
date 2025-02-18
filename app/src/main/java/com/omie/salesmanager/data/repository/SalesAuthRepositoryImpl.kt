package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import kotlinx.coroutines.tasks.await

class SalesAuthRepositoryImpl(private val auth: FirebaseAuth): SalesAuthRepository {
    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success("Login bem-sucedido!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
