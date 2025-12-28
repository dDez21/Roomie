package com.example.roomieproject.util

sealed class AuthState {
    data object Idle : AuthState()  //standard
    data object Loading : AuthState()  //operazione in corso
    data class Success(val uid: String) : AuthState()  //successo
    data class Error(val e: Exception) : AuthState()  //errore
}