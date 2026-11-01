package com.example.recordatorios.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.data.syncResultMessage
import com.example.recordatorios.domain.Reminder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ListUiState {
    data object Loading : ListUiState
    data object Empty : ListUiState
    data class Success(val reminders: List<Reminder>) : ListUiState
    data class Error(val message: String) : ListUiState
}

class ListViewModel(
    private val repository: ReminderRepository = AppContainer.reminders
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { ListUiState.Loading }
            repository.observeReminders()
                .catch { _uiState.update { ListUiState.Error("No se pudieron leer los recordatorios") } }
                .collect { list ->
                    _uiState.update { if (list.isEmpty()) ListUiState.Empty else ListUiState.Success(list) }
                }
        }
    }

    fun synchronize() {
        viewModelScope.launch {
            _syncMessage.update { "Sincronizando…" }
            val message = syncResultMessage(repository.synchronize())
            _syncMessage.update { message }
        }
    }
}
