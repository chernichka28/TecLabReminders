package com.example.recordatorios.ui.list

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
import java.io.IOException

sealed interface ListUiState {
    data object Loading : ListUiState
    data object Empty : ListUiState
    data class Success(val reminders: List<Reminder>) : ListUiState
    data class Error(val message: String) : ListUiState
}

class ListViewModel(
    private val repository: ReminderRepository = InMemoryReminderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.update { ListUiState.Loading }
            try {
                val list = repository.getReminders()
                _uiState.update { if (list.isEmpty()) ListUiState.Empty else ListUiState.Success(list) }
            } catch (e: IOException) {
                _uiState.update { ListUiState.Error(e.message ?: "Error al cargar") }
            }
        }
    }
}
