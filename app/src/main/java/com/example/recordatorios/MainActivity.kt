package com.example.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.ui.AppNavHost
import com.example.recordatorios.ui.settings.SettingsViewModel
import com.example.recordatorios.ui.theme.RecordatoriosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings: SettingsViewModel = viewModel()
            val preferences by settings.preferences.collectAsStateWithLifecycle()

            RecordatoriosTheme(darkTheme = preferences.darkModeEnabled) {
                AppNavHost()
            }
        }
    }
}
