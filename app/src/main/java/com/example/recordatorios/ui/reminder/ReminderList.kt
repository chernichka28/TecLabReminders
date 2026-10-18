package com.example.recordatorios.ui.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.priorityLabel
import com.example.recordatorios.domain.stateLabel
import com.example.recordatorios.sample.categoriesColorMapping


@Composable
fun ReminderList(reminders: List<Reminder>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(reminders, key = { it.id }) { reminder ->
            ShowReminder(reminder = reminder)
        }
    }
}

@Composable
fun ShowReminder(reminder: Reminder, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = categoriesColorMapping[reminder.category.id]
                ?: MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reminder.title,
                style = MaterialTheme.typography.titleMedium
            )
            reminder.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(text = "Categoría: ${reminder.category.title}")
            Text(text = "Prioridad: ${priorityLabel(reminder.priority)}")
            Text(text = "Estado: ${stateLabel(reminder.state)}")
        }
    }
}
