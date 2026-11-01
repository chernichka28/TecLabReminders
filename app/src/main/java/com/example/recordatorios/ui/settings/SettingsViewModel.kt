package com.example.recordatorios.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.UserPreferencesRepository
import com.example.recordatorios.domain.ReminderOrder
import com.example.recordatorios.domain.UserPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: UserPreferencesRepository = AppContainer.preferences
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = repository.preferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    fun changeDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }

    fun changeShowCompleted(show: Boolean) {
        viewModelScope.launch { repository.setShowCompleted(show) }
    }

    fun changeReminderOrder(order: ReminderOrder) {
        viewModelScope.launch { repository.setReminderOrder(order) }
    }
}
