package com.example.recordatorios.domain

data class Reminder(
    val id: Long,
    val title: String,
    val description: String?,
    val state: ReminderState,
    val category: Category,
    val priority: Priority
)

fun remindersByPriority(reminders: List<Reminder>, priority: Priority): List<Reminder> =
    reminders.filter { it.priority == priority }

fun remindersByState(reminders: List<Reminder>, predicate: (ReminderState) -> Boolean): List<Reminder> =
    reminders.filter { predicate(it.state) }
