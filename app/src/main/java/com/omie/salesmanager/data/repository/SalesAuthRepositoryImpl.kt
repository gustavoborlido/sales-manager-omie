package com.omie.salesmanager.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import kotlinx.coroutines.tasks.await

class SalesAuthRepositoryImpl(private val auth: FirebaseAuth): SalesAuthRepository {
    override suspend fun login(email: String, password: String): Result<String> {
        return runCatching {
            auth.signInWithEmailAndPassword(email, password).await()
            "Login bem-sucedido!"
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<String> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            "Login com Google bem-sucedido!"
        }
    }
}
