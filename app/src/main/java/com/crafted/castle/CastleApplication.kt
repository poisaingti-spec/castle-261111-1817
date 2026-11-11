package com.crafted.castle

import android.app.Application
import com.crafted.castle.data.Repository
import com.crafted.castle.network.AnalyticsService
import com.crafted.castle.viewmodel.CanvasViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class CastleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@CastleApplication)
            modules(appModule)
        }
    }
}

val appModule = module {
    single { Repository(get()) }
    single { AnalyticsService() }
    factory { CanvasViewModel(get(), get()) }
}