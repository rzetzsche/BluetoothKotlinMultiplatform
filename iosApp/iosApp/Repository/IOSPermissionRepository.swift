//
//  UIKitPermissionRepository.swift
//  iosApp
//
//  Created by Robert Zetzsche on 03.11.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import CoreBluetooth
import SwiftUI

class IOSPermissionRepository: IPermissionRepository{
    
    
    func __hasPermissions() async throws -> KotlinBoolean {
        return KotlinBoolean(bool: CBCentralManager.authorization == .allowedAlways)
    }
    
    func __requestPermission() async throws -> KotlinBoolean {
        if let url = await URL(string: UIApplication.openSettingsURLString) {
            if await UIApplication.shared.canOpenURL(url) {
                await UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
        return try await  __hasPermissions()
    }
}
