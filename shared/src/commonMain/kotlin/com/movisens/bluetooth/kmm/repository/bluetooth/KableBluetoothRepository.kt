package com.movisens.bluetooth.kmm.repository.bluetooth

import com.juul.kable.Advertisement
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.WriteType
import com.juul.kable.toIdentifier
import com.movisens.bluetooth.kmm.util.MovisensCharacteristic
import com.movisens.bluetooth.kmm.util.getPeripheral
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.experimental.and

class KableBluetoothRepository : IBluetoothRepository {

    override fun startScan(): Flow<List<BluetoothDevice>> {
        return Scanner()
            .advertisements
            .filter { it.rssi > -70 }
            .map { Pair(it, Clock.System.now()) }
            .bufferToMap(
                selector = { it.first.identifier.toString() },
                valueSelector = { old, new -> old?.run { new.copy(second = this.second) } ?: new })
            .map { it.values.map { getBluetoothDevice(it.first, it.second) } }
    }

    private fun getBluetoothDevice(
        advertisement: Advertisement,
        timeOfFirstDiscovery: Instant
    ): BluetoothDevice {
        val manufacturerData = advertisement.manufacturerData(0x004c)
        if (manufacturerData != null) {
            val statusByte: Byte = manufacturerData[2]
            val deviceTypeInt = (statusByte.and(0x30).toInt() shr 4)
            if (deviceTypeInt.toUInt() == 1u) {
                return BluetoothDevice.AirTag(
                    advertisement.name,
                    advertisement.identifier.toString(),
                    advertisement.rssi.toFloat(),
                    manufacturerData,
                    timeOfFirstDiscovery
                )
            }
        } else {
            if (advertisement.name?.startsWith("MOVISENS") == true) {
                return BluetoothDevice.MovisensSensor(
                    advertisement.name,
                    advertisement.identifier.toString(),
                    advertisement.rssi.toFloat(),
                    timeOfFirstDiscovery
                )
            }
        }
        return BluetoothDevice.OtherBleDevice(
            advertisement.name,
            advertisement.identifier.toString(),
            advertisement.rssi.toFloat(),
            timeOfFirstDiscovery
        )
    }

    override suspend fun connect(identifier: String): String {
        return kableCallback(identifier) {
            it.connect()
            it.identifier.toString()
        }.single()
    }


    override suspend fun disconnect(identifier: String): String {
        return kableCallback(identifier) {
            it.disconnect()
            it.identifier.toString()
        }.single()
    }

    private fun <T> kableCallback(
        identifier: String,
        callback: suspend (Peripheral) -> T
    ) =
        callbackFlow {
            val peripheral = getPeripheral(identifier.toIdentifier())
            trySend(callback(peripheral))
            channel.close()
            awaitClose()
        }

    override suspend fun <T> readCharacteristic(
        identifier: String,
        characteristic: MovisensCharacteristic<T>
    ): T {
        return kableCallback(identifier) {
            it.connect()
            val char = it.services
                ?.map { it.characteristics }
                ?.flatten()
                ?.first { it.characteristicUuid.toString() == characteristic.uuid }
            characteristic.fromByte(it.read(char!!))
        }.single()
    }

    override suspend fun getManufacturerData(identifier: String): ByteArray? {
        return null
    }

    override suspend fun readCharacteristics(
        identifier: String,
        characteristics: List<MovisensCharacteristic<*>>
    ): List<Any?> {
        return kableCallback(identifier) { peripheral ->
            peripheral.connect()

            val discoveredCharacteristics = peripheral.services
                ?.map { it.characteristics }
                ?.flatten()

            characteristics.map { movChar ->
                Pair(
                    movChar,
                    discoveredCharacteristics
                        ?.find { it.characteristicUuid.toString() == movChar.uuid }
                )
            }.filter { it.second != null }
                .map { it.first.fromByte(peripheral.read(it.second!!)) }
                .toList()
        }.single()
    }

    override suspend fun writeCharacteristic(
        identifier: String,
        characteristicUuid: String,
        data: ByteArray
    ) {
        kableCallback(identifier) {
            it.connect()
            val char = it.services
                ?.map { it.characteristics }
                ?.flatten()
                ?.first { it.characteristicUuid.toString() == characteristicUuid }

            it.write(char!!, data, WriteType.WithResponse)
        }.single()
    }
}

private fun <S, T> Flow<T>.bufferToMap(
    selector: (T) -> S,
    valueSelector: (T?, T) -> T
): Flow<HashMap<S, T>> {
    return flow {
        val buffer: HashMap<S, T> = hashMapOf()

        collect { element ->
            val key = selector(element)
            val value = valueSelector(buffer[key], element)
            buffer[key] = value
            emit(buffer)
        }
    }
}