package com.example.recordatorios.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result =
        when (AppContainer.reminders.synchronize()) {
            SyncResult.Success -> Result.success()
            SyncResult.NoConnection, SyncResult.Timeout, SyncResult.ServerError -> Result.retry()
            SyncResult.PermissionDenied, SyncResult.StorageError, SyncResult.SignedOut -> Result.failure()
        }
}

fun scheduleSync(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val request = OneTimeWorkRequestBuilder<SyncWorker>()
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context)
        .enqueueUniqueWork("reminder_sync", ExistingWorkPolicy.KEEP, request)
}
