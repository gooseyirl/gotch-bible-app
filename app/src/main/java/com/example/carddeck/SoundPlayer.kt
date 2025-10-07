package com.example.carddeck

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundPlayer(context: Context) {
    private var soundPool: SoundPool
    private var clickSoundId: Int = -1
    private var countdownSoundId: Int = -1
    private var celebrationSoundId: Int = -1
    private var loaded = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                loaded = true
            }
        }

        // Try to load click sound (will fail gracefully if file doesn't exist)
        try {
            val clickResId = context.resources.getIdentifier("click", "raw", context.packageName)
            if (clickResId != 0) {
                clickSoundId = soundPool.load(context, clickResId, 1)
            }
        } catch (e: Exception) {
            // Sound file not found, continue without it
        }

        // Try to load countdown timer sound
        try {
            val countdownResId = context.resources.getIdentifier("countdown_timer", "raw", context.packageName)
            if (countdownResId != 0) {
                countdownSoundId = soundPool.load(context, countdownResId, 1)
            }
        } catch (e: Exception) {
            // Sound file not found, continue without it
        }

        // Try to load celebration sound
        try {
            val celebrationResId = context.resources.getIdentifier("celebration", "raw", context.packageName)
            if (celebrationResId != 0) {
                celebrationSoundId = soundPool.load(context, celebrationResId, 1)
            }
        } catch (e: Exception) {
            // Sound file not found, continue without it
        }
    }

    fun playClick() {
        if (loaded && clickSoundId != -1) {
            soundPool.play(clickSoundId, 0.5f, 0.5f, 1, 0, 1.0f)
        }
    }

    fun playCountdown() {
        if (loaded && countdownSoundId != -1) {
            soundPool.play(countdownSoundId, 0.5f, 0.5f, 1, 0, 1.0f)
        }
    }

    fun playCelebration() {
        if (loaded && celebrationSoundId != -1) {
            soundPool.play(celebrationSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool.release()
    }
}
