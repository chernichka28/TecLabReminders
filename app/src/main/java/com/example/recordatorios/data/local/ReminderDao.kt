package com.example.recordatorios.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE userId = :userId AND syncStatus != 'PENDING_DELETE' ORDER BY date DESC")
    fun observeAll(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE userId = :userId AND syncStatus != 'PENDING_DELETE' AND state != 'DONE' ORDER BY date DESC")
    fun observeNotCompleted(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id AND userId = :userId AND syncStatus != 'PENDING_DELETE'")
    fun observeById(id: String, userId: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE id = :id AND userId = :userId")
    suspend fun findById(id: String, userId: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE userId = :userId AND syncStatus != 'SYNCED'")
    suspend fun findNotSynced(userId: String): List<ReminderEntity>

    @Upsert
    suspend fun save(reminder: ReminderEntity)

    @Query("UPDATE reminders SET syncStatus = 'SYNCED' WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun markSynced(id: String, updatedAt: Long)

    @Query("UPDATE reminders SET syncStatus = 'ERROR' WHERE id = :id")
    suspend fun markError(id: String)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: String)
}
