package com.example.recordatorios.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.R
import com.example.recordatorios.domain.ReminderState
import com.example.recordatorios.domain.formatDate
import com.example.recordatorios.domain.priorityLabel
import com.example.recordatorios.domain.stateLabel
import com.example.recordatorios.domain.syncLabel
import com.example.recordatorios.ui.components.StatusMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    id: String,
    onVolver: () -> Unit,
    viewModel: DetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id) { viewModel.load(id) }

    LaunchedEffect(uiState) {
        if (uiState == DetailUiState.Deleted) onVolver()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        when (val state = uiState) {
            DetailUiState.Loading -> StatusMessage("Cargando…", modifier, loading = true)
            DetailUiState.Deleted -> StatusMessage("Recordatorio eliminado", modifier)
            is DetailUiState.Error -> StatusMessage(state.message, modifier, actionLabel = "Volver", onAction = onVolver)
            is DetailUiState.Success -> Column(
                modifier = modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val reminder = state.reminder
                Text(reminder.title, style = MaterialTheme.typography.headlineSmall)
                Text("Categoría: ${reminder.category.title}")
                Text("Prioridad: ${priorityLabel(reminder.priority)}")
                Text("Estado: ${stateLabel(reminder.state)}")
                Text("Fecha: ${formatDate(reminder.date)}")
                Text("Descripción: ${reminder.description ?: "Sin descripción"}")
                Text("Sincronización: ${syncLabel(reminder.syncStatus)}")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reminder.state !is ReminderState.Done) {
                        Button(onClick = { viewModel.complete(reminder) }) { Text("Completar") }
                    }
                    OutlinedButton(onClick = { viewModel.delete(reminder.id) }) { Text("Eliminar") }
                }
            }
        }
    }
}
