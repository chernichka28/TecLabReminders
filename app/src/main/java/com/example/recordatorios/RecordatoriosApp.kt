package com.example.recordatorios

import android.app.Application
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.scheduleSync

class RecordatoriosApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
        scheduleSync(this)
    }
}
