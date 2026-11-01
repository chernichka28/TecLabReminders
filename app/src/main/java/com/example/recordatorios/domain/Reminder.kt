package com.example.recordatorios.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SyncStatus { SYNCED, PENDING, PENDING_DELETE, ERROR }

data class Reminder(
    val id: String,
    val title: String,
    val description: String?,
    val state: ReminderState,
    val category: Category,
    val priority: Priority,
    val date: Long,
    val syncStatus: SyncStatus
)

fun syncLabel(status: SyncStatus): String =
    when (status) {
        SyncStatus.SYNCED -> "Sincronizado"
        SyncStatus.PENDING -> "Pendiente de sincronizar"
        SyncStatus.PENDING_DELETE -> "Eliminación pendiente"
        SyncStatus.ERROR -> "Error de sincronización"
    }

fun formatDate(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))

fun remindersByPriority(reminders: List<Reminder>, priority: Priority): List<Reminder> =
    reminders.filter { it.priority == priority }

fun remindersByState(reminders: List<Reminder>, predicate: (ReminderState) -> Boolean): List<Reminder> =
    reminders.filter { predicate(it.state) }
