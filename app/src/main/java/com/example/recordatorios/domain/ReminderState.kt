package com.example.recordatorios.domain

sealed class ReminderState {
    data object Waiting :
        ReminderState()
    data object InProgress :
        ReminderState()
    data class Done(
        val finishDate: String
    ) : ReminderState()
    data class Error(
        val message: String
    ) : ReminderState()
}

fun stateLabel(state: ReminderState): String =
    when (state) {
        ReminderState.Waiting -> "Pendiente"
        ReminderState.InProgress -> "En progreso"
        is ReminderState.Done -> "Completado (${state.finishDate})"
        is ReminderState.Error -> "Error: ${state.message}"
    }
