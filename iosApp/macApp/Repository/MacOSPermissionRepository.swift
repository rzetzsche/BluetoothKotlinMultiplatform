//
//  MacOSPermissionRepository.swift
//  macApp
//
//  Created by Robert Zetzsche on 03.11.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import CoreBluetooth
import SwiftUI

class MacOSPermissionRepository: IPermissionRepository{
   
    func __hasPermissions() async throws -> KotlinBoolean {
        return KotlinBoolean(bool: CBCentralManager.authorization == .allowedAlways)
    }
    
    func __requestPermission() async throws -> KotlinBoolean {
        if let urlString = URL(string: "x-apple.systempreferences:com.apple.preference.security?Privacy_Bluetooth") {
            NSWorkspace.shared.open(urlString)
        }
        return try await __hasPermissions()
    }
}
