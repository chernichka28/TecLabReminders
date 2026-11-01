package com.example.recordatorios.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.recordatorios.ui.create.CreateScreen
import com.example.recordatorios.ui.detail.DetailScreen
import com.example.recordatorios.ui.list.ListScreen
import com.example.recordatorios.ui.settings.SettingsScreen
import kotlinx.serialization.Serializable

@Serializable object ListaRoute
@Serializable object CrearRoute
@Serializable object AjustesRoute
@Serializable data class DetalleRoute(val id: String)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ListaRoute) {
        composable<ListaRoute> {
            ListScreen(
                onCrearRecordatorio = { navController.navigate(CrearRoute) },
                onRecordatorioClick = { id -> navController.navigate(DetalleRoute(id)) },
                onAjustes = { navController.navigate(AjustesRoute) }
            )
        }
        composable<CrearRoute> {
            CreateScreen(
                onGuardado = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }
        composable<DetalleRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<DetalleRoute>()
            DetailScreen(
                id = route.id,
                onVolver = { navController.popBackStack() }
            )
        }
        composable<AjustesRoute> {
            SettingsScreen(onVolver = { navController.popBackStack() })
        }
    }
}
