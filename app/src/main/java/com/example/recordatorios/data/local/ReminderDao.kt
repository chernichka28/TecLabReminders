package com.example.recordatorios.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE syncStatus != 'PENDING_DELETE' ORDER BY date DESC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE syncStatus != 'PENDING_DELETE' AND state != 'DONE' ORDER BY date DESC")
    fun observeNotCompleted(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id AND syncStatus != 'PENDING_DELETE'")
    fun observeById(id: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun findById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE syncStatus != 'SYNCED'")
    suspend fun findNotSynced(): List<ReminderEntity>

    @Upsert
    suspend fun save(reminder: ReminderEntity)

    @Query("UPDATE reminders SET remoteId = :remoteId WHERE id = :id")
    suspend fun saveRemoteId(id: String, remoteId: String?)

    @Query("UPDATE reminders SET syncStatus = 'SYNCED' WHERE id = :id AND updatedAt = :updatedAt")
    suspend fun markSynced(id: String, updatedAt: Long)

    @Query("UPDATE reminders SET syncStatus = 'ERROR' WHERE id = :id")
    suspend fun markError(id: String)

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun delete(id: String)
}
