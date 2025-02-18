package com.omie.salesmanager.domain.repository

interface SalesAuthRepository {
    suspend fun login(email: String, password: String): Result<String>
}
