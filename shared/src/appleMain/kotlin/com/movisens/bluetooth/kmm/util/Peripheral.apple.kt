package com.movisens.bluetooth.kmm.util

import com.juul.kable.Identifier
import com.juul.kable.Peripheral
import com.juul.kable.peripheral
import kotlinx.coroutines.CoroutineScope

actual fun CoroutineScope.getPeripheral(identifier: Identifier): Peripheral {
    return peripheral(identifier)
}