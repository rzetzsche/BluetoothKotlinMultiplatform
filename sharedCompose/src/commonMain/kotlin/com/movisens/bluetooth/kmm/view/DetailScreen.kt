package com.movisens.bluetooth.kmm.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.movisens.bluetooth.kmm.repository.bluetooth.BluetoothDevice
import com.movisens.bluetooth.kmm.viewmodel.DetailViewModel
import com.movisens.bluetooth.kmm.viewmodel.DetailViewModel.State.Error
import com.movisens.bluetooth.kmm.viewmodel.DetailViewModel.State.Loaded.AirTag
import com.movisens.bluetooth.kmm.viewmodel.DetailViewModel.State.Loading
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class DetailScreen(private val bluetoothDevice: BluetoothDevice) : Screen, KoinComponent {
    private val detailViewModel =
        getKoin().get<DetailViewModel>(parameters = { parametersOf(bluetoothDevice) })

    @Composable
    override fun Content() {
        val detailState by detailViewModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(topBar = {
            TopAppBar(title = { Text("App") }, navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            })
        }) {

            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(bluetoothDevice.name ?: "Air Tag")
                Text(bluetoothDevice.identifier)
                Text(bluetoothDevice.rssi.toString())
                Box(modifier = Modifier.height(100.dp))

                detailState.let { detailState ->
                    when (detailState) {
                        is DetailViewModel.State.Loaded.MovisensSensorLoaded -> Column {
                            val movisensSensorValues = detailState.movisensSensorValues
                            Text("${movisensSensorValues.manufacturer} ${movisensSensorValues.modelNumber}")
                            Text("Firmware Version: ${movisensSensorValues.firmware}")
                            Text("Serial Number: ${movisensSensorValues.serialNumber}")
                            Text("Battery Level: ${movisensSensorValues.batteryLevel} %")
                        }

                        is Error -> Text(detailState.error)
                        is AirTag -> {
                            Text("Battery Level: ${detailState.batteryLevel}")
                            Box(modifier = Modifier.height(100.dp))
                            if (detailState is AirTag.Loaded)
                                Button(onClick = { detailViewModel.playAirtagSound() }) {
                                    Text("Play Sound")
                                }
                            else {
                                CircularProgressIndicator()
                            }
                        }

                        is Loading -> CircularProgressIndicator()
                    }
                }
            }
        }
    }
}