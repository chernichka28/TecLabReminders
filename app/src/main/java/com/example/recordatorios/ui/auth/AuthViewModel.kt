package com.example.recordatorios.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Checking : AuthUiState
    data object SignedOut : AuthUiState
    data object Loading : AuthUiState
    data object SignedIn : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val auth: AuthRepository = AppContainer.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Checking)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            auth.checkSession()
            auth.userId.collect { userId ->
                _uiState.update { if (userId != null) AuthUiState.SignedIn else AuthUiState.SignedOut }
            }
        }
    }

    fun signIn(email: String, password: String) = submit(email, password) { auth.signIn(email.trim(), password) }

    fun register(email: String, password: String) = submit(email, password) { auth.register(email.trim(), password) }

    fun signOut() {
        auth.signOut()
    }

    private fun submit(email: String, password: String, action: suspend () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { AuthUiState.Error("Completá el correo y la contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { AuthUiState.Loading }
            try {
                action()
            } catch (e: FirebaseException) {
                _uiState.update { AuthUiState.Error(errorMessage(e)) }
            }
        }
    }

    private fun errorMessage(e: FirebaseException): String =
        when (e) {
            is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres"
            is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
            is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
            is FirebaseNetworkException -> "Sin conexión. Volvé a intentarlo."
            else -> "No se pudo iniciar sesión"
        }
}
