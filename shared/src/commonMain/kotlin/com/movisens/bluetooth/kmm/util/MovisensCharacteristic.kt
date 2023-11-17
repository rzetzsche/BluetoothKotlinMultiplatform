package com.movisens.bluetooth.kmm.util

sealed class MovisensCharacteristic<T>(val name: String, val uuid: String) {
    abstract fun toByte(): ByteArray
    abstract fun fromByte(byteArray: ByteArray): T
}

data object BatteryLevel :
    MovisensCharacteristic<Short>("Battery Level", "00002a19-0000-1000-8000-00805f9b34fb") {
    override fun toByte(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun fromByte(byteArray: ByteArray): Short {
        return GattByteBuffer.wrap(byteArray).uint8
    }
}

data object FirmwareRevision :
    MovisensCharacteristic<String>(
        "Firmware Revision String",
        "00002a26-0000-1000-8000-00805f9b34fb"
    ) {
    override fun toByte(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun fromByte(byteArray: ByteArray): String {
        return GattByteBuffer.wrap(byteArray).getString()
    }
}

data object ManufacturerName :
    MovisensCharacteristic<String>(
        "Manufacturer Name String",
        "00002a29-0000-1000-8000-00805f9b34fb"
    ) {
    override fun toByte(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun fromByte(byteArray: ByteArray): String {
        return GattByteBuffer.wrap(byteArray).getString()
    }
}

data object ModelNumber :
    MovisensCharacteristic<String>("Model Number String", "00002a24-0000-1000-8000-00805f9b34fb") {
    override fun toByte(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun fromByte(byteArray: ByteArray): String {
        return GattByteBuffer.wrap(byteArray).getString()
    }
}

data object SerialNumber :
    MovisensCharacteristic<String>("Serial Number String", "00002a25-0000-1000-8000-00805f9b34fb") {
    override fun toByte(): ByteArray {
        TODO("Not yet implemented")
    }

    override fun fromByte(byteArray: ByteArray): String {
        return GattByteBuffer.wrap(byteArray).getString()
    }
}