package com.omie.salesmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.repository.SalesAuthRepositoryImpl
import com.omie.salesmanager.data.repository.SalesOrderRepositoryImpl
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.viewmodel.SalesLoginViewModel
import com.omie.salesmanager.presentation.viewmodel.SalesOrderViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single<FirebaseDatabase> { FirebaseDatabase.getInstance() }
    single<SalesAuthRepository> { SalesAuthRepositoryImpl(get()) }
    single<SalesOrderRepository> { SalesOrderRepositoryImpl(get(), get()) }

    viewModel { SalesLoginViewModel(get()) }
    viewModel { SalesOrderViewModel(get()) }
}
