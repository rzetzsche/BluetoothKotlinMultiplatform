package com.movisens.bluetooth.kmm.di

import org.koin.core.KoinApplication

object DIHelper {
    fun initKoin(additionalKoinConfig: KoinApplication.() -> Unit = {}) {
        com.movisens.bluetooth.kmm.di.initKoin(additionalKoinConfig)
    }
}
