package com.example.recordatorios

import android.app.Application
import com.example.recordatorios.data.AppContainer

class RecordatoriosApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
