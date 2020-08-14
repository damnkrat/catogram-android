package ua.itaysonlab

import ua.itaysonlab.CatogramNative.isHqAvailable

object HqVoice {
    @JvmStatic
    fun getSampleRate(): Int {
        return if (isHqAvailable()) {
            48000
        } else {
            16000
        }
    }

    @JvmStatic
    fun isEnabled(): Int {
        return if (isHqAvailable()) {
            1
        } else {
            0
        }
    }
}