package com.example.recordatorios.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.ui.components.CategorySelector
import com.example.recordatorios.ui.components.FormTextField
import com.example.recordatorios.ui.components.PrioritySelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    onGuardado: () -> Unit,
    onCancelar: () -> Unit,
    viewModel: CreateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val saveState = uiState.saveState

    LaunchedEffect(saveState) {
        if (saveState == SaveState.Success) onGuardado()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Nuevo recordatorio") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormTextField("Título", uiState.title, uiState.titleError, viewModel::onTitleChange)
            FormTextField(
                "Descripción", uiState.description, uiState.descriptionError, viewModel::onDescriptionChange,
                minLines = 3
            )

            Text("Categoría")
            CategorySelector(uiState.categories, uiState.categoryId, viewModel::onCategorySelect)

            Text("Prioridad")
            PrioritySelector(uiState.priority, viewModel::onPrioritySelect)

            if (saveState is SaveState.Error) {
                Text(saveState.message, color = MaterialTheme.colorScheme.error)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
                Button(onClick = viewModel::onSave, enabled = uiState.canSave) {
                    Text(if (saveState == SaveState.Saving) "Guardando…" else "Guardar")
                }
            }
        }
    }
}
