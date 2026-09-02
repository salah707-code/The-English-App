package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("english_learning_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "ar") ?: "ar")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(prefs.getString(KEY_THEME, "SYSTEM") ?: "SYSTEM")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _pronunciation = MutableStateFlow(prefs.getString(KEY_PRONUNCIATION, "US") ?: "US")
    val pronunciation: StateFlow<String> = _pronunciation.asStateFlow()

    private val _speechRate = MutableStateFlow(prefs.getFloat(KEY_SPEECH_RATE, 1.0f))
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _audioEnabled = MutableStateFlow(prefs.getBoolean(KEY_AUDIO_ENABLED, true))
    val audioEnabled: StateFlow<Boolean> = _audioEnabled.asStateFlow()

    private val _dailyGoal = MutableStateFlow(prefs.getInt(KEY_DAILY_GOAL, 20))
    val dailyGoal: StateFlow<Int> = _dailyGoal.asStateFlow()

    private val _hideMastered = MutableStateFlow(prefs.getBoolean(KEY_HIDE_MASTERED, false))
    val hideMastered: StateFlow<Boolean> = _hideMastered.asStateFlow()

    private val _streakDays = MutableStateFlow(prefs.getInt(KEY_STREAK, 1))
    val streakDays: StateFlow<Int> = _streakDays.asStateFlow()

    private val _wordsLearnedToday = MutableStateFlow(getLearnedTodayCountInternal())
    val wordsLearnedToday: StateFlow<Int> = _wordsLearnedToday.asStateFlow()

    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
        _language.value = lang
    }

    fun setThemeMode(theme: String) {
        prefs.edit().putString(KEY_THEME, theme).apply()
        _themeMode.value = theme
    }

    fun setPronunciation(accent: String) {
        prefs.edit().putString(KEY_PRONUNCIATION, accent).apply()
        _pronunciation.value = accent
    }

    fun setSpeechRate(rate: Float) {
        prefs.edit().putFloat(KEY_SPEECH_RATE, rate).apply()
        _speechRate.value = rate
    }

    fun setAudioEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUDIO_ENABLED, enabled).apply()
        _audioEnabled.value = enabled
    }

    fun setDailyGoal(goal: Int) {
        prefs.edit().putInt(KEY_DAILY_GOAL, goal).apply()
        _dailyGoal.value = goal
    }

    fun setHideMastered(hide: Boolean) {
        prefs.edit().putBoolean(KEY_HIDE_MASTERED, hide).apply()
        _hideMastered.value = hide
    }

    fun recordLearningAction() {
        val today = getTodayDateKey()
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""
        var count = prefs.getInt(KEY_TODAY_COUNT, 0)
        var streak = prefs.getInt(KEY_STREAK, 1)

        if (lastDate != today) {
            val yesterday = getYesterdayDateKey()
            streak = if (lastDate == yesterday) streak + 1 else 1
            count = 1
            prefs.edit()
                .putString(KEY_LAST_DATE, today)
                .putInt(KEY_STREAK, streak)
                .putInt(KEY_TODAY_COUNT, count)
                .apply()
        } else {
            count += 1
            prefs.edit().putInt(KEY_TODAY_COUNT, count).apply()
        }

        _streakDays.value = streak
        _wordsLearnedToday.value = count
    }

    private fun getLearnedTodayCountInternal(): Int {
        val today = getTodayDateKey()
        val lastDate = prefs.getString(KEY_LAST_DATE, "") ?: ""
        return if (lastDate == today) prefs.getInt(KEY_TODAY_COUNT, 0) else 0
    }

    private fun getTodayDateKey(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun getYesterdayDateKey(): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    fun getAllSettingsMap(): Map<String, String> {
        return mapOf(
            KEY_LANGUAGE to _language.value,
            KEY_THEME to _themeMode.value,
            KEY_PRONUNCIATION to _pronunciation.value,
            KEY_SPEECH_RATE to _speechRate.value.toString(),
            KEY_AUDIO_ENABLED to _audioEnabled.value.toString(),
            KEY_DAILY_GOAL to _dailyGoal.value.toString(),
            KEY_HIDE_MASTERED to _hideMastered.value.toString(),
            KEY_STREAK to _streakDays.value.toString()
        )
    }

    fun restoreSettings(map: Map<String, String>) {
        map[KEY_LANGUAGE]?.let { setLanguage(it) }
        map[KEY_THEME]?.let { setThemeMode(it) }
        map[KEY_PRONUNCIATION]?.let { setPronunciation(it) }
        map[KEY_SPEECH_RATE]?.toFloatOrNull()?.let { setSpeechRate(it) }
        map[KEY_AUDIO_ENABLED]?.toBooleanStrictOrNull()?.let { setAudioEnabled(it) }
        map[KEY_DAILY_GOAL]?.toIntOrNull()?.let { setDailyGoal(it) }
        map[KEY_HIDE_MASTERED]?.toBooleanStrictOrNull()?.let { setHideMastered(it) }
    }

    companion object {
        const val KEY_LANGUAGE = "key_language"
        const val KEY_THEME = "key_theme"
        const val KEY_PRONUNCIATION = "key_pronunciation"
        const val KEY_SPEECH_RATE = "key_speech_rate"
        const val KEY_AUDIO_ENABLED = "key_audio_enabled"
        const val KEY_DAILY_GOAL = "key_daily_goal"
        const val KEY_HIDE_MASTERED = "key_hide_mastered"
        const val KEY_STREAK = "key_streak"
        const val KEY_LAST_DATE = "key_last_date"
        const val KEY_TODAY_COUNT = "key_today_count"
    }
}
