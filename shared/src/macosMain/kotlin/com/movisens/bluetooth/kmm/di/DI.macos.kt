package com.movisens.bluetooth.kmm.di

import com.movisens.bluetooth.kmm.repository.IPermissionRepository
import com.movisens.bluetooth.kmm.repository.PermissionRepository
import com.movisens.bluetooth.kmm.repository.bluetooth.ApplicationContextHelper
import dev.bluefalcon.ApplicationContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModules(): Array<Module> = arrayOf(
    module {
        single<ApplicationContextHelper> { ApplicationContextHelper(ApplicationContext()) }
        single<IPermissionRepository> { PermissionRepository() }
    })
