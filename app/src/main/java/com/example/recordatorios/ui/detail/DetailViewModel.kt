package com.example.recordatorios.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.InMemoryReminderRepository
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.domain.Reminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val reminder: Reminder) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: ReminderRepository = InMemoryReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            _uiState.update { DetailUiState.Loading }
            val reminder = repository.getReminder(id)
            _uiState.update {
                if (reminder != null) DetailUiState.Success(reminder)
                else DetailUiState.Error("El recordatorio no existe")
            }
        }
    }
}
