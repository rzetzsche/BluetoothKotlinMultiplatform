package com.movisens.bluetooth.kmm.repository

import platform.AppKit.NSWorkspace
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBManagerAuthorizationAllowedAlways
import platform.Foundation.NSURL

class PermissionRepository : IPermissionRepository {

    override suspend fun requestPermission(): Boolean {
        val settingsURL =
            NSURL(string = "x-apple.systempreferences:com.apple.preference.security?Privacy_Bluetooth")
        NSWorkspace.sharedWorkspace.openURL(settingsURL)
        return hasPermissions()
    }

    override suspend fun hasPermissions(): Boolean {
        return CBCentralManager.authorization == CBManagerAuthorizationAllowedAlways
    }
}
