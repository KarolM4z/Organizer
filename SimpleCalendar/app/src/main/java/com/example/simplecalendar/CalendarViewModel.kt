package com.example.simplecalendar

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalendarViewModel : ViewModel() {
    private val sharedPreferencesKey = "SimpleCalendarPreferences"
    private val data = mutableMapOf<LocalDate, DayData>()
    private val themePreferenceKey = "themePreference"

    enum class ThemeMode { LIGHT, DARK, SYSTEM }

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun loadThemeFromPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val themeName = sharedPreferences.getString(themePreferenceKey, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        try {
            _themeMode.value = ThemeMode.valueOf(themeName)
        } catch (e: IllegalArgumentException) {
            _themeMode.value = ThemeMode.SYSTEM
            sharedPreferences.edit().putString(themePreferenceKey, ThemeMode.SYSTEM.name).apply()
            Log.e("ViewModel", "Invalid theme stored. Resetting to SYSTEM.", e)
        }
    }

    fun saveThemePreference(context: Context, mode: ThemeMode) {
        val sharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(themePreferenceKey, mode.name)
            apply()
        }
        _themeMode.value = mode
    }

    fun loadNotesFromSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        sharedPreferences.all.forEach { entry ->
            try {
                val (dateString, type) = entry.key.split("_")
                val date = LocalDate.parse(dateString)
                val dayData = data.getOrPut(date) { DayData(0f, 0f, "") }

                when (type) {
                    "earnings" -> dayData.earnings = (entry.value as? Float) ?: 0f
                    "spendings" -> dayData.spendings = (entry.value as? Float) ?: 0f
                    "note" -> dayData.note = entry.value as? String ?: ""
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error loading SharedPreferences data: ${entry.key}", e)
            }
        }
    }

    fun saveDayData(context: Context, date: LocalDate, dayData: DayData) {
        val sharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putFloat("${date}_earnings", dayData.earnings)
            putFloat("${date}_spendings", dayData.spendings)
            putString("${date}_note", dayData.note)
            apply()
        }
        data[date] = dayData
    }

    fun calculateMonthlyProfit(yearMonth: YearMonth): Double {
        val monthDays = yearMonth.lengthOfMonth()
        return (1..monthDays).sumOf { day ->
            val dayData = getDayData(LocalDate.of(yearMonth.year, yearMonth.month, day))
            (dayData.earnings - dayData.spendings).toDouble() // Convert Float result to Double
        }
    }


    fun getDayData(date: LocalDate): DayData = data[date] ?: DayData(0f, 0f, "")

    data class DayData(var earnings: Float, var spendings: Float, var note: String)
}
