package com.example.recordatorios.data

import android.database.sqlite.SQLiteException
import com.example.recordatorios.data.local.ReminderDao
import com.example.recordatorios.data.local.ReminderEntity
import com.example.recordatorios.data.remote.ReminderApiService
import com.example.recordatorios.domain.Category
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderOrder
import com.example.recordatorios.domain.SyncStatus
import com.example.recordatorios.domain.priorityOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

sealed interface SyncResult {
    data object Success : SyncResult
    data object NoConnection : SyncResult
    data object Timeout : SyncResult
    data class InvalidRequest(val code: Int) : SyncResult
    data class ServerError(val code: Int) : SyncResult
    data object InvalidData : SyncResult
    data object StorageError : SyncResult
}

fun syncResultMessage(result: SyncResult): String =
    when (result) {
        SyncResult.Success -> "Sincronización completa"
        SyncResult.NoConnection -> "Sin conexión. Los cambios se sincronizarán más tarde."
        SyncResult.Timeout -> "El servidor no respondió a tiempo. Volvé a intentarlo."
        is SyncResult.InvalidRequest -> "El servidor rechazó la solicitud (${result.code})."
        is SyncResult.ServerError -> "Error del servidor (${result.code}). Volvé a intentarlo."
        SyncResult.InvalidData -> "El servidor envió datos inválidos."
        SyncResult.StorageError -> "No se pudo guardar en el dispositivo."
    }

interface ReminderRepository {
    fun observeReminders(): Flow<List<Reminder>>
    fun observeReminder(id: String): Flow<Reminder?>
    suspend fun save(reminder: Reminder)
    suspend fun delete(id: String)
    suspend fun synchronize(): SyncResult
    fun getCategories(): List<Category>
}

class LocalFirstReminderRepository(
    private val dao: ReminderDao,
    private val api: ReminderApiService,
    private val preferences: UserPreferencesRepository,
    private val onChanged: () -> Unit
) : ReminderRepository {

    private val syncMutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeReminders(): Flow<List<Reminder>> =
        preferences.preferences.flatMapLatest { prefs ->
            val source = if (prefs.showCompleted) dao.observeAll() else dao.observeNotCompleted()
            source.map { list ->
                val reminders = list.map { it.toDomain() }
                if (prefs.reminderOrder == ReminderOrder.BY_PRIORITY) {
                    reminders.sortedBy { priorityOrder(it.priority) }
                } else {
                    reminders
                }
            }
        }

    override fun observeReminder(id: String): Flow<Reminder?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun save(reminder: Reminder) {
        val remoteId = dao.findById(reminder.id)?.remoteId
        dao.save(reminder.toEntity(remoteId, System.currentTimeMillis(), SyncStatus.PENDING))
        onChanged()
    }

    override suspend fun delete(id: String) {
        val reminder = dao.findById(id) ?: return
        dao.save(reminder.copy(updatedAt = System.currentTimeMillis(), syncStatus = SyncStatus.PENDING_DELETE))
        onChanged()
    }

    override suspend fun synchronize(): SyncResult = syncMutex.withLock {
        try {
            downloadChanges()
            dao.findNotSynced().forEach { upload(it) }
            SyncResult.Success
        } catch (e: SocketTimeoutException) {
            SyncResult.Timeout
        } catch (e: IOException) {
            SyncResult.NoConnection
        } catch (e: HttpException) {
            if (e.code() >= 500) SyncResult.ServerError(e.code()) else SyncResult.InvalidRequest(e.code())
        } catch (e: SerializationException) {
            SyncResult.InvalidData
        } catch (e: SQLiteException) {
            SyncResult.StorageError
        }
    }

    private suspend fun downloadChanges() {
        api.getReminders().forEach { dto ->
            val local = dao.findById(dto.localId)
            if (local == null || dto.updatedAt > local.updatedAt) {
                dao.save(dto.toEntity())
            } else if (local.remoteId == null) {
                dao.saveRemoteId(local.id, dto.id)
            }
        }
    }

    private suspend fun upload(reminder: ReminderEntity) {
        val remoteId = reminder.remoteId
        try {
            if (reminder.syncStatus == SyncStatus.PENDING_DELETE) {
                if (remoteId != null) api.deleteReminder(remoteId)
                dao.delete(reminder.id)
            } else if (remoteId == null) {
                val created = api.createReminder(reminder.toDto())
                dao.saveRemoteId(reminder.id, created.id)
                dao.markSynced(reminder.id, reminder.updatedAt)
            } else {
                api.updateReminder(remoteId, reminder.toDto())
                dao.markSynced(reminder.id, reminder.updatedAt)
            }
        } catch (e: HttpException) {
            if (reminder.syncStatus != SyncStatus.PENDING_DELETE) {
                dao.markError(reminder.id)
            } else if (e.code() == 404) {
                dao.delete(reminder.id)
                return
            }
            throw e
        }
    }

    override fun getCategories(): List<Category> = categories.toList()
}
