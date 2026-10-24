package com.example.recordatorios.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.recordatorios.data.categoriesColorMapping
import com.example.recordatorios.domain.Category
import com.example.recordatorios.domain.Priority
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.priorityLabel
import com.example.recordatorios.domain.stateLabel

@Composable
fun ReminderCard(
    reminder: Reminder,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = categoriesColorMapping[reminder.category.id]
                ?: MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(reminder.title, style = MaterialTheme.typography.titleMedium)
            Text("Categoría: ${reminder.category.title}")
            Text("Prioridad: ${priorityLabel(reminder.priority)}")
            Text("Estado: ${stateLabel(reminder.state)}")
        }
    }
}

@Composable
fun StatusMessage(
    message: String,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    actionLabel: String? = null,
    onAction: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        if (loading) CircularProgressIndicator()
        Text(message)
        if (actionLabel != null) {
            Button(onClick = onAction) { Text(actionLabel) }
        }
    }
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        minLines = minLines,
        modifier = modifier
            .fillMaxWidth()
            .semantics { if (error != null) error(error) }
    )
}

@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        categories.forEach { category ->
            FilterChip(
                selected = category.id == selectedId,
                onClick = { onSelect(category.id) },
                label = { Text(category.title) }
            )
        }
    }
}

@Composable
fun PrioritySelector(
    selected: Priority?,
    onSelect: (Priority) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        listOf(Priority.High, Priority.Medium, Priority.Low).forEach { priority ->
            RadioButton(selected = priority == selected, onClick = { onSelect(priority) })
            Text(priorityLabel(priority))
        }
    }
}
