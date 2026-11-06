package com.example.recordatorios.data.remote

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class FirestoreReminderDataSource(
    private val firestore: FirebaseFirestore
) {

    private fun reminders(userId: String): CollectionReference =
        firestore.collection("users").document(userId).collection("reminders")

    suspend fun getReminders(userId: String): List<DocumentSnapshot> =
        reminders(userId).get(Source.SERVER).await().documents

    suspend fun saveReminder(userId: String, id: String, data: Map<String, Any?>) {
        reminders(userId).document(id).set(data).await()
    }

    suspend fun deleteReminder(userId: String, id: String) {
        reminders(userId).document(id).delete().await()
    }
}
