package com.example.recordatorios.data

import com.example.recordatorios.data.local.ReminderEntity
import com.example.recordatorios.data.remote.ReminderDto
import com.example.recordatorios.domain.Priority
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderState
import com.example.recordatorios.domain.SyncStatus

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

fun Reminder.toEntity(remoteId: String?, updatedAt: Long, syncStatus: SyncStatus): ReminderEntity =
    ReminderEntity(
        id = id,
        remoteId = remoteId,
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

fun ReminderEntity.toDto(): ReminderDto =
    ReminderDto(
        id = remoteId,
        localId = id,
        title = title,
        description = description,
        categoryId = categoryId,
        priority = priority,
        state = state,
        finishDate = finishDate,
        date = date,
        updatedAt = updatedAt
    )

fun ReminderDto.toEntity(): ReminderEntity =
    ReminderEntity(
        id = localId,
        remoteId = id,
        title = title,
        description = description,
        categoryId = categoryId,
        priority = priority,
        state = state,
        finishDate = finishDate,
        date = date,
        updatedAt = updatedAt,
        syncStatus = SyncStatus.SYNCED
    )
