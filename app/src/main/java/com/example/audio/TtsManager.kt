package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeech: String? = null
    private var pendingLocale: Locale = Locale.US
    private var speechRate: Float = 1.0f

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setLanguage(Locale.US)
            tts?.setSpeechRate(speechRate)
            pendingSpeech?.let { text ->
                speak(text, if (pendingLocale == Locale.UK) "UK" else "US", speechRate)
                pendingSpeech = null
            }
        } else {
            Log.e("TtsManager", "TextToSpeech initialization failed")
        }
    }

    fun speak(text: String, accent: String = "US", rate: Float = 1.0f) {
        if (text.isBlank()) return
        speechRate = rate
        val targetLocale = if (accent.equals("UK", ignoreCase = true)) Locale.UK else Locale.US

        if (!isInitialized || tts == null) {
            pendingSpeech = text
            pendingLocale = targetLocale
            return
        }

        try {
            val result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default US
                tts?.setLanguage(Locale.US)
            }
            tts?.setSpeechRate(rate.coerceIn(0.5f, 2.0f))
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TtsManager", "Error speaking text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TtsManager", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("TtsManager", "Error shutting down TTS", e)
        }
    }
}
