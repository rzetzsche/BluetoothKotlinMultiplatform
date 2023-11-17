package com.movisens.bluetooth.kmm.util

import AdvertisementDataRetrievalKeys
import dev.bluefalcon.BlueFalconDelegate
import dev.bluefalcon.BluetoothCharacteristic
import dev.bluefalcon.BluetoothCharacteristicDescriptor
import dev.bluefalcon.BluetoothPeripheral

interface MovisensBlueFalconDelegate : BlueFalconDelegate {
    override fun didCharacteristcValueChanged(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic
    ) {
    }

    override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {
    }

    override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {
    }

    override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {
    }

    override fun didDiscoverDevice(
        bluetoothPeripheral: BluetoothPeripheral,
        advertisementData: Map<AdvertisementDataRetrievalKeys, Any>
    ) {
    }

    override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {
    }

    override fun didReadDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
    }

    override fun didRssiUpdate(bluetoothPeripheral: BluetoothPeripheral) {
    }

    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {
    }

    override fun didWriteCharacteristic(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic,
        success: Boolean
    ) {
    }

    override fun didWriteDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
    }
}