package com.movisens.bluetooth.kmm.di

import com.movisens.bluetooth.kmm.repository.IPermissionRepository
import com.movisens.bluetooth.kmm.repository.PermissionRepository
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

val androidIosModule: Module
    get() = module {
        single { PermissionRepository(get()) } bind IPermissionRepository::class
    }