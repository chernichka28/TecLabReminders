package com.example.recordatorios.data

import android.database.sqlite.SQLiteException
import com.example.recordatorios.data.local.ReminderDao
import com.example.recordatorios.data.local.ReminderEntity
import com.example.recordatorios.data.remote.FirestoreReminderDataSource
import com.example.recordatorios.domain.Category
import com.example.recordatorios.domain.Reminder
import com.example.recordatorios.domain.ReminderOrder
import com.example.recordatorios.domain.SyncStatus
import com.example.recordatorios.domain.priorityOrder
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

sealed interface SyncResult {
    data object Success : SyncResult
    data object NoConnection : SyncResult
    data object Timeout : SyncResult
    data object PermissionDenied : SyncResult
    data object ServerError : SyncResult
    data object StorageError : SyncResult
    data object SignedOut : SyncResult
}

fun syncResultMessage(result: SyncResult): String =
    when (result) {
        SyncResult.Success -> "Sincronización completa"
        SyncResult.NoConnection -> "Sin conexión. Los cambios se sincronizarán más tarde."
        SyncResult.Timeout -> "El servidor no respondió a tiempo. Volvé a intentarlo."
        SyncResult.PermissionDenied -> "No tenés permiso para acceder a estos datos."
        SyncResult.ServerError -> "Error del servidor. Volvé a intentarlo."
        SyncResult.StorageError -> "No se pudo guardar en el dispositivo."
        SyncResult.SignedOut -> "Iniciá sesión para sincronizar."
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
    private val remote: FirestoreReminderDataSource,
    private val auth: AuthRepository,
    private val preferences: UserPreferencesRepository,
    private val onChanged: () -> Unit
) : ReminderRepository {

    private val syncMutex = Mutex()

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeReminders(): Flow<List<Reminder>> =
        combine(auth.userId, preferences.preferences) { userId, prefs -> userId to prefs }
            .flatMapLatest { (userId, prefs) ->
                if (userId == null) return@flatMapLatest flowOf(emptyList())
                val source = if (prefs.showCompleted) dao.observeAll(userId) else dao.observeNotCompleted(userId)
                source.map { list ->
                    val reminders = list.map { it.toDomain() }
                    if (prefs.reminderOrder == ReminderOrder.BY_PRIORITY) {
                        reminders.sortedBy { priorityOrder(it.priority) }
                    } else {
                        reminders
                    }
                }
            }

    override fun observeReminder(id: String): Flow<Reminder?> {
        val userId = auth.currentUserId ?: return flowOf(null)
        return dao.observeById(id, userId).map { it?.toDomain() }
    }

    override suspend fun save(reminder: Reminder) {
        val userId = auth.currentUserId ?: return
        dao.save(reminder.toEntity(userId, System.currentTimeMillis(), SyncStatus.PENDING))
        onChanged()
    }

    override suspend fun delete(id: String) {
        val userId = auth.currentUserId ?: return
        val reminder = dao.findById(id, userId) ?: return
        dao.save(reminder.copy(updatedAt = System.currentTimeMillis(), syncStatus = SyncStatus.PENDING_DELETE))
        onChanged()
    }

    override suspend fun synchronize(): SyncResult = syncMutex.withLock {
        val userId = auth.currentUserId ?: return SyncResult.SignedOut
        try {
            withTimeout(15_000) {
                downloadChanges(userId)
                dao.findNotSynced(userId).forEach { upload(it) }
            }
            SyncResult.Success
        } catch (e: TimeoutCancellationException) {
            SyncResult.Timeout
        } catch (e: FirebaseFirestoreException) {
            when (e.code) {
                FirebaseFirestoreException.Code.UNAVAILABLE -> SyncResult.NoConnection
                FirebaseFirestoreException.Code.PERMISSION_DENIED,
                FirebaseFirestoreException.Code.UNAUTHENTICATED -> SyncResult.PermissionDenied
                else -> SyncResult.ServerError
            }
        } catch (e: SQLiteException) {
            SyncResult.StorageError
        }
    }

    private suspend fun downloadChanges(userId: String) {
        remote.getReminders(userId).forEach { document ->
            val cloud = document.toEntity(userId)
            val local = dao.findById(cloud.id, userId)
            if (local == null || cloud.updatedAt > local.updatedAt) {
                dao.save(cloud)
            }
        }
    }

    private suspend fun upload(reminder: ReminderEntity) {
        try {
            if (reminder.syncStatus == SyncStatus.PENDING_DELETE) {
                remote.deleteReminder(reminder.userId, reminder.id)
                dao.delete(reminder.id)
            } else {
                remote.saveReminder(reminder.userId, reminder.id, reminder.toCloud())
                dao.markSynced(reminder.id, reminder.updatedAt)
            }
        } catch (e: FirebaseFirestoreException) {
            if (reminder.syncStatus != SyncStatus.PENDING_DELETE) dao.markError(reminder.id)
            throw e
        }
    }

    override fun getCategories(): List<Category> = categories.toList()
}
