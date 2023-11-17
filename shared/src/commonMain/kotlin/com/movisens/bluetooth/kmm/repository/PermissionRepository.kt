package com.movisens.bluetooth.kmm.repository

interface IPermissionRepository {
    suspend fun requestPermission(): Boolean

    suspend fun hasPermissions(): Boolean
}