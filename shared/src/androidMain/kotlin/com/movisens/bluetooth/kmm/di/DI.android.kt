package com.movisens.bluetooth.kmm.di

import android.app.Application
import com.movisens.bluetooth.kmm.repository.bluetooth.ApplicationContextHelper
import dev.icerock.moko.permissions.PermissionsController
import org.koin.core.module.Module
import org.koin.dsl.module


actual fun platformModules(): Array<Module> = arrayOf(
    androidIosModule,
    module {
        single { PermissionsController(applicationContext = get()) }
        single<ApplicationContextHelper> { ApplicationContextHelper(get<Application>()) }
    },
)