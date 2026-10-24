package com.example.recordatorios.data

import androidx.compose.ui.graphics.Color
import com.example.recordatorios.domain.Category
import com.example.recordatorios.domain.Priority
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderState

const val SIMULATE_LOAD_ERROR = false
const val SIMULATE_SAVE_ERROR = false

val categories = setOf(
    Category(1L, "Leer"),
    Category(2L, "Hacer"),
    Category(3L, "Mirar"),
)

val categoriesColorMapping = mapOf(
    1L to Color(0xFFBBDEFB),
    2L to Color(0xFFC8E6C9),
    3L to Color(0xFFFFF9C4),
)

val reminders = listOf(
    Reminder(1L, "Programacion mobile paso 1", "Leer leccion 1", ReminderState.Done("17/10/2026"),categories.elementAt(0), Priority.High),
    Reminder(2L, "Programacion mobile paso 2", "Hacer AE 1", ReminderState.Done("18/10/2026"),categories.elementAt(1), Priority.Medium),
    Reminder(3L, "Programacion mobile paso 3", "Hacer API 1", ReminderState.InProgress ,categories.elementAt(1), Priority.Low),
    Reminder(4L, "Programacion mobile paso 4", "Mirar Leccion 2", ReminderState.Waiting ,categories.elementAt(2), Priority.Medium)
)
