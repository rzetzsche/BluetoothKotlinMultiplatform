package com.movisens.bluetooth.kmm.repository

import co.touchlab.kermit.Logger
import dev.icerock.moko.permissions.Permission.BLUETOOTH_CONNECT
import dev.icerock.moko.permissions.Permission.BLUETOOTH_LE
import dev.icerock.moko.permissions.Permission.BLUETOOTH_SCAN
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController

class PermissionRepository(private val permissionsController: PermissionsController) :
    IPermissionRepository {
    override suspend fun requestPermission(): Boolean {
        PERMISSIONS.forEach {
            permissionsController.providePermission(it)
        }
        return PERMISSIONS.map { permissionsController.getPermissionState(it) }
            .onEach { Logger.e { it.name } }
            .map { it == PermissionState.Granted }
            .reduce { bool1, bool2 -> bool1 && bool2 }
    }

    override suspend fun hasPermissions(): Boolean {
        return PERMISSIONS.map { permissionsController.getPermissionState(it) }
            .map { it == PermissionState.Granted }
            .reduce { bool1, bool2 -> bool1 && bool2 }
    }

    companion object {
        private val PERMISSIONS =
            listOf(BLUETOOTH_SCAN, BLUETOOTH_CONNECT, BLUETOOTH_LE)
    }
}