package com.example.recordatorios.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.recordatorios.domain.ReminderOrder
import com.example.recordatorios.domain.UserPreferences
import com.example.recordatorios.domain.parseOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

private object PreferenceKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    val REMINDER_ORDER = stringPreferencesKey("reminder_order")
}

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {

    val preferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            UserPreferences(
                darkModeEnabled = values[PreferenceKeys.DARK_MODE] ?: false,
                showCompleted = values[PreferenceKeys.SHOW_COMPLETED] ?: true,
                reminderOrder = parseOrder(values[PreferenceKeys.REMINDER_ORDER])
            )
        }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[PreferenceKeys.DARK_MODE] = enabled }
    }

    suspend fun setShowCompleted(show: Boolean) {
        dataStore.edit { it[PreferenceKeys.SHOW_COMPLETED] = show }
    }

    suspend fun setReminderOrder(order: ReminderOrder) {
        dataStore.edit { it[PreferenceKeys.REMINDER_ORDER] = order.name }
    }
}
