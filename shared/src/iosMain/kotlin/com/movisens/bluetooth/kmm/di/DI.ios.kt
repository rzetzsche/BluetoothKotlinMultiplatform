package com.movisens.bluetooth.kmm.di

import com.movisens.bluetooth.kmm.repository.bluetooth.ApplicationContextHelper
import dev.bluefalcon.ApplicationContext
import dev.icerock.moko.permissions.ios.PermissionsController
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModules(): Array<Module> = arrayOf(
    androidIosModule,
    module {
        single<PermissionsControllerProtocol> { PermissionsController() }
        single<ApplicationContextHelper> { ApplicationContextHelper(ApplicationContext()) }
    },
)