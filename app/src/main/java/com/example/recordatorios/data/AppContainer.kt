package com.example.recordatorios.data

import android.content.Context
import androidx.room.Room
import com.example.recordatorios.data.local.AppDatabase
import com.example.recordatorios.data.remote.FirestoreReminderDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings

object AppContainer {

    lateinit var auth: AuthRepository
    lateinit var preferences: UserPreferencesRepository
    lateinit var reminders: ReminderRepository

    fun init(context: Context) {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, "reminders.db").build()

        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.addAuthStateListener {
            if (it.currentUser != null) scheduleSync(context)
        }

        auth = AuthRepository(firebaseAuth)
        preferences = UserPreferencesRepository(context.userPreferencesDataStore)
        reminders = LocalFirstReminderRepository(
            database.reminderDao(),
            FirestoreReminderDataSource(firestore),
            auth,
            preferences
        ) {
            scheduleSync(context)
        }
    }
}
