package com.movisens.bluetooth.kmm.viewmodel

import co.touchlab.kermit.Logger
import com.movisens.bluetooth.kmm.repository.bluetooth.BluetoothDevice
import com.movisens.bluetooth.kmm.repository.bluetooth.IBluetoothRepository
import com.movisens.bluetooth.kmm.util.BatteryLevel
import com.movisens.bluetooth.kmm.util.FirmwareRevision
import com.movisens.bluetooth.kmm.util.ManufacturerName
import com.movisens.bluetooth.kmm.util.ModelNumber
import com.movisens.bluetooth.kmm.util.SerialNumber
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.MutableStateFlow
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DetailViewModel(
    private val bluetoothRepository: IBluetoothRepository,
    val bluetoothDevice: BluetoothDevice
) : KMMViewModel() {

    private val mutableState: MutableStateFlow<State> =
        MutableStateFlow(viewModelScope, State.Loading(bluetoothDevice))

    @NativeCoroutinesState
    val state: StateFlow<State> = mutableState

    init {
        Logger.e { "init called" }
        viewModelScope.coroutineScope.launch {
            try {
                val peripheral = bluetoothRepository.connect(bluetoothDevice.identifier)
                when (bluetoothDevice) {
                    is BluetoothDevice.AirTag -> {
                        val batteryLevel = getAirTagData(bluetoothDevice)
                        mutableState.value =
                            State.Loaded.AirTag.Loaded(bluetoothDevice, batteryLevel)
                    }

                    is BluetoothDevice.MovisensSensor -> {
                        val model = readMovisensCharacteristics(peripheral)
                        mutableState.value =
                            State.Loaded.MovisensSensorLoaded(bluetoothDevice, model)
                    }

                    is BluetoothDevice.OtherBleDevice -> TODO()
                }
            } catch (e: Exception) {
                mutableState.value = State.Error(bluetoothDevice, e.toString())
            }
        }
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun playAirtagSound() {
        val loadedState = mutableState.value as State.Loaded.AirTag.Loaded
        viewModelScope.coroutineScope.launch {
            try {
                mutableState.value =
                    State.Loaded.AirTag.Writing(bluetoothDevice, loadedState.batteryLevel)
                bluetoothRepository.writeCharacteristic(
                    bluetoothDevice.identifier,
                    "7DFC9001-7D1C-4951-86AA-8D9728F8D66C".lowercase(),
                    ubyteArrayOf(175.toUByte()).toByteArray()
                )
                mutableState.value =
                    State.Loaded.AirTag.Loaded(bluetoothDevice, loadedState.batteryLevel)
            } catch (e: Exception) {
                mutableState.value = State.Error(bluetoothDevice, e.toString())
            }

        }
    }

    private fun getAirTagData(airTag: BluetoothDevice.AirTag): String {
        val status = airTag.manufacturerData[2]

        return when ((status.toInt() shr 6) and 0x03) {
            0x00 -> "FULL"
            0x01 -> "MEDIUM"
            0x02 -> "LOW"
            0x03 -> "VERY LOW"
            else -> "UNKNOWN"
        }
    }

    private suspend fun readMovisensCharacteristics(identifier: String): MovisensSensorValues {

        val list = bluetoothRepository.readCharacteristics(
            identifier,
            listOf(BatteryLevel, ManufacturerName, FirmwareRevision, ModelNumber, SerialNumber)
        )

        Logger.e { list.toString() }

        return MovisensSensorValues(
            list!![0]!! as Short,
            list[1]!! as String,
            list[2]!! as String,
            list[3]!! as String,
            list[4]!! as String,
        )
    }

    override fun onCleared() {
        runBlocking {
            bluetoothRepository.disconnect(bluetoothDevice.identifier)
        }
        super.onCleared()
    }

    sealed class State(open val bluetoothDevice: BluetoothDevice) {
        data class Loading(override val bluetoothDevice: BluetoothDevice) : State(bluetoothDevice)
        sealed class Loaded(override val bluetoothDevice: BluetoothDevice) :
            State(bluetoothDevice) {
            data class MovisensSensorLoaded(
                override val bluetoothDevice: BluetoothDevice,
                val movisensSensorValues: MovisensSensorValues
            ) : Loaded(bluetoothDevice)

            sealed class AirTag(
                override val bluetoothDevice: BluetoothDevice,
                open val batteryLevel: String
            ) : Loaded(bluetoothDevice) {
                data class Loaded(
                    override val bluetoothDevice: BluetoothDevice,
                    override val batteryLevel: String
                ) : AirTag(bluetoothDevice, batteryLevel)

                data class Writing(
                    override val bluetoothDevice: BluetoothDevice,
                    override val batteryLevel: String
                ) : AirTag(bluetoothDevice, batteryLevel)
            }
        }

        class Error(
            override val bluetoothDevice: BluetoothDevice,
            errorMessage: String?
        ) : State(bluetoothDevice) {
            val error: String = errorMessage ?: "Error"
        }
    }

}

data class MovisensSensorValues(
    val batteryLevel: Short,
    val manufacturer: String,
    val firmware: String,
    val modelNumber: String,
    val serialNumber: String
)