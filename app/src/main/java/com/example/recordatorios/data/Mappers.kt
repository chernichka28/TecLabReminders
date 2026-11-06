package com.example.recordatorios.data

import com.example.recordatorios.data.local.ReminderEntity
import com.example.recordatorios.domain.Priority
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderState
import com.example.recordatorios.domain.SyncStatus
import com.google.firebase.firestore.DocumentSnapshot

private fun Priority.toCode(): String =
    when (this) {
        Priority.High -> "HIGH"
        Priority.Medium -> "MEDIUM"
        Priority.Low -> "LOW"
    }

private fun String.toPriority(): Priority =
    when (this) {
        "HIGH" -> Priority.High
        "LOW" -> Priority.Low
        else -> Priority.Medium
    }

private fun ReminderState.toCode(): String =
    when (this) {
        ReminderState.Waiting -> "WAITING"
        ReminderState.InProgress -> "IN_PROGRESS"
        is ReminderState.Done -> "DONE"
    }

private fun stateFrom(code: String, finishDate: String?): ReminderState =
    when (code) {
        "IN_PROGRESS" -> ReminderState.InProgress
        "DONE" -> ReminderState.Done(finishDate ?: "")
        else -> ReminderState.Waiting
    }

fun ReminderEntity.toDomain(): Reminder =
    Reminder(
        id = id,
        title = title,
        description = description,
        state = stateFrom(state, finishDate),
        category = categories.find { it.id == categoryId } ?: categories.first(),
        priority = priority.toPriority(),
        date = date,
        syncStatus = syncStatus
    )

fun Reminder.toEntity(userId: String, updatedAt: Long, syncStatus: SyncStatus): ReminderEntity =
    ReminderEntity(
        id = id,
        userId = userId,
        title = title,
        description = description,
        categoryId = category.id,
        priority = priority.toCode(),
        state = state.toCode(),
        finishDate = (state as? ReminderState.Done)?.finishDate,
        date = date,
        updatedAt = updatedAt,
        syncStatus = syncStatus
    )

fun ReminderEntity.toCloud(): Map<String, Any?> =
    mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "categoryId" to categoryId,
        "priority" to priority,
        "state" to state,
        "finishDate" to finishDate,
        "date" to date,
        "updatedAt" to updatedAt
    )

fun DocumentSnapshot.toEntity(userId: String): ReminderEntity =
    ReminderEntity(
        id = id,
        userId = userId,
        title = getString("title") ?: "",
        description = getString("description"),
        categoryId = getLong("categoryId") ?: 1L,
        priority = getString("priority") ?: "MEDIUM",
        state = getString("state") ?: "WAITING",
        finishDate = getString("finishDate"),
        date = getLong("date") ?: 0L,
        updatedAt = getLong("updatedAt") ?: 0L,
        syncStatus = SyncStatus.SYNCED
    )
