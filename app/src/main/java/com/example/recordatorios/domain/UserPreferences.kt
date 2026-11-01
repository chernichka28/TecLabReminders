package com.example.recordatorios.domain

enum class ReminderOrder { BY_DATE, BY_PRIORITY }

data class UserPreferences(
    val darkModeEnabled: Boolean = false,
    val showCompleted: Boolean = true,
    val reminderOrder: ReminderOrder = ReminderOrder.BY_DATE
)

fun parseOrder(value: String?): ReminderOrder =
    ReminderOrder.entries.firstOrNull { it.name == value } ?: ReminderOrder.BY_DATE
