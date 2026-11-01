package com.example.recordatorios.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReminderApiService {

    @GET("Reminders")
    suspend fun getReminders(): List<ReminderDto>

    @POST("Reminders")
    suspend fun createReminder(@Body reminder: ReminderDto): ReminderDto

    @PUT("Reminders/{id}")
    suspend fun updateReminder(@Path("id") id: String, @Body reminder: ReminderDto): ReminderDto

    @DELETE("Reminders/{id}")
    suspend fun deleteReminder(@Path("id") id: String)
}
