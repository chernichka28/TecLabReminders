package com.example.recordatorios.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ReminderDto(
    val id: String? = null,
    val localId: String,
    val title: String,
    val description: String?,
    val categoryId: Long,
    val priority: String,
    val state: String,
    val finishDate: String?,
    val date: Long,
    val updatedAt: Long
)
