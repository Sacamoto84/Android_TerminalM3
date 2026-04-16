package com.example.terminalm3.utils

import android.media.AudioManager
import android.media.ToneGenerator
import timber.log.Timber

object PhoneBeeper {

    private val lock = Any()
    @Volatile
    private var toneGenerator: ToneGenerator? = null

    private fun getToneGenerator(): ToneGenerator? {
        toneGenerator?.let { return it }

        return synchronized(lock) {
            toneGenerator?.let { return@synchronized it }

            runCatching {
                ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100).also {
                    toneGenerator = it
                }
            }.getOrElse { error ->
                Timber.e(error, "Не удалось создать ToneGenerator")
                null
            }
        }
    }

    fun beep(durationMs: Int = 250) {
        val generator = getToneGenerator() ?: return

        runCatching {
            generator.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs)
        }.onFailure { error ->
            Timber.e(error, "Не удалось воспроизвести звуковой сигнал")
        }
    }
}
