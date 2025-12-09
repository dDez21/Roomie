package com.example.roomieproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.roomieproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseUser
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout


class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth //per autenticazione normale e google
    private lateinit var credentialManager: CredentialManager // API autenticazione google

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //adatta layout
        setContentView(R.layout.activity_login) //layout da usare


        auth = FirebaseAuth.getInstance() //per autenticazione
        credentialManager = CredentialManager.create(this)


        //collego a id del layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // collego gli input esterni
        val txtEmail = findViewById<TextInputEditText>(R.id.textEmail)
        val txtPassword = findViewById<TextInputEditText>(R.id.textPassword)
        val btnPpwDim = findViewById<TextView>(R.id.forgotPassword)
        val login = findViewById<MaterialButton>(R.id.LoginButton)
        val register = findViewById<TextView>(R.id.Register)
        val googleLogin = findViewById<MaterialButton>(R.id.Google)

        requireNotNull(txtEmail){"Email non trovata nel layout"}
        requireNotNull(txtPassword){"Password non trovata nel layout"}
        requireNotNull(btnPpwDim){"Bottone PpwDim non trovato nel layout"}
        requireNotNull(login){"Bottone Login non trovato nel layout"}
        requireNotNull(register){"Bottone Registrati non trovato nel layout"}
        requireNotNull(googleLogin){"Bottone Login Google non trovato nel layout"}

        //azione password dimenticata
        btnPpwDim.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        //azione login
        login.setOnClickListener {
            val email = txtEmail.text?.toString()?.trim().orEmpty()
            val ppw  = txtPassword.text?.toString()?.trim().orEmpty()
            signIn(email, ppw)
        }

        //azione registrati
        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //azione login Google
        googleLogin.setOnClickListener { launchGoogle() }
    }

    //verifico se utente già loggato per saltare login
    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)
    }


    //verifico se ho connessione ad internet
    private fun hasNetwork(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val nc = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private fun signIn(email: String, password: String) {
        //verifica connessione
        if (!hasNetwork()) {
            Toast.makeText(this, "Errore di connessione", Toast.LENGTH_SHORT).show()
            return
        }

        //verifica email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email non valida", Toast.LENGTH_SHORT).show()
            return
        }

        //verifica password
        if (password.length < 8) {
            Toast.makeText(this, "Password troppo corta (min 8)", Toast.LENGTH_SHORT).show()
            return
        }


        val btn = findViewById<MaterialButton>(R.id.LoginButton)
        btn?.isEnabled = false



        lifecycleScope.launch {
            try {
                val res = withTimeout(15_000) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }
                Log.d(TAG, "signInWithEmail:success uid=${res.user?.uid}")
                updateUI(res.user)

            } catch (e: FirebaseAuthException) {
                val code = e.errorCode
                Log.w(TAG, "LOGIN FAIL code=$code msg=${e.localizedMessage}", e)
                val msg = when (code) {
                    "ERROR_INVALID_EMAIL" -> "Email non valida"
                    "ERROR_USER_NOT_FOUND" -> "Utente non trovato"
                    "ERROR_WRONG_PASSWORD" -> "Password errata"
                    "ERROR_TOO_MANY_REQUESTS" -> "Troppi tentativi. Riprova più tardi"
                    "ERROR_NETWORK_REQUEST_FAILED" -> "Errore di rete"
                    else -> "Accesso fallito ($code)"
                }
                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                updateUI(null)

            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "LOGIN TIMEOUT", e)
                Toast.makeText(this@LoginActivity, "Login timeout", Toast.LENGTH_SHORT).show()
                updateUI(null)

            } catch (e: Exception) {
                Log.e(TAG, "LOGIN unexpected ${e.javaClass.simpleName}: ${e.localizedMessage}", e)
                Toast.makeText(this@LoginActivity, "Unexpected error", Toast.LENGTH_SHORT).show()
                updateUI(null)
            } finally {
                btn?.isEnabled = true
            }
        }
    }


    private fun launchGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                handleGoogleCredential(result.credential)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "getCredential FAILED: ${e.javaClass.simpleName} - ${e.localizedMessage}", e)
                Toast.makeText(this@LoginActivity, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "getCredential unexpected: ${e.localizedMessage}", e)
                Toast.makeText(this@LoginActivity, "Unexpected error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleCredential(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleCred.idToken)
        } else {
            Log.w(TAG, "Credential is not Google ID token: ${credential::class.java.simpleName}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val cred = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(cred)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Firebase Google sign-in: success uid=${auth.currentUser?.uid}")
                    updateUI(auth.currentUser)
                } else {
                    Log.w(TAG, "Firebase Google FAILED: ${task.exception?.javaClass?.simpleName} - ${task.exception?.localizedMessage}", task.exception)
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }


    // Decide cosa fare dopo il login
    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Vai alla menu app
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        } else {
            Log.d(TAG, "Login fallito o utente nullo")
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}