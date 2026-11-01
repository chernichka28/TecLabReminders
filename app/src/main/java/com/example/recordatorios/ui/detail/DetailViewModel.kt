package com.example.recordatorios.ui.detail

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderState
import com.example.recordatorios.domain.formatDate
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val reminder: Reminder) : DetailUiState
    data class Error(val message: String) : DetailUiState
    data object Deleted : DetailUiState
}

class DetailViewModel(
    private val repository: ReminderRepository = AppContainer.reminders
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun load(id: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.observeReminder(id).collect { reminder ->
                _uiState.update {
                    if (reminder != null) DetailUiState.Success(reminder)
                    else DetailUiState.Error("El recordatorio no existe")
                }
            }
        }
    }

    fun complete(reminder: Reminder) {
        viewModelScope.launch {
            try {
                repository.save(reminder.copy(state = ReminderState.Done(formatDate(System.currentTimeMillis()))))
            } catch (e: SQLiteException) {
                _uiState.update { DetailUiState.Error("No se pudo guardar el cambio") }
            }
        }
    }

    fun delete(id: String) {
        loadJob?.cancel()
        viewModelScope.launch {
            try {
                repository.delete(id)
                _uiState.update { DetailUiState.Deleted }
            } catch (e: SQLiteException) {
                _uiState.update { DetailUiState.Error("No se pudo eliminar el recordatorio") }
            }
        }
    }
}
