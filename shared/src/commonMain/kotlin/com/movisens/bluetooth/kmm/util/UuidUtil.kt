package com.movisens.bluetooth.kmm.util

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom


object UuidUtil {
    private const val leastSigUuidBits = -0x7fffff7fa064cb05L
    fun stringToUuid(uuidString: String): Uuid {
        val uuid: Uuid = if (uuidString.length == 4) {
            /* it is a short form uuid */
            Uuid(uuidString.toLong(16) shl 32 or 0x1000L, leastSigUuidBits)
        } else {
            uuidFrom(uuidString)
        }
        return uuid
    }
}

