package com.example.recordatorios.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.R
import com.example.recordatorios.domain.ReminderOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onVolver: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SwitchRow("Tema oscuro", preferences.darkModeEnabled, viewModel::changeDarkMode)
            SwitchRow("Mostrar completados", preferences.showCompleted, viewModel::changeShowCompleted)

            Text("Ordenar por")
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = preferences.reminderOrder == ReminderOrder.BY_DATE,
                    onClick = { viewModel.changeReminderOrder(ReminderOrder.BY_DATE) }
                )
                Text("Fecha")
                RadioButton(
                    selected = preferences.reminderOrder == ReminderOrder.BY_PRIORITY,
                    onClick = { viewModel.changeReminderOrder(ReminderOrder.BY_PRIORITY) }
                )
                Text("Prioridad")
            }

            OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}
