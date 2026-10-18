package com.example.recordatorios.domain

sealed class Priority {
    data object High :
            Priority()
    data object Medium :
            Priority()
    data object Low :
            Priority()
}

fun priorityLabel(priority: Priority): String =
    when (priority) {
        Priority.High -> "Alta"
        Priority.Medium -> "Media"
        Priority.Low -> "Baja"
    }
