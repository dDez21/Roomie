package com.example.roomieproject.viewmodel

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomieproject.firebase.AuthFirebase
import com.example.roomieproject.firebase.GoogleLinkRequiredException
import com.example.roomieproject.util.AuthState
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel (): ViewModel() {

    private val auth = AuthFirebase(FirebaseAuth.getInstance())

    //per gestire lo stato del login
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _authState
    private var pendingGoogleCredential: AuthCredential? = null //in caso da login google ho email già registrata

    //creo nuovo utente
    fun register(username: String, email: String, password: String) {
        if (_authState.value is AuthState.Loading) return
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val uid = auth.register(username, email, password)
                _authState.value = AuthState.Success(uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e)
            }
        }
    }

    //effettuo login
    fun login(email: String, password: String) {
        if (_authState.value is AuthState.Loading) return
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val uid = auth.login(email, password)
                pendingGoogleCredential?.let { cred ->
                    auth.linkGoogleToCurrentUser(cred)
                    pendingGoogleCredential = null
                }
                _authState.value = AuthState.Success(uid)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e)
            }
        }
    }

    //login con google
    fun loginWithGoogle(getCredential: suspend () -> Credential) {
        if (_authState.value is AuthState.Loading) return
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val credential = getCredential()
                val idToken = extractGoogleIdToken(credential)?: error("Google ID token non valido")
                val uid = auth.loginWithGoogle(idToken)
                _authState.value = AuthState.Success(uid)
            } catch (e: GoogleLinkRequiredException) {
                pendingGoogleCredential = e.pendingCredential
                _authState.value = AuthState.Error(e)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e)
            }
        }
    }

    private fun extractGoogleIdToken(credential: Credential): String? {
        return if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } else null
    }

    //porto stato a default
    fun setIdle() {
        _authState.value = AuthState.Idle
    }

    //verifico se utente già loggato
    fun checkSession() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        _authState.value = if (uid != null) AuthState.Success(uid) else AuthState.Idle
    }
}

