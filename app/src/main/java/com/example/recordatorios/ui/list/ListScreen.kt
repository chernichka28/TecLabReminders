package com.example.recordatorios.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.recordatorios.ui.components.ReminderCard
import com.example.recordatorios.ui.components.StatusMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onCrearRecordatorio: () -> Unit,
    onRecordatorioClick: (Long) -> Unit,
    viewModel: ListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Recordatorios") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearRecordatorio) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "Crear recordatorio")
            }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        when (val state = uiState) {
            ListUiState.Loading -> StatusMessage("Cargando…", modifier, loading = true)
            ListUiState.Empty -> StatusMessage(
                "Todavía no existen recordatorios", modifier,
                actionLabel = "Crear recordatorio", onAction = onCrearRecordatorio
            )
            is ListUiState.Error -> StatusMessage(
                state.message, modifier,
                actionLabel = "Reintentar", onAction = viewModel::load
            )
            is ListUiState.Success -> LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.reminders, key = { it.id }) { reminder ->
                    ReminderCard(reminder, onClick = { onRecordatorioClick(reminder.id) })
                }
            }
        }
    }
}
