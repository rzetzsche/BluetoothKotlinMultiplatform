package com.movisens.bluetooth.kmm.util

import com.ditchoom.buffer.ByteOrder
import com.ditchoom.buffer.PlatformBuffer
import com.ditchoom.buffer.wrap

class GattByteBuffer {
    private lateinit var buffer: PlatformBuffer
    val uint8: Short
        get() {
            var result: Short = buffer.readByte().toShort()
            if (result < 0) {
                val i = (1 shl 8).toShort()
                result = (result + i).toShort()
            }
            return result
        }

    fun getString(): String {
        var result = ""
        var c: Byte? = null
        while (buffer.hasRemaining() && buffer.readByte().also { c = it }.toInt() != 0) {
            result += Char(c!!.toUShort())
        }
        return result
    }

    companion object {
        fun wrap(byteArray: ByteArray): GattByteBuffer {
            val result = GattByteBuffer()
            result.buffer = PlatformBuffer.wrap(byteArray, byteOrder = ByteOrder.LITTLE_ENDIAN)
            return result
        }
    }
}