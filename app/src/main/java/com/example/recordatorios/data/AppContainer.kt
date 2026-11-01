package com.example.recordatorios.data

import android.content.Context
import androidx.room.Room
import com.example.recordatorios.BuildConfig
import com.example.recordatorios.data.local.AppDatabase
import com.example.recordatorios.data.remote.ReminderApiService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object AppContainer {

    lateinit var preferences: UserPreferencesRepository
    lateinit var reminders: ReminderRepository

    fun init(context: Context) {
        val database = Room.databaseBuilder(context, AppDatabase::class.java, "reminders.db").build()

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        val api = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ReminderApiService::class.java)

        preferences = UserPreferencesRepository(context.userPreferencesDataStore)
        reminders = LocalFirstReminderRepository(database.reminderDao(), api, preferences) {
            scheduleSync(context)
        }
    }
}
