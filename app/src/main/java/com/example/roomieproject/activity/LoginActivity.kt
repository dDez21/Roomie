package com.example.roomieproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.credentials.CredentialManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.GetCredentialRequest
import com.example.roomieproject.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.roomieproject.util.AuthState
import com.example.roomieproject.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private val vm: AuthViewModel by viewModels()
    private lateinit var credentialManager: CredentialManager
    private lateinit var Email: TextInputLayout
    private lateinit var txtEmail: TextInputEditText
    private lateinit var Password: TextInputLayout
    private lateinit var txtPassword: TextInputEditText
    private lateinit var forgotPpw: TextView
    private lateinit var loginButton: MaterialButton
    private lateinit var register: TextView
    private lateinit var googleLoginButton: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() //adatta layout
        setContentView(R.layout.activity_login) //layout da usare

        //collego a id del layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        credentialManager = CredentialManager.create(this)
        Email = findViewById(R.id.Email)
        txtEmail = findViewById(R.id.textEmail)
        Password = findViewById(R.id.Password)
        txtPassword = findViewById(R.id.textPassword)
        forgotPpw = findViewById(R.id.forgotPassword)
        loginButton = findViewById(R.id.LoginButton)
        register = findViewById(R.id.Register)
        googleLoginButton = findViewById(R.id.Google)


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state ->
                    when (state) {

                        AuthState.Idle -> {
                            forgotPpw.isEnabled = true
                            loginButton.isEnabled = true
                            register.isEnabled = true
                            googleLoginButton.isEnabled = true
                        }

                        AuthState.Loading -> {
                            forgotPpw.isEnabled = false
                            loginButton.isEnabled = false
                            register.isEnabled = false
                            googleLoginButton.isEnabled = false
                        }

                        is AuthState.Success -> {
                            val intent =
                                Intent(this@LoginActivity, MenuActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            startActivity(intent)
                        }

                        is AuthState.Error -> {
                            Toast.makeText(
                                this@LoginActivity,
                                state.e.message ?: "Errore registrazione",
                                Toast.LENGTH_SHORT
                            ).show()
                            vm.setIdle()
                        }
                    }
                }
            }
        }
        Log.d("LOGIN", "currentUser = ${FirebaseAuth.getInstance().currentUser?.uid}")
        forgotPpw.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // login email/password
        loginButton.setOnClickListener {
            val email = txtEmail.text?.toString()?.trim().orEmpty()
            val password = txtPassword.text?.toString()?.trim().orEmpty()

            if (!validateInput(email, password)) return@setOnClickListener
            vm.login(email, password)
        }

        // vai alla schermata di registrazione
        register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // login con Google
        googleLoginButton.setOnClickListener {
            vm.loginWithGoogle{
               val googleIdOption = GetGoogleIdOption.Builder().setServerClientId(getString(R.string.default_web_client_id)).setFilterByAuthorizedAccounts(false).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(this@LoginActivity, request)
                result.credential
            }
        }

    }

    //verifico se utente già loggato per saltare login
    override fun onStart() {
        super.onStart()
        if (vm.state.value !is AuthState.Loading) {vm.checkSession()}
    }

    private fun validateInput(email: String, password: String): Boolean{
        var isValid = true
        Email.error = null
        Password.error = null

        //verifica email
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.error = "Email non valida"
            isValid = false
        }

        //verifico password
        if (password.isBlank()) {
            Password.error = "Inserisci la password"
            isValid = false
        }
        return isValid
    }
}