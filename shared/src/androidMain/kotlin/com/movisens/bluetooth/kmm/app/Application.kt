package com.movisens.bluetooth.kmm.di.app

import android.app.Application
import com.movisens.bluetooth.kmm.di.initKoin
import org.koin.android.ext.koin.androidContext

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@Application)
        }
    }
}