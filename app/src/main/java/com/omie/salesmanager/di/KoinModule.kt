package com.omie.salesmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.omie.salesmanager.data.repository.SalesAuthRepositoryImpl
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import com.omie.salesmanager.presentation.viewmodel.SalesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single<SalesAuthRepository> { SalesAuthRepositoryImpl(get()) }

    viewModel { SalesViewModel(get()) }
}
