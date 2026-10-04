package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class AudioPlayerManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val _currentlyPlaying = MutableStateFlow<String?>(null)
    val currentlyPlaying: StateFlow<String?> = _currentlyPlaying.asStateFlow()

    private val audioDir: File by lazy {
        File(context.filesDir, "user_audio").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Copies an audio file picked via SAF/ContentResolver into the app's private files directory.
     * This guarantees persistent access across app restarts and device reboots.
     * Returns the absolute path of the saved file.
     */
    suspend fun saveAudioFromUri(uri: Uri, prefix: String = "audio"): Result<String> = withContext(Dispatchers.IO) {
        try {
            val extension = getExtensionFromUri(uri)
            val fileName = "${prefix}_${System.currentTimeMillis()}.$extension"
            val targetFile = File(audioDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext Result.failure(Exception("تعذر فتح ملف الصوت المحدد"))

            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to save audio file", e)
            Result.failure(e)
        }
    }

    private fun getExtensionFromUri(uri: Uri): String {
        return try {
            val mime = context.contentResolver.getType(uri)
            when (mime) {
                "audio/mpeg", "audio/mp3" -> "mp3"
                "audio/wav", "audio/x-wav" -> "wav"
                "audio/m4a", "audio/mp4" -> "m4a"
                "audio/ogg" -> "ogg"
                "audio/aac" -> "aac"
                else -> {
                    val path = uri.path.orEmpty()
                    if (path.contains(".")) path.substringAfterLast(".") else "mp3"
                }
            }
        } catch (_: Exception) {
            "mp3"
        }
    }

    /**
     * Plays a local audio file or content URI safely.
     * If the path is empty, non-existent, or invalid, invokes onError without crashing.
     */
    fun play(
        pathOrUri: String,
        onCompletion: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (pathOrUri.isBlank()) {
            onError("الصوت غير متوفر")
            return
        }

        stop()

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }

            if (pathOrUri.startsWith("content://") || pathOrUri.startsWith("file://")) {
                val uri = Uri.parse(pathOrUri)
                player.setDataSource(context, uri)
            } else {
                val file = File(pathOrUri)
                if (!file.exists() || file.length() == 0L) {
                    onError("الصوت غير متوفر أو تم نقل الملف")
                    return
                }
                player.setDataSource(file.absolutePath)
            }

            player.setOnPreparedListener { mp ->
                mp.start()
                _currentlyPlaying.value = pathOrUri
            }

            player.setOnCompletionListener { mp ->
                _currentlyPlaying.value = null
                mp.release()
                mediaPlayer = null
                onCompletion()
            }

            player.setOnErrorListener { mp, what, extra ->
                Log.e("AudioPlayerManager", "MediaPlayer error: what=$what, extra=$extra")
                _currentlyPlaying.value = null
                mp.release()
                mediaPlayer = null
                onError("تعذر تشغيل الملف الصوتي")
                true
            }

            player.prepareAsync()
            mediaPlayer = player

        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Exception preparing audio playback", e)
            _currentlyPlaying.value = null
            mediaPlayer?.release()
            mediaPlayer = null
            onError("الصوت غير متوفر: ${e.localizedMessage ?: "خطأ في الملف"}")
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error stopping playback", e)
        } finally {
            mediaPlayer = null
            _currentlyPlaying.value = null
        }
    }

    fun isPlaying(pathOrUri: String): Boolean {
        return _currentlyPlaying.value == pathOrUri && mediaPlayer?.isPlaying == true
    }

    fun deleteAudioFile(path: String) {
        if (path.isBlank()) return
        try {
            val file = File(path)
            if (file.exists() && file.parentFile?.canonicalPath == audioDir.canonicalPath) {
                file.delete()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to delete audio file: $path", e)
        }
    }

    fun release() {
        stop()
    }
}
