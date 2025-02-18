package com.omie.salesmanager

import android.app.Application
import com.omie.salesmanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SalesManager: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SalesManager)
            modules(appModule)
        }
    }
}
