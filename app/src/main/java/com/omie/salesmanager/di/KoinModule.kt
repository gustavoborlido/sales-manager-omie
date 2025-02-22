package com.omie.salesmanager.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.omie.salesmanager.data.repository.SalesAuthRepositoryImpl
import com.omie.salesmanager.data.repository.SalesOrderRepositoryImpl
import com.omie.salesmanager.domain.repository.SalesAuthRepository
import com.omie.salesmanager.domain.repository.SalesOrderRepository
import com.omie.salesmanager.presentation.viewmodel.SalesItemListViewModel
import com.omie.salesmanager.presentation.viewmodel.SalesAuthViewModel
import com.omie.salesmanager.presentation.viewmodel.SalesOrderAddViewModel
import com.omie.salesmanager.presentation.viewmodel.SalesItemAddViewModel
import com.omie.salesmanager.presentation.viewmodel.SalesOrderListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single<FirebaseDatabase> { FirebaseDatabase.getInstance() }
    single<SalesAuthRepository> { SalesAuthRepositoryImpl(get()) }
    single<SalesOrderRepository> { SalesOrderRepositoryImpl(get(), get()) }

    viewModel { SalesAuthViewModel(get()) }
    viewModel { SalesItemAddViewModel(get()) }
    viewModel { SalesItemListViewModel(get()) }
    viewModel { SalesOrderAddViewModel(get()) }
    viewModel { SalesOrderListViewModel(get()) }
}
