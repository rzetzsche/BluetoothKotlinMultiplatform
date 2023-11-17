package com.movisens.bluetooth.kmm.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import com.movisens.bluetooth.kmm.repository.bluetooth.FalconBluetoothRepository
import com.movisens.bluetooth.kmm.repository.bluetooth.IBluetoothRepository
import com.movisens.bluetooth.kmm.repository.bluetooth.KableBluetoothRepository
import com.movisens.bluetooth.kmm.viewmodel.DetailViewModel
import com.movisens.bluetooth.kmm.viewmodel.MainViewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

expect fun platformModules(): Array<Module>

fun initKoin(additionalKoinConfig: KoinApplication.() -> Unit = {}) {
    startKoin {
        modules(module {
            single { KableBluetoothRepository() } bind IBluetoothRepository::class
            factory { MainViewModel(get(), get()) }
            factory { DetailViewModel(get(), get()) }
        }, *platformModules())
        logger(KermitKoinLogger(Logger.withTag("koin")))
        additionalKoinConfig()
    }
}