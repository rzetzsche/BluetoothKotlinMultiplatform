package com.movisens.bluetooth.kmm.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import com.movisens.bluetooth.kmm.repository.bluetooth.BluetoothDevice
import com.movisens.bluetooth.kmm.viewmodel.MainViewModel
import org.koin.core.component.KoinComponent

class MainScreen : Screen, KoinComponent {
    private val mainViewModel = getKoin().get<MainViewModel>()

    @Composable
    override fun Content() {
        val state by mainViewModel.state.collectAsState()
        Logger.e { state.toString() }
        Scaffold(topBar = {
            TopAppBar(title = { Text("App") })
        }) {
            Column(
                modifier = Modifier.padding(all = 16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                state.let { state ->
                    when (state) {
                        is MainViewModel.State.PermissionsDenied -> {
                            Button(onClick = {
                                mainViewModel.requestPermissions()
                            }) {
                                Column {
                                    Text("Grant Permissions")
                                }
                            }
                        }

                        is MainViewModel.State.Error -> TODO()
                        is MainViewModel.State.NotScanning -> Column {
                            Button(onClick = {
                                mainViewModel.startScanning()
                            }) {
                                Column {
                                    Text("Start Scanning")
                                }
                            }
                        }

                        is MainViewModel.State.Scanning -> Column {
                            Column {
                                Button(onClick = {
                                    mainViewModel.stopScanning()
                                }) {
                                    Text("Stop Scanning")
                                }
                            }
                        }
                    }
                }
                LazyColumn {
                    items(state.sensorList) {
                        BluetoothDeviceView(it)
                    }
                }
                if (state is MainViewModel.State.Scanning) {
                    CircularProgressIndicator()
                }
            }
        }

        LifecycleEffect(onDisposed = {
            mainViewModel.stopScanning()
            mainViewModel.onCleared()
        })
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BluetoothDeviceView(bluetoothDevice: BluetoothDevice) {
    val navigator = LocalNavigator.currentOrThrow
    Card(modifier = Modifier.padding(8.dp), onClick = {
        navigator.push(DetailScreen(bluetoothDevice))
    }) {
        Column(modifier = Modifier.fillMaxWidth().padding(all = 8.dp)) {
            Text(bluetoothDevice.name ?: "Air Tag")
            Text(bluetoothDevice.identifier)
            Text(bluetoothDevice.rssi.toString())
        }
    }
}