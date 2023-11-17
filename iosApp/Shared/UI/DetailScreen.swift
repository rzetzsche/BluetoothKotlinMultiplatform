//
//  DetailScreen.swift
//  iosApp
//
//  Created by Robert Zetzsche on 28.10.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import SwiftUI
import KMMViewModelSwiftUI

@available(iOS 15.0, *)
struct DetailScreen: View {
    let bluetoothDevice: BluetoothDevice
    @StateViewModel var viewModel: DetailViewModel
    
    init(bluetoothDevice: BluetoothDevice) {
        self.bluetoothDevice = bluetoothDevice
        let detailViewModel: DetailViewModel = getKoinInstance(parameters: [bluetoothDevice])
        self._viewModel = StateViewModel(wrappedValue: detailViewModel)
    }
    
    var body: some View {
        
        VStack{
            Text(bluetoothDevice.name ?? "Air Tag")
            Text(bluetoothDevice.identifier)
            Text(bluetoothDevice.rssi!.description)
            Spacer()
            
            switch onEnum(of: viewModel.state) {
            case .loading(_):
                ProgressView()
            case .loaded(let data):
                switch  onEnum(of: data) {
                case.airTag(let loaded):
                    Text("Battery Level: \(loaded.batteryLevel)")
                    if(viewModel.state is DetailViewModel.StateLoadedAirTagLoaded){
                        Button(action: {viewModel.playAirtagSound()}, label: {
                            Text("Play Sound")
                        })
                    }else{
                        ProgressView()
                    }
                case .movisensSensorLoaded(let loaded):
                    let values =   loaded.movisensSensorValues
                    Text("\(values.manufacturer) \(values.modelNumber)")
                    Text("Firmware Version: \(values.firmware)")
                    Text("Serial Number: \(values.serialNumber)")
                    Text("Battery Level: \(values.batteryLevel) %")
                }
                
            case .error(let error):
                Text(error.error)
            }
            
        }.padding(EdgeInsets(top: 32, leading: 16, bottom: 16, trailing: 16))
    }
}

