//
//  MainScreen.swift
//  iosApp
//
//  Created by Robert Zetzsche on 27.10.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI
import shared
import KMMViewModelSwiftUI

@available(iOS 16.0, *)
struct MainView: View {
    @StateViewModel var viewModel: MainViewModel = getKoinInstance()
    
    var body: some View {
      
            VStack{
                
                switch onEnum(of: viewModel.state) {
                case .permissionsDenied(_):
                    Button(action: { viewModel.requestPermissions()}, label: {
                        VStack{
                            Text("Grant Permissions")
                        }
                    })
                case .scanning(_):
                    Button(action: { viewModel.stopScanning()}, label: {
                        VStack{
                            Text("Stop Scanning")
                        }
                    })
                case .notScanning(_):
                    Button(action: { viewModel.startScanning()}, label: {
                        Text("Start Scanning")
                    })
                case .error(_):
                    Text("ERROR")
                }
                NavigationStack{
                    List(viewModel.state.sensorList, id: \.self){ sensor in
                        NavigationLink(value: sensor)  {
                            VStack(alignment: .leading){
                                Text(sensor.name ?? "Air Tag")
                                Text(sensor.identifier)
                                Text(sensor.rssi?.description ?? "0.0")
                            }
                            
                        }
                    }.navigationDestination(for: BluetoothDevice.self)  { sensor in
                        DetailScreen(bluetoothDevice: sensor)
                    }
                    if(viewModel.state is MainViewModel.StateScanning){
                        ProgressView()
                    }
                }
                
            }.padding(EdgeInsets(top: 32, leading: 16, bottom: 16, trailing: 16))
        
    }
}

