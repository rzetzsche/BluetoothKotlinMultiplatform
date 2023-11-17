package com.movisens.bluetooth.kmm.viewmodel

import co.touchlab.kermit.Logger
import com.movisens.bluetooth.kmm.repository.IPermissionRepository
import com.movisens.bluetooth.kmm.repository.bluetooth.BluetoothDevice
import com.movisens.bluetooth.kmm.repository.bluetooth.IBluetoothRepository
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val bluetoothRepository: IBluetoothRepository,
    private val permissionRepository: IPermissionRepository
) :
    KMMViewModel() {
    private val mutableState: MutableStateFlow<State> =
        MutableStateFlow(viewModelScope, State.PermissionsDenied)

    init {
        viewModelScope.coroutineScope.launch {
            if (permissionRepository.hasPermissions()) {
                mutableState.value = State.NotScanning(emptyList())
            }
        }
    }

    @NativeCoroutinesState
    val state: StateFlow<State> = mutableState
    private var job: Job? = null

    sealed class State(open val sensorList: List<BluetoothDevice>) {
        data object PermissionsDenied : State(emptyList())
        data class Scanning(override val sensorList: List<BluetoothDevice>) : State(sensorList)
        data class NotScanning(override val sensorList: List<BluetoothDevice>) : State(sensorList)
        data class Error(override val sensorList: List<BluetoothDevice>) : State(sensorList)
    }

    fun startScanning() {
        mutableState.value = State.Scanning(emptyList())
        job = viewModelScope.coroutineScope.launch {
            try {
                bluetoothRepository.startScan()
                    .collect {
                        val oldList = mutableState.value.sensorList.toMutableList()

                        it.filter { it !is BluetoothDevice.OtherBleDevice }
                            .filter { newDevice -> oldList.firstOrNull { it.identifier == newDevice.identifier } == null }
                            .forEach { oldList.add(it) }

                        mutableState.value =
                            State.Scanning(oldList.sortedBy { it.timeOfFirstDiscorvery })
                    }
            } catch (e: Exception) {
                Logger.e { "ERROR $e" }
            }
        }
    }

    fun stopScanning() {
        try {
            job?.cancel()
            mutableState.value = State.NotScanning(mutableState.value.sensorList)
        } catch (e: Exception) {
            Logger.e { "ERROR $e" }
        }
    }

    fun requestPermissions() {
        viewModelScope.coroutineScope.launch {
            if (permissionRepository.requestPermission()) {
                mutableState.value = State.NotScanning(emptyList())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}