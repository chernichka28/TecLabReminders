package com.example.recordatorios.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.recordatorios.domain.SyncStatus

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val description: String?,
    val categoryId: Long,
    val priority: String,
    val state: String,
    val finishDate: String?,
    val date: Long,
    val updatedAt: Long,
    val syncStatus: SyncStatus
)
