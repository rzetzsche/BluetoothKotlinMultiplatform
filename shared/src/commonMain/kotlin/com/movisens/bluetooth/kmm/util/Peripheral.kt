package com.movisens.bluetooth.kmm.util

import com.juul.kable.Identifier
import com.juul.kable.Peripheral
import kotlinx.coroutines.CoroutineScope

expect fun CoroutineScope.getPeripheral(identifier: Identifier): Peripheral
