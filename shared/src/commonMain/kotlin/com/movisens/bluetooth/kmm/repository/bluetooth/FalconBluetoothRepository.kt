package com.movisens.bluetooth.kmm.repository.bluetooth

import com.movisens.bluetooth.kmm.util.MovisensBlueFalconDelegate
import com.movisens.bluetooth.kmm.util.MovisensCharacteristic
import com.movisens.bluetooth.kmm.util.UuidUtil.stringToUuid
import dev.bluefalcon.ApplicationContext
import dev.bluefalcon.BlueFalcon
import dev.bluefalcon.BluetoothCharacteristic
import dev.bluefalcon.BluetoothPeripheral
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.isActive


class FalconBluetoothRepository(applicationContext: ApplicationContextHelper) :
    IBluetoothRepository {
    private val blueFalcon = BlueFalcon(applicationContext.applicationContext, null)
    private val _mutableDeviceFlow = MutableStateFlow(hashMapOf<String, BluetoothPeripheral>())

    override fun startScan(): Flow<List<BluetoothDevice>> {
        return blueFalcon.peripherals
            .onStart {
                if (!blueFalcon.isScanning)
                    blueFalcon.scan()
            }
            .map { it.distinctBy { it.uuid } }
            .distinctUntilChanged()
            .onEach {
                _mutableDeviceFlow.value = it.fold(hashMapOf()) { map, device ->
                    map[device.uuid] = device
                    map
                }
            }
            .map { it.map { BluetoothDevice.OtherBleDevice(it.name, it.uuid, it.rssi) } }
            .onCompletion { blueFalcon.stopScanning() }
            .flowOn(Dispatchers.IO)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun connect(identifier: String): String {
        return connectFlow(identifier)
            .flatMapLatest {
                flow {
                    delay(1000)
                    emit(it)
                }
            }
            .onEach { device ->
                _mutableDeviceFlow.value = _mutableDeviceFlow.value.also {
                    it[device.uuid] = device
                }
            }
            .map { it.uuid }.first()
    }

    private fun connectFlow(deviceUuid: String) = channelFlow {
        val peripheral = _mutableDeviceFlow.value[deviceUuid]!!
        val callback = object : MovisensBlueFalconDelegate {
            override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {
                trySend(bluetoothPeripheral)
            }
        }
        blueFalcon.delegates.add(callback)
        blueFalcon.connect(peripheral, false)
        while (isActive) {
            delay(1000)
        }
        channel.close()
        awaitClose { blueFalcon.delegates.remove(callback) }
    }

    override suspend fun disconnect(identifier: String): String {
        return callbackFlow {
            val peripheral = _mutableDeviceFlow.value[identifier]!!
            val callback = object : MovisensBlueFalconDelegate {
                override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {
                    trySend(
                        bluetoothPeripheral.uuid
                    )
                    channel.close()
                }
            }
            blueFalcon.delegates.add(callback)
            blueFalcon.disconnect(peripheral)
            awaitClose { blueFalcon.delegates.remove(callback) }
        }.single()
    }

    override suspend fun <T> readCharacteristic(
        identifier: String,
        characteristic: MovisensCharacteristic<T>
    ): T {
        return callbackFlow {
            val peripheral = _mutableDeviceFlow.value[identifier]!!
            val callback = object : MovisensBlueFalconDelegate {
                override fun didCharacteristcValueChanged(
                    bluetoothPeripheral: BluetoothPeripheral,
                    bluetoothCharacteristic: BluetoothCharacteristic
                ) {
                    val correctChar = try {
                        stringToUuid(bluetoothCharacteristic.name!!).toString().lowercase() ==
                                stringToUuid(characteristic.uuid).toString().lowercase()
                    } catch (e: Exception) {
                        bluetoothCharacteristic.name.toString()
                            .lowercase() == characteristic.name.lowercase()
                    }
                    if (correctChar) {
                        trySendBlocking(characteristic.fromByte(bluetoothCharacteristic.value!!))
                        channel.close()
                    }
                }
            }
            try {
                val filteredChar = peripheral.services
                    .map { it.characteristics }
                    .flatten()
                    .first {
                        try {
                            stringToUuid(it.name!!).toString().lowercase() ==
                                    stringToUuid(characteristic.uuid).toString().lowercase()
                        } catch (e: Exception) {
                            it.name.toString().lowercase() == characteristic.name.lowercase()
                        }
                    }
                blueFalcon.delegates.add(callback)
                blueFalcon.readCharacteristic(peripheral, filteredChar)
            } catch (e: Exception) {
                cancel(cause = CancellationException("Error", e))
            } finally {
                awaitClose { blueFalcon.delegates.remove(callback) }
            }
        }.single()
    }

    override suspend fun readCharacteristics(
        identifier: String,
        characteristics: List<MovisensCharacteristic<*>>
    ): List<Any?> {
        return characteristics.map { readCharacteristic(identifier, it) }
    }

    override suspend fun getManufacturerData(identifier: String): ByteArray? {
        TODO("Not yet implemented")
    }

    override suspend fun writeCharacteristic(
        identifier: String,
        characteristicUuid: String,
        data: ByteArray
    ) {
        TODO("Not yet implemented")
    }
}

data class ApplicationContextHelper(val applicationContext: ApplicationContext)