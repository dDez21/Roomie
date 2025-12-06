package com.example.roomieproject.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import com.example.roomieproject.R
import com.example.roomieproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth //per autenticazione normale e google
    private lateinit var credentialManager: CredentialManager // API autenticazione google

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

        auth = FirebaseAuth.getInstance() //per autenticazione
        credentialManager = CredentialManager.create(this)

        val txtEmail = findViewById<TextInputEditText>(R.id.textEmail)
        val txtPassword = findViewById<TextInputEditText>(R.id.textPassword)
        val btnPpwDim = findViewById<TextView>(R.id.forgotPassword)
        val login = findViewById<MaterialButton>(R.id.LoginButton)
        val register = findViewById<TextView>(R.id.Register)


    }
}