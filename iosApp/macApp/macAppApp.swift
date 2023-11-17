//
//  macAppApp.swift
//  macApp
//
//  Created by Robert Zetzsche on 03.11.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared

@main
struct macAppApp: App {
    init(){
        startKoin {
            swiftModule{
                $0.single {
                    MacOSPermissionRepository() as IPermissionRepository
                }
            }
        }
    }
    
    var body: some Scene {
        WindowGroup {
            MainView()
        }
    }
}
