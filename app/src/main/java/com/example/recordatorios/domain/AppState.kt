package com.example.recordatorios.domain

sealed class AppState {
    data object Initial :
        AppState()
    data object InProgress :
        AppState()
    data class Done(
        val reminders: List<Reminder>
    ) : AppState()
    data class Error(
        val message: String
    ) : AppState()
}
