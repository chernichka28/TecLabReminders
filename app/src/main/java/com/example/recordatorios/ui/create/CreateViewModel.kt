package com.example.recordatorios.ui.create

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.domain.Category
import com.example.recordatorios.domain.Priority
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderState
import com.example.recordatorios.domain.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface SaveState {
    data object Editing : SaveState
    data object Saving : SaveState
    data object Success : SaveState
    data class Error(val message: String) : SaveState
}

data class CreateUiState(
    val categories: List<Category> = emptyList(),
    val title: String = "",
    val description: String = "",
    val categoryId: Long? = null,
    val priority: Priority? = null,
    val titleTouched: Boolean = false,
    val saveState: SaveState = SaveState.Editing
) {
    private val titleValid get() = title.trim().length >= 4
    private val descriptionValid get() = description.length <= 200

    val titleError get() = if (titleTouched && !titleValid) "El título debe tener al menos 4 caracteres" else null
    val descriptionError get() = if (!descriptionValid) "La descripción no puede superar los 200 caracteres" else null

    val isValid get() = titleValid && descriptionValid &&
        categories.any { it.id == categoryId } && priority != null
    val canSave get() = isValid && saveState != SaveState.Saving
}

class CreateViewModel(
    private val repository: ReminderRepository = AppContainer.reminders
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState(categories = repository.getCategories()))
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title, titleTouched = true) }

    fun onDescriptionChange(description: String) = _uiState.update { it.copy(description = description) }

    fun onCategorySelect(id: Long) = _uiState.update { it.copy(categoryId = id) }

    fun onPrioritySelect(priority: Priority) = _uiState.update { it.copy(priority = priority) }

    fun onSave() {
        val form = _uiState.value
        val priority = form.priority ?: return
        if (!form.canSave) return

        viewModelScope.launch {
            _uiState.update { it.copy(saveState = SaveState.Saving) }
            try {
                repository.save(
                    Reminder(
                        id = UUID.randomUUID().toString(),
                        title = form.title.trim(),
                        description = form.description.ifBlank { null },
                        state = ReminderState.Waiting,
                        category = form.categories.first { it.id == form.categoryId },
                        priority = priority,
                        date = System.currentTimeMillis(),
                        syncStatus = SyncStatus.PENDING
                    )
                )
                _uiState.update { it.copy(saveState = SaveState.Success) }
            } catch (e: SQLiteException) {
                _uiState.update { it.copy(saveState = SaveState.Error("No se pudo guardar el recordatorio")) }
            }
        }
    }
}
