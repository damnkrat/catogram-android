package ua.itaysonlab

import ua.itaysonlab.CatogramNative.isHqAvailable

object HqVoice {
    @JvmStatic
    fun getSampleRate(): Int {
        return 48000
    }

    @JvmStatic
    fun isEnabled(): Int {
        return 1
    }
}