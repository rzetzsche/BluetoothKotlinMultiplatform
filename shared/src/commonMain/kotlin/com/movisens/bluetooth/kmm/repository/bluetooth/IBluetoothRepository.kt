package com.movisens.bluetooth.kmm.repository.bluetooth

import com.movisens.bluetooth.kmm.util.JavaSerializable
import com.movisens.bluetooth.kmm.util.MovisensCharacteristic
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface IBluetoothRepository {
    fun startScan(): Flow<List<BluetoothDevice>>
    suspend fun connect(identifier: String): String
    suspend fun disconnect(identifier: String): String
    suspend fun <T> readCharacteristic(
        identifier: String, characteristic: MovisensCharacteristic<T>
    ): T

    suspend fun readCharacteristics(
        identifier: String, characteristics: List<MovisensCharacteristic<*>>
    ): List<Any?>

    suspend fun getManufacturerData(identifier: String): ByteArray?

    @OptIn(ExperimentalUnsignedTypes::class)
    suspend fun writeCharacteristic(identifier: String, characteristicUuid: String, data: ByteArray)
}

sealed class BluetoothDevice(
    open val name: String?,
    open val identifier: String,
    open val rssi: Float?,
    open val timeOfFirstDiscorvery: Instant = Clock.System.now()
) : JavaSerializable {
    class AirTag(
        override val name: String?,
        override val identifier: String,
        override val rssi: Float?,
        val manufacturerData: ByteArray,
        override val timeOfFirstDiscorvery: Instant = Clock.System.now(),
    ) : BluetoothDevice(name, identifier, rssi, timeOfFirstDiscorvery) {
        enum class BatteryLevel(s: String) {
            Full("Full"), Medium("Medium"), Low("Low"), Very_Low("Very Low"), Unknown("Unknown")
        }
    }

    class MovisensSensor(
        override val name: String?,
        override val identifier: String,
        override val rssi: Float?,
        override val timeOfFirstDiscorvery: Instant = Clock.System.now()
    ) : BluetoothDevice(name, identifier, rssi, timeOfFirstDiscorvery)

    class OtherBleDevice(
        override val name: String?,
        override val identifier: String,
        override val rssi: Float?,
        override val timeOfFirstDiscorvery: Instant = Clock.System.now()
    ) : BluetoothDevice(name, identifier, rssi, timeOfFirstDiscorvery)
}