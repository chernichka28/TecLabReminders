package com.example.recordatorios.data

import com.example.recordatorios.domain.Category
import com.example.recordatorios.domain.Reminder
import kotlinx.coroutines.delay
import java.io.IOException

interface ReminderRepository {
    suspend fun getReminders(): List<Reminder>
    suspend fun getReminder(id: Long): Reminder?
    suspend fun save(reminder: Reminder)
    fun getCategories(): List<Category>
}

object InMemoryReminderRepository : ReminderRepository {

    private val items = reminders.toMutableList()

    override suspend fun getReminders(): List<Reminder> {
        delay(1000)
        if (SIMULATE_LOAD_ERROR) throw IOException("No se pudieron cargar los recordatorios")
        return items.toList()
    }

    override suspend fun getReminder(id: Long): Reminder? {
        delay(500)
        return items.find { it.id == id }
    }

    override suspend fun save(reminder: Reminder) {
        delay(1000)
        if (SIMULATE_SAVE_ERROR) throw IOException("No se pudo guardar el recordatorio")
        val newId = (items.maxOfOrNull { it.id } ?: 0) + 1
        items.add(reminder.copy(id = newId))
    }

    override fun getCategories(): List<Category> = categories.toList()
}
