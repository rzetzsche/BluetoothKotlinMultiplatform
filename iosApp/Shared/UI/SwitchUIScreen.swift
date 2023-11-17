//
//  SwitchUIScreen.swift
//  iosApp
//
//  Created by Robert Zetzsche on 05.11.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import SwiftUI

@available(iOS 16.0, *)
struct SwitchUIScreen: View {
    
    var body: some View {
        NavigationStack {
            VStack(spacing: 20) {
                NavigationLink("Swift UI"){
                    MainView()
                }
                
                NavigationLink("Compose UI"){
                    IOSComposeView()
                }
            }
        }
    }
}
