package com.example.simplecalendar.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    private val THEME_KEY = booleanPreferencesKey("dark_theme_enabled")

    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: false
        }

    suspend fun toggleTheme(darkTheme: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = darkTheme
        }
    }
}
